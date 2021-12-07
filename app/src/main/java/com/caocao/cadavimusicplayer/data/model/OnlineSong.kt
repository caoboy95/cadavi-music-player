package com.caocao.cadavimusicplayer.data.model

class OnlineSong(id: Long,
                 title: String,
                 albumId: Long,
                 albumName: String,
                 artistId: Long,
                 artistName: String,
                 duration: Long,
                 val url: String,
                 val art: ByteArray?
): Song(id, title, albumId, albumName, artistId, artistName, duration)
