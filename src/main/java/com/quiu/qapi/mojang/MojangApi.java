package com.quiu.qapi.mojang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public final class MojangApi {

    public final String UUID_URL_STRING                     = "https://api.mojang.com/users/profiles/minecraft/";
    public final String SKIN_URL_STRING                     = "https://sessionserver.mojang.com/session/minecraft/profile/";

    public final JsonParser JSON_PARSER                     = new JsonParser();

    public final Map<String, MojangSkin> MOJANG_SKIN_MAP    = new HashMap<>();
    public final Map<String, String> USER_UUIDS_MAP         = new HashMap<>();
    public final Map<String, String> USER_NAMES_MAP         = new HashMap<>();


    @SneakyThrows
    public String readURL(@NonNull String url) {
        HttpURLConnection connection = ((HttpURLConnection) new URL(url).openConnection());

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", RandomStringUtils.randomAlphanumeric(16));
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);

        StringBuilder output = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            while (bufferedReader.ready()) {
                output.append(bufferedReader.readLine());
            }
        }

        return output.toString();
    }

    public MojangSkin createSkinObject(String playerName, String value, String signature) {
        return new MojangSkin(playerName, UUID.randomUUID().toString(), value, signature, System.currentTimeMillis());
    }

    public MojangSkin createSkinObject(String value, String signature) {
        return createSkinObject(RandomStringUtils.randomAlphanumeric(16), value, signature);
    }

    public String getUserUUID(@NonNull String playerName) {
        if (USER_UUIDS_MAP.containsKey(playerName.toLowerCase())) {
            return USER_UUIDS_MAP.get(playerName.toLowerCase());
        }

        String result = null;
        JsonElement jsonElement = JSON_PARSER.parse(readURL(UUID_URL_STRING + playerName));

        if (jsonElement.isJsonObject()) {
            result = jsonElement.getAsJsonObject().get("id").getAsString();
        }

        USER_UUIDS_MAP.put(playerName.toLowerCase(), result);
        return result;
    }

    public String getOriginalName(@NonNull String playerName) {
        if (USER_NAMES_MAP.containsKey(playerName.toLowerCase())) {
            return USER_NAMES_MAP.get(playerName.toLowerCase());
        }

        String result = playerName;
        JsonElement jsonElement = JSON_PARSER.parse(readURL(UUID_URL_STRING + playerName));

        if (jsonElement.isJsonObject()) {
            result = jsonElement.getAsJsonObject().get("name").getAsString();
        }

        USER_NAMES_MAP.put(playerName.toLowerCase(), result);
        return result;
    }

    public boolean isPremium(@NonNull String playerName, @NonNull UUID playerUuid) {
        String mojangUuid = MojangApi.getUserUUID(playerName);
        return playerUuid.toString().replace("-", "").equals(mojangUuid);
    }

    @SneakyThrows
    public MojangSkin getMojangSkinOrNull(@NonNull String playerSkin) {
        return getMojangSkinOrDefault(playerSkin, (MojangSkin) null);
    }

    @SneakyThrows
    public MojangSkin getMojangSkinOrDefault(@NonNull String playerSkin) {
        return getMojangSkinOrDefault(playerSkin, "Steve");
    }

    @SneakyThrows
    public MojangSkin getMojangSkinOrDefault(@NonNull String playerSkin, String defaultSkin) {
        return getMojangSkinOrDefault(playerSkin, getMojangSkinOrNull(defaultSkin));
    }

    @SneakyThrows
    public MojangSkin getMojangSkinOrDefault(@NonNull String playerSkin, MojangSkin defaultSkin) {
        MojangSkin mojangSkin = MOJANG_SKIN_MAP.get(playerSkin.toLowerCase());

        if (mojangSkin != null && !mojangSkin.isExpired()) {
            return mojangSkin;
        }

        String playerUUID = getUserUUID(playerSkin);

        if (playerUUID == null) {
            return defaultSkin;
        }

        String skinUrl = readURL(SKIN_URL_STRING + playerUUID + "?unsigned=false");

        JsonObject textureProperty = JSON_PARSER.parse(skinUrl)
                .getAsJsonObject()

                .get("properties")
                .getAsJsonArray()

                .get(0)
                .getAsJsonObject();

        String texture = textureProperty.get("value").getAsString();
        String signature = textureProperty.get("signature").getAsString();

        mojangSkin = new MojangSkin(playerSkin, playerUUID, texture, signature, System.currentTimeMillis());

        MOJANG_SKIN_MAP.put(playerSkin.toLowerCase(), mojangSkin);
        return mojangSkin;
    }
}
