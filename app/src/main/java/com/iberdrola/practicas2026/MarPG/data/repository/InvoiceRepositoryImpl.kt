package com.iberdrola.practicas2026.MarPG.data.repository

import android.content.Context
import com.google.gson.Gson
import com.iberdrola.practicas2026.MarPG.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.MarPG.data.mapper.toDomain
import com.iberdrola.practicas2026.MarPG.data.mapper.toDomainList
import com.iberdrola.practicas2026.MarPG.data.mapper.toEntity
import com.iberdrola.practicas2026.MarPG.data.model.InvoiceResponse
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceApiServer
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

/**
 * Implementación del repositorio de facturas
 * Gestiona la lógica de decisión entre datos remotos (API), locales (Assets) y caché (Room)
 */
class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceApiServer: InvoiceApiServer,
    private val invoiceDao: InvoiceDao,
    private val gson: Gson,
    @ApplicationContext private val context: Context
): InvoiceRepository {

    /**
     * Obtiene el listado de facturas según el modo seleccionado
     * @param isCloud True para Mockoon + Caché, False para archivo JSON local
     * @return [Flow] con la lista de facturas mapeadas a dominio
     */
    override fun getAllInvoices(isCloud: Boolean): Flow<List<Invoice>> = flow {
        if(isCloud){
            try {
                //Opcion 1: Red (Mockoon)
                //aqui no uso delay, ya que de por si la respuesta de la red suele tardar un pelin
                val response = invoiceApiServer.getInvoices()
                val remoteInvoices = response.invoices.toDomainList()

                //Guardo rl caché de Room
                val entities = response.invoices.map { it.toEntity() }
                invoiceDao.insertInvoices(entities)

                emit(remoteInvoices)
            }catch (e: Exception){
                val cached = getInvoicesFromDatabaseOnce()

                // Mapeamos el error de sistema a nuestra excepción personalizada
                val customException = when (e) {
                    is UnknownHostException, is IOException -> InvoiceException.NoInternet
                    is HttpException -> InvoiceException.ServerError(e.code())
                    else -> InvoiceException.Unknown
                }

                if (cached.isNotEmpty()) {
                    emit(cached) //Primero damos los datos que tenemos
                    throw customException //Y luego lanzamos el aviso para el ViewModel
                } else {
                    throw customException
                }
            }

        }else {
            try {
                //Simulo tiempo de carga entre 1 y 3 segundos
                val delayTime = (1000..3000).random().toLong()
                delay(delayTime)

                //Intento leer el archivo de assets
                val jsonText = context.assets.open("invoice.json").bufferedReader().use {
                    it.readText()
                }

                //Intento convertir el texto en objeto
                val response = gson.fromJson(jsonText, InvoiceResponse::class.java)

                //transformo y emito
                val invoiceList = response.invoices.toDomainList()
                emit(invoiceList)

            } catch (e: Exception) {
                //Si el archivo no existe, el JSON es inválido o falla el mapeo,
                //lanzo la excepción personalizada para que el ViewModel la capture.
                e.printStackTrace()
                throw InvoiceException.LocalDataError
            }
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
     * Obtiene facturas de Room mediante un flujo reactivo
     * Actualmente en desuso, reservado para futuras funcionalidades
     */
    fun getInvoicesFromDatabase(): Flow<List<Invoice>> {
        //Aqui primero llamo al dao que me devuelve el flujo de facturas,
        //luego uso el .map de flow para entrar en la emision
        //y luego uso el .map de list para transformar las entidades con el mapper
        return invoiceDao.getAllInvoices().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}