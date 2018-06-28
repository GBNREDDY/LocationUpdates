package io.github.gbnredy.locationupdates

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import io.github.gbnredy.locationupdates.util.StaticData
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.ConnectionResult
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import io.github.gbnredy.locationupdates.networking.APIClient
import io.github.gbnredy.locationupdates.networking.ApiInterface


class Main2Activity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val REQUEST_CODE_RECOVER_PLAY_SERVICES = 200

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null
    private lateinit var client: ApiInterface

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        if (checkGooglePlayServices()) {
            buildGoogleApiClient();

            //prepare connection request
            createLocationRequest();

        }
        client = APIClient.client.create(ApiInterface::class.java)
    }

    @SuppressLint("NewApi")
    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this@Main2Activity, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && StaticData.isLocationON(this)) {
            if (mGoogleApiClient != null) {
                mGoogleApiClient!!.connect();
            }
        } else {
            processRequest()
        }


    }

    @SuppressLint("NewApi")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this@Main2Activity, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && StaticData.isLocationON(this)) {
            if (mGoogleApiClient != null) {
                mGoogleApiClient!!.connect();
            }
        } else {
            processRequest()
        }
    }

    private fun checkGooglePlayServices(): Boolean {

        val checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this)
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {

            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show()

            return false
        }

        return true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES) {

            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient!!.isConnecting() && !mGoogleApiClient!!.isConnected()) {
                    mGoogleApiClient!!.connect()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Google Play Services must be installed.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (requestCode == 30) {
            processRequest()
        }
    }

    @SuppressLint("NewApi")
    fun processRequest() {
        if (!StaticData.isLocationON(this)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, 30)
        }
        if (ContextCompat.checkSelfPermission(this@Main2Activity, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 10)
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

    }

    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, { location ->
            StaticData.location = location
            Toast.makeText(this, "Latitude:" + location!!.getLatitude() + ", Longitude:" + location!!.getLongitude(), Toast.LENGTH_LONG).show();
            Log.d("locationtag", "Latitude:" + location!!.getLatitude() + ", Longitude:" + location!!.getLongitude())
            StaticData.makeCall(client)
        })
        /* LocationServices.FusedLocationApi.requestLocationUpdates(
                 mGoogleApiClient, mLocationRequest, this)*/
    }

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(10000)
        mLocationRequest!!.setFastestInterval(10000)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            StaticData.location = mLastLocation
            Toast.makeText(this, "Latitude:" + mLastLocation!!.getLatitude() + ", Longitude:" + mLastLocation!!.getLongitude(), Toast.LENGTH_LONG).show();
            Log.d("locationtag", "Latitude:" + mLastLocation!!.getLatitude() + ", Longitude:" + mLastLocation!!.getLongitude())

        }
        startLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()
        }
    }
}
