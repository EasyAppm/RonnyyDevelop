package com.easyapp.ronnyy.rpeasyapp;
import com.http.ceas.core.HttpConnection;
import com.http.ceas.core.annotation.verbs.GET;
import com.http.ceas.core.annotation.Params;
import com.http.ceas.core.annotation.Headers;
import com.http.ceas.core.annotation.Insert;
import com.http.ceas.core.annotation.InsertionType;

public interface RpClient {
    
    @Insert(InsertionType.BASE_URL)
    String BASE_URL = "https://rpeasyapp.xyz/api/v2/";
    
    @GET("auth")
    @Params({"@:{0}"})
    @Headers({"RPEASYAPP:{1}"})
    HttpConnection login(String name, String token);
    
    @GET("update")
    @Params({"@:{0}"})
    @Headers({"RPEASYAPP:{1}"})
    HttpConnection banner(String name, String token);
    
    @GET("updatetk")
    @Params({"@:{0}"})
    @Headers({"RPEASYAPP:{1}"})
    HttpConnection changeCipher(String name, String token);
    
}
