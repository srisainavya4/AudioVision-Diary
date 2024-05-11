package com.example.visuallyimpaired.Models;

import android.graphics.Bitmap;

public class PersonModel {

    String pid,personName;
    byte[] personBitmap;
    Bitmap bt;
    float[][] embeedings;

    public PersonModel(String pid, String personName, byte[] personBitmap){
        this.pid = pid;
        this.personName = personName;
        this.personBitmap = personBitmap;

    }

    public PersonModel(String personName, byte[] personBitmap){
        this.personName = personName;
        this.personBitmap = personBitmap;

    }

    public Bitmap getBt() {
        return bt;
    }

    public void setBt(Bitmap bt) {
        this.bt = bt;
    }

    public float[][] getEmbeedings() {
        return embeedings;
    }

    public void setEmbeedings(float[][] embeedings) {
        this.embeedings = embeedings;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public byte[] getPersonBitmap() {
        return personBitmap;
    }

    public void setPersonBitmap(byte[] personBitmap) {
        this.personBitmap = personBitmap;
    }
}
