package br.com.victorlsn.flash.ui.activities

import android.annotation.SuppressLint
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import br.com.victorlsn.flash.interfaces.BaseMVP


/**
 * Created by victorlsn on 18/03/19.
 *
 */

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), BaseMVP.View {
    private var progressBarView: View? = null

    override fun setProgressView(progressView: View) {
        progressBarView = progressView
    }


    override fun showProgressBar(message: String?) {
        progressBarView?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBarView?.visibility = View.GONE
    }

    override fun showToast(message: String, duration: Int) {
        if (duration != Toast.LENGTH_SHORT && duration != Toast.LENGTH_LONG) return

        Toast.makeText(this, message, duration).show()
    }

    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        showToast( "Please click back again to exit", Toast.LENGTH_SHORT)

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}
