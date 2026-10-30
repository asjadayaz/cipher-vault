package com.ciphervault.app.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciphervault.app.data.model.UnencryptedCard
import com.ciphervault.app.data.model.UnencryptedCredential
import com.ciphervault.app.data.model.UnencryptedEmail
import com.ciphervault.app.data.model.UnencryptedPassword
import com.ciphervault.app.ui.components.CardModalBottomSheet
import com.ciphervault.app.ui.components.CredentialModalBottomSheet
import com.ciphervault.app.ui.components.CredentialVaultItem
import com.ciphervault.app.ui.components.EmailModalBottomSheet
import com.ciphervault.app.ui.components.EmailVaultItem
import com.ciphervault.app.ui.components.EmptyStateIllustration
import com.ciphervault.app.ui.components.GlassmorphicBankCard
import com.ciphervault.app.ui.components.PasswordModalBottomSheet
import com.ciphervault.app.ui.components.PasswordVaultItem
import com.ciphervault.app.ui.theme.EmeraldPrimary
import com.ciphervault.app.ui.theme.GoldAccent
import com.ciphervault.app.ui.theme.ObsidianBackground
import com.ciphervault.app.ui.theme.ObsidianSurface
import com.ciphervault.app.ui.theme.TextPrimary
import com.ciphervault.app.ui.theme.TextSecondary
import com.ciphervault.app.ui.viewmodel.VaultViewModel

@Composable
fun MainVaultScreen(viewModel: VaultViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val cards by viewModel.cards.collectAsState()
    val passwords by viewModel.passwords.collectAsState()
    val emails by viewModel.emails.collectAsState()
    val credentials by viewModel.credentials.collectAsState()

    var showAddBottomSheet by remember { mutableStateOf(false) }
    var editingCard by remember { mutableStateOf<UnencryptedCard?>(null) }
    var editingPassword by remember { mutableStateOf<UnencryptedPassword?>(null) }
    var editingEmail by remember { mutableStateOf<UnencryptedEmail?>(null) }
    var editingCredential by remember { mutableStateOf<UnencryptedCredential?>(null) }

    var deleteConfirmItem by remember { mutableStateOf<Any?>(null) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianBackground)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CIPHER VAULT",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { viewModel.lockVault() }) {
                        Icon(
                            imageVector = Icons.Default.LockClock,
                            contentDescription = "Lock Vault",
                            tint = EmeraldPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Instant Local Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search vault records...", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = EmeraldPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = ObsidianSurface) {
                val tabs = listOf(
                    Triple(0, "Cards", Icons.Default.CreditCard),
                    Triple(1, "Passwords", Icons.Default.Lock),
                    Triple(2, "Emails", Icons.Default.AlternateEmail),
                    Triple(3, "Credentials", Icons.Default.Key)
                )

                tabs.forEach { (index, label, icon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { viewModel.setSelectedTab(index) },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GoldAccent,
                            selectedTextColor = GoldAccent,
                            indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddBottomSheet = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Item")
            }
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(300),
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    0 -> {
                        val filteredCards = cards.filter {
                            it.bankLabel.contains(searchQuery, ignoreCase = true) ||
                                    it.cardholderName.contains(searchQuery, ignoreCase = true) ||
                                    it.cardNumber.contains(searchQuery, ignoreCase = true)
                        }
                        if (filteredCards.isEmpty()) {
                            EmptyStateIllustration("Payment Cards")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredCards, key = { it.id }) { card ->
                                    GlassmorphicBankCard(
                                        card = card,
                                        onEdit = { editingCard = card },
                                        onDelete = { deleteConfirmItem = card }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }

                    1 -> {
                        val filteredPasswords = passwords.filter {
                            it.websiteName.contains(searchQuery, ignoreCase = true) ||
                                    it.username.contains(searchQuery, ignoreCase = true) ||
                                    it.notes.contains(searchQuery, ignoreCase = true)
                        }
                        if (filteredPasswords.isEmpty()) {
                            EmptyStateIllustration("Passwords")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredPasswords, key = { it.id }) { pwd ->
                                    PasswordVaultItem(
                                        item = pwd,
                                        onEdit = { editingPassword = pwd },
                                        onDelete = { deleteConfirmItem = pwd }
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        val filteredEmails = emails.filter {
                            it.emailAddress.contains(searchQuery, ignoreCase = true) ||
                                    it.provider.contains(searchQuery, ignoreCase = true) ||
                                    it.recoveryInfo.contains(searchQuery, ignoreCase = true)
                        }
                        if (filteredEmails.isEmpty()) {
                            EmptyStateIllustration("Email Accounts")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredEmails, key = { it.id }) { email ->
                                    EmailVaultItem(
                                        item = email,
                                        onEdit = { editingEmail = email },
                                        onDelete = { deleteConfirmItem = email }
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        val filteredCredentials = credentials.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                                    it.category.contains(searchQuery, ignoreCase = true) ||
                                    it.notes.contains(searchQuery, ignoreCase = true)
                        }
                        if (filteredCredentials.isEmpty()) {
                            EmptyStateIllustration("Credentials & Notes")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredCredentials, key = { it.id }) { cred ->
                                    CredentialVaultItem(
                                        item = cred,
                                        onEdit = { editingCredential = cred },
                                        onDelete = { deleteConfirmItem = cred }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets
    if (showAddBottomSheet) {
        when (selectedTab) {
            0 -> CardModalBottomSheet(
                onDismiss = { showAddBottomSheet = false },
                onSave = {
                    viewModel.saveCard(it)
                    showAddBottomSheet = false
                }
            )

            1 -> PasswordModalBottomSheet(
                onDismiss = { showAddBottomSheet = false },
                onSave = {
                    viewModel.savePassword(it)
                    showAddBottomSheet = false
                }
            )

            2 -> EmailModalBottomSheet(
                onDismiss = { showAddBottomSheet = false },
                onSave = {
                    viewModel.saveEmail(it)
                    showAddBottomSheet = false
                }
            )

            3 -> CredentialModalBottomSheet(
                onDismiss = { showAddBottomSheet = false },
                onSave = {
                    viewModel.saveCredential(it)
                    showAddBottomSheet = false
                }
            )
        }
    }

    // Edit Modals
    editingCard?.let { card ->
        CardModalBottomSheet(
            initialCard = card,
            onDismiss = { editingCard = null },
            onSave = {
                viewModel.saveCard(it)
                editingCard = null
            }
        )
    }

    editingPassword?.let { pwd ->
        PasswordModalBottomSheet(
            initialPassword = pwd,
            onDismiss = { editingPassword = null },
            onSave = {
                viewModel.savePassword(it)
                editingPassword = null
            }
        )
    }

    editingEmail?.let { email ->
        EmailModalBottomSheet(
            initialEmail = email,
            onDismiss = { editingEmail = null },
            onSave = {
                viewModel.saveEmail(it)
                editingEmail = null
            }
        )
    }

    editingCredential?.let { cred ->
        CredentialModalBottomSheet(
            initialCredential = cred,
            onDismiss = { editingCredential = null },
            onSave = {
                viewModel.saveCredential(it)
                editingCredential = null
            }
        )
    }

    // Delete Confirmation Dialog
    deleteConfirmItem?.let { item ->
        AlertDialog(
            onDismissRequest = { deleteConfirmItem = null },
            title = { Text("Delete Item?", color = GoldAccent) },
            text = { Text("Are you sure you want to permanently delete this item from your encrypted vault?", color = TextPrimary) },
            confirmButton = {
                TextButton(onClick = {
                    when (item) {
                        is UnencryptedCard -> viewModel.deleteCard(item)
                        is UnencryptedPassword -> viewModel.deletePassword(item)
                        is UnencryptedEmail -> viewModel.deleteEmail(item)
                        is UnencryptedCredential -> viewModel.deleteCredential(item)
                    }
                    deleteConfirmItem = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmItem = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = ObsidianSurface
        )
    }
}
