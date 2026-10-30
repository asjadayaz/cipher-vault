package com.ciphervault.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UtilTest {

    @Test
    fun testCardNetworkDetector() {
        assertEquals(CardNetwork.VISA, CardNetworkDetector.detectNetwork("4111 2222 3333 4444"))
        assertEquals(CardNetwork.MASTERCARD, CardNetworkDetector.detectNetwork("5100 0000 0000 0000"))
        assertEquals(CardNetwork.AMEX, CardNetworkDetector.detectNetwork("3782 822463 10005"))
        assertEquals(CardNetwork.DISCOVER, CardNetworkDetector.detectNetwork("6011 0000 0000 0000"))
        assertEquals(CardNetwork.JCB, CardNetworkDetector.detectNetwork("3528 0000 0000 0000"))
    }

    @Test
    fun testFormatCardNumber() {
        val formatted = CardNetworkDetector.formatCardNumber("4111222233334444")
        assertEquals("4111 2222 3333 4444", formatted)
    }

    @Test
    fun testPasswordGenerator() {
        val password = PasswordGenerator.generatePassword(16, true, true, true, true)
        assertEquals(16, password.length)
        assertTrue(password.any { it.isUpperCase() })
        assertTrue(password.any { it.isLowerCase() })
        assertTrue(password.any { it.isDigit() })
    }
}
