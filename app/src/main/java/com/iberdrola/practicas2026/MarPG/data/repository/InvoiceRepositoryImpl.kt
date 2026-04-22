package com.iberdrola.practicas2026.MarPG.data.repository

import android.content.Context
import com.google.gson.Gson
import com.iberdrola.practicas2026.MarPG.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.MarPG.data.mapper.toDomain
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
import javax.inject.Singleton
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Implementación del repositorio de facturas con patrón SSOT (Single Source of Truth).
 * Gestiona la sincronización entre API (Mockoon) y base de datos local (Room).
 */
@Singleton
class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceApiServer: InvoiceApiServer,
    private val invoiceDao: InvoiceDao,
    private val gson: Gson,
    @ApplicationContext private val context: Context
): InvoiceRepository {

    private var lastModeIsCloud: Boolean? = null

    override fun getAllInvoices(isCloud: Boolean): Flow<List<Invoice>> = flow {
        val databaseFlow = getInvoicesFromDatabase()
        val modeChanged = lastModeIsCloud != isCloud
        lastModeIsCloud = isCloud
        if(isCloud){
            emit(getInvoicesFromDatabaseOnce())

            try {
                val response = invoiceApiServer.getInvoices()
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

        } else {
            try {
                val currentDb = getInvoicesFromDatabaseOnce()
                if (modeChanged || currentDb.isEmpty()) {
                    delay((1000..3000).random().toLong())
                    
                    val jsonText = context.assets.open("invoice.json").bufferedReader().use { it.readText() }
                    val response = gson.fromJson(jsonText, InvoiceResponse::class.java)
                    
                    invoiceDao.refreshCache(response.invoices.toEntityList())
                }
            } catch (e: Exception) {
                throw InvoiceException.LocalDataError
            }
            emitAll(databaseFlow)
        }
    }

    private suspend fun getInvoicesFromDatabaseOnce(): List<Invoice> {
        return try {
            invoiceDao.getAllInvoicesOnce().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getInvoicesFromDatabase(): Flow<List<Invoice>> {
        return invoiceDao.getAllInvoices().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun payInvoice(id: String, isCloud: Boolean) {
        if (isCloud) {
            try {
                invoiceApiServer.payInvoice(id)
            } catch (e: Exception) {  }
        }
        invoiceDao.updateInvoiceToPaid(id)
    }
}
