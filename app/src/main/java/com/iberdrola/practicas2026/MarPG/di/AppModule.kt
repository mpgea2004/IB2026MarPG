package com.iberdrola.practicas2026.MarPG.di

import android.content.Context
import com.google.gson.Gson
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
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InvoiceDatabase {
        return InvoiceDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideInvoiceDao(database: InvoiceDatabase): InvoiceDao {
        return database.invoiceDao()
    }

    @Provides
    @Singleton
    fun provideContractDao(db: InvoiceDatabase): ElectronicInvoiceDao = db.electronicInvoiceDao()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

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

}