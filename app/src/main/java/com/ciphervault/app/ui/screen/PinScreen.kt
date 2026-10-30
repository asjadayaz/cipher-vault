package com.ciphervault.app.ui.screen

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciphervault.app.ui.theme.EmeraldPrimary
import com.ciphervault.app.ui.theme.GoldAccent
import com.ciphervault.app.ui.theme.ObsidianBackground
import com.ciphervault.app.ui.theme.ObsidianSurface
import com.ciphervault.app.ui.theme.TextPrimary
import com.ciphervault.app.ui.theme.TextSecondary
import com.ciphervault.app.ui.viewmodel.VaultViewModel

@Composable
fun PinScreen(viewModel: VaultViewModel) {
    val isPinSet = remember { viewModel.pinManager.isPinSet() }
    var pinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val titleText = when {
        !isPinSet && !isConfirming -> "Create Master PIN"
        !isPinSet && isConfirming -> "Confirm Master PIN"
        else -> "Enter Master PIN"
    }

    val subtitleText = when {
        !isPinSet && !isConfirming -> "Set a 4-digit PIN to secure your vault"
        !isPinSet && isConfirming -> "Re-enter your 4-digit PIN to confirm"
        else -> "Enter your 4-digit PIN to unlock"
    }

    val currentInput = if (!isPinSet && isConfirming) confirmPinInput else pinInput

    fun onKeyPress(digit: String) {
        try {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        } catch (e: Exception) {
            // Safe fallback if haptic feedback fails on some devices
        }
        errorMessage = null

        val newInput = currentInput + digit
        if (newInput.length <= 4) {
            if (!isPinSet && isConfirming) {
                confirmPinInput = newInput
            } else {
                pinInput = newInput
            }

            if (newInput.length == 4) {
                if (!isPinSet) {
                    if (!isConfirming) {
                        isConfirming = true
                    } else {
                        if (pinInput == confirmPinInput) {
                            val success = viewModel.setupMasterPin(pinInput)
                            if (!success) {
                                errorMessage = "Failed to set PIN. Please try again."
                                pinInput = ""
                                confirmPinInput = ""
                                isConfirming = false
                            }
                        } else {
                            errorMessage = "PINs do not match. Try again."
                            pinInput = ""
                            confirmPinInput = ""
                            isConfirming = false
                        }
                    }
                } else {
                    val success = viewModel.unlockVault(newInput)
                    if (!success) {
                        errorMessage = "Invalid Master PIN"
                        pinInput = ""
                    }
                }
            }
        }
    }

    fun onDeletePress() {
        try {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        } catch (e: Exception) {
            // Safe fallback
        }
        errorMessage = null

        if (!isPinSet && isConfirming) {
            if (confirmPinInput.isNotEmpty()) {
                confirmPinInput = confirmPinInput.dropLast(1)
            }
        } else {
            if (pinInput.isNotEmpty()) {
                pinInput = pinInput.dropLast(1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Header Section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(ObsidianSurface)
                    .border(2.dp, GoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    tint = GoldAccent,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = titleText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitleText,
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PIN Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < currentInput.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) EmeraldPrimary else ObsidianSurface)
                            .border(
                                width = 1.5.dp,
                                color = if (isFilled) EmeraldPrimary else TextSecondary,
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Keypad Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val keypad = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "back")
            )

            keypad.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { item ->
                        when (item) {
                            "" -> {
                                Spacer(modifier = Modifier.size(72.dp))
                            }
                            "back" -> {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(ObsidianSurface)
                                        .clickable { onDeletePress() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Delete",
                                        tint = TextPrimary
                                    )
                                }
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(ObsidianSurface)
                                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), CircleShape)
                                        .clickable { onKeyPress(item) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
