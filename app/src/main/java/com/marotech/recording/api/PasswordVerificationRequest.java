package com.marotech.recording.api;


import com.marotech.recording.model.GsonExcludeField;
import com.marotech.recording.util.StringUtils;

public class PasswordVerificationRequest {
    protected String password;
    protected String mobileNumber;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @GsonExcludeField


    public boolean isValid() {
        return !StringUtils.isBlank(mobileNumber)
                && !StringUtils.isBlank(password);
    }

    @Override
    public String toString() {
        return "PasswordVerificationRequest{" +
                "password='" + password + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                '}';
    }
}