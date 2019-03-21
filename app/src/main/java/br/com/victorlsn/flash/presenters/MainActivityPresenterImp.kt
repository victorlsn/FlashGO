package br.com.victorlsn.flash.presenters

import android.util.Log
import android.widget.Toast
import br.com.victorlsn.flash.entities.Scooter
import br.com.victorlsn.flash.interfaces.BaseMVP
import br.com.victorlsn.flash.interfaces.MainActivityMVP
import br.com.victorlsn.flash.models.MainActivityModelImp
import br.com.victorlsn.flash.util.ConnectionTools
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Created by victorlsn on 18/03/19.
 *
 */

class MainActivityPresenterImp() : MainActivityMVP.Presenter {
    private var model: MainActivityMVP.Model
    private lateinit var view: MainActivityMVP.View
    private var connectionTools = ConnectionTools()

    init {
        this.model = MainActivityModelImp(this)
    }

    // Constructor used for testing
    constructor(model: MainActivityMVP.Model, connectionTools: ConnectionTools) : this() {
        this.model = model
        this.connectionTools = connectionTools
    }

    override fun attachView(view: BaseMVP.View) {
        this.view = view as MainActivityMVP.View
    }

    // Vehicle List methods

    override fun requestVehicles() {
        if (connectionTools.isOnline()) {
            view.showProgressBar("Retrieving information…")
            model.getVehicles()
        }
        else {
            requestVehiclesFailure("Error retrieving list of vehicles. Please enable your internet connection or try again later.")
        }
    }

    override fun requestVehiclesFailure(error: String?) {
        if (error != null) {
            view.showToast(error, Toast.LENGTH_LONG)
        }
        view.hideProgressBar()
    }

    override fun requestVehiclesSuccessfully(scooters: List<Scooter>) {
        view.hideProgressBar()
        createMapInformation(scooters)

        for (scooter in scooters) {
            Log.d("Scooter: ", scooter.toString())
        }
    }

    // Vehicle List Map preparation methods

    override fun createMapInformation(scooters: List<Scooter>) {
        if (scooters.isNotEmpty()) {
            calculateMapBounds(scooters)
            val markerOptions = createMapMarkerOptions(scooters)
            if (markerOptions.isNotEmpty()) {
                view.populateMapWithMarkers(markerOptions)
            }
        }
    }

    override fun createMapMarkerOptions(scooters: List<Scooter>): List<MarkerOptions?> {
        return scooters.map { getMarkerOptions(it) }
    }

    override fun getMarkerOptions(scooter: Scooter): MarkerOptions? {
        try {
            return MarkerOptions()
                    .position(LatLng(scooter.latitude!!, scooter.longitude!!))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(scooter.id.toString())
        }
        catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Can't build MarkerOptions. Error: ", e)

        }
        return null
    }

    override fun calculateMapBounds(scooters: List<Scooter>) {
        val builder = LatLngBounds.builder()

        scooters
                .filter { it.latitude != null && it.longitude != null }
                .map { LatLng(it.latitude!!, it.longitude!!) }
                .forEach { builder.include(it) }

        try {
            val bounds = builder.build()
            val padding = 100

            view.setMapBounds(bounds, padding)
        }
        catch (e: IllegalStateException) {
            Log.e(this.javaClass.simpleName, "Can't build Bounds. Error: ", e)
        }
    }

    // Vehicle Details methods

    override fun requestVehicleDetails(id: String) {
        if (connectionTools.isOnline()) {
            view.showProgressBar("Retrieving information…")
            model.getVehicleDetails(id)
        }
        else {
            requestVehicleDetailsFailure("Error retrieving details of this vehicle. Please enable your internet connection or try again later.")
        }
    }

    override fun requestVehicleDetailsFailure(error: String?) {
        if (error != null) {
            view.showToast(error, Toast.LENGTH_LONG)
        }
        view.hideProgressBar()
    }


    override fun requestVehicleDetailsSuccessfully(scooter: Scooter) {
        view.hideProgressBar()
        overwriteMarker(scooter)
        view.simulateMarkerClick(scooter.id.toString())
    }

    // Vehicle Detail Map preparation methods

    override fun overwriteMarker(scooter: Scooter) {
        val formattedSnippet = getFormattedSnippet(scooter)
        if (formattedSnippet != null) {
            view.overwriteMarkerInfo(scooter.id.toString(), formattedSnippet)
        }
        else {
            view.showToast("Sorry, it wasn't possible to retrieve this vehicle's information. Please try again later.", Toast.LENGTH_SHORT)
        }
    }

    override fun getFormattedSnippet(scooter: Scooter): String? {
        val snippet: String = String.format("Type: %s\nBattery level: %s%%\nPrice: %s %s/%sm",
                scooter.description,
                scooter.batteryLevel,
                scooter.currency,
                scooter.price,
                scooter.priceTime)

        if (!snippet.contains("null")) {
            return snippet
        }
        return null
    }

}
