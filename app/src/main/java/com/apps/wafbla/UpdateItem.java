package com.apps.wafbla;

/**
 * Created by sathv on 6/4/2018.
 */

public class UpdateItem {
    String message, title;

    public UpdateItem(String title, String message) {
        this.title = title;
        this.message = message;

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
