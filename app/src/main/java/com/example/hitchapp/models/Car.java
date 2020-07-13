package com.example.hitchapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze={Car.class})
@ParseClassName("Car")
public class Car extends ParseObject {

    public static final String KEY_CAR_CAPACITY = "carCapacity";
    public static final String KEY_CAR_YEAR = "carYear";
    public static final String KEY_CAR_MODEL = "carModel";
    public static final String KEY_LICENSE_PLATE = "licensePlate";
    public static final String KEY_CAR_MAKER = "carMaker";

    public int getCarCapacity(){return (int) getNumber(KEY_CAR_CAPACITY);}

    public void setCarCapacity(int carCapacity){put(KEY_CAR_CAPACITY, carCapacity);}

    public int getCarYear(){return (int) getNumber(KEY_CAR_YEAR);}

    public void setCarYear(int carYear){put(KEY_CAR_YEAR, carYear);}

    public String getCarModel(){return getString(KEY_CAR_MODEL);}

    public void setCarModel(String carModel){put(KEY_CAR_MODEL, carModel);}

    public String getLicensePlate(){return getString(KEY_LICENSE_PLATE);}

    public void setLicensePlate(String licensePlate){put(KEY_LICENSE_PLATE, licensePlate);}

    public String getCarMaker(){return getString(KEY_CAR_MAKER);}

    public void setCarMaker(String carMaker){put(KEY_CAR_MAKER, carMaker);}
}
