package com.ciphervault.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciphervault.app.data.model.UnencryptedCard
import com.ciphervault.app.data.model.UnencryptedCredential
import com.ciphervault.app.data.model.UnencryptedEmail
import com.ciphervault.app.data.model.UnencryptedPassword
import com.ciphervault.app.ui.theme.EmeraldPrimary
import com.ciphervault.app.ui.theme.GoldAccent
import com.ciphervault.app.ui.theme.ObsidianSurface
import com.ciphervault.app.ui.theme.TextSecondary
import com.ciphervault.app.util.CardNetworkDetector
import com.ciphervault.app.util.PasswordGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardModalBottomSheet(
    initialCard: UnencryptedCard? = null,
    onDismiss: () -> Unit,
    onSave: (UnencryptedCard) -> Unit
) {
    var cardholderName by remember { mutableStateOf(initialCard?.cardholderName ?: "") }
    var cardNumber by remember { mutableStateOf(initialCard?.cardNumber ?: "") }
    var expiryDate by remember { mutableStateOf(initialCard?.expiryDate ?: "") }
    var cvv by remember { mutableStateOf(initialCard?.cvv ?: "") }
    var cardType by remember { mutableStateOf(initialCard?.cardType.takeIf { !it.isNullOrEmpty() } ?: "Credit") }
    var bankLabel by remember { mutableStateOf(initialCard?.bankLabel ?: "") }

    val autoNetwork = CardNetworkDetector.detectNetwork(cardNumber).displayName

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (initialCard == null) "Add Payment Card" else "Edit Payment Card",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = bankLabel,
                onValueChange = { bankLabel = it },
                label = { Text("Bank / Label (e.g. Chase Sapphire)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cardholderName,
                onValueChange = { cardholderName = it },
                label = { Text("Cardholder Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cardNumber,
                onValueChange = { cardNumber = CardNetworkDetector.formatCardNumber(it) },
                label = { Text("Card Number (16 Digits)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry (MM/YY)") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                OutlinedTextField(
                    value = cvv,
                    onValueChange = { cvv = it },
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Card Type: ", color = TextSecondary)
                TextButton(onClick = { cardType = if (cardType == "Credit") "Debit" else "Credit" }) {
                    Text(cardType, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("Detected Network: $autoNetwork", color = GoldAccent, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(
                        UnencryptedCard(
                            id = initialCard?.id ?: 0L,
                            cardholderName = cardholderName,
                            cardNumber = cardNumber,
                            expiryDate = expiryDate,
                            cvv = cvv,
                            cardType = cardType,
                            bankLabel = bankLabel,
                            cardNetwork = autoNetwork
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Save Card", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordModalBottomSheet(
    initialPassword: UnencryptedPassword? = null,
    onDismiss: () -> Unit,
    onSave: (UnencryptedPassword) -> Unit
) {
    var websiteName by remember { mutableStateOf(initialPassword?.websiteName ?: "") }
    var username by remember { mutableStateOf(initialPassword?.username ?: "") }
    var password by remember { mutableStateOf(initialPassword?.password ?: "") }
    var notes by remember { mutableStateOf(initialPassword?.notes ?: "") }

    var passwordLength by remember { mutableFloatStateOf(16f) }
    var includeSymbols by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (initialPassword == null) "Add Password / Login" else "Edit Password / Login",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = websiteName,
                onValueChange = { websiteName = it },
                label = { Text("Website / App Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username / Email / Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Generator Box
            Text("Password Generator", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Length: ${passwordLength.toInt()}", color = TextSecondary, modifier = Modifier.weight(1f))
                Slider(
                    value = passwordLength,
                    onValueChange = { passwordLength = it },
                    valueRange = 8f..32f,
                    modifier = Modifier.weight(2f),
                    colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Include Symbols", color = TextSecondary, modifier = Modifier.weight(1f))
                Switch(
                    checked = includeSymbols,
                    onCheckedChange = { includeSymbols = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                )
            }
            Button(
                onClick = {
                    password = PasswordGenerator.generatePassword(
                        length = passwordLength.toInt(),
                        includeSymbols = includeSymbols
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text("Generate Strong Password", color = GoldAccent)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(
                        UnencryptedPassword(
                            id = initialPassword?.id ?: 0L,
                            websiteName = websiteName,
                            username = username,
                            password = password,
                            notes = notes
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Save Password", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailModalBottomSheet(
    initialEmail: UnencryptedEmail? = null,
    onDismiss: () -> Unit,
    onSave: (UnencryptedEmail) -> Unit
) {
    var emailAddress by remember { mutableStateOf(initialEmail?.emailAddress ?: "") }
    var provider by remember { mutableStateOf(initialEmail?.provider.takeIf { !it.isNullOrEmpty() } ?: "Gmail") }
    var password by remember { mutableStateOf(initialEmail?.password ?: "") }
    var recoveryInfo by remember { mutableStateOf(initialEmail?.recoveryInfo ?: "") }
    var backupCodes by remember { mutableStateOf(initialEmail?.backupCodes ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (initialEmail == null) "Add Email Account" else "Edit Email Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = emailAddress,
                onValueChange = { emailAddress = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = provider,
                onValueChange = { provider = it },
                label = { Text("Provider (Gmail, Outlook, Yahoo, Custom)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Account Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = recoveryInfo,
                onValueChange = { recoveryInfo = it },
                label = { Text("Recovery Email / Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = backupCodes,
                onValueChange = { backupCodes = it },
                label = { Text("2FA Backup Codes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(
                        UnencryptedEmail(
                            id = initialEmail?.id ?: 0L,
                            emailAddress = emailAddress,
                            provider = provider,
                            password = password,
                            recoveryInfo = recoveryInfo,
                            backupCodes = backupCodes
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Save Email Account", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialModalBottomSheet(
    initialCredential: UnencryptedCredential? = null,
    onDismiss: () -> Unit,
    onSave: (UnencryptedCredential) -> Unit
) {
    var title by remember { mutableStateOf(initialCredential?.title ?: "") }
    var category by remember { mutableStateOf(initialCredential?.category.takeIf { !it.isNullOrEmpty() } ?: "Bank PIN") }
    var secretValue by remember { mutableStateOf(initialCredential?.secretValue ?: "") }
    var notes by remember { mutableStateOf(initialCredential?.notes ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (initialCredential == null) "Add Credential / Secret" else "Edit Credential / Secret",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (Bank PIN, National ID, API Key, Wi-Fi, etc.)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = secretValue,
                onValueChange = { secretValue = it },
                label = { Text("Credential / Secret Value") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Encrypted Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(
                        UnencryptedCredential(
                            id = initialCredential?.id ?: 0L,
                            title = title,
                            category = category,
                            secretValue = secretValue,
                            notes = notes
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Save Credential", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
