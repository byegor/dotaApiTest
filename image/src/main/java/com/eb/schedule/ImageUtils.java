package com.eb.schedule;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Egor on 14.06.2016.
 */
public class ImageUtils {

    private static final String GET_TEAM_LOGO = "http://api.steampowered.com/ISteamRemoteStorage/GetUGCFileDetails/v1/?key=9EBD51CD27F27324F1554C53BEDA17C3&appid=570&ugcid=";

    private final String dataFolder;
    private final byte[] defaultImage;

    private final LoadingCache<String, byte[]> images = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, byte[]>() {
                        public byte[] load(String imageId) {
                            return processImage(imageId);
                        }
                    });

    public ImageUtils(String dataFolder) throws IOException {
        this.dataFolder = dataFolder;
        this.defaultImage = getDefaultImage(dataFolder);
    }

    private byte[] getDefaultImage(String dataFolder) throws IOException {
        Path path = Paths.get(dataFolder).resolve("-1.png");
        return Files.readAllBytes(path);
    }

    public byte[] getImage(String id) {
        try {
            return images.get(id);
        } catch (ExecutionException e) {
            // TODO logging
            System.out.println(e.getMessage());
        }
        return defaultImage;
    }

    private byte[] processImage(String id) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(GET_TEAM_LOGO + id)
                    .header("accept", "application/json")
                    .asJson();
            JSONObject data = jsonResponse.getBody().getObject().getJSONObject("data");
            String url = data.getString("url");

            try (InputStream inputStream = Unirest.get(url).asBinary().getBody()) {
                int read = 0;
                byte[] bytes = new byte[1024];
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }

                    saveImage(outputStream, id);
                    return outputStream.toByteArray();
                }
            }
        } catch (Exception e) {
            // TODO logging
            System.out.println(e.getMessage());
        }
        return defaultImage;
    }

    private void saveImage(ByteArrayOutputStream outputStream, String id) throws Exception {
        try (FileOutputStream file = new FileOutputStream(new File(dataFolder + id + ".png"))) {
            outputStream.writeTo(file);
        }
    }
}
