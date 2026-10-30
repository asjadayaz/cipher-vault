package com.ciphervault.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ciphervault.app.data.repository.VaultRepository
import com.ciphervault.app.ui.screen.MainVaultScreen
import com.ciphervault.app.ui.theme.CipherVaultTheme
import com.ciphervault.app.ui.viewmodel.VaultViewModel
import com.ciphervault.app.ui.viewmodel.VaultViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: VaultViewModel by viewModels {
        val app = application as CipherVaultApplication
        val repository = VaultRepository(
            app.database.cardDao(),
            app.database.passwordDao(),
            app.database.emailDao(),
            app.database.credentialDao()
        )
        VaultViewModelFactory(repository, app.pinManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            CipherVaultTheme {
                val isUnlocked by viewModel.isUnlocked.collectAsState()
                if (isUnlocked) {
                    MainVaultScreen(viewModel = viewModel)
                } else {
                    // PinScreen will be rendered here
                    com.ciphervault.app.ui.screen.PinScreen(viewModel = viewModel)
                }
            }
        }
    }
}
