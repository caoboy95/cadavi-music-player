package com.caocao.cadavimusicplayer.data.model

import android.database.Cursor
import android.provider.MediaStore
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.Collator
import java.util.*

@Entity(tableName = "songs")
data class Song(@PrimaryKey @ColumnInfo(name = "media_store_id") var id:Long,
                @ColumnInfo(name = "title") val title:String,
                @ColumnInfo(name = "albumId") val albumId:Long,
                @ColumnInfo(name = "albumName") val albumName:String,
                @ColumnInfo(name = "artistId") val artistId:Long,
                @ColumnInfo(name = "artistName") val artistName:String,
                @ColumnInfo(name = "duration") val duration:Long): Serializable {

//    @IgnoredOnParcel
//    var discNumber : Int = 0
//    @IgnoredOnParcel
//    var playlistSongId: Long = 0
//    @IgnoredOnParcel
//    var playcount: Int = 0
//    @IgnoredOnParcel
//    var lastplayed: Int = 0

    constructor(cursor: Cursor) : this(
        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
    )


    operator fun compareTo(song: Song): Int {
        val collator : Collator = Collator.getInstance(Locale.getDefault())

        return when (this.title) {
            song.title -> 0
            else -> collator.compare(this.title , song.title)
        }
    }
}