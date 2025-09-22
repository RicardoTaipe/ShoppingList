package com.rico.shoppinglist.di

import android.content.Context
import androidx.room.Room
import com.rico.shoppinglist.data.ShoppingItemDao
import com.rico.shoppinglist.data.ShoppingItemRepository
import com.rico.shoppinglist.data.ShoppingItemRepositoryImpl
import com.rico.shoppinglist.data.ShoppingListDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideShoppingListDatabase(@ApplicationContext context: Context): ShoppingListDatabase {
        return Room.databaseBuilder(
                context,
                ShoppingListDatabase::class.java,
                "shopping_list_database"
            ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    fun provideShoppingItemDao(database: ShoppingListDatabase): ShoppingItemDao {
        return database.shoppingItemDao()
    }

    @Provides
    @Singleton
    fun provideShoppingItemRepository(dao: ShoppingItemDao): ShoppingItemRepository {
        return ShoppingItemRepositoryImpl(dao)
    }
}