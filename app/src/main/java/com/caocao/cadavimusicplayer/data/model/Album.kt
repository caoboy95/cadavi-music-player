package com.caocao.cadavimusicplayer.data.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
//import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
//import kotlinx.android.parcel.Parcelize
import java.text.Collator
import java.util.*

@Parcelize
data class Album(
    val id:Long,
    val title:String,
    val artist:String
    ): Parcelable {

    @IgnoredOnParcel
    @Transient var songCount: Int = 0

    operator fun compareTo(album: Album): Int {
        val collator : Collator = Collator.getInstance(Locale.getDefault())

        return when (title) {
            album.title -> 0
            else -> collator.compare(this.title , album.title)
        }
    }
}

