package com.example.recipehive.services;

import android.os.StrictMode;

import com.example.recipehive.data.Ingredient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DataService {
    private static ArrayList<Ingredient> arrIngredients = new ArrayList<Ingredient>();

    public static ArrayList<Ingredient> getArrIngredients() throws MalformedURLException {
        String sURL="https://www.themealdb.com/api/json/v1/1/list.php?i=list";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject rootObj=root.getAsJsonObject();
            JsonArray ingredientsArray = rootObj.getAsJsonArray("meals");
            for (JsonElement je:ingredientsArray){
                JsonObject obj=je.getAsJsonObject();
                JsonElement ingName = obj.get("strIngredient");
                String name = ingName.getAsString();

                arrIngredients.add(new Ingredient(name));
            }


        }catch (MalformedURLException e){
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return arrIngredients;
    }
}
