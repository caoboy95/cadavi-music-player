package com.example.testapp.data.network

import com.caocao.cadavimusicplayer.BuildConfig
import com.caocao.cadavimusicplayer.util.Constant.URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    companion object {
        private const val BASE_URL = "${URL}api/"
    }

    fun <Api> buildApi(
        api: Class<Api>,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .also { client ->
                        if(BuildConfig.DEBUG) {
                            val logging = HttpLoggingInterceptor()
                            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                            client.addInterceptor(networkConnectionInterceptor)
                                .addInterceptor(logging)
                        }
                    }.build()
            ).addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}