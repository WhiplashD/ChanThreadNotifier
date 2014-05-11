/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chanthreadnotifier;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Whiplash
 */
public class MonitorBoard extends Thread {

    String keyword, board, url;
    long oldTime, currentTime, newTime, delay;
    boolean running, shouldFetch;
    Gson gson = new Gson();
    static Date date;
    static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

    public MonitorBoard(String keyword, String board, int time) {
        this.keyword = keyword;
        this.board = board;
        delay = time;
    }

    @Override
    public void run() {
        running = true;
        shouldFetch = true;
        while (running) {
            while (shouldFetch) {

                date = new Date();
                System.out.println("[" + sdf.format(date) + "] " + "Fetching.");

                try {

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Creating URL...");

                    url = "http://a.4cdn.org/" + board + "/catalog.json";

                    URL chanurl = new URL(url);

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "URL created. " + url);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(chanurl.openStream()));
                    StringBuilder buffer = new StringBuilder();
                    int read;

                    char[] chars = new char[1024];

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Building string buffer...");

                    while ((read = reader.read(chars)) != -1) {
                        buffer.append(chars, 0, read);
                    }
                    String chanjson = buffer.toString();

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "String buffer created. " + buffer.length());
                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Creating gson object...");

                    Page[] page = gson.fromJson(chanjson, Page[].class);

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Gson object created.");
                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Searching board threads for keyword matches...");

                    for (Page p : page) {
                        for (Post post : p.threads) {
                            try {
                                if (post.com.toLowerCase().contains(keyword) | post.sub.toLowerCase().contains(keyword)) {

                                    date = new Date();
                                    System.out.println("[" + sdf.format(date) + "] " + "Match found at " + "boards.4chan.org/" + board + "/thread/" + post.no);

                                }
                            } catch (NullPointerException npe) {

                            }
                        }
                    }

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Search finished.");

                } catch (IOException | JsonSyntaxException ex) {

                }
                shouldFetch = false;
                oldTime = System.currentTimeMillis();
                newTime = oldTime + (delay * 60000);

                date = new Date();
                System.out.println("[" + sdf.format(date) + "] " + "Sleeping for " + (delay) + " minutes.");

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                this.running = false;
            }
            currentTime = System.currentTimeMillis();
            if (currentTime >= newTime) {

                date = new Date();
                System.out.println("[" + sdf.format(date) + "] " + "Waking.");

                shouldFetch = true;
            }
        }
    }

    public void end() {
        this.running = false;
    }

    public static class Post {

        int no;
        String com;
        String sub;
    }

    public static class Page {

        int page;
        Post[] threads;
    }

}
