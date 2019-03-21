package br.com.victorlsn.flash.ui.activities
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import br.com.victorlsn.flash.R
import br.com.victorlsn.flash.interfaces.MainActivityMVP
import br.com.victorlsn.flash.presenters.MainActivityPresenterImp
import br.com.victorlsn.flash.ui.widgets.CustomInfoWindow
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by victorlsn on 18/03/19.
 *
 */

class MainActivity : BaseActivity(), MainActivityMVP.View, GoogleMap.OnMarkerClickListener {
    // Presenter used for calls and setup of information
    private var presenter: MainActivityMVP.Presenter? = null

    // Map object used for configuration
    lateinit var map: GoogleMap

    // Used to keep track of markers on map and check if their info is populated.
    private var mapMarkers = HashMap<String, Marker>()

    // Used to keep track of connectivity changes
    private var connectivityDisposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setProgressView(progress_view)


        initPresenter()
        getScooterList()
        initializeMap()
        initConnectivityListener()

    }

    private fun initConnectivityListener() {
        connectivityDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isConnectedToInternet ->
                    if (mapMarkers.isEmpty() && isConnectedToInternet) {
                        presenter!!.requestVehicles()
                    }
                }
    }

    private fun initPresenter() {
        if (null == presenter) {
            presenter = MainActivityPresenterImp()
            presenter!!.attachView(this)
        }
    }

    private fun getScooterList() {
        presenter!!.requestVehicles()
    }

    private fun initializeMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { mMap ->
            map = mMap
            configureMap()
        }
    }

    // Map configuration methods

    // Calls methods for map configuration
    private fun configureMap() {
        setMapClickListener()
        setMapCustomInfoWindow()
        setMapType()
        setMapStyle()
    }

    // Sets activity as marker's click listener (used for customization of action)
    private fun setMapClickListener() {
        map.setOnMarkerClickListener(this)
    }

    // Sets map info window for marker clicks
    private fun setMapCustomInfoWindow() {
        map.setInfoWindowAdapter(CustomInfoWindow(this))
    }

    // Sets map type
    private fun setMapType() {
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    // Styling of map after it's loaded. Only works with MAP_TYPE_NORMAL
    private fun setMapStyle() {
        try {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
        }
        catch (e: Resources.NotFoundException) {
            Log.e(this.javaClass.simpleName, "Can't find style. Error: ", e);
        }
    }

    // Marker click related listener methods

    // Marker click listener
    override fun onMarkerClick(marker: Marker?): Boolean {
        if (!isMarkerPopulatedWithInfo(marker?.title!!)) {
            presenter!!.requestVehicleDetails(marker.title)
            return true
        }
        return false
    }


    // Checks if marker snippet is populated (meaning: getVehicle/{id} already called for this pin)
    private fun isMarkerPopulatedWithInfo(id: String): Boolean {
        if (mapMarkers[id]?.snippet != null) {
            return true
        }

        return false
    }

    // Map Methods

    // Populates map with markers after /getVehicles call
    override fun populateMapWithMarkers(markerOptions: List<MarkerOptions?>) {
        map.clear()
        markerOptions
                .filterNotNull()
                .map { map.addMarker(it) }
                .forEach { saveMarker(it) }
    }

    // Stores reference to a marker in a hashMap
    private fun saveMarker(marker: Marker) {
        mapMarkers.put(marker.title, marker)
    }

    // Sets up map bounds based on the markers inserted
    override fun setMapBounds(bounds: LatLngBounds, padding: Int) {
        map.setLatLngBoundsForCameraTarget(bounds)
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))

        setMinZoomForMap()
    }

    // Sets minimal zoom to avoid excessive zoom out.
    private fun setMinZoomForMap() {
        val currentZoom = map.cameraPosition.zoom
        map.setMinZoomPreference(currentZoom)
    }

    // Overwrites marker information after caling /getVehicles/{id}
    override fun overwriteMarkerInfo(title: String, snippet: String) {
        val marker = mapMarkers[title]
        marker?.snippet = snippet
    }

    // Method used to open marker info window and moving camera to marker
    // after retrieving information from /getVehicles/{id}
    override fun simulateMarkerClick(title: String) {
        val marker = mapMarkers[title]!!

        marker.showInfoWindow()
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.position), 250, null)
    }

}
