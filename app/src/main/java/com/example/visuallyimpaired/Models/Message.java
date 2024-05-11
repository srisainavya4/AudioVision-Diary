package com.example.visuallyimpaired.Models;

public class Message {

    String title,body,dt;

    public Message(String title,String body,String dt){
        this.title = title;
        this.body = body;
        this.dt = dt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
