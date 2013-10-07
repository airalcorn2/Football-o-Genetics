/*
 * Copyright (c) 2013 Michael A. Alcorn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Stats {

    String gamesPath, cleanedGames, homeTeam, opponent, currentQuarter, line;
    File[] gameFiles;
    File cleanedGamesDirectory, rawGamesDirectory, data;
    ArrayList<String> qbs, rbs, playCall;
    int numberDrives;
    Scanner scanner;
    BufferedWriter bw;
    FileWriter fw;

    public void cleanData() {

        File folder = new File(gamesPath);
        gameFiles = folder.listFiles();

        String gamesParent = folder.getParent() + System.getProperty("file.separator");

        // Create directories for raw game files and cleaned game files
        cleanedGamesDirectory = new File(gamesParent + "Cleaned Games");
        if (!cleanedGamesDirectory.exists())
            cleanedGamesDirectory.mkdir();
        cleanedGames = cleanedGamesDirectory.getPath();

        if (!gamesPath.contains("Raw Games")) {

            rawGamesDirectory = new File(gamesParent + "Raw Games");
            if (!rawGamesDirectory.exists())
                rawGamesDirectory.mkdir();

        }

        clean();
    }

    // Remove unnecessary words, punctuation, whitespace, etc. from play-by-plays
    public void clean() {

        for (File game : gameFiles) {

            String gameFile = game.getName();

            if (gameFile.equals("Results") || gameFile.equals("Cleaned Games")
                    || gameFile.equals("Raw Games")
                    || gameFile.equals("InterceptionRates.csv")
                    || gameFile.equals("FumbleRates.csv"))
                continue;

            File newGameFile = new File(cleanedGamesDirectory.getPath() + System.getProperty("file.separator")
                    + gameFile);

            try {

                newGameFile.createNewFile();

                fw = new FileWriter(newGameFile.getAbsoluteFile());
                bw = new BufferedWriter(fw);

                Scanner thisScanner;

                try {

                    thisScanner = new Scanner(game);

                    while (thisScanner.hasNextLine()) {

                        String line = thisScanner.nextLine();

                        line = line.replaceAll("\\(Shotgun\\) ", "");
                        line = line.replaceAll("\\(No Huddle\\) ", "");
                        line = line.replaceAll("\\(No Huddle, Shotgun\\) ", "");
                        line = line.replaceAll("\\(Punt formation\\) ", "");
                        line = line.replaceAll("Direct snap to ", "");
                        line = line.replaceAll("\t", " ");
                        line = line.replaceAll("  ", "");
                        line = line.replaceAll(",", "");
                        line = line.replaceAll("\\. ", ".");
                        line = line.replaceAll("\\.", " ");
                        bw.write(line + System.getProperty("line.separator"));

                    }

                    thisScanner.close();

                    bw.close();

                } catch (FileNotFoundException e) {

                    e.printStackTrace();

                }

            } catch (IOException e) {

                e.printStackTrace();

            }

            if (rawGamesDirectory != null)
                game.renameTo(new File(rawGamesDirectory.getPath() + System.getProperty("file.separator")
                        + gameFile));

        }
    }

    public void readInData() {

        qbs = new ArrayList<String>();
        rbs = new ArrayList<String>();

        File folder = new File(cleanedGames);
        File[] gameFiles = folder.listFiles();

        numberDrives = 0;

        File resultsDirectory = new File(folder.getParent() + System.getProperty("file.separator") + "Results");
        if (!resultsDirectory.exists())
            resultsDirectory.mkdir();

        data = new File(resultsDirectory.getPath() + System.getProperty("file.separator") + "RawData.csv");

        try {

            data.createNewFile();

            fw = new FileWriter(data.getAbsoluteFile());
            bw = new BufferedWriter(fw);

            for (File game : gameFiles) {

                opponent = game.getName();

                try {

                    scanner = new Scanner(game);

                    while (scanner.hasNextLine()) {

                        line = scanner.nextLine();

                        if (line.contains(homeTeam)) {

                            homeTeamDrive();
                            continue;

                        }

                        if (line.contains(opponent)) {

                            opponentDrive();
                            continue;

                        }

                        playCall = new ArrayList<String>();
                        Collections.addAll(playCall, line.split(" "));

                        if (playCall.get(1).equals("Quarter"))
                            currentQuarter = playCall.get(0);

                    }

                } catch (FileNotFoundException e) {

                    e.printStackTrace();

                }
            }

            bw.close();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public void homeTeamDrive() {

        while (scanner.hasNextLine()) {

            line = scanner.nextLine();

            if (line.contains("DRIVE TOTALS")
                    || line.contains("End of 4th Quarter")) {

                numberDrives++;
                return;

            }

            if (line.contains(opponent)) {

                opponentDrive();
                return;

            }

            playCall = new ArrayList<String>();
            Collections.addAll(playCall, line.split(" "));

            if (specialCircumstances(line)) {

                if (playCall.get(1).equals("Quarter"))
                    currentQuarter = playCall.get(0);
                continue;

            }

            if (playCall.size() < 8)
                continue;

            printPlay();

        }
    }

    public void opponentDrive() {

        while (scanner.hasNextLine()) {

            line = scanner.nextLine();

            if (line.contains("DRIVE TOTALS")
                    || line.contains("End of 4th Quarter"))
                return;

            if (line.contains(homeTeam)) {

                homeTeamDrive();
                return;

            }

            playCall = new ArrayList<String>();
            Collections.addAll(playCall, line.split(" "));

            if (playCall.size() < 5)
                continue;

            if (playCall.get(1).equals("Quarter"))
                currentQuarter = playCall.get(0);

        }
    }

    public boolean specialCircumstances(String line) {

        return (line.contains("penalty") || line.contains("PENALTY")
                || line.contains("punt") || line.contains("kicks")
                || line.contains("kickoff") || line.contains("extra")
                || line.contains("TWO-POINT") || line.contains("fumbled")
                || line.contains("FUMBLES") || line.contains("intercepted")
                || line.contains("INTERCEPTED") || line.contains("Timeout")
                || line.contains("Quarter") || line.contains("goal")
                || line.contains("ball on") || line.contains("Start of"))
                || line.contains("coin");

    }

    public void printPlay() {

        String down = playCall.get(0);
        String distance = playCall.get(2);
        String sideOfField = playCall.get(4);
        String spotOnField = playCall.get(5);
        String playerFirst = playCall.get(6);
        if (playerFirst.length() > 1)
            playerFirst = playerFirst.substring(0, 1);
        String playerLast = playCall.get(7);
        String player = playerFirst + " " + playerLast;

        if (sideOfField.equals("50")) {

            spotOnField = playCall.get(4);
            playerFirst = playCall.get(5);
            if (playerFirst.length() > 1)
                playerFirst = playerFirst.substring(0, 1);
            playerLast = playCall.get(6);
            player = playerFirst + " " + playerLast;

        }

        String play;

        if (line.contains("sacked") || line.contains("pass")
                || line.contains("scramble")) {

            play = "Pass";
            if (!qbs.contains(player))
                qbs.add(player);

        } else {

            play = "Rush";
            if (!rbs.contains(player))
                rbs.add(player);

        }

        if (distance.equals("Goal"))
            distance = spotOnField;

        int yards = 0;

        if (line.contains("yards"))
            yards = Integer
                    .parseInt(playCall.get(playCall.indexOf("yards") - 1));

        else if (line.contains("1 yard"))
            yards = 1;

        if (line.contains("loss"))
            yards *= -1;

        try {

            bw.write(opponent + "," + currentQuarter + "," + down + ","
                    + distance + "," + sideOfField + "," + spotOnField + ","
                    + playerFirst + "," + playerLast + "," + play + "," + yards
                    + System.getProperty("line.separator"));

        } catch (IOException e) {

            e.printStackTrace();

        }
    }
}