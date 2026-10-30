package com.ciphervault.app.security

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.SecretKey

class PinManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var currentSecretKey: SecretKey? = null
        private set

    fun isPinSet(): Boolean {
        return prefs.contains(KEY_PIN_HASH) && prefs.contains(KEY_SALT)
    }

    fun setupPin(pin: String): Boolean {
        if (pin.length != 4) return false
        val salt = CryptoManager.generateSalt()
        val saltBase64 = Base64.getEncoder().encodeToString(salt)
        val hash = hashPin(pin, salt)

        prefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_SALT, saltBase64)
            .apply()

        currentSecretKey = CryptoManager.deriveKey(pin.toCharArray(), salt)
        return true
    }

    fun verifyPin(pin: String): Boolean {
        if (pin.length != 4) return false
        val savedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val saltBase64 = prefs.getString(KEY_SALT, null) ?: return false
        val salt = Base64.getDecoder().decode(saltBase64)

        val computedHash = hashPin(pin, salt)
        return if (savedHash == computedHash) {
            currentSecretKey = CryptoManager.deriveKey(pin.toCharArray(), salt)
            true
        } else {
            false
        }
    }

    fun lockVault() {
        currentSecretKey = null
    }

    val isUnlocked: Boolean
        get() = currentSecretKey != null

    private fun hashPin(pin: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val hashedBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hashedBytes)
    }

    companion object {
        private const val PREFS_NAME = "ciphervault_sec_prefs"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_SALT = "pin_salt"
    }
}
