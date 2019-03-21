package br.com.victorlsn.flash.domain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by victorlsn on 19/03/19.
 */

fun <T> callback(callResponse: (response: Response<T>?, error: String?,
                                throwable: Throwable?) -> Unit): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>?) {
            if (response?.isSuccessful!!) {
                callResponse(response, null, null)
            }

            else if (response.errorBody() != null) {
                callResponse(null, response.errorBody().toString(), null)
            }

        }

        override fun onFailure(call: Call<T>?, t: Throwable?) {
            callResponse(null, null, t)
        }
    }
}