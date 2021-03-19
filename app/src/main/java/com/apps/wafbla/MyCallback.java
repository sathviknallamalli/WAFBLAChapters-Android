package com.apps.wafbla;

import java.util.ArrayList;

public interface MyCallback {
    void onCallback(Boolean isExists, String who);
    void callbackGroups(ArrayList<String> groups);

}
