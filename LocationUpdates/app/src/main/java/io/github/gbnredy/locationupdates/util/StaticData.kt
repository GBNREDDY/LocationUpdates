package io.github.gbnredy.locationupdates.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager

import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.app.job.JobScheduler
import android.app.job.JobInfo
import android.content.ComponentName
import android.util.Log
import io.github.gbnredy.locationupdates.model.DataModel
import io.github.gbnredy.locationupdates.networking.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object StaticData : Callback<DataModel> {
    override fun onFailure(call: Call<DataModel>?, t: Throwable?) {
        Log.d("locationtag","${call!!.request().url()}")
    }

    override fun onResponse(call: Call<DataModel>?, response: Response<DataModel>?) {
        Log.d("locationtag","${call!!.request().url()}")
    }

    val BaseURL = "https://github.com"
    var location: Location? = null
    internal fun isLocationON(c: Context): Boolean {
        val service = c.getSystemService(LOCATION_SERVICE) as LocationManager
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER)

    }

    internal fun makeCall(client:ApiInterface){
        if(location!= null) {
            client.updateLocation("${location!!.latitude}", "${location!!.longitude}").enqueue(this)
        }
    }
}
