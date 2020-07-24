package com.example.hitchapp.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser
import org.parceler.Parcel

@Parcel(analyze = [Request::class])
@ParseClassName("Request")
class Request : ParseObject() {
    var requester: ParseUser?
        get() = getParseUser(KEY_REQUESTER)
        set(user) {
            user?.let { put(KEY_REQUESTER, it) }
        }

    var ride: Ride?
        get() = getParseObject(KEY_RIDE) as Ride?
        set(ride) {
            ride?.let { put(KEY_RIDE, it) }
        }

    var driver: ParseUser?
        get() = getParseUser(KEY_DRIVER)
        set(driver) {
            driver?.let { put(KEY_DRIVER, it) }
        }

    companion object {
        const val KEY_RIDE = "ride"
        const val KEY_REQUESTER = "requester"
        const val KEY_DRIVER = "driver"
    }
}