package io.github.gbnredy.locationupdates.networking

import io.github.gbnredy.locationupdates.model.DataModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ApiInterface {

    @GET("api")
    fun updateLocation(@Query("lat") lat: String,@Query("lng") lng: String): Call<DataModel>
}