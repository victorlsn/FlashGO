package br.com.victorlsn.flash.entities

import com.google.gson.annotations.Expose
import java.util.*

/**
 * Created by victorlsn on 18/03/19.
 *
 */

class Scooter {
    var id: Int? = 0
    var name: String? = null
    var description: String? = null
    var latitude: Double? = 0.toDouble()
    var longitude: Double? = 0.toDouble()
    var batteryLevel: Int? = null
    var timestamp: Date? = null
    var price: Int? = null
    var priceTime: Int? = null
    var currency: String? = null

    override fun toString(): String {
        return "Scooter(id=$id, name=$name, description=$description, latitude=$latitude, longitude=$longitude, batteryLevel=$batteryLevel, timestamp=$timestamp, price=$price, priceTime=$priceTime, currency=$currency)"
    }
}
