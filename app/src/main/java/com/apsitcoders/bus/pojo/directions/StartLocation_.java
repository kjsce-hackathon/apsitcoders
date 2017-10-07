
package com.apsitcoders.bus.pojo.directions;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adityathanekar on 06/10/17.
 */

public class StartLocation_ implements Parcelable
{

    private double lat;
    private double lng;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Creator<StartLocation_> CREATOR = new Creator<StartLocation_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public StartLocation_ createFromParcel(Parcel in) {
            StartLocation_ instance = new StartLocation_();
            instance.lat = ((double) in.readValue((double.class.getClassLoader())));
            instance.lng = ((double) in.readValue((double.class.getClassLoader())));
            instance.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
            return instance;
        }

        public StartLocation_[] newArray(int size) {
            return (new StartLocation_[size]);
        }

    }
    ;

    /**
     * No args constructor for use in serialization
     * 
     */
    public StartLocation_() {
    }

    /**
     * 
     * @param lng
     * @param lat
     */
    public StartLocation_(double lat, double lng) {
        super();
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public StartLocation_ withLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public StartLocation_ withLng(double lng) {
        this.lng = lng;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public StartLocation_ withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(lat);
        dest.writeValue(lng);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return  0;
    }

}
