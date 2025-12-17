package com.marotech.recording.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marotech.recording.gson.CustomExclusionStrategy;
import com.marotech.recording.gson.LocalDateAdapter;
import com.marotech.recording.gson.LocalDateTimeAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BaseService {

    protected static final Gson GSON = new GsonBuilder()
            .setExclusionStrategies(new CustomExclusionStrategy())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
}
