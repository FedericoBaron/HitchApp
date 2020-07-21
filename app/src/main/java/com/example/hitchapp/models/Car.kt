package com.example.hitchapp.models

import com.parse.ParseClassName
import com.parse.ParseObject
import org.parceler.Parcel

@Parcel(analyze = [Car::class])
@ParseClassName("Car")
class Car : ParseObject() {
    var carCapacity: Int
        get() = getNumber(KEY_CAR_CAPACITY) as Int
        set(carCapacity) {
            put(KEY_CAR_CAPACITY, carCapacity)
        }

    var carYear: Int
        get() = getNumber(KEY_CAR_YEAR) as Int
        set(carYear) {
            put(KEY_CAR_YEAR, carYear)
        }

    var carModel: String?
        get() = getString(KEY_CAR_MODEL)
        set(carModel) {
            carModel?.let { put(KEY_CAR_MODEL, it) }
        }

    var licensePlate: String?
        get() = getString(KEY_LICENSE_PLATE)
        set(licensePlate) {
            licensePlate?.let { put(KEY_LICENSE_PLATE, it) }
        }

    var carMaker: String?
        get() = getString(KEY_CAR_MAKER)
        set(carMaker) {
            carMaker?.let { put(KEY_CAR_MAKER, it) }
        }

    companion object {
        const val KEY_CAR_CAPACITY = "carCapacity"
        const val KEY_CAR_YEAR = "carYear"
        const val KEY_CAR_MODEL = "carModel"
        const val KEY_LICENSE_PLATE = "licensePlate"
        const val KEY_CAR_MAKER = "carMaker"
    }
}