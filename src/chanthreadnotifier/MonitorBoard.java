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
                    // Create the url where the json is at.
                    url = "http://a.4cdn.org/" + board + "/catalog.json";
                    URL chanurl = new URL(url);
                    // Throw it in the reader.
                    BufferedReader reader = new BufferedReader(new InputStreamReader(chanurl.openStream()));
                    StringBuilder buffer = new StringBuilder();
                    int read;
                    char[] chars = new char[1024];

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Building string buffer...");

                    // Reader stores the json page in a char array
                    // then reads it to the stringbuilder.
                    while ((read = reader.read(chars)) != -1) {
                        buffer.append(chars, 0, read);
                    }
                    // Then we put the page in a string.
                    String chanjson = buffer.toString();

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "String buffer created. " + buffer.length());
                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Creating gson object...");

                    // Now we parse the string which contains json objects into java objects.
                    Page[] page = gson.fromJson(chanjson, Page[].class);

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Gson object created.");
                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Searching board threads for keyword matches...");

                    for (Page p : page) { // We search each page
                        for (Post post : p.threads) { // And each thread
                            try {
                                // To see if the thread contains our keyword.
                                if (post.com.toLowerCase().contains(keyword) | post.sub.toLowerCase().contains(keyword)) {

                                    // If it does, we let the user know, with a URL to the thread.
                                    date = new Date();
                                    System.out.println("[" + sdf.format(date) + "] " + "Match found at " + "boards.4chan.org/" + board + "/thread/" + post.no);

                                }
                            } catch (NullPointerException npe) {
                                // Some threads may not have comments or subjects.
                            }
                        }
                    }

                    date = new Date();
                    System.out.println("[" + sdf.format(date) + "] " + "Search finished.");

                    // Cleanup because java a shit.
                    page = null;
                    chanjson = null;
                    buffer = null;
                    reader.close();
                    chars = null;
                    System.gc();

                } catch (JsonSyntaxException | IOException e) {

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
