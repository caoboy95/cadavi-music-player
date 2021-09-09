package com.caocao.cadavimusicplayer.util

import android.net.Uri

data class Query(val uri: Uri, val projection: Array<String>?, val selection: String?, val args: Array<String>?, val sort: String?)