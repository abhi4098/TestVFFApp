package com.valleyforge.cdi.generated.parser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.valleyforge.cdi.generated.model.LoginResponse;


import java.lang.reflect.Type;


public class UserProfileDeserializer implements JsonDeserializer<LoginResponse> {

    @Override
    public LoginResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder().create();
        Log.e("abhi", "deserialize: " +json );
        LoginResponse otpResponse = gson.fromJson(json, LoginResponse.class);

        JsonObject jsonObject = json.getAsJsonObject();


        return otpResponse;
    }
}
