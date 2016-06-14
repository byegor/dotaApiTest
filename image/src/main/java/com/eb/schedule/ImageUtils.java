package com.eb.schedule;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

/**
 * Created by Egor on 14.06.2016.
 */
public class ImageUtils {

    private static final String GET_TEAM_LOGO = "http://api.steampowered.com/ISteamRemoteStorage/GetUGCFileDetails/v1/?key=9EBD51CD27F27324F1554C53BEDA17C3&appid=570&ugcid=";

    public static byte[] getImage(String id){
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(GET_TEAM_LOGO + id)
                    .header("accept", "application/json")
                    .asJson();
            JSONObject data = jsonResponse.getBody().getObject().getJSONObject("data");
            String url = data.getString("url");

            try(InputStream inputStream = Unirest.get(url).asBinary().getBody()){
                int read = 0;
                byte[] bytes = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                saveImage(outputStream, id);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            //todo default behaviour
            e.printStackTrace();
        }
        return null;
    }

    private static void saveImage(ByteArrayOutputStream outputStream, String id) throws FileNotFoundException {
        try(FileOutputStream file = new FileOutputStream(new File("f:\\nginx-1.11.1\\data\\assets\\" +id + ".png"))){
            outputStream.writeTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
