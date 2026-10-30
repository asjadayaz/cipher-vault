package com.ciphervault.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ciphervault.app.data.model.UnencryptedCard
import com.ciphervault.app.data.model.UnencryptedCredential
import com.ciphervault.app.data.model.UnencryptedEmail
import com.ciphervault.app.data.model.UnencryptedPassword
import com.ciphervault.app.data.repository.VaultRepository
import com.ciphervault.app.security.PinManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaultViewModel(
    private val repository: VaultRepository,
    val pinManager: PinManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isUnlocked = MutableStateFlow(pinManager.isUnlocked)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun unlockVault(pin: String): Boolean {
        val success = pinManager.verifyPin(pin)
        _isUnlocked.value = pinManager.isUnlocked
        return success
    }

    fun setupMasterPin(pin: String): Boolean {
        val success = pinManager.setupPin(pin)
        _isUnlocked.value = pinManager.isUnlocked
        return success
    }

    fun lockVault() {
        pinManager.lockVault()
        _isUnlocked.value = false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val cards: StateFlow<List<UnencryptedCard>> = _isUnlocked.flatMapLatest { unlocked ->
        val key = pinManager.currentSecretKey
        if (unlocked && key != null) {
            repository.getCards(key)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val passwords: StateFlow<List<UnencryptedPassword>> = _isUnlocked.flatMapLatest { unlocked ->
        val key = pinManager.currentSecretKey
        if (unlocked && key != null) {
            repository.getPasswords(key)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val emails: StateFlow<List<UnencryptedEmail>> = _isUnlocked.flatMapLatest { unlocked ->
        val key = pinManager.currentSecretKey
        if (unlocked && key != null) {
            repository.getEmails(key)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val credentials: StateFlow<List<UnencryptedCredential>> = _isUnlocked.flatMapLatest { unlocked ->
        val key = pinManager.currentSecretKey
        if (unlocked && key != null) {
            repository.getCredentials(key)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Card CRUD
    fun saveCard(card: UnencryptedCard) {
        val key = pinManager.currentSecretKey ?: return
        viewModelScope.launch {
            repository.saveCard(card, key)
        }
    }

    fun deleteCard(card: UnencryptedCard) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    // Password CRUD
    fun savePassword(password: UnencryptedPassword) {
        val key = pinManager.currentSecretKey ?: return
        viewModelScope.launch {
            repository.savePassword(password, key)
        }
    }

    fun deletePassword(password: UnencryptedPassword) {
        viewModelScope.launch {
            repository.deletePassword(password)
        }
    }

    // Email CRUD
    fun saveEmail(email: UnencryptedEmail) {
        val key = pinManager.currentSecretKey ?: return
        viewModelScope.launch {
            repository.saveEmail(email, key)
        }
    }

    fun deleteEmail(email: UnencryptedEmail) {
        viewModelScope.launch {
            repository.deleteEmail(email)
        }
    }

    // Credential CRUD
    fun saveCredential(credential: UnencryptedCredential) {
        val key = pinManager.currentSecretKey ?: return
        viewModelScope.launch {
            repository.saveCredential(credential, key)
        }
    }

    fun deleteCredential(credential: UnencryptedCredential) {
        viewModelScope.launch {
            repository.deleteCredential(credential)
        }
    }
}

class VaultViewModelFactory(
    private val repository: VaultRepository,
    private val pinManager: PinManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VaultViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VaultViewModel(repository, pinManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
