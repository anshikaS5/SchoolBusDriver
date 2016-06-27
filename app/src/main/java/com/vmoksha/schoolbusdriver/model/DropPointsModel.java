package com.vmoksha.schoolbusdriver.model;

import java.util.ArrayList;

/**
 * Created by anshikas on 22-01-2016.
 */
public class DropPointsModel {
    /*"Code": "CK71U4",
            "Name": "Agara Village,Bangalore,India",
            "Latitude": 12.9201183,
            "Longitude": 77.64319,*/
    private  String dropPointName;
    private  String sequenceId;
    private  String dropPointCode;
    private  String Latitude;
    private  String Longitude;
    private String ArrivalTime;


    public String getDropPointName() {
        return dropPointName;
    }

    public void setDropPointName(String dropPointName) {
        this.dropPointName = dropPointName;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getDropPointCode() {
        return dropPointCode;
    }

    public void setDropPointCode(String dropPointCode) {
        this.dropPointCode = dropPointCode;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        ArrivalTime = arrivalTime;
    }
}
