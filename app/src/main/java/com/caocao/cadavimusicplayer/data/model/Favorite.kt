package com.caocao.cadavimusicplayer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey @ColumnInfo(name = "fav") var id: Long
)
