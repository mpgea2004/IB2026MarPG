package com.iberdrola.practicas2026.MarPG.di

import android.os.Build
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
 * Módulo de red para la configuración de Retrofit.
 * Soporta tanto emulador como móvil físico automáticamente.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provee la URL base dinámica.
     * Móvil Físico: usa localhost (requiere adb reverse tcp:3000 tcp:3000)
     * Emulador: usa 10.0.2.2
     */
    private fun getBaseUrl(): String {
        return if (Build.FINGERPRINT.contains("generic") || 
                   Build.MODEL.contains("Emulator") || 
                   Build.MODEL.contains("Android SDK built for x86")) {
            "http://10.0.2.2:3000/"
        } else {
            "http://localhost:3000/"
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getBaseUrl())
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