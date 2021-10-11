package com.engineersapps.eapps.util

import android.Manifest
import android.app.Application
import android.content.Context
import android.net.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData


object NetworkUtils {
    fun isNetworkConnected(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val ni = cm.activeNetworkInfo

                if (ni != null) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI ||
                            ni.type == ConnectivityManager.TYPE_MOBILE)
                }
            } else {
                val n = cm.activeNetwork

                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)

                    return nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                }
            }
        }

        return false
    }

    class ConnectivityLiveData @VisibleForTesting internal constructor(private val connectivityManager: ConnectivityManager)
        : LiveData<Boolean>() {

        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        constructor(application: Application) : this(application.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager)

        private val networkCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network?) {
                postValue(true)
            }

            override fun onLost(network: Network?) {
                postValue(false)
            }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onActive() {
            super.onActive()

            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            postValue(activeNetwork?.isConnectedOrConnecting == true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                val builder = NetworkRequest.Builder()
                connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
            }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onInactive() {
            super.onInactive()
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}