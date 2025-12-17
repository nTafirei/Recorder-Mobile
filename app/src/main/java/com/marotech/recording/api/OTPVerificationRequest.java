package com.marotech.recording.api;


import com.marotech.recording.model.GsonExcludeField;
import com.marotech.recording.util.StringUtils;

public class OTPVerificationRequest {
    private String otp;
    protected String mobileNumber;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @GsonExcludeField
    public boolean isValid() {
        if (!StringUtils.isBlank(mobileNumber) && !StringUtils.isBlank(otp)) {
            return true;
        }
        return false;
    }
}