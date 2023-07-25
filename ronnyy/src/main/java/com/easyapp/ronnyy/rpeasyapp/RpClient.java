package com.easyapp.ronnyy.rpeasyapp;

import com.http.ceas.core.HttpConnection;
import com.http.ceas.core.annotation.Headers;
import com.http.ceas.core.annotation.Insert;
import com.http.ceas.core.annotation.InsertionType;
import com.http.ceas.core.annotation.Params;
import com.http.ceas.core.annotation.verbs.GET;


public interface RpClient {
    
    @Insert(InsertionType.BASE_URL)
    String BASE_URL = "https://rpeasyapp.xyz/api";
    
    @GET("v3/auth")
    @Params({"@:user_info", "v3:{0}", "user:{1}", "pass:{2}"})
    @Headers({"RPEASYAPP:{3}"})
    HttpConnection login(String auth, String user, String password, String token);

    @GET("v3/auth")
    @Params({"@:get_live", "v3:{0}", "user:{1}", "pass:{2}"})
    @Headers({"RPEASYAPP:{3}"})
    HttpConnection categoriaTV(String auth, String user, String password, String token);

    @GET("v3/auth")
    @Params({"@:get_vod", "v3:{0}", "user:{1}", "pass:{2}"})
    @Headers({"RPEASYAPP:{3}"})
    HttpConnection categoriaFilmes(String auth, String user, String password, String token);

    @GET("v3/auth")
    @Params({"@:get_series", "v3:{0}", "user:{1}", "pass:{2}"})
    @Headers({"RPEASYAPP:{3}"})
    HttpConnection categoriaSeries(String auth, String user, String password, String token);
    
    @GET("v2/update")
    @Params({"@:{0}"})
    @Headers({"RPEASYAPP:{1}"})
    HttpConnection banner(String auth, String token);
    
    @GET("v2/updatetk")
    @Params({"@:{0}"})
    @Headers({"RPEASYAPP:{1}"})
    HttpConnection changeCipher(String name, String token);
    
}
