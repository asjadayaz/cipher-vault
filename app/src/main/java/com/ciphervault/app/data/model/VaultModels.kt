package com.ciphervault.app.data.model

data class UnencryptedCard(
    val id: Long = 0,
    val cardholderName: String,
    val cardType: String,
    val bankLabel: String,
    val cardNetwork: String,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val updatedAt: Long = System.currentTimeMillis()
)

data class UnencryptedPassword(
    val id: Long = 0,
    val websiteName: String,
    val username: String,
    val password: String,
    val notes: String,
    val updatedAt: Long = System.currentTimeMillis()
)

data class UnencryptedEmail(
    val id: Long = 0,
    val emailAddress: String,
    val provider: String,
    val password: String,
    val recoveryInfo: String,
    val backupCodes: String,
    val updatedAt: Long = System.currentTimeMillis()
)

data class UnencryptedCredential(
    val id: Long = 0,
    val title: String,
    val category: String,
    val secretValue: String,
    val notes: String,
    val updatedAt: Long = System.currentTimeMillis()
)
