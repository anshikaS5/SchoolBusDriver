package com.vmoksha.schoolbusdriver.model;


import com.vmoksha.schoolbusdriver.ApkModel;


public class ModelClass {
    public static String DriverLoginPreference = "DriverLoginPreference";
    public static String isLogin = "isLogedIn";

    // Base URL for Mob DB

    //testUrl
    /*http://prithiviraj.vmoksha.com:8018/swagger*/
//  public static final String baseUrl = "http://prithiviraj.vmokshagroup.com:8018/api/";

    // public static final String baseUrl = "http://prithiviraj.vmoksha.com:8018/api/";
    public static final String LOGIN_URL = ApkModel.baseUrl + "account/authenticate";
    public static final String FORGOT_PASSWORD_URL = ApkModel.baseUrl + "account/forgotpassword";
    public static final String BUS_API_URL = ApkModel.baseUrl + "bus/";
    public static final String BUS_ROUTE_API = ApkModel.baseUrl + "route/";
    public static final String CHANGE_PASS_URL = ApkModel.baseUrl + "user/";
    public static final String SEARCH_STUDENTS_API = ApkModel.baseUrl + "search/user";
    public static final String USER_CURRENT_TRAVEL_INFO = ApkModel.baseUrl + "trackbus/current/0";
    public static final String USER_CURRENT_TRAVEL_INFO_UPDATE = ApkModel.baseUrl + "trackbus/current/1";


    public static final String TOKEN = "token";
    /*public static String Username = "Username";
    public static String Password = "Password";*/
    public static String DriverRole = "DriverRole";

    public static String Id;
    public static String Code = "Code";
    public static String RegistrationId = "RegistrationId";
    public static String FirstName = "FirstName";
    public static String MiddleName = "MiddleName";
    public static String LastName = "LastName";
    public static String Email = "Email";
    public static String Mobile = "Mobile";
    public static String UserName = "UserName";
    public static String Password = "Password";
    public static String RoleCode = "RoleCode";
    public static String GenderCode = "GenderCode";
    public static String EntityCode = "EntityCode";
    public static String DriveProfileUrl = "DriveProfileUrl";
    public static String RouteCode = "RouteCode";
    public static String PickUpCode = "PickUpCode";
    public static String DropCode = "DropCode";
    public static String BusNumber = "Name";
    public static String Count;
    public static String PickupPointCount = "PickupPointCount";
    public static String DropPointCount = "DropPointCount";
    public static String CurrentTravelInfoCode = "CurrentTravelInfoCode";
    public static String CompanyCode = "CompanyCode";
    public static String TravelDate = "TravelDate";

    public static String UserId="UserId";
    public static String isPickUpPoint="isPickUpPoint";
    public static String isDropPoint="isDropPoint";
    public static String isPickUpJourney=" isPickUpJourney";
    public static String  canStartPickupJourney="canStartPickupJourney";
    public static String canStartDropJourney="canStartDropJourney";

}
