package com.example.hitchapp.models

import com.parse.ParseClassName
import com.parse.ParseGeoPoint
import com.parse.ParseObject
import com.parse.ParseUser
import org.json.JSONArray
import org.parceler.Parcel
import java.util.*

@Parcel(analyze = [Ride::class])
@ParseClassName("Ride")
class Ride : ParseObject() {
    var driver: ParseUser?
        get() = getParseUser(KEY_DRIVER)
        set(user) {
            user?.let { put(KEY_DRIVER, it) }
        }

    var participants: JSONArray?
        get() = getJSONArray(KEY_PARTICIPANTS)
        set(array) {
            array?.let { put(KEY_PARTICIPANTS, it) }
        }

    var requests: JSONArray?
        get() = getJSONArray(KEY_REQUESTS)
        set(array) {
            array?.let { put(KEY_REQUESTS, it) }
        }

    var from: String?
        get() = getString(KEY_FROM)
        set(from) {
            from?.let { put(KEY_FROM, it) }
        }

    var to: String?
        get() = getString(KEY_TO)
        set(to) {
            to?.let { put(KEY_TO, it) }
        }

    var departureTime: String?
        get() = getString(KEY_DEPARTURE_TIME)
        set(time) {
            time?.let { put(KEY_DEPARTURE_TIME, it) }
        }

    var state: String?
        get() = getString(KEY_STATE)
        set(state) {
            state?.let { put(KEY_STATE, it) }
        }

    var fromLocation: ParseGeoPoint?
        get() = getParseGeoPoint(KEY_FROM_LOCATION)
        set(fromLocation) {
            fromLocation?.let { put(KEY_FROM_LOCATION, it) }
        }


    var departureDate: Date?
        get() = getDate(KEY_DEPARTURE_DATE)
        set(time) {
            time?.let { put(KEY_DEPARTURE_DATE, it) }
        }

    var price: Int
        get() = getNumber(KEY_PRICE) as Int
        set(price) {
            put(KEY_PRICE, price)
        }

    var pricePerParticipant: Boolean
        get() = getBoolean(KEY_PRICE_PER_PARTICIPANT)
        set(perParticipant) {
            put(KEY_PRICE_PER_PARTICIPANT, perParticipant)
        }

    var seatsAvailable: Int
        get() = getNumber(KEY_SEATS_AVAILABLE) as Int
        set(seatsAvailable) {
            put(KEY_SEATS_AVAILABLE, seatsAvailable)
        }
    companion object {
        const val KEY_DRIVER = "driver"
        const val KEY_FROM = "from"
        const val KEY_TO = "to"
        const val KEY_DEPARTURE_TIME = "departureTime"
        const val KEY_PARTICIPANTS = "participants"
        const val KEY_PRICE = "price"
        const val KEY_PRICE_PER_PARTICIPANT = "pricePerParticipant"
        const val KEY_SEATS_AVAILABLE = "seatsAvailable"
        const val KEY_DEPARTURE_DATE = "departureDate"
        const val KEY_REQUESTS = "requests"
        const val KEY_FROM_LOCATION = "fromLocation"
        const val KEY_STATE = "state"
    }
}