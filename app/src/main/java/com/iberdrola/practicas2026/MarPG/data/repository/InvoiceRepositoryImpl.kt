package com.iberdrola.practicas2026.MarPG.data.repository

import android.content.Context
import com.google.gson.Gson
import com.iberdrola.practicas2026.MarPG.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.MarPG.data.mapper.toDomain
import com.iberdrola.practicas2026.MarPG.data.mapper.toDomainList
import com.iberdrola.practicas2026.MarPG.data.mapper.toEntity
import com.iberdrola.practicas2026.MarPG.data.mapper.toEntityList
import com.iberdrola.practicas2026.MarPG.data.model.InvoiceResponse
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceApiServer
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Implementación del repositorio de facturas.
 * Centraliza la lógica de datos mediante el patrón Single Source of Truth (SSOT),
 * priorizando la base de datos local (Room) sobre la red (Retrofit).
 * Ahora el modo local también usa Room como fuente de verdad para permitir persistencia de cambios.
 */
class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceApiServer: InvoiceApiServer,
    private val invoiceDao: InvoiceDao,
    private val gson: Gson,
    @ApplicationContext private val context: Context
): InvoiceRepository {

    /**
     * Obtiene el listado de facturas.
     * @param isCloud True: Sincroniza API con Room. False: Sincroniza JSON con Room.
     */
    override fun getAllInvoices(isCloud: Boolean): Flow<List<Invoice>> = flow {
        if(isCloud){
            //Preparo el flujo reactivo de la base de datos local (SSOT)
            val databaseFlow = getInvoicesFromDatabase()

            try {
                //Emito el primer valor de la DB rápido para que no haya espera
                emit(getInvoicesFromDatabaseOnce())
                //Intento de actualización desde Mockoon
                val response = invoiceApiServer.getInvoices()

                //Actualizamos la Fuente Única de Verdad
                invoiceDao.refreshCache(response.invoices.toEntityList())

            } catch (e: Exception) {
                val customException = when (e) {
                    is UnknownHostException,
                    is ConnectException,
                    is SocketTimeoutException,
                    is IOException -> InvoiceException.NetworkError

                    is HttpException -> InvoiceException.ServerError(e.code())
                    else -> InvoiceException.Unknown
                }
                throw customException
            }
            emitAll(databaseFlow)

        }else {
            val databaseFlow = getInvoicesFromDatabase()
            try {
                val currentLocal = getInvoicesFromDatabaseOnce()
                if (currentLocal.isEmpty()) {
                    delay((1000..3000).random().toLong())

                    val jsonText = context.assets.open("invoice.json").bufferedReader().use {
                        it.readText()
                    }

                    val response = gson.fromJson(jsonText, InvoiceResponse::class.java)

                    invoiceDao.insertInvoices(response.invoices.toEntityList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw InvoiceException.LocalDataError
            }
            emitAll(databaseFlow)
        }
    }

    /**
     *Función auxiliar para obtener las facturas de Room una sola vez
     */
    private suspend fun getInvoicesFromDatabaseOnce(): List<Invoice> {
        //leo la lista actual de entidades y la mapeo a dominio
        return try {
            //lectura de la caché
            invoiceDao.getAllInvoicesOnce().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()//si falla devuelvo vacia
        }
    }

    /**
     * Crea un flujo reactivo ligado a Room.
     * El operador .map transforma las entidades de base de datos a modelos de dominio.
     */
    fun getInvoicesFromDatabase(): Flow<List<Invoice>> {

        return invoiceDao.getAllInvoices().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun payInvoice(id: String, isCloud: Boolean) {
        if (isCloud) {
            try {
                invoiceApiServer.payInvoice(id)
            } catch (e: Exception) {
                // En prácticas, si falla el servidor, seguimos para actualizar Room
            }
        }
        // Actualizamos Room. Como getAllInvoices emite un Flow de Room, la UI reaccionará sola.
        invoiceDao.updateInvoiceToPaid(id)
    }
}
