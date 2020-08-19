package com.github.wreinn.SteamCurrentPlayers;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Comparator;

public class Game {
    private int appID;
    private String name;
    private int playerCount;
    private boolean failedPlayerCountLookup = false;

    public static final Comparator<Game> NAME_ORDER_ASC;
    public static final Comparator<Game> NAME_ORDER_DESC;
    public static final Comparator<Game> PLAYER_ORDER_ASC;
    public static final Comparator<Game> PLAYER_ORDER_DESC;

    static {
        NAME_ORDER_ASC = new Comparator<Game>() {
            @Override
            public int compare(Game o1, Game o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        };
        NAME_ORDER_DESC = new Comparator<Game>() {
            @Override
            public int compare(Game o1, Game o2) {
                return o2.getName().compareToIgnoreCase(o1.getName());
            }
        };
        PLAYER_ORDER_ASC = new Comparator<Game>() {
            @Override
            public int compare(Game o1, Game o2) {
                if (o1.getPlayerCount() < o2.getPlayerCount()) {
                    return -1;
                } else if (o1.getPlayerCount() > o2.getPlayerCount()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        PLAYER_ORDER_DESC = new Comparator<Game>() {
            @Override
            public int compare(Game o1, Game o2) {
                if (o1.getPlayerCount() < o2.getPlayerCount()) {
                    return 1;
                } else if (o1.getPlayerCount() > o2.getPlayerCount()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    public Game(int appID, String name) {
        this.name = name;
        this.appID = appID;
    }

    public int getAppID() {
        return appID;
    }

    public String getName() {
        return name;
    }

    public int getPlayerCount() {
        int playerCount = 0;

        if (this.playerCount == 0 && ! failedPlayerCountLookup) {
            try {
                playerCount = playerCountFromSteamAPI(appID);
            } catch (Exception e) {
                failedPlayerCountLookup = true;
                System.err.println(e);
            }
            this.playerCount = playerCount;
        }

        return this.playerCount;
    }

    private int playerCountFromSteamAPI(int gameID) throws IOException {
        URL url = new URL("https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?format=json&appid="+gameID);
        InputStream inputStream = url.openStream();
        int playerCount = 0;

        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, "UTF-8");
            JSONObject jsonObject = new JSONObject(writer.toString());
            playerCount = jsonObject.getJSONObject("response").getInt("player_count");
        } finally {
            inputStream.close();
        }

        return playerCount;
    }
}
