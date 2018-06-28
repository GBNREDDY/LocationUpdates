package io.github.gbnredy.locationupdates.networking

import io.github.gbnredy.locationupdates.util.StaticData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object APIClient {

    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {
            retrofit = Retrofit.Builder()
                    .baseUrl(StaticData.BaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit!!
        }

}
