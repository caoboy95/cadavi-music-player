package com.example.testapp.data.repository

import com.caocao.cadavimusicplayer.util.NoInternetException
import com.example.testapp.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {
    suspend fun <T> safeApiCall(
            apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO){
            try{
                Resource.Success(apiCall.invoke())
            }
            catch (throwable: Throwable){
                when(throwable){
                    is HttpException -> {
                        Resource.Failure(true,throwable.code(),throwable.response()?.errorBody(),null)
                    }
                    is NoInternetException -> {
                        Resource.Failure(false,null,null,"Make sure you have an active data connection")
                    }
                    else -> {
                        Resource.Failure(false,null,null,throwable.message)
                    }
                }
            }

        }
    }
}




