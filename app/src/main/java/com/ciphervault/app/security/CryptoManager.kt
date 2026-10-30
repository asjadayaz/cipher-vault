package com.ciphervault.app.security

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

data class EncryptedData(
    val ciphertextBase64: String,
    val ivBase64: String
)

object CryptoManager {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12
    private const val PBKDF2_ITERATIONS = 10000
    private const val KEY_LENGTH_BIT = 256

    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

    fun deriveKey(pin: CharArray, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(pin, salt, PBKDF2_ITERATIONS, KEY_LENGTH_BIT)
        val factory = try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        } catch (e: NoSuchAlgorithmException) {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        }
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    fun encrypt(plainText: String, secretKey: SecretKey): EncryptedData {
        if (plainText.isEmpty()) {
            return EncryptedData("", "")
        }
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val iv = ByteArray(IV_LENGTH_BYTE)
            SecureRandom().nextBytes(iv)
            val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

            val cipherTextBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val ciphertextBase64 = Base64.getEncoder().encodeToString(cipherTextBytes)
            val ivBase64 = Base64.getEncoder().encodeToString(iv)

            return EncryptedData(ciphertextBase64, ivBase64)
        } catch (e: Exception) {
            e.printStackTrace()
            return EncryptedData("", "")
        }
    }

    fun decrypt(encryptedData: EncryptedData, secretKey: SecretKey): String {
        if (encryptedData.ciphertextBase64.isEmpty() || encryptedData.ivBase64.isEmpty()) {
            return ""
        }
        try {
            val cipherTextBytes = Base64.getDecoder().decode(encryptedData.ciphertextBase64)
            val ivBytes = Base64.getDecoder().decode(encryptedData.ivBase64)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, ivBytes)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)

            val plainTextBytes = cipher.doFinal(cipherTextBytes)
            return String(plainTextBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}
