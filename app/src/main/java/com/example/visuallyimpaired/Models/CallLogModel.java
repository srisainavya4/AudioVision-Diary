package com.example.visuallyimpaired.Models;

public class CallLogModel {

    String contactNumber,callType,callDuration,callTime;

    public CallLogModel(String contactNumber, String callType, String callDuration, String callTime){
        this.contactNumber = contactNumber;
        this.callType = callType;
        this.callDuration = callDuration;
        this.callTime = callTime;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }
}
