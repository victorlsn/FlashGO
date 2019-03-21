package br.com.victorlsn.flash.ui.widgets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import br.com.victorlsn.flash.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.custom_info_window.view.*

/**
 * Created by victorlsn on 19/03/19.
 *
 */

class CustomInfoWindow internal constructor(private val mContext: Context) : GoogleMap.InfoWindowAdapter {


    private fun renderWindow(marker: Marker): View {
        val mInfoView = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null)

        val title = marker.title
        mInfoView.titleTv.text = title

        val snippet = marker.snippet
        mInfoView.detailsTv.text = snippet

        return mInfoView
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View? {
        return renderWindow(marker)
    }


}
