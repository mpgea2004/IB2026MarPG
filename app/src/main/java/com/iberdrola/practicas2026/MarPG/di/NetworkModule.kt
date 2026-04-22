package com.iberdrola.practicas2026.MarPG.di

import android.os.Build
import android.util.Log
import com.iberdrola.practicas2026.MarPG.data.network.ElectronicInvoiceApiService
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceApiServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private fun getBaseUrl(): String {
        val url = if (Build.FINGERPRINT.contains("generic") || 
                   Build.MODEL.contains("Emulator") || 
                   Build.MODEL.contains("Android SDK built for x86")) {
            "https://10.0.2.2:3000/"
        } else {
            "https://localhost:3000/"
        }
        
        Log.d("NetworkModule", ">>> CONFIGURACIÓN DE RED <<<")
        Log.d("NetworkModule", "URL Base: $url")
        Log.d("NetworkModule", "Protocolo: ${if (url.startsWith("https")) "HTTPS (Seguro)" else "HTTP (No seguro)"}")
        Log.d("NetworkModule", "Dispositivo: ${if (url.contains("10.0.2.2")) "Emulador" else "Móvil Físico (localhost)"}")
        
        return url
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideInvoiceApi(retrofit: Retrofit): InvoiceApiServer = retrofit.create(InvoiceApiServer::class.java)

    @Provides
    @Singleton
    fun provideElectronicInvoiceApiService(retrofit: Retrofit): ElectronicInvoiceApiService = retrofit.create(ElectronicInvoiceApiService::class.java)
}