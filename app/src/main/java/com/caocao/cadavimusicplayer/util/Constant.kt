package com.caocao.cadavimusicplayer.util

import androidx.core.net.toUri

val ArtURI = "content://media/external/audio/albumart".toUri()

object Constant {
    //REST API Localhost
//    public val URL: String = "http://10.0.3.2:8000/"
    const val URL: String = "http://192.168.1.96:8000/"
    const val URL_IMAGE : String = URL+"storage/image/"
    //Firebase
    const val NODE_PRODUCTS : String = "products"
    const val NODE_BILLS : String = "bills"
    const val NODE_BILL_DETAILS : String = "billdetails"
    const val NODE_COMPANIES : String = "companies"
    const val NODE_TYPE_PRODUCTS : String = "type_products"
    const val NODE_NEWS : String = "news"
    const val NODE_SLIDES : String = "slides"
    const val NODE_USERS : String = "users"
    const val NODE_PRODUCT_VARIANTS : String = "product_variants"
    const val NODE_IMAGES : String = "images"
    const val NODE_CUSTOMERS : String = "customers"
    const val URL_FIREBASE_STORAGE = "gs://dien-thoai-viet.appspot.com"
    const val PRODUCT_KEY = "product"
    const val DESCRIPTION_KEY = "description"
    const val SELECTED_VARIANT_KEY = "selected"
}