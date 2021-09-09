package net.simplifiedcoding.mvvmsampleapp.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.caocao.cadavimusicplayer.service.MusicPlayer

class PreferenceProvider(
    context: Context
) {
    private val appContext = context
    val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    var repeatMode: Int
        set(repeatMode) {
            preference.edit().putInt(REPEAT_MODE, repeatMode).apply()
        }
        get() = preference.getInt(REPEAT_MODE, MusicPlayer.PlayingMode.NORMAL_MODE)

    var isShuffle: Boolean
        set(isShuffle) {
            preference.edit().putBoolean(SHUFFLE, isShuffle).apply()
        }
        get() = preference.getBoolean(SHUFFLE, false)

    companion object {
        const val SHUFFLE = "SHUFFLE"
        const val LAST_PLAY = "LAST_PLAY"
        const val LAST_PLAYED_POS = "LAST_PLAYED_POS"
        const val LAST_CURRENT_POS = "LAST_CURRENT_POS"
        const val REPEAT_MODE = "REPEAT_MODE"
        const val IS_LOADING_FIRST_TIME = "IS_LOADING_FIRST_TIME"
        const val LAST_OPEN_PLAYLIST = "LAST_OPEN_PLAYLIST"
        const val LAST_VERSION = "LAST_VERSION"
        const val LAST_OPEN_MORE = "LAST_OPEN_MORE"
        const val HOUR_SLEEP_TIMER = "HOUR_SLEEP_TIMER"
        const val MINUTE_SLEEP_TIMER = "MINUTE_SLEEP_TIMER"
        const val LAST_OPEN_MENU = "LAST_OPEN_MENU"
        const val LAST_OPEN_SLEEP_TIMER = "LAST_OPEN_SLEEP_TIMER"
        const val NUMBER_SONG = "NUMBER_SONG"
    }
}