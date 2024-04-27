package com.aeroshide.rose_bush.internet;

import com.aeroshide.rose_bush.RoseBush;
import com.aeroshide.rose_bush.api.FileService;
import com.aeroshide.rose_bush.api.HttpService;
import com.aeroshide.rose_bush.api.VersionService;
import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UpdateUtils implements HttpService, FileService, VersionService {

    private final HttpClient client;

    public UpdateUtils(HttpClient client) {
        this.client = client;
    }

    @Override
    public String fetchDataFromUrl(String url) {
        if (url == null) {
            RoseBush.LOG.error("URL cannot be null.");
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                RoseBush.LOG.error("Failed to fetch data. HTTP status: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            RoseBush.LOG.error("Exception occurred while fetching data: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getGistRawUrl(String gistId, String fileToLookFor) {
        String apiGistUrl = "https://api.github.com/gists/" + gistId;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiGistUrl))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) { // Check if the response is successful
                String responseBody = response.body();
                Gson gson = new Gson();
                JsonObject gistJson = gson.fromJson(responseBody, JsonObject.class);

                // Extract the raw URL
                JsonObject files = gistJson.getAsJsonObject("files");
                if (files != null && files.has("versions.txt")) {
                    JsonObject versionsFile = files.getAsJsonObject("versions.txt");
                    return versionsFile.get("raw_url").getAsString();
                } else {
                    System.err.println("Error: 'versions.txt' file not found in the Gist.");
                }
            } else {
                System.err.println("Error fetching Gist data. HTTP status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean downloadFile(String url, String localPath) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                Files.write(Paths.get(localPath), response.body());
                return true;
            } else {
                RoseBush.LOG.error("Failed to download file. HTTP status: " + response.statusCode());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            RoseBush.LOG.error("Exception occurred while downloading file: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteOldModJar(String file) {
        File oldModJarFile = new File(file);
        if (oldModJarFile.exists() && oldModJarFile.delete()) {
            RoseBush.LOG.info("Old mod JAR file deleted successfully.");
        } else {
            RoseBush.LOG.error("Failed to delete old mod JAR file or file does not exist.");
        }
    }

    @Override
    public int convertVersionToInt(String version) {
        // Extract the major and minor parts (e.g., "1.2.3" -> 102)
        String[] versionParts = version.split("\\.");
        int major = versionParts.length > 0 ? Integer.parseInt(versionParts[0]) : 0;
        int minor = versionParts.length > 1 ? Integer.parseInt(versionParts[1]) : 0;

        // Combine the major and minor parts into an integer (e.g., 1.2.3 -> 102)
        return major * 100 + minor;
    }

    @Override
    public int getVersionAsInt(String modId) {
        String versionString = FabricLoader.getInstance().getModContainer(modId)
                .map(container -> container.getMetadata().getVersion().toString())
                .orElse("0.0"); // Default to 0 if version is not available

        // Extract the major and minor parts (e.g., "1.2.3" -> 102)
        String[] versionParts = versionString.split("\\.");
        int major = versionParts.length > 0 ? Integer.parseInt(versionParts[0]) : 0;
        int minor = versionParts.length > 1 ? Integer.parseInt(versionParts[1]) : 0;

        // Combine the major and minor parts into an integer (e.g., 1.2.3 -> 102)
        return major * 100 + minor;
    }

    @Override
    public String convertIntToVersion(int versionInt) {
        // Extract the major and minor parts
        int major = versionInt / 100;
        int minor = versionInt % 100;

        // Combine the parts into a string with dots
        return String.format("%d.%d", major, minor);
    }

}