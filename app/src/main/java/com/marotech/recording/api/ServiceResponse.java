package com.marotech.recording.api;


import java.util.HashMap;
import java.util.Map;

public class ServiceResponse {

    private String message = "OK";
    private int code = HttpCode.OK;
    private String token;
    private ResponseType responseType;
    private Map<String, Object> additionalInfo = new HashMap<>();

    public String getMessage() {
        return message;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
                "message='" + message + '\'' +
                ", code=" + code +
                ", token='" + token + '\'' +
                ", responseType='" + responseType + '\'' +
                ", additionalInfo=" + additionalInfo +
                '}';
    }
}
