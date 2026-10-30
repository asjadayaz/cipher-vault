package com.ciphervault.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciphervault.app.data.model.UnencryptedCard
import com.ciphervault.app.ui.theme.EmeraldPrimary
import com.ciphervault.app.ui.theme.GoldAccent
import com.ciphervault.app.ui.theme.TextPrimary
import com.ciphervault.app.ui.theme.TextSecondary
import com.ciphervault.app.util.ClipboardHelper

@Composable
fun GlassmorphicBankCard(
    card: UnencryptedCard,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var isFlipped by remember { mutableStateOf(false) }
    var isNumberRevealed by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "cardFlipAnimation"
    )

    val gradientBrush = when (card.cardNetwork.lowercase()) {
        "visa" -> Brush.linearGradient(listOf(Color(0xFF1A1F36), Color(0xFF0052D4), Color(0xFF4364F7)))
        "mastercard" -> Brush.linearGradient(listOf(Color(0xFF2C003E), Color(0xFFB1005B), Color(0xFFFF5252)))
        "american express", "amex" -> Brush.linearGradient(listOf(Color(0xFF0D324D), Color(0xFF7F5A83), Color(0xFF00E676)))
        else -> Brush.linearGradient(listOf(Color(0xFF141E30), Color(0xFF243B55), Color(0xFF380036)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clickable { isFlipped = !isFlipped },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(18.dp)
        ) {
            if (rotation <= 90f) {
                // Front Side
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = card.bankLabel.ifEmpty { "Cipher Vault Bank" },
                                color = GoldAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = card.cardType,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = card.cardNetwork,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Serif
                            )
                            IconButton(onClick = onEdit) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Card", tint = TextPrimary)
                            }
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Card", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    // Masked / Unmasked Card Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val maskedNumber = if (isNumberRevealed) {
                            card.cardNumber
                        } else {
                            val last4 = card.cardNumber.takeLast(4).ifEmpty { "••••" }
                            "•••• •••• •••• $last4"
                        }

                        Text(
                            text = maskedNumber,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )

                        Row {
                            IconButton(onClick = { isNumberRevealed = !isNumberRevealed }) {
                                Icon(
                                    imageVector = if (isNumberRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Reveal",
                                    tint = EmeraldPrimary
                                )
                            }
                            IconButton(onClick = {
                                ClipboardHelper.copyToClipboard(context, "Card Number", card.cardNumber)
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Card Number", tint = GoldAccent)
                            }
                        }
                    }

                    // Cardholder & Expiry
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(text = "CARDHOLDER", color = TextSecondary, fontSize = 10.sp)
                            Text(
                                text = card.cardholderName.uppercase().ifEmpty { "VALUED HOLDER" },
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "EXPIRES", color = TextSecondary, fontSize = 10.sp)
                            Text(
                                text = card.expiryDate.ifEmpty { "MM/YY" },
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Back Side (Rotated 180 deg)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Magnetic Strip
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .background(Color.Black.copy(alpha = 0.8f))
                        )

                        // CVV Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "SECURITY CODE (CVV)", color = TextSecondary, fontSize = 12.sp)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = card.cvv.ifEmpty { "•••" },
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 16.sp
                                    )
                                }

                                IconButton(onClick = {
                                    ClipboardHelper.copyToClipboard(context, "CVV", card.cvv)
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy CVV", tint = GoldAccent)
                                }
                            }
                        }

                        Text(
                            text = "Tap anywhere on card to flip back",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}
