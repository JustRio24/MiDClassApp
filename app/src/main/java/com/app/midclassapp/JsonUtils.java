package com.app.midclassapp;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class JsonUtils {

    public static List<AcademicEvent> loadAcademicCalendar(Context context) {
        List<AcademicEvent> events = null;
        try {
            // Membaca file JSON dari folder assets
            InputStream is = context.getAssets().open("academic_calendar.json");
            InputStreamReader reader = new InputStreamReader(is);
            Gson gson = new Gson();

            // Mengonversi JSON menjadi list of AcademicEvent
            events = gson.fromJson(reader, new TypeToken<List<AcademicEvent>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
}
