package com.iberdrola.practicas2026.MarPG.data.repository

import com.iberdrola.practicas2026.MarPG.data.local.dao.ElectronicInvoiceDao
import com.iberdrola.practicas2026.MarPG.data.mapper.toDomain
import com.iberdrola.practicas2026.MarPG.data.mapper.toEntity
import com.iberdrola.practicas2026.MarPG.data.network.ElectronicInvoiceApiService
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.repository.ElectronicInvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Implementación del repositorio de fact elect.
 * Sigue el patrón SSOT (Single Source of Truth) igual que InvoiceRepositoryImpl.
 */
class ElectronicInvoiceRepositoryImpl @Inject constructor(
    private val api: ElectronicInvoiceApiService,
    private val dao: ElectronicInvoiceDao
) : ElectronicInvoiceRepository {

    override fun getAllElectronicInvoice(isCloud: Boolean): Flow<List<ElectronicInvoice>> = flow {
        if (isCloud) {
            //Preparo el flujo reactivo de la DB
            val databaseFlow = getElectronicInvoiceFromDatabase()

            //Emitimo lo que haya en la DB ahora mismo para carga instantánea
            emit(getElectronicInvoiceFromDatabaseOnce())

            try {
                //Petición a la API
                val remoteDtos = api.getElectronicInvoice()
                val entities = remoteDtos.map { it.toEntity() }
                // Actualizo la caché local
                dao.insertAll(entities)

            } catch (e: Exception) {
                // Manejo de excepciones idéntico al de facturas
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

            //Me quedo escuchando cambios en la DB
            emitAll(databaseFlow)

        } else {
            // Si en el futuro necesitas leer contratos de un JSON local (Assets),
            // la lógica iría aquí, similar a InvoiceRepositoryImpl.
            emitAll(getElectronicInvoiceFromDatabase())
        }
    }

    override suspend fun updateElectronicInvoice(electronicInvoice: ElectronicInvoice) {
        dao.updateElectronicInvoice(electronicInvoice.toEntity())
    }

    /**
     * Obtiene los contratos de Room una sola vez (Snapshot).
     */
    private suspend fun getElectronicInvoiceFromDatabaseOnce(): List<ElectronicInvoice> {
        return try {
            dao.getAllElectronicInvoiceOnce().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Crea el flujo reactivo conectado a Room.
     */
    private fun getElectronicInvoiceFromDatabase(): Flow<List<ElectronicInvoice>> {
        return dao.getAllElectronicInvoice().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}