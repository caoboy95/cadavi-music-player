package com.caocao.cadavimusicplayer.util

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.caocao.cadavimusicplayer.service.MusicBinder
import com.caocao.cadavimusicplayer.service.MusicService
import java.util.*

object ServiceConnectionUtil {
    var musicBinder : MusicBinder? = null
    private const val TAG = "ServiceConnectionUtil"
    private var serviceBinders : WeakHashMap<Context, ServiceConnection> = WeakHashMap()

    fun bind(context: Context) : ServiceConnectionToken? {
        Log.d(TAG, "bind")
        val intent = Intent(context, MusicService::class.java)
        initServiceConnection().let {
            if (context.bindService(intent, it, Service.BIND_AUTO_CREATE)) {
                Log.d(TAG, "binded, musicbinder $musicBinder service connection $it")
                serviceBinders[context] = it
                return ServiceConnectionToken(context)
            }
        }
        return null
    }

    private fun initServiceConnection() : ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                musicBinder = service as MusicBinder
                Log.d("ServiceConnection", "onServiceConnected")
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                musicBinder = null
                Log.d("ServiceConnection", "onServiceDisconnected")
            }
        }
    }

    fun unbind(serviceConnectionToken: ServiceConnectionToken) {
        serviceBinders.remove(serviceConnectionToken.context)?.let {
            serviceConnectionToken.context.unbindService(it)
        }
        if (serviceBinders.isEmpty()) {
            musicBinder = null
        }
    }

    class ServiceConnectionToken(private val _context: Context) {
        val context : Context
            get() = _context
    }

}