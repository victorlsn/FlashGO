package br.com.victorlsn.flash.interfaces

/**
 * Created by victorlsn on 18/03/19.
 *
 */

interface BaseMVP {

    interface Presenter {
        fun attachView(view: View)
    }

    interface View {
        fun showProgressBar(message: String?)

        fun hideProgressBar()

        fun showToast(message: String, duration: Int)

        fun setProgressView(progressView: android.view.View)
    }
}