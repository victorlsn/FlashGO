package br.com.victorlsn.flash.domain

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by victorlsn on 18/03/19.
 *
 */

class APIController private constructor(baseUrl: String?) {
    private val apiCall: RetrofitInterface
    var BASE_URL = "https://my-json-server.typicode.com/"



    init {
        if (baseUrl != null) {
            BASE_URL = baseUrl
        }
        val okHttpBuilder = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)

            val client = okHttpBuilder.build()


        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        apiCall = retrofit.create(RetrofitInterface::class.java)
    }

    fun getRetrofitInterface(): RetrofitInterface {
        return apiCall
    }

    companion object {

        @Volatile private var INSTANCE : APIController? = null

        fun getInstance(baseUrl: String?): APIController {
            return INSTANCE?: synchronized(this){
                APIController(baseUrl).also {
                    INSTANCE = it
                }
            }
        }
    }
}