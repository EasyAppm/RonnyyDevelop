package com.easyapp.ronnyy.rpeasyapp;

public interface RpCallback {
    
    void onResponse(String body, ErrorCode error);
    void onException(Exception e);
    
}
