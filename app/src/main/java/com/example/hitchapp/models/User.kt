package com.example.hitchapp.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import org.json.JSONArray
import org.parceler.Parcel

@Parcel(analyze = [User::class])
@ParseClassName("_User")
class User : ParseUser() {
    var firstName: String?
        get() = getString(KEY_FIRST_NAME)
        set(firstName) {
            firstName?.let { put(KEY_FIRST_NAME, it) }
        }

    var lastName: String?
        get() = getString(KEY_LAST_NAME)
        set(lastName) {
            lastName?.let { put(KEY_LAST_NAME, it) }
        }

    var profilePicture: ParseFile?
        get() = getParseFile(KEY_PROFILE_PICTURE)
        set(profilePicture) {
            profilePicture?.let { put(KEY_PROFILE_PICTURE, it) }
        }

    var biography: String?
        get() = getString(KEY_BIOGRAPHY)
        set(biography) {
            biography?.let { put(KEY_BIOGRAPHY, it) }
        }

    var college: String?
        get() = getString(KEY_COLLEGE)
        set(college) {
            college?.let { put(KEY_COLLEGE, it) }
        }

    var collegeId: ParseFile?
        get() = getParseFile(KEY_COLLEGE_ID)
        set(collegeId) {
            collegeId?.let { put(KEY_COLLEGE_ID, it) }
        }

    var currentLocation: ParseGeoPoint?
        get() = getParseGeoPoint(KEY_CURRENT_LOCATION)
        set(currentLocation) {
            currentLocation?.let { put(KEY_CURRENT_LOCATION, it) }
        }


    val driversLicense: ParseFile?
        get() = getParseFile(KEY_DRIVERS_LICENSE)

    fun setDriversLicense(driversLicense: String?) {
        driversLicense?.let { put(KEY_DRIVERS_LICENSE, it) }
    }

    var driverType: JSONArray?
        get() = getJSONArray(KEY_DRIVER_TYPE)
        set(driverType) {
            driverType?.let { put(KEY_DRIVER_TYPE, it) }
        }

    var isDriver: Boolean
        get() = getBoolean(KEY_IS_DRIVER)
        set(isDriver) {
            put(KEY_IS_DRIVER, isDriver)
        }

    var car: Car?
        get() = getParseObject(KEY_CAR) as Car?
        set(car) {
            car?.let { put(KEY_CAR, it) }
        }

    var reviews: JSONArray?
        get() = getJSONArray(KEY_REVIEWS)
        set(reviews) {
            reviews?.let { put(KEY_REVIEWS, it) }
        }

    companion object {
        // Define all the user keys
        const val KEY_USERNAME = "username"
        const val KEY_PASSOWRD = "password"
        const val KEY_EMAIL = "email"
        const val KEY_FIRST_NAME = "firstName"
        const val KEY_LAST_NAME = "lastName"
        const val KEY_PROFILE_PICTURE = "profilePicture"
        const val KEY_BIOGRAPHY = "biography"
        const val KEY_COLLEGE = "college"
        const val KEY_COLLEGE_ID = "collegeId"
        const val KEY_DRIVERS_LICENSE = "driversLicense"
        const val KEY_DRIVER_TYPE = "driverType"
        const val KEY_CAR = "car"
        const val KEY_REVIEWS = "reviews"
        const val KEY_IS_DRIVER = "isDriver"
        const val KEY_CURRENT_LOCATION = "currentLocation"
    }
}