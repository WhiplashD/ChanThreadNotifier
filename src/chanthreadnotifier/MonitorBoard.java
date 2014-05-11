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

    public MonitorBoard(String keyword, String board, int time) {
        this.keyword = keyword;
        this.board = board;
        delay = time;
    }

    @Override
    public void run() {
        running = true;
        shouldFetch = true;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss");
        String timestamp = "[" + sdf.format(date) + "]";
        while (running) {
            while (shouldFetch) {

                System.out.println(timestamp + "Fetching.");

                try {

                    System.out.println(timestamp + "Creating URL...");

                    url = "http://a.4cdn.org/" + board + "/catalog.json";

                    URL chanurl = new URL(url);
                    System.out.println(timestamp + "URL created. " + url);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(chanurl.openStream()));
                    StringBuilder buffer = new StringBuilder();
                    int read;

                    char[] chars = new char[1024];

                    System.out.println(timestamp + "Building string buffer...");

                    while ((read = reader.read(chars)) != -1) {
                        buffer.append(chars, 0, read);
                    }
                    String chanjson = buffer.toString();

                    System.out.println(timestamp + "String buffer created. " + buffer.length());
                    System.out.println(timestamp + "Creating gson object...");

                    Page[] page = gson.fromJson(chanjson, Page[].class);

                    System.out.println(timestamp + "Gson object created.");
                    System.out.println(timestamp + "Searching board threads for keyword matches...");

                    for (Page p : page) {
                        for (Post post : p.threads) {
                            try {
                                if (post.com.toLowerCase().contains(keyword) | post.sub.toLowerCase().contains(keyword)) {

                                    System.out.println(timestamp + "Match found at " + "boards.4chan.org/" + board + "/thread/" + post.no);

                                }
                            } catch (NullPointerException npe) {

                            }
                        }
                    }

                    System.out.println(timestamp + "Search finished.");

                } catch (IOException | JsonSyntaxException ex) {

                }
                shouldFetch = false;
                oldTime = System.currentTimeMillis();
                newTime = oldTime + (delay * 60000);

                System.out.println(timestamp + "Sleeping for " + (delay) + " minutes.");

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                this.running = false;
            }
            currentTime = System.currentTimeMillis();
            if (currentTime >= newTime) {

                System.out.println(timestamp + "Waking.");

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
