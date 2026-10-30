package com.ciphervault.app

import android.app.Application
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
        database = CipherVaultDatabase.getDatabase(this)
        pinManager = PinManager(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                pinManager.lockVault()
                _lockEvents.tryEmit(Unit)
            }
        })
    }
}
