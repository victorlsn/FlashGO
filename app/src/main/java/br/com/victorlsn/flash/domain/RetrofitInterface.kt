package br.com.victorlsn.flash.domain

import br.com.victorlsn.flash.entities.Scooter
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * Created by victorlsn on 18/03/19.
 *
 */

interface RetrofitInterface {
    @Headers("Content-Type: application/json")
    @GET("/FlashScooters/Challenge/vehicles")
    fun getVehiclesList(): Call<List<Scooter>>

    @Headers("Content-Type: application/json")
    @GET("/FlashScooters/Challenge/vehicles/{id}")
    fun getVehicleDetails(@Path("id") id: String): Call<Scooter>
}