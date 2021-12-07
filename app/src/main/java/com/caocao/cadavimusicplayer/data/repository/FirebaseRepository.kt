package com.caocao.cadavimusicplayer.data.repository

import com.google.firebase.storage.StorageReference

class FirebaseRepository(
    private val mStorageRef: StorageReference
    ) {
    fun getAllAudio() = mStorageRef.child("audio-ma").listAll()
}