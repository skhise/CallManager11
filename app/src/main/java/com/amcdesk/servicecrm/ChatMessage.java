package com.amcdesk.servicecrm;

/**
 * Created by Shekhar on 4/20/2018.
 */

public class ChatMessage  {

    public Integer side;
    public String message;
    public String date_time;

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSide(Integer side) {
        this.side = side;
    }

    public Integer getSide() {
        return side;
    }

    public String getMessage() {
        return message;
    }

    public String getDate_time() {
        return date_time;
    }
}
