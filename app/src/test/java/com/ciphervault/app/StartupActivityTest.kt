package com.ciphervault.app

import com.ciphervault.app.security.CryptoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StartupLogicTest {

    @Test
    fun testCryptoManagerDeriveKeyAndEncryptDecrypt() {
        val pin = "1234"
        val salt = CryptoManager.generateSalt()
        val key = CryptoManager.deriveKey(pin.toCharArray(), salt)
        assertNotNull(key)

        val encrypted = CryptoManager.encrypt("test-secret", key)
        assertTrue(encrypted.ciphertextBase64.isNotEmpty())
        assertTrue(encrypted.ivBase64.isNotEmpty())

        val decrypted = CryptoManager.decrypt(encrypted, key)
        assertEquals("test-secret", decrypted)
    }
}
