package com.ciphervault.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardholderName: String,
    val cardType: String, // "Credit" or "Debit"
    val bankLabel: String,
    val cardNetwork: String, // Visa, Mastercard, Amex, Discover, JCB, Unknown
    val encryptedCardNumber: String,
    val ivCardNumber: String,
    val encryptedExpiryDate: String,
    val ivExpiryDate: String,
    val encryptedCvv: String,
    val ivCvv: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val websiteName: String,
    val username: String,
    val encryptedPassword: String,
    val ivPassword: String,
    val encryptedNotes: String,
    val ivNotes: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "email_accounts")
data class EmailEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emailAddress: String,
    val provider: String, // Gmail, Outlook, Yahoo, Custom
    val encryptedPassword: String,
    val ivPassword: String,
    val encryptedRecoveryInfo: String,
    val ivRecoveryInfo: String,
    val encryptedBackupCodes: String,
    val ivBackupCodes: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "security_credentials")
data class CredentialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Bank PIN, National ID, API Key, Crypto Seed, Wi-Fi, Other
    val encryptedSecretValue: String,
    val ivSecretValue: String,
    val encryptedNotes: String,
    val ivNotes: String,
    val updatedAt: Long = System.currentTimeMillis()
)
