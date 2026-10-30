package com.ciphervault.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ciphervault.app.data.local.dao.CardDao
import com.ciphervault.app.data.local.dao.CredentialDao
import com.ciphervault.app.data.local.dao.EmailDao
import com.ciphervault.app.data.local.dao.PasswordDao
import com.ciphervault.app.data.local.entity.CardEntity
import com.ciphervault.app.data.local.entity.CredentialEntity
import com.ciphervault.app.data.local.entity.EmailEntity
import com.ciphervault.app.data.local.entity.PasswordEntity

@Database(
    entities = [
        CardEntity::class,
        PasswordEntity::class,
        EmailEntity::class,
        CredentialEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CipherVaultDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun passwordDao(): PasswordDao
    abstract fun emailDao(): EmailDao
    abstract fun credentialDao(): CredentialDao

    companion object {
        @Volatile
        private var INSTANCE: CipherVaultDatabase? = null

        fun getDatabase(context: Context): CipherVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CipherVaultDatabase::class.java,
                    "cipher_vault_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
