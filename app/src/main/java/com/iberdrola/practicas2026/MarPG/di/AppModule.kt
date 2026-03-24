package com.iberdrola.practicas2026.MarPG.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.iberdrola.practicas2026.MarPG.data.analytics.FirebaseAnalyticsManager
import com.iberdrola.practicas2026.MarPG.data.local.dao.ElectronicInvoiceDao
import com.iberdrola.practicas2026.MarPG.data.local.dao.InvoiceDao
import com.iberdrola.practicas2026.MarPG.data.local.dao.InvoiceDatabase
import com.iberdrola.practicas2026.MarPG.data.local.dao.UserDao
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.data.network.ElectronicInvoiceApiService
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceApiServer
import com.iberdrola.practicas2026.MarPG.data.repository.ElectronicInvoiceRepositoryImpl
import com.iberdrola.practicas2026.MarPG.data.repository.InvoiceRepositoryImpl
import com.iberdrola.practicas2026.MarPG.domain.repository.ElectronicInvoiceRepository
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsManager
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo principal de Inyección de Dependencias (Dagger Hilt)
 * Provee las instancias únicas (Singletons) necesarias para toda la aplicación
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** Provee la instancia única de la base de datos Room */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InvoiceDatabase {
        return InvoiceDatabase.getDatabase(context)
    }

    /** Provee el DAO para el acceso a las tablas de facturas */
    @Provides
    @Singleton
    fun provideInvoiceDao(database: InvoiceDatabase): InvoiceDao {
        return database.invoiceDao()
    }

    /** Provee el DAO para el acceso a las tablas de fact elec */
    @Provides
    @Singleton
    fun provideContractDao(db: InvoiceDatabase): ElectronicInvoiceDao = db.electronicInvoiceDao()

    /** Provee el motor de serialización GSON */
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()


    /**
     * Provee la implementación del repositorio de facturas
     * Vincula la interfaz de dominio [InvoiceRepository] con su implementación de datos
     */
    @Provides
    @Singleton
    fun provideInvoiceRepository(
        @ApplicationContext context: Context,
        gson: Gson,
        invoiceDao: InvoiceDao,
        invoiceApiServer: InvoiceApiServer
    ): InvoiceRepository {
        return InvoiceRepositoryImpl(
            invoiceApiServer = invoiceApiServer,
            invoiceDao = invoiceDao,
            gson = gson,
            context = context
        )
    }

    /**
     * Provee la implementación del repositorio de factura electrónica
     */
    @Provides
    @Singleton
    fun provideElectronicInvoiceRepository(
        api: ElectronicInvoiceApiService,
        dao: ElectronicInvoiceDao,
        gson: Gson,
        @ApplicationContext context: Context
    ): ElectronicInvoiceRepository {
        return ElectronicInvoiceRepositoryImpl(api, dao, gson, context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: InvoiceDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAnalyticsManager(firebaseAnalytics: FirebaseAnalytics): AnalyticsManager {
        return FirebaseAnalyticsManager(firebaseAnalytics)
    }

}
