package com.vmoksha.schoolbusdriver.model;

/**
 * Created by shwethap on 21-01-2016.
 */
public class PickupPointModel {
   /* public String childName, childClass, childRoll;
    public int childpic, childrenSelect;
    boolean select;
    boolean childSelection;
    boolean showText;*/
   private  String Code;
    private  String StudentClass;
    private String studentpic;
    private int studentIconSelected;
    private  String RegistrationId;
    private  String FirstName;
    private  String LastName;
    private  int Mobile;
    private  String UserName;
    private  String DropPointCode;
    private  String Status;
    private String Address;
    private String ParentCode;
    private Boolean isBoarding;
    boolean select;
    boolean childSelection;
    boolean showText;

    public PickupPointModel(){

    }
    public PickupPointModel(String studentClass, String studentpic, String registrationId, String firstName,String lastName, boolean isBoarding){
        StudentClass = studentClass;
        this.studentpic = studentpic;
        this.isBoarding=isBoarding;
        RegistrationId = registrationId;
        FirstName = firstName;
        LastName = lastName;

    }

   /* public PickupPointModel(String studentClass, int studentpic, String registrationId, String firstName, boolean isBoarding, boolean childSelection, boolean showText) {
        StudentClass = studentClass;
        this.studentpic = studentpic;
        this.isBoarding=isBoarding;
        this.studentIconSelected = studentIconSelected;
        RegistrationId = registrationId;
        FirstName = firstName;
        this.select = select;
        this.childSelection = childSelection;
        this.showText = showText;
    }*/

    public PickupPointModel(String registrationId, String firstName, String studentpic, String studentClass) {
        RegistrationId = registrationId;
        FirstName = firstName;
        this.studentpic = studentpic;
        StudentClass = studentClass;
    }


    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getStudentClass() {
        return StudentClass;
    }

    public void setStudentClass(String studentClass) {
        StudentClass = studentClass;
    }

    public String getStudentpic() {

        return studentpic;
    }

    public void setStudentpic(String studentpic) {
        this.studentpic = studentpic;
    }

    public int getStudentIconSelected() {
        return studentIconSelected;
    }

    public void setStudentIconSelected(int studentIconSelected) {
        this.studentIconSelected = studentIconSelected;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getParentCode() {
        return ParentCode;
    }

    public void setParentCode(String parentCode) {
        ParentCode = parentCode;
    }

    public Boolean getIsBoarding() {
        return isBoarding;
    }

    public void setIsBoarding(Boolean isBoarding) {
        this.isBoarding = isBoarding;
    }

    public boolean isSelect() {
        return select;
    }
    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isChildSelection() {
        return childSelection;
    }

    public void setChildSelection(boolean childSelection) {
        this.childSelection = childSelection;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }
}

