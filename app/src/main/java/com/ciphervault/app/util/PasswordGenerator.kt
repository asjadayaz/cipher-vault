package com.ciphervault.app.util

import java.security.SecureRandom

object PasswordGenerator {
    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?"

    fun generatePassword(
        length: Int = 16,
        includeUppercase: Boolean = true,
        includeLowercase: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true
    ): String {
        val charPool = StringBuilder()
        val guaranteedChars = mutableListOf<Char>()
        val random = SecureRandom()

        if (includeLowercase) {
            charPool.append(LOWERCASE)
            guaranteedChars.add(LOWERCASE[random.nextInt(LOWERCASE.length)])
        }
        if (includeUppercase) {
            charPool.append(UPPERCASE)
            guaranteedChars.add(UPPERCASE[random.nextInt(UPPERCASE.length)])
        }
        if (includeDigits) {
            charPool.append(DIGITS)
            guaranteedChars.add(DIGITS[random.nextInt(DIGITS.length)])
        }
        if (includeSymbols) {
            charPool.append(SYMBOLS)
            guaranteedChars.add(SYMBOLS[random.nextInt(SYMBOLS.length)])
        }

        if (charPool.isEmpty()) {
            charPool.append(LOWERCASE)
            guaranteedChars.add(LOWERCASE[random.nextInt(LOWERCASE.length)])
        }

        val actualLength = maxOf(length, guaranteedChars.size)
        val passwordChars = ArrayList(guaranteedChars)

        for (i in guaranteedChars.size until actualLength) {
            val randomIndex = random.nextInt(charPool.length)
            passwordChars.add(charPool[randomIndex])
        }

        passwordChars.shuffle(random)
        return passwordChars.joinToString("")
    }
}
