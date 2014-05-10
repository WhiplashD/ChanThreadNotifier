/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chanthreadnotifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Whiplash
 */
public class ChanThreadNotifier {

    static boolean selectstate = true;
    static String keyword, board;
    static int time, newliner;
    static MonitorBoard monitor;
    static Scanner in;
    static String[] boards = new String[]{"a", "b", "c", "d", "e", "f", "g", "gif",
        "h", "hr", "k", "m", "o", "p", "r", "s", "t", "u", "v", "vg", "vr", "w", "wg",
        "i", "ic", "r9k", "s4s", "cm", "hm", "lgbt", "y", "3", "adv", "an", "asp", "biz",
        "cgl", "ck", "co", "diy", "fa", "fit", "gd", "hc", "int", "jp", "lit", "mlp",
        "mu", "n", "out", "po", "soc", "sp", "tg", "toy", "trv", "tv", "vp", "wsg", "x"};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Initialize();
        System.out.println("Press q at any time to quit, r to reset.");
        monitor.start();
        while (monitor.isAlive()) {
            switch (in.next()) {
                case "q":
                    System.out.println("Exiting.");
                    System.exit(1);
                    break;
                case "r":
                    monitor.end();
                    ClearConsole();
                    Initialize();
                    monitor.start();
                    break;
            }
        }
    }

    public static void Initialize() {
        in = new Scanner(System.in);
        while (selectstate) {
            System.out.println("Enter a board to monitor on (example g for /g/): ");
            board = in.next();
            if (Arrays.asList(boards).contains(board)) {
                break;
            }
            System.out.println("Valid boards: ");
            for (String s : boards) {
                System.out.print(s + " ");
                newliner++;
                if (newliner > 5) {
                    System.out.println();
                    newliner = 0;
                }
            }
            System.out.println();
        }
        System.out.print("Enter a keyword to monitor for: ");
        keyword = in.next();
        while (selectstate) {
            System.out.println("Enter delay interval (in minutes, e.g. 1 for 1 minute) for board update fetching (cannot be lower than 1 minute): ");
            {
                try {
                    time = in.nextInt();
                    if (time >= 1) {
                        break;
                    }
                } catch (Exception nfe) {
                    in.nextLine();
                }
            }
        }
        System.out.println("Will monitor for " + keyword + " on board /" + board + "/" + " every " + time + " minutes.");
        monitor = new MonitorBoard(keyword, board, time);
    }

    public static void ClearConsole() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException ioe) {

        }
    }
}
