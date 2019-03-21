package br.com.victorlsn.flash.presenters

import android.widget.Toast
import br.com.victorlsn.flash.TestUtil.Util
import br.com.victorlsn.flash.entities.Scooter
import br.com.victorlsn.flash.interfaces.MainActivityMVP
import br.com.victorlsn.flash.util.ConnectionTools
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

/**
 * Created by victorlsn on 18/03/2019.
 *
 */

@RunWith(MockitoJUnitRunner::class)
class MainActivityPresenterTest {

    private fun <T> any(): T {
        Mockito.any<T>()
        return unintialized()
    }

    private fun <T> unintialized(): T = null as T

    @Spy
    private var spiedPresenter: MainActivityMVP.Presenter = MainActivityPresenterImp()

    @Mock
    private var presenter: MainActivityMVP.Presenter? = null

    @Mock
    private val model: MainActivityMVP.Model? = null

    @Mock
    private val view: MainActivityMVP.View? = null

    @Mock
    private val connectionTools: ConnectionTools? = null

    lateinit var scooterList: List<Scooter>

//    val context = InstrumentationRegistry.getTargetContext()


    @Before
    @Throws(Exception::class)
    fun setup() {
        // Force isOnline true
        Mockito.`when`(connectionTools?.isOnline()).thenReturn(true)

        // Presenter configuration
        presenter = MainActivityPresenterImp(model!!, connectionTools!!)
        presenter!!.attachView(view!!)

        // Spied presenter configuration
        spiedPresenter.attachView(view)

        // ScooterList initializer
        val response = Util().getJson("json/vehicles.json")

        val gson = Gson()
        val listType = object : TypeToken<ArrayList<Scooter>>() {}.type
        scooterList = gson.fromJson(response, listType)
    }

    // RequestVehicles

    @Test
    fun testRequestVehicles() {
        presenter!!.requestVehicles()

        Mockito.verify<MainActivityMVP.View>(view).showProgressBar("Retrieving information…")
        Mockito.verify<MainActivityMVP.Model>(model).getVehicles()
    }

    // RequestVehiclesSuccessfully

    @Test
    fun testRequestVehiclesSuccessfullyWithEmptyList() {
        spiedPresenter.requestVehiclesSuccessfully(ArrayList())

        Mockito.verify<MainActivityMVP.View>(view).hideProgressBar()
        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter).createMapInformation(Mockito.anyList())
    }

    @Test
    fun testRequestVehiclesSuccessfullyWithFilledList() {
        spiedPresenter.requestVehiclesSuccessfully(scooterList)

        Mockito.verify<MainActivityMVP.View>(view).hideProgressBar()
        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter).createMapInformation(scooterList)
    }

    // RequestVehiclesFailure

    @Test
    fun testRequestVehiclesFailureWithError() {
        presenter!!.requestVehiclesFailure("Error")
        Mockito.verify<MainActivityMVP.View>(view).showToast("Error", Toast.LENGTH_SHORT)
        Mockito.verify<MainActivityMVP.View>(view).hideProgressBar()
    }

    @Test
    fun testRequestVehiclesFailureWithoutError() {
        presenter!!.requestVehiclesFailure(null)
        Mockito.verify<MainActivityMVP.View>(view).hideProgressBar()
    }

    // CreateMapInformation

    @Test
    fun testCreateMapInformationWithEmptyList() {
        spiedPresenter.createMapInformation(ArrayList())

        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter, Mockito.never()).createMapMarkerOptions(Mockito.anyList())
        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter, Mockito.never()).calculateMapBounds(Mockito.anyList())
        Mockito.verify<MainActivityMVP.View>(view, Mockito.never()).populateMapWithMarkers(Mockito.anyList())
    }

    @Test
    fun testCreateMapInformationWithFilledList() {
        spiedPresenter.createMapInformation(scooterList)

        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter).createMapMarkerOptions(scooterList)
        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter).calculateMapBounds(scooterList)
        Mockito.verify<MainActivityMVP.View>(view).populateMapWithMarkers(Mockito.anyList())
    }

    // CreateMapMarkersOptions

    @Test
    fun testCreateMapMarkersOptionsWithEmptyList() {
        spiedPresenter.createMapMarkerOptions(ArrayList())

        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter, Mockito.never()).getMarkerOptions(any())
    }

    @Test
    fun testCreateMapMarkersOptionsWithFilledList() {
        spiedPresenter.createMapMarkerOptions(scooterList)

        for (scooter in scooterList) {
            Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter).getMarkerOptions(scooter)
        }
    }

    // CalculateMapBounds

    @Test
    fun testCalculateMapsBoundsWithEmptyList() {
        spiedPresenter.calculateMapBounds(ArrayList())

        Mockito.verify<MainActivityMVP.View>(view, Mockito.never()).setMapBounds(any(), Mockito.anyInt())
    }

    @Test
    fun testCalculateMapsBoundsWithFilledList() {
        spiedPresenter.calculateMapBounds(scooterList)

        Mockito.verify<MainActivityMVP.View>(view).setMapBounds(any(), Mockito.eq(100))
    }

    @Test
    fun testCalculateMapsBoundsWithSomeScooterWithoutLocation() {
        val scooterListCopy = scooterList.toList()

        scooterListCopy[0].latitude = null
        scooterListCopy[0].longitude = null

        spiedPresenter.calculateMapBounds(scooterListCopy)

        Mockito.verify<MainActivityMVP.View>(view).setMapBounds(any(), Mockito.anyInt())
    }

    @Test
    fun testCalculateMapsBoundsWithScootersWithoutLocation() {
        val scooterListCopy = scooterList.toList()
        for (scooter in scooterListCopy) {
            scooter.latitude = null
            scooter.longitude
        }
        spiedPresenter.calculateMapBounds(scooterList)

        Mockito.verify<MainActivityMVP.View>(view, Mockito.never()).setMapBounds(any(), Mockito.anyInt())
    }

    // RequestVehicleDetails

    @Test
    fun testRequestVehicleDetails() {
        presenter!!.requestVehicleDetails("1")

        Mockito.verify<MainActivityMVP.View>(view).showProgressBar("Retrieving information…")
        Mockito.verify<MainActivityMVP.Model>(model).getVehicleDetails("1")
    }

    // RequesVehicleDetailsSuccessfully

    @Test
    fun testRequestVehicleDetailsSuccessfully() {
        spiedPresenter.requestVehicleDetailsSuccessfully(scooterList[0])

        Mockito.verify<MainActivityMVP.View>(view).hideProgressBar()
        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter).overwriteMarker(scooterList[0])
        Mockito.verify<MainActivityMVP.View>(view).simulateMarkerClick(scooterList[0].id.toString())
    }

    // RequestVehicleDetailsFailure

    @Test
    fun testRequestVehicleDetailsFailureWithError() {
        presenter!!.requestVehicleDetailsFailure("Error")
        Mockito.verify<MainActivityMVP.View>(view).showToast("Error", Toast.LENGTH_SHORT)
        Mockito.verify<MainActivityMVP.View>(view).hideProgressBar()
    }

    @Test
    fun testRequestVehicleDetailsFailureWithoutError() {
        presenter!!.requestVehicleDetailsFailure(null)
        Mockito.verify<MainActivityMVP.View>(view).hideProgressBar()
    }

    // CreateMarker

    @Test
    fun testCreateMarkerWithValidScooter() {
        val scooter = scooterList[0]

        spiedPresenter.overwriteMarker(scooter)

        val snippet = String.format("Type: %s\nBattery level: %s%%\nPrice: %s %s/%sm",
                scooter.description,
                scooter.batteryLevel,
                scooter.currency,
                scooter.price,
                scooter.priceTime)


        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter).getFormattedSnippet(scooterList[0])
        Mockito.verify<MainActivityMVP.View>(view).overwriteMarkerInfo(scooterList[0].id.toString(), snippet)
    }

    @Test
    fun testCreateMarkerWithNullFieldsScooter() {
        val scooter = scooterList[0]
        scooter.description = null
        scooter.batteryLevel = null
        scooter.priceTime = null
        scooter.price = null
        scooter.currency = null

        spiedPresenter.overwriteMarker(scooter)

        Mockito.verify<MainActivityMVP.Presenter>(spiedPresenter).getFormattedSnippet(scooter)
        Mockito.verify<MainActivityMVP.View>(view, Mockito.never()).overwriteMarkerInfo(Mockito.anyString(), Mockito.anyString())
        Mockito.verify<MainActivityMVP.View>(view).showToast("Sorry, it wasn't possible to retrieve this vehicle's information correctly. Please try again later.", Toast.LENGTH_SHORT)
    }

    // FormatSnippetForMarker

    @Test
    fun testGetFormattedSnippetWithValidScooter() {
        val scooter = scooterList[0]

        val expectedSnippet = String.format("Type: %s\nBattery level: %s%%\nPrice: %s %s/%sm",
                scooter.description,
                scooter.batteryLevel,
                scooter.currency,
                scooter.price,
                scooter.priceTime)


        assertEquals(spiedPresenter.getFormattedSnippet(scooter), expectedSnippet)

    }

    @Test
    fun testGetFormattedSnippetWithNullFieldsScooter() {
        val scooter = scooterList[0]
        scooter.description = null
        scooter.batteryLevel = null
        scooter.priceTime = null
        scooter.price = null
        scooter.currency = null

        assertNull(spiedPresenter.getFormattedSnippet(scooter))
    }

}
