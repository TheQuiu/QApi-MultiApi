package com.quiu.qapi;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

public class Database {

    public static MongoDatabase db;
    public static Jedis jedis;

    public static void DBConnectString(String dbURI, String dbNAME) {
        if (dbURI == null || dbURI.isEmpty() && dbNAME == null || dbNAME.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("Database URI, Database Name, and Database Collections names are required!");
        } else {
            try {
                MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
                MongoDatabase database = mongoClient.getDatabase(dbNAME);

                db = database;
            } catch (Exception ignored) {

            }
        }
    }

    public static void redisConnect(JavaPlugin plugin, String redisUrl) {
        try {
            if (redisUrl.isEmpty()) {
                Bukkit.getConsoleSender().sendMessage("Redis URL is required!");
            } else {
                jedis = new Jedis(redisUrl);
            }
        } catch (Exception ignored) {
        }
    }
}
