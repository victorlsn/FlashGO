package br.com.victorlsn.flash.util

import android.net.ConnectivityManager

/**
 * Created by victorlsn on 18/03/19.
 *
 */
class ConnectionTools {

    fun isOnline(): Boolean {
        if (connManager != null) {
            val netInfo = connManager?.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
        return false
    }

    companion object {
        var connManager : ConnectivityManager? = null
        fun init(connManager: ConnectivityManager) { this.connManager = connManager }
    }
}