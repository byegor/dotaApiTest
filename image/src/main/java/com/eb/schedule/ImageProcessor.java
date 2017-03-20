package com.eb.schedule;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
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
            try (InputStream inputStream = getImageAsStream(name)) {
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    return saveImage(name, inputStream, outputStream);
                }
            }
        } catch (Exception e) {
            logger.error("couldn't get image with id: " + name);
        }
        return defaultImage;
    }

    protected InputStream getImageAsStream(String name) throws UnirestException {
        return Unirest.get(getImageUrl(name)).asBinary().getBody();
    }

    protected byte[] saveImage(String name, InputStream inputStream, ByteArrayOutputStream outputStream) throws Exception {
        int read;
        byte[] bytes = new byte[1024];
        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

        try (FileOutputStream file = new FileOutputStream(new File(dataFolder, name + ".png"))) {
            outputStream.writeTo(file);
        }
        return outputStream.toByteArray();
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
