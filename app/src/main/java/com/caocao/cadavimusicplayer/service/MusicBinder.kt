package com.caocao.cadavimusicplayer.service

import android.os.Binder
import java.lang.ref.WeakReference

class MusicBinder(musicService: MusicService) : Binder() {
    var service : WeakReference<MusicService> = WeakReference(musicService)
}