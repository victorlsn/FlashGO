package br.com.victorlsn.flash

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import br.com.victorlsn.flash.util.ConnectionTools

/**
 * Created by victorlsn on 20/03/19.
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initConnectionToons()
    }

    private fun initConnectionToons() {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        ConnectionTools.init(connManager)
    }
}