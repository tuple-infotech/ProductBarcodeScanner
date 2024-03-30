package com.tupleinfotech.productbarcodescanner.di

import com.jmsc.postab.data.repository.db.DBRepository
import com.jmsc.postab.data.repository.db.DBRepositoryImpl
import com.tupleinfotech.productbarcodescanner.repository.BarcodeRepository
import com.tupleinfotech.productbarcodescanner.repository.BarcodeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun providesloginrepositoryimpl(repository: BarcodeRepositoryImpl) : BarcodeRepository

    @Binds
    fun provideDao(repository: DBRepositoryImpl): DBRepository

}