package com.example.bloodbankmanagement;

public class User {
    String FName,LName,UID,Email,BloodGroup,Mobile,State,District,Town,Pincode,Step, Visible,RequestBlood;

    public User(String FName, String LName, String UID, String email, String bloodGroup, String mobile, String state, String district, String town, String pincode, String step, String visible, String requestBlood) {
        this.FName = FName;
        this.LName = LName;
        this.UID = UID;
        Email = email;
        BloodGroup = bloodGroup;
        Mobile = mobile;
        State = state;
        District = district;
        Town = town;
        Pincode = pincode;
        Step = step;
        Visible = visible;
        RequestBlood = requestBlood;
    }

    public User() {
    }

    public String getFName() {
        return FName;
    }

    public String getLName() {
        return LName;
    }

    public String getUID() {
        return UID;
    }

    public String getEmail() {
        return Email;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getState() {
        return State;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getTown() {
        return Town;
    }

    public String getPincode() {
        return Pincode;
    }

    public String getStep() {
        return Step;
    }

    public String getVisible() {
        return Visible;
    }

    public String getRequestBlood() {
        return RequestBlood;
    }
}
