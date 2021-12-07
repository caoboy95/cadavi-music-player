package com.caocao.cadavimusicplayer.ui.viewmodel

import android.app.Application
import android.content.IntentFilter
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.cantrowitz.rxbroadcast.RxBroadcast
import com.caocao.cadavimusicplayer.base.BaseViewModel
import com.caocao.cadavimusicplayer.data.model.OnlineSong
import com.caocao.cadavimusicplayer.data.repository.FirebaseRepository
import com.caocao.cadavimusicplayer.service.MusicService
import com.caocao.cadavimusicplayer.util.NetworkLiveData
import com.caocao.cadavimusicplayer.util.getService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class AudioOnlineViewModel(
    application: Application,
    private val firebaseRepository: FirebaseRepository,
): BaseViewModel(application) {

    override fun start() {
        super.start()
        val intentFilter = IntentFilter().apply {
            addAction(MusicService.InternalIntents.PLAYBACK_STATE_CHANGED)
        }
        broadcastDisposable = RxBroadcast.fromLocalBroadcast(getApplication(), intentFilter)
            .subscribe {
                if (it?.action == MusicService.InternalIntents.PLAYBACK_STATE_CHANGED) {
                    isPlaying.value = getService()?.isPlaying()
                }
            }
    }

    override fun fetchSongs() {
        checkInternetConnectionAndFetch()
    }

    fun checkInternetConnectionAndFetch() {
        NetworkLiveData.value?.also {
            if (it) {
                fetchData()
            } else {
                Toast.makeText(this.getApplication(), "Please check your internet connection...!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchData() {
        val songs = ArrayList<OnlineSong>()
        firebaseRepository.getAllAudio().addOnSuccessListener { results ->
            var count: Long = 0
            results.items.forEach {
                it.downloadUrl.addOnSuccessListener { uri ->
                    count++
                    viewModelScope.launch {
                        retrieveData(uri, count, (count.toInt() == results.items.size), songs)?.let { songs ->
                            setSongs(songs)
                        }
                    }

                }
            }
        }.addOnFailureListener { setSongs(songs) }
    }

    private suspend fun retrieveData(
        uri: Uri,
        count: Long,
        isLast: Boolean,
        songs: ArrayList<OnlineSong>
    ): ArrayList<OnlineSong>? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever().apply { setDataSource(uri.toString()) }
        val art = retriever.embeddedPicture
        val title: String = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown"
        val album: String = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown"
        val artist: String = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown"
        val duration: Long = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
        val url = uri.toString()
        retriever.close()
        songs.add(OnlineSong(count, title, 0, album, 0, artist, duration, url, art))
        if (isLast) {
            songs.sortBy { songs ->
                songs.title
            }
            return@withContext songs
        }
        return@withContext null
    }

    override fun disposeSongs() {

    }

    companion object {
        const val TAG = "AudioOnlineViewModel"
    }
}