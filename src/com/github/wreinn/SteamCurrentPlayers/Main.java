package com.github.wreinn.SteamCurrentPlayers;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args)  {
        ArrayList<Game> games = gamesFromConfig();
        populatePlayerCount(games, true);

        ArrayList<Game> sortedGames = new ArrayList<>(games);
        sortGames(sortedGames, Game.NAME_ORDER_ASC);

        System.out.println("   Name PlayerCount");
        for (Game game : sortedGames) {
            System.out.printf("%7d %s\n", game.getPlayerCount(), game.getName());
        }
    }

    private static ArrayList<Game> gamesFromConfig() {
        Properties prop = new Properties();
        String configFile = "config/app.properties";
        InputStream isOne = null;
        try {
            isOne = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
        }
        try {
            prop.load(isOne);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }

        ArrayList<Game> gameList = new ArrayList<Game>();

        for (Enumeration e = prop.keys(); e.hasMoreElements();) {
            String gamekey = e.nextElement().toString();

            Pattern pattern = Pattern.compile("game\\.(\\d+)$");
            Matcher matcher = pattern.matcher(gamekey);

            if (matcher.find() && matcher.groupCount() == 1) {
                int appID = Integer.parseInt(matcher.group(1));
                String gameName = prop.getProperty(gamekey);
                gameList.add(new Game(appID, gameName));
            }

        }
        return gameList;
    }

    private static void populatePlayerCount(ArrayList<Game> games, boolean showLoading) {
        for (Game game : games) {
            game.getPlayerCount();
            if (showLoading) System.out.print('.');
        }
        if (showLoading) System.out.print('\n');
    }

    private static void sortGames(ArrayList<Game> games, Comparator<Game> comparator) {
        games.sort(comparator);
//        Collections.sort(games, comparator);
    }
}

/*
Some links that helped me:
* https://stackoverflow.com/a/1359722
* https://stackoverflow.com/a/35446009
* https://stackoverflow.com/q/18410035
* https://stackoverflow.com/a/15960913
* https://stackoverflow.com/a/52016533
* https://stackoverflow.com/a/48673336
* https://stackoverflow.com/q/1647260
* https://stackoverflow.com/q/1104975
* https://stackoverflow.com/q/2279030
 */