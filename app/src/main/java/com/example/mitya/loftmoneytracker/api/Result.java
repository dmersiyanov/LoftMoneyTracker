package com.example.mitya.loftmoneytracker.api;

import android.text.TextUtils;

/**
 * Created by Mitya on 29.06.2017.
 */

public class Result {
    String status;

    public boolean isSuccess() {

        return TextUtils.equals(status, "success");
    }
}