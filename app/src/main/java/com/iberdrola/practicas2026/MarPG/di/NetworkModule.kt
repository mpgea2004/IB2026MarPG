package com.iberdrola.practicas2026.MarPG.di

import com.iberdrola.practicas2026.MarPG.data.network.ElectronicInvoiceApiService
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceApiServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Módulo de red para la configuración de Retrofit
 * Centraliza la comunicación con los servicios externos (Mockoon)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Configura y provee el cliente de Retrofit
     * URL base: http://10.0.2.2:3000/ (Referencia al localhost desde el emulador)
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // IP para el emulador hacia tu PC
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Provee la implementación de la interfaz [InvoiceApiServer] */
    @Provides
    @Singleton
    fun provideInvoiceApi(retrofit: Retrofit): InvoiceApiServer {
        return retrofit.create(InvoiceApiServer::class.java)
    }

    @Provides
    @Singleton
    fun provideElectronicInvoiceApiService(retrofit: Retrofit): ElectronicInvoiceApiService {
        return retrofit.create(ElectronicInvoiceApiService::class.java)
    }
}