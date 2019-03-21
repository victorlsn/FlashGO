package br.com.victorlsn.flash.interfaces

import android.content.Context
import br.com.victorlsn.flash.entities.Scooter
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Created by victorlsn on 18/03/19.
 *
 */

interface MainActivityMVP {
    interface Model {
        fun getVehicles()

        fun getVehicleDetails(id: String)
    }

    interface Presenter : BaseMVP.Presenter {
        // Vehicle List methods

        fun requestVehicles()

        fun requestVehiclesSuccessfully(scooters: List<Scooter>)

        fun requestVehiclesFailure(error: String?)

        //Vehicle List Map preparation methods

        fun createMapInformation(scooters: List<Scooter>)

        fun createMapMarkerOptions(scooters: List<Scooter>) : List<MarkerOptions?>

        fun calculateMapBounds(scooters: List<Scooter>)

        fun getMarkerOptions(scooter: Scooter): MarkerOptions?

        // Vehicle Details methods

        fun requestVehicleDetails(id: String)

        fun requestVehicleDetailsSuccessfully(scooter: Scooter)

        fun requestVehicleDetailsFailure(error: String?)

        //Vehicle Detail Map preparation methods

        fun overwriteMarker(scooter: Scooter)

        fun getFormattedSnippet(scooter: Scooter): String?


    }

    interface View : BaseMVP.View {
        fun populateMapWithMarkers(markerOptions: List<MarkerOptions?>)

        fun setMapBounds(bounds: LatLngBounds, padding: Int)

        fun overwriteMarkerInfo(title: String, snippet: String)

        fun simulateMarkerClick(title: String)
    }
}
