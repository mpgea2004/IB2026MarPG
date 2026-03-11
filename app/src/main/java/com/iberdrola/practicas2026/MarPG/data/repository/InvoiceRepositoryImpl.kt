package com.iberdrola.practicas2026.MarPG.data.repository

import android.content.Context
import com.google.gson.Gson
import com.iberdrola.practicas2026.MarPG.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.MarPG.data.mapper.toDomain
import com.iberdrola.practicas2026.MarPG.data.mapper.toDomainList
import com.iberdrola.practicas2026.MarPG.data.model.InvoiceResponse
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val gson: Gson,
    @ApplicationContext private val context: Context
): InvoiceRepository {

    override fun getAllInvoices(): Flow<List<Invoice>> = flow {
        //Espero un pelín para que parezca que carga internet
        delay(1500)

        //leo el archivo y lo guardo en un String
        val jsonText = context.assets.open("invoice.json").bufferedReader().use {
            it.readText()
        }

        //Despues convierto el texto en el objeto de respuesta
        val answer = gson.fromJson(jsonText, InvoiceResponse::class.java)

        //Luego transformo la lista de los datos son a datos de la app
        val invoiceList = answer.invoices.toDomainList()

        emit(invoiceList)


    }
//Aun no lo uso pero lo dejo por que más adelante lo usaré
    fun getInvoicesFromDatabase(): Flow<List<Invoice>> {
        //Aqui primero llamo al dao que me devuelve el flujo de facturas,
        //luego uso el .map de flow para entrar en la emision
        //y luego uso el .map de list para transformar las entidades con el mapper
        return invoiceDao.getAllInvoices().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}