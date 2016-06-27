package com.vmoksha.schoolbusdriver.model;

/**
 * Created by anshikas on 27-01-2016.
 */
public class Students_DetailsModel {
    private  String Code;
    private  String RegistrationId;
    private  String FirstName;
    private  String LastName;
    private  Integer Mobile;
    private  String UserName;
    private  String DropPointCode;
    private  String Status;


    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getRegistrationId() {
        return RegistrationId;
    }

    public void setRegistrationId(String registrationId) {
        RegistrationId = registrationId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public Integer getMobile() {
        return Mobile;
    }

    public void setMobile(Integer mobile) {
        Mobile = mobile;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getDropPointCode() {
        return DropPointCode;
    }

    public void setDropPointCode(String dropPointCode) {
        DropPointCode = dropPointCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
