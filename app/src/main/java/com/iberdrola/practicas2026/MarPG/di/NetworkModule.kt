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
        val isEmulator = Build.FINGERPRINT.contains("generic") ||
                Build.FINGERPRINT.contains("vnc") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.BOARD.contains("goldfish") ||
                Build.HARDWARE.contains("goldfish") ||
                Build.HARDWARE.contains("ranchu") ||
                Build.PRODUCT.contains("sdk_google") ||
                Build.PRODUCT.contains("google_sdk") ||
                Build.PRODUCT.contains("sdk") ||
                Build.PRODUCT.contains("sdk_x86") ||
                Build.PRODUCT.contains("vbox86p") ||
                Build.PRODUCT.contains("emulator") ||
                Build.PRODUCT.contains("simulator")

        val url = if (isEmulator) {
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