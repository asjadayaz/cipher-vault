package com.ciphervault.app.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CryptoManagerTest {

    @Test
    fun testEncryptionAndDecryption() {
        val pin = "1234"
        val salt = CryptoManager.generateSalt()
        val key = CryptoManager.deriveKey(pin.toCharArray(), salt)

        val originalText = "4532 1234 5678 9012"
        val encryptedData = CryptoManager.encrypt(originalText, key)

        assertNotEquals(originalText, encryptedData.ciphertextBase64)
        assertTrue(encryptedData.ciphertextBase64.isNotEmpty())
        assertTrue(encryptedData.ivBase64.isNotEmpty())

        val decryptedText = CryptoManager.decrypt(encryptedData, key)
        assertEquals(originalText, decryptedText)
    }

    @Test
    fun testEmptyStringEncryption() {
        val pin = "1234"
        val salt = CryptoManager.generateSalt()
        val key = CryptoManager.deriveKey(pin.toCharArray(), salt)

        val encryptedData = CryptoManager.encrypt("", key)
        assertEquals("", encryptedData.ciphertextBase64)
        assertEquals("", encryptedData.ivBase64)

        val decryptedText = CryptoManager.decrypt(encryptedData, key)
        assertEquals("", decryptedText)
    }
}
