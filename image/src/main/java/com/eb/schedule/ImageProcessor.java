package com.eb.schedule;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Egor on 15.03.2017.
 */
public abstract class ImageProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final byte[] defaultImage;
    private final String dataFolder;

    ImageProcessor(String dataFolder) throws IOException {
        this.dataFolder = dataFolder;
        defaultImage = getDefaultImage(dataFolder);
    }

    private final LoadingCache<String, byte[]> images = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, byte[]>() {
                        public byte[] load(String imageId) {
                            return processImage(imageId);
                        }
                    });
    private byte[] processImage(String name) {
        try {
            try (InputStream inputStream = Unirest.get(getImageUrl(name)).asBinary().getBody()) {
                int read = 0;
                byte[] bytes = new byte[1024];
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }

                    saveImage(outputStream, name);
                    return outputStream.toByteArray();
                }
            }
        } catch (Exception e) {
            logger.error("couldn't get image with id: " + name);
        }
        return defaultImage;
    }

    private void saveImage(ByteArrayOutputStream outputStream, String name) throws Exception {
        try (FileOutputStream file = new FileOutputStream(new File(dataFolder, name + ".png"))) {
            outputStream.writeTo(file);
        }
    }


    public byte[] getImage(String id) {
        try {
            return images.get(id);
        } catch (ExecutionException ignore) {
        }
        return defaultImage;
    }

    private byte[] getDefaultImage(String dataFolder) throws IOException {
        Path path = Paths.get(dataFolder).resolve("-1.png");
        return Files.readAllBytes(path);
    }


    public abstract String getImageUrl(String name);
}
