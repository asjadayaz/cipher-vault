package com.ciphervault.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.ciphervault.app.data.local.CipherVaultDatabase
import com.ciphervault.app.security.PinManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class CipherVaultApplication : Application() {

    lateinit var database: CipherVaultDatabase
        private set

    lateinit var pinManager: PinManager
        private set

    private val _lockEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val lockEvents: SharedFlow<Unit> = _lockEvents.asSharedFlow()

    override fun onCreate() {
        super.onCreate()

        // Global Uncaught Exception Handler to catch and log startup/runtime exceptions
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CipherVault", "Uncaught exception in thread ${thread.name}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        database = CipherVaultDatabase.getDatabase(this)
        pinManager = PinManager(this)

        try {
            ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    try {
                        pinManager.lockVault()
                        _lockEvents.tryEmit(Unit)
                    } catch (e: Exception) {
                        Log.e("CipherVault", "Error during vault lock on app stop", e)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("CipherVault", "Failed to register ProcessLifecycleOwner observer", e)
        }
    }
}
