package com.aeroshide.rose_bush.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static Config instance;
    private Map<String, Object> data;
    private File config;

    private Config(String configFileName) {
        this.config = new File(configFileName);
        loadConfig();
    }

    public static Config getInstance(String configFileName) {
        if (instance == null) {
            instance = new Config(configFileName);
        }
        return instance;
    }

    public Object getOption(String key) {
        return data.get(key);
    }

    public void setOption(String key, Object value) {
        data.put(key, value);
        saveConfig();
    }

    public boolean doesExists() {
        return config.exists();
    }

    public void loadConfig() {
        if (!config.exists()) {
            data = new HashMap<>();
            saveConfig();
        } else {
            try (Reader reader = new FileReader(config)) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
                data = gson.fromJson(reader, type);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveConfig() {
        try (Writer writer = new FileWriter(config)) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}