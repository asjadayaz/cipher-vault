package com.ciphervault.app.util

enum class CardNetwork(val displayName: String) {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMEX("American Express"),
    DISCOVER("Discover"),
    JCB("JCB"),
    DINERS_CLUB("Diners Club"),
    UNKNOWN("Unknown")
}

object CardNetworkDetector {
    fun detectNetwork(cardNumber: String): CardNetwork {
        val cleanNumber = cardNumber.replace("\\s|-".toRegex(), "")
        if (cleanNumber.isEmpty()) return CardNetwork.UNKNOWN

        return when {
            cleanNumber.startsWith("4") -> CardNetwork.VISA
            cleanNumber.matches(Regex("^(5[1-5]|2[2-7]).*")) -> CardNetwork.MASTERCARD
            cleanNumber.matches(Regex("^3[47].*")) -> CardNetwork.AMEX
            cleanNumber.matches(Regex("^(6011|65|64[4-9]|622).*")) -> CardNetwork.DISCOVER
            cleanNumber.matches(Regex("^(352[89]|35[3-8][0-9]).*")) -> CardNetwork.JCB
            cleanNumber.matches(Regex("^3(0[0-5]|[68]).*")) -> CardNetwork.DINERS_CLUB
            else -> CardNetwork.UNKNOWN
        }
    }

    fun formatCardNumber(cardNumber: String): String {
        val clean = cardNumber.replace("\\D".toRegex(), "").take(16)
        return clean.chunked(4).joinToString(" ")
    }
}
