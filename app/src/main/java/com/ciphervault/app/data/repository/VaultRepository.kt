package com.ciphervault.app.data.repository

import com.ciphervault.app.data.local.dao.CardDao
import com.ciphervault.app.data.local.dao.CredentialDao
import com.ciphervault.app.data.local.dao.EmailDao
import com.ciphervault.app.data.local.dao.PasswordDao
import com.ciphervault.app.data.local.entity.CardEntity
import com.ciphervault.app.data.local.entity.CredentialEntity
import com.ciphervault.app.data.local.entity.EmailEntity
import com.ciphervault.app.data.local.entity.PasswordEntity
import com.ciphervault.app.data.model.UnencryptedCard
import com.ciphervault.app.data.model.UnencryptedCredential
import com.ciphervault.app.data.model.UnencryptedEmail
import com.ciphervault.app.data.model.UnencryptedPassword
import com.ciphervault.app.security.CryptoManager
import com.ciphervault.app.security.EncryptedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.crypto.SecretKey

class VaultRepository(
    private val cardDao: CardDao,
    private val passwordDao: PasswordDao,
    private val emailDao: EmailDao,
    private val credentialDao: CredentialDao
) {

    // --- Cards ---
    fun getCards(secretKey: SecretKey): Flow<List<UnencryptedCard>> {
        return cardDao.getAllCards().map { list ->
            list.map { entity ->
                UnencryptedCard(
                    id = entity.id,
                    cardholderName = entity.cardholderName,
                    cardType = entity.cardType,
                    bankLabel = entity.bankLabel,
                    cardNetwork = entity.cardNetwork,
                    cardNumber = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedCardNumber, entity.ivCardNumber),
                        secretKey
                    ),
                    expiryDate = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedExpiryDate, entity.ivExpiryDate),
                        secretKey
                    ),
                    cvv = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedCvv, entity.ivCvv),
                        secretKey
                    ),
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    suspend fun saveCard(card: UnencryptedCard, secretKey: SecretKey) {
        val encNum = CryptoManager.encrypt(card.cardNumber, secretKey)
        val encExp = CryptoManager.encrypt(card.expiryDate, secretKey)
        val encCvv = CryptoManager.encrypt(card.cvv, secretKey)

        val entity = CardEntity(
            id = card.id,
            cardholderName = card.cardholderName,
            cardType = card.cardType,
            bankLabel = card.bankLabel,
            cardNetwork = card.cardNetwork,
            encryptedCardNumber = encNum.ciphertextBase64,
            ivCardNumber = encNum.ivBase64,
            encryptedExpiryDate = encExp.ciphertextBase64,
            ivExpiryDate = encExp.ivBase64,
            encryptedCvv = encCvv.ciphertextBase64,
            ivCvv = encCvv.ivBase64,
            updatedAt = System.currentTimeMillis()
        )
        if (card.id == 0L) {
            cardDao.insertCard(entity)
        } else {
            cardDao.updateCard(entity)
        }
    }

    suspend fun deleteCard(card: UnencryptedCard) {
        cardDao.deleteCard(
            CardEntity(
                id = card.id,
                cardholderName = card.cardholderName,
                cardType = card.cardType,
                bankLabel = card.bankLabel,
                cardNetwork = card.cardNetwork,
                encryptedCardNumber = "",
                ivCardNumber = "",
                encryptedExpiryDate = "",
                ivExpiryDate = "",
                encryptedCvv = "",
                ivCvv = ""
            )
        )
    }

    // --- Passwords ---
    fun getPasswords(secretKey: SecretKey): Flow<List<UnencryptedPassword>> {
        return passwordDao.getAllPasswords().map { list ->
            list.map { entity ->
                UnencryptedPassword(
                    id = entity.id,
                    websiteName = entity.websiteName,
                    username = entity.username,
                    password = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedPassword, entity.ivPassword),
                        secretKey
                    ),
                    notes = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedNotes, entity.ivNotes),
                        secretKey
                    ),
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    suspend fun savePassword(item: UnencryptedPassword, secretKey: SecretKey) {
        val encPwd = CryptoManager.encrypt(item.password, secretKey)
        val encNotes = CryptoManager.encrypt(item.notes, secretKey)

        val entity = PasswordEntity(
            id = item.id,
            websiteName = item.websiteName,
            username = item.username,
            encryptedPassword = encPwd.ciphertextBase64,
            ivPassword = encPwd.ivBase64,
            encryptedNotes = encNotes.ciphertextBase64,
            ivNotes = encNotes.ivBase64,
            updatedAt = System.currentTimeMillis()
        )
        if (item.id == 0L) {
            passwordDao.insertPassword(entity)
        } else {
            passwordDao.updatePassword(entity)
        }
    }

    suspend fun deletePassword(item: UnencryptedPassword) {
        passwordDao.deletePassword(
            PasswordEntity(
                id = item.id,
                websiteName = item.websiteName,
                username = item.username,
                encryptedPassword = "",
                ivPassword = "",
                encryptedNotes = "",
                ivNotes = ""
            )
        )
    }

    // --- Emails ---
    fun getEmails(secretKey: SecretKey): Flow<List<UnencryptedEmail>> {
        return emailDao.getAllEmails().map { list ->
            list.map { entity ->
                UnencryptedEmail(
                    id = entity.id,
                    emailAddress = entity.emailAddress,
                    provider = entity.provider,
                    password = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedPassword, entity.ivPassword),
                        secretKey
                    ),
                    recoveryInfo = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedRecoveryInfo, entity.ivRecoveryInfo),
                        secretKey
                    ),
                    backupCodes = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedBackupCodes, entity.ivBackupCodes),
                        secretKey
                    ),
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    suspend fun saveEmail(item: UnencryptedEmail, secretKey: SecretKey) {
        val encPwd = CryptoManager.encrypt(item.password, secretKey)
        val encRec = CryptoManager.encrypt(item.recoveryInfo, secretKey)
        val encCodes = CryptoManager.encrypt(item.backupCodes, secretKey)

        val entity = EmailEntity(
            id = item.id,
            emailAddress = item.emailAddress,
            provider = item.provider,
            encryptedPassword = encPwd.ciphertextBase64,
            ivPassword = encPwd.ivBase64,
            encryptedRecoveryInfo = encRec.ciphertextBase64,
            ivRecoveryInfo = encRec.ivBase64,
            encryptedBackupCodes = encCodes.ciphertextBase64,
            ivBackupCodes = encCodes.ivBase64,
            updatedAt = System.currentTimeMillis()
        )
        if (item.id == 0L) {
            emailDao.insertEmail(entity)
        } else {
            emailDao.updateEmail(entity)
        }
    }

    suspend fun deleteEmail(item: UnencryptedEmail) {
        emailDao.deleteEmail(
            EmailEntity(
                id = item.id,
                emailAddress = item.emailAddress,
                provider = item.provider,
                encryptedPassword = "",
                ivPassword = "",
                encryptedRecoveryInfo = "",
                ivRecoveryInfo = "",
                encryptedBackupCodes = "",
                ivBackupCodes = ""
            )
        )
    }

    // --- Credentials ---
    fun getCredentials(secretKey: SecretKey): Flow<List<UnencryptedCredential>> {
        return credentialDao.getAllCredentials().map { list ->
            list.map { entity ->
                UnencryptedCredential(
                    id = entity.id,
                    title = entity.title,
                    category = entity.category,
                    secretValue = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedSecretValue, entity.ivSecretValue),
                        secretKey
                    ),
                    notes = CryptoManager.decrypt(
                        EncryptedData(entity.encryptedNotes, entity.ivNotes),
                        secretKey
                    ),
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    suspend fun saveCredential(item: UnencryptedCredential, secretKey: SecretKey) {
        val encSec = CryptoManager.encrypt(item.secretValue, secretKey)
        val encNotes = CryptoManager.encrypt(item.notes, secretKey)

        val entity = CredentialEntity(
            id = item.id,
            title = item.title,
            category = item.category,
            encryptedSecretValue = encSec.ciphertextBase64,
            ivSecretValue = encSec.ivBase64,
            encryptedNotes = encNotes.ciphertextBase64,
            ivNotes = encNotes.ivBase64,
            updatedAt = System.currentTimeMillis()
        )
        if (item.id == 0L) {
            credentialDao.insertCredential(entity)
        } else {
            credentialDao.updateCredential(entity)
        }
    }

    suspend fun deleteCredential(item: UnencryptedCredential) {
        credentialDao.deleteCredential(
            CredentialEntity(
                id = item.id,
                title = item.title,
                category = item.category,
                encryptedSecretValue = "",
                ivSecretValue = "",
                encryptedNotes = "",
                ivNotes = ""
            )
        )
    }
}
