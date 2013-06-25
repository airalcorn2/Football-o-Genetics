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
import java.util.*;

public class Genetics {

    String rootDir;
    Hashtable<String, Double> interception, fumble;
    File data, intData, fumbleData, strategy;
    Hashtable<String, String> playerA, playerB;
    Hashtable<String, Hashtable<String, ArrayList<Integer>>> downDistanceStats,
            allStats;
    int popSize, generations, trials, numberDrives;
    boolean useDistance;
    double mutateProb = 0.01;
    ArrayList<Double> maxFitness;
    ArrayList<Hashtable<String, Double>> maxRunProb, maxPlayerAProb;
    Hashtable<String, Double> observedRunProb, observedPlayerAProb;

    public Hashtable<String, Double> setTurnoverRates(File turnoverData) {

        Hashtable<String, Double> turnover = new Hashtable<String, Double>();

        Scanner csv;

        try {

            csv = new Scanner(turnoverData);

            while (csv.hasNextLine()) {

                String[] components = csv.nextLine().split(",");

                String name = components[0];
                double turnoverRate = Double.parseDouble(components[1]);

                turnover.put(name, turnoverRate);

            }

            csv.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }

        return turnover;

    }

    /*For each player in the raw data, create an array containing the yards
    gained by that player throughout the season. For example, if a player
    gained 3, 7, 4, and -2 yards in a season, we would assign an array of
    [3, 7, 4, -2] to that player's name.*/
    public void getAllStats() {

        allStats = new Hashtable<String, Hashtable<String, ArrayList<Integer>>>();

        Scanner csv;

        try {

            csv = new Scanner(data);

            while (csv.hasNextLine()) {

                String[] components = csv.nextLine().split(",");

                String firstName = components[6];
                String lastName = components[7];
                String player = firstName + " " + lastName;

                String play = components[8];

                int yards;

                try {

                    yards = Integer.parseInt(components[9]);

                } catch (NumberFormatException e) {

                    System.out.println("\"" + components[9] + "\" is in the " +
                            "yards column, but is not an integer");
                    throw e;

                }

                if (allStats.containsKey(play)) {

                    Hashtable<String, ArrayList<Integer>> thisPlay = allStats
                            .get(play);

                    if (thisPlay.containsKey(player))
                        thisPlay.get(player).add(yards);

                    else {

                        ArrayList<Integer> firstList = new ArrayList<Integer>();
                        firstList.add(yards);
                        thisPlay.put(player, firstList);

                    }

                } else {

                    ArrayList<Integer> firstList = new ArrayList<Integer>();
                    firstList.add(yards);

                    Hashtable<String, ArrayList<Integer>> firstHashtable = new Hashtable<String, ArrayList<Integer>>();
                    firstHashtable.put(player, firstList);

                    allStats.put(play, firstHashtable);

                }
            }

            csv.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }
    }

    // For each player in the raw data, create an array containing the yards
    // gained by that player for various down and distance situations
    // throughout the season. For example, if a player gained 3, 7, 4, and -2
    // yards on 1st down in a season, we would assign an array of
    // [3, 7, 4, -2] to the 1st down key for that player.
    public void getDownDistanceStats() {

        downDistanceStats = new Hashtable<String, Hashtable<String, ArrayList<Integer>>>();

        Scanner csv;

        try {

            csv = new Scanner(data);

            while (csv.hasNextLine()) {

                String[] components = csv.nextLine().split(",");

                String downString = components[2];
                String distanceString = "";

                int distance;

                if (useDistance) {

                    if (!downString.equals("1st")) {

                        distanceString = components[3];
                        if (distanceString.equals("Goal"))
                            distanceString = components[5];
                        distance = Integer.parseInt(distanceString);

                        if (distance <= 2)
                            distanceString = "Short";
                        if (distance > 2 && distance <= 6)
                            distanceString = "Med";
                        if (distance > 6)
                            distanceString = "Long";

                    }
                }

                if (downString.equals("4th"))
                    downString = "3rd";

                String firstName = components[6];
                String lastName = components[7];
                String player = firstName + " " + lastName;

                String play = components[8];

                int yards = Integer.parseInt(components[9]);

                String fullKey = downString + distanceString + play;

                if (downDistanceStats.containsKey(fullKey)) {

                    Hashtable<String, ArrayList<Integer>> thisDownDistancePlay = downDistanceStats
                            .get(fullKey);

                    if (thisDownDistancePlay.containsKey(player))
                        thisDownDistancePlay.get(player).add(yards);

                    else {

                        ArrayList<Integer> firstList = new ArrayList<Integer>();
                        firstList.add(yards);
                        thisDownDistancePlay.put(player, firstList);

                    }

                } else {

                    ArrayList<Integer> firstList = new ArrayList<Integer>();
                    firstList.add(yards);
                    Hashtable<String, ArrayList<Integer>> firstHashtable = new Hashtable<String, ArrayList<Integer>>();
                    firstHashtable.put(player, firstList);
                    downDistanceStats.put(fullKey, firstHashtable);

                }
            }

            csv.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }
    }

    public void doGeneticAlgorithm() {

        maxFitness = new ArrayList<Double>();
        maxRunProb = new ArrayList<Hashtable<String, Double>>();
        maxPlayerAProb = new ArrayList<Hashtable<String, Double>>();

        ArrayList<double[]> population = createInitialPopulation();

        // Number of generations to evolve
        for (int currentGen = 0; currentGen < generations; currentGen++) {

            // Keeps track of the fitness of each individual in the population
            ArrayList<Double> absoluteFitness = new ArrayList<Double>();

            // Calculate the fitness for each individual
            for (double[] individual : population) {

                ArrayList<Hashtable<String, Double>> individualParameters = fillInProbs(individual);

                double individualFitness = findFitness(
                        individualParameters.get(0),
                        individualParameters.get(1));

                if (maxFitness.size() < 10) {

                    maxFitness.add(individualFitness);
                    maxRunProb.add(individualParameters.get(0));
                    maxPlayerAProb.add(individualParameters.get(1));

                } else if (individualFitness > Collections.min(maxFitness)) {

                    int minIndex = maxFitness.indexOf(Collections
                            .min(maxFitness));

                    maxFitness.remove(minIndex);
                    maxRunProb.remove(minIndex);
                    maxPlayerAProb.remove(minIndex);

                    maxFitness.add(individualFitness);
                    maxRunProb.add(individualParameters.get(0));
                    maxPlayerAProb.add(individualParameters.get(1));

                }

                absoluteFitness.add(individualFitness);
            }

            // Assign each individual a probability of reproduction based on its
            // relative fitness.
            double totalFitness = 0.0;
            for (Double individualFitness : absoluteFitness)
                totalFitness += individualFitness;

            ArrayList<Double> fitnessProb = new ArrayList<Double>();
            double cumulativeFitnessProb = 0.0;

            for (double thisFitness : absoluteFitness) {

                cumulativeFitnessProb += thisFitness / totalFitness;
                fitnessProb.add(cumulativeFitnessProb);

            }

            // Mate individuals in the population with a probability
            // proportional to their relative fitness values.
            ArrayList<double[]> newPopulation = new ArrayList<double[]>();

            for (int i = 0; i < population.size(); i++) {

                double individualA = Math.random();
                int indexA = 0;
                while (individualA > fitnessProb.get(indexA))
                    indexA++;
                double[] parentA = population.get(indexA);

                double individualB = Math.random();
                int indexB = 0;
                while (individualB > fitnessProb.get(indexB))
                    indexB++;
                double[] parentB = population.get(indexB);

                double[] child = reproduce(parentA, parentB);

                if (Math.random() < mutateProb)
                    child = mutate(child);

                newPopulation.add(child);

            }

            population = newPopulation;

        }
    }

    // Create a population of individuals with randomly initialized genes.
    public ArrayList<double[]> createInitialPopulation() {

        int paramNum = 3;

        if (useDistance) {

            paramNum += 4;

            if (playerB.containsKey("QB"))
                paramNum += 7;
            if (playerB.containsKey("RB"))
                paramNum += 7;

        } else {

            if (playerB.containsKey("QB"))
                paramNum += 3;
            if (playerB.containsKey("RB"))
                paramNum += 3;

        }

        ArrayList<double[]> population = new ArrayList<double[]>();

        for (int n = 0; n < popSize; n++) {

            double[] parameters = new double[paramNum];

            for (int i = 0; i < paramNum; i++)
                parameters[i] = Math.random();

            population.add(parameters);

        }

        return population;
    }

    public ArrayList<Hashtable<String, Double>> fillInProbs(double[] params) {

        if (useDistance) {

            Hashtable<String, Double> downDistanceRunProb = new Hashtable<String, Double>();
            downDistanceRunProb.put("1st", params[0]);
            downDistanceRunProb.put("2ndShort", params[1]);
            downDistanceRunProb.put("2ndMed", params[2]);
            downDistanceRunProb.put("2ndLong", params[3]);
            downDistanceRunProb.put("3rdShort", params[4]);
            downDistanceRunProb.put("3rdMed", params[5]);
            downDistanceRunProb.put("3rdLong", params[6]);

            Hashtable<String, Double> downDistancePlayPlayerAProb = new Hashtable<String, Double>();

            if (playerB.containsKey("QB")) {

                downDistancePlayPlayerAProb.put("1stPass", params[7]);
                downDistancePlayPlayerAProb.put("2ndShortPass", params[8]);
                downDistancePlayPlayerAProb.put("2ndMedPass", params[9]);
                downDistancePlayPlayerAProb.put("2ndLongPass", params[10]);
                downDistancePlayPlayerAProb.put("3rdShortPass", params[11]);
                downDistancePlayPlayerAProb.put("3rdMedPass", params[12]);
                downDistancePlayPlayerAProb.put("3rdLongPass", params[13]);

            }

            if (playerB.containsKey("RB")) {

                int paramPlus = 0;
                if (playerB.containsKey("QB"))
                    paramPlus = 7;

                downDistancePlayPlayerAProb.put("1stRush",
                        params[7 + paramPlus]);
                downDistancePlayPlayerAProb.put("2ndShortRush",
                        params[8 + paramPlus]);
                downDistancePlayPlayerAProb.put("2ndMedRush",
                        params[9 + paramPlus]);
                downDistancePlayPlayerAProb.put("2ndLongRush",
                        params[10 + paramPlus]);
                downDistancePlayPlayerAProb.put("3rdShortRush",
                        params[11 + paramPlus]);
                downDistancePlayPlayerAProb.put("3rdMedRush",
                        params[12 + paramPlus]);
                downDistancePlayPlayerAProb.put("3rdLongRush",
                        params[13 + paramPlus]);

            }

            ArrayList<Hashtable<String, Double>> paramValues = new ArrayList<Hashtable<String, Double>>();
            paramValues.add(downDistanceRunProb);
            paramValues.add(downDistancePlayPlayerAProb);

            return paramValues;

        } else {

            Hashtable<String, Double> downRunProb = new Hashtable<String, Double>();
            downRunProb.put("1st", params[0]);
            downRunProb.put("2nd", params[1]);
            downRunProb.put("3rd", params[2]);

            Hashtable<String, Double> downPlayPlayerAProb = new Hashtable<String, Double>();

            if (playerB.containsKey("QB")) {

                downPlayPlayerAProb.put("1stPass", params[3]);
                downPlayPlayerAProb.put("2ndPass", params[4]);
                downPlayPlayerAProb.put("3rdPass", params[5]);

            }

            if (playerB.containsKey("RB")) {

                int paramPlus = 0;
                if (playerB.containsKey("QB"))
                    paramPlus = 3;

                downPlayPlayerAProb.put("1stRush", params[3 + paramPlus]);
                downPlayPlayerAProb.put("2ndRush", params[4 + paramPlus]);
                downPlayPlayerAProb.put("3rdRush", params[5 + paramPlus]);

            }

            ArrayList<Hashtable<String, Double>> paramValues = new ArrayList<Hashtable<String, Double>>();
            paramValues.add(downRunProb);
            paramValues.add(downPlayPlayerAProb);

            return paramValues;
        }
    }

    boolean drive(Hashtable<String, Double> downDistanceRunProb,
                  Hashtable<String, Double> downDistancePlayPlayerAProb) {

        int field = 27;
        int distance = 10;

        for (int down = 1; down <= 3; ) {

            // Create the down/distance/play key.
            String downDistanceKey = getDownString(down)
                    + getDistanceString(down, distance);
            String play = getPlay(downDistanceRunProb, downDistanceKey);
            String fullKey = downDistanceKey + play;

            // Get player.
            String pos = getPosition(play);
            String player = getPlayer(pos, fullKey, downDistancePlayPlayerAProb);

            // Turnover?
            if (fullKey.contains("Pass")) {
                if (Math.random() < interception.get(player))
                    return false;
            } else {
                if (Math.random() < fumble.get(player))
                    return false;
            }

            // Get yards gained on the play.
            int yards = getYards(fullKey, player, play);

            field += yards;

            // Touchdown?
            if (field >= 100) return true;

            distance -= yards;
            down++;

            // First down?
            if (distance <= 0) {

                down = 1;

                if (field > 90)
                    distance = 100 - field;
                else
                    distance = 10;

            }
        }

        return false;

    }

    String getDownString(int down) {

        if (down == 1)
            return "1st";
        if (down == 2)
            return "2nd";
        if (down == 3)
            return "3rd";

        return "";

    }

    String getDistanceString(int down, int distance) {

        if (useDistance) {

            if (down > 1) {

                if (distance <= 2)
                    return "Short";
                else if (distance > 2 && distance <= 6)
                    return "Med";
                else if (distance > 6)
                    return "Long";

            }
        }

        return "";

    }

    String getPlay(Hashtable<String, Double> downDistanceRunProb,
                   String fullKey) {

        if (downDistanceRunProb.containsKey(fullKey)
                && Math.random() < downDistanceRunProb.get(fullKey))
            return "Rush";
        else
            return "Pass";

    }

    String getPosition(String play) {

        if (play.equals("Rush"))
            return "RB";
        else
            return "QB";

    }

    String getPlayer(String pos, String fullKey,
                     Hashtable<String, Double> downDistancePlayPlayerAProb) {

        if (!playerB.containsKey(pos)
                || Math.random() < downDistancePlayPlayerAProb.get(fullKey))
            return playerA.get(pos);
        else
            return playerB.get(pos);

    }

    int getYards(String fullKey, String player, String play) {

        ArrayList<Integer> playerYards;

        if (downDistanceStats.containsKey(fullKey)
                && downDistanceStats.get(fullKey).containsKey(player)
                && downDistanceStats.get(fullKey).get(player).size() >= 10)

            playerYards = downDistanceStats.get(
                    fullKey).get(player);
        else
            playerYards = allStats.get(play).get(player);

        int index = new Random().nextInt(playerYards.size());
        return playerYards.get(index);

    }

    public double findFitness(Hashtable<String, Double> downDistanceRunProb,
                              Hashtable<String, Double> downDistancePlayPlayerAProb) {

        int TD = 0;

        for (int trial = 0; trial < trials; trial++)
            if (drive(downDistanceRunProb, downDistancePlayPlayerAProb)) TD++;

        return (double) TD / (double) trials;

    }

    public double[] reproduce(double[] parentA, double[] parentB) {

        int n = parentA.length;
        Random rand = new Random();
        int c = rand.nextInt(n);

        ArrayList<Double> child = new ArrayList<Double>();

        for (int i = 0; i < c; i++)
            child.add(parentA[i]);
        for (int i = c; i < n; i++)
            child.add(parentB[i]);

        double[] thisChild = new double[child.size()];

        for (int j = 0; j < child.size(); j++)
            thisChild[j] = child.get(j);

        return thisChild;

    }

    public double[] mutate(double[] child) {

        Random rand = new Random();
        int index = rand.nextInt(child.length);
        child[index] = Math.random();
        return child;

    }

    public void getObservedRunProb() {

        observedRunProb = new Hashtable<String, Double>();

        Hashtable<String, Integer> downDistanceRunPassTotals = new Hashtable<String, Integer>();

        if (strategy != null) {

            Scanner csv;

            try {

                csv = new Scanner(strategy);

                String[] runKeys = (String[]) maxRunProb.get(0).keySet()
                        .toArray();
                Arrays.sort(runKeys);

                for (String runKey : runKeys) {

                    double runProb = Double.parseDouble(csv.nextLine());
                    observedRunProb.put(runKey, runProb);

                }

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            }

        } else {

            for (String stats : downDistanceStats.keySet()) {

                int totalPlays = 0;

                for (String player : downDistanceStats.get(stats).keySet()) {

                    totalPlays += downDistanceStats.get(stats).get(player)
                            .size();

                }

                downDistanceRunPassTotals.put(stats, totalPlays);
            }

            for (String rushStats : downDistanceStats.keySet()) {

                if (rushStats.contains("Rush")) {

                    int rushIndex = rushStats.indexOf("Rush");
                    String passStats = rushStats.substring(0, rushIndex)
                            + "Pass";

                    double runProb = (double) downDistanceRunPassTotals
                            .get(rushStats)
                            / ((double) downDistanceRunPassTotals
                            .get(rushStats) + (double) downDistanceRunPassTotals
                            .get(passStats));
                    String downDist = rushStats.substring(0, rushIndex);
                    observedRunProb.put(downDist, runProb);

                }
            }
        }
    }

    public void getObservedPlayerAProb() {

        observedPlayerAProb = new Hashtable<String, Double>();

        for (String downDistancePlay : downDistanceStats.keySet()) {

            Hashtable<String, ArrayList<Integer>> thisDDP = downDistanceStats
                    .get(downDistancePlay);

            double playerAProb = 0.0;

            String position;

            if (downDistancePlay.contains("Pass"))
                position = "QB";
            else
                position = "RB";

            if (playerB.containsKey(position)) {

                String thisPlayerA = playerA.get(position);
                String thisPlayerB = playerB.get(position);

                int thisPlayerATotals = 0;
                int thisPlayerBTotals = 0;

                if (thisDDP.containsKey(thisPlayerA))
                    thisPlayerATotals = thisDDP.get(thisPlayerA).size();

                if (thisDDP.containsKey(thisPlayerB))
                    thisPlayerBTotals = thisDDP.get(thisPlayerB).size();

                int playTotals = thisPlayerATotals + thisPlayerBTotals;

                if (thisDDP.containsKey(thisPlayerA))
                    playerAProb = (double) thisPlayerATotals
                            / (double) playTotals;
            }

            observedPlayerAProb.put(downDistancePlay, playerAProb);
        }
    }

    public void printResults() {

        File resultsDirectory = new File(rootDir + "/Results");
        if (!resultsDirectory.exists())
            resultsDirectory.mkdir();

        File data = new File(resultsDirectory.getPath()
                + "/OptimizationResults.csv");
        try {

            data.createNewFile();
            FileWriter fw = new FileWriter(data.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Trial," + "Fitness," + "QB A," + "QB B," + "RB A,"
                    + "RB B,");

            String[] playerAKeys = maxPlayerAProb.get(0).keySet()
                    .toArray(new String[maxPlayerAProb.get(0).keySet().size()]);
            Arrays.sort(playerAKeys);
            for (String playerAKey : playerAKeys) {
                bw.write(playerAKey + ",");
            }

            String[] runKeys = maxRunProb.get(0).keySet()
                    .toArray(new String[maxRunProb.get(0).keySet().size()]);
            Arrays.sort(runKeys);
            for (String runKey : runKeys)
                bw.write(runKey + ",");

            bw.write("\n");

            double observedFitness = findFitness(observedRunProb,
                    observedPlayerAProb);

            String qb1 = playerA.get("QB");
            String qb2 = "None";
            if (playerB.containsKey("QB"))
                qb2 = playerB.get("QB");

            String rb1 = playerA.get("RB");
            String rb2 = "None";
            if (playerB.containsKey("RB"))
                rb2 = playerB.get("RB");

            bw.write("Observed (" + numberDrives + ")," + observedFitness + ","
                    + qb1 + "," + qb2 + "," + rb1 + "," + rb2 + ",");

            for (String playerAKey : playerAKeys)
                bw.write(observedPlayerAProb.get(playerAKey) + ",");

            for (String runKey : runKeys)
                bw.write(observedRunProb.get(runKey) + ",");

            bw.write("\n");

            for (int i = 0; i < maxFitness.size(); i++) {

                bw.write("Optimized " + (i + 1) + "," + maxFitness.get(i) + ","
                        + qb1 + "," + qb2 + "," + rb1 + "," + rb2 + ",");

                for (String playerAKey : playerAKeys)
                    bw.write(maxPlayerAProb.get(i).get(playerAKey) + ",");

                for (String runKey : runKeys)
                    bw.write(maxRunProb.get(i).get(runKey) + ",");

                bw.write("\n");
            }

            bw.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public void runAnalysis() {

        interception = setTurnoverRates(intData);
        fumble = setTurnoverRates(fumbleData);
        getAllStats();
        getDownDistanceStats();
        doGeneticAlgorithm();
        getObservedRunProb();
        getObservedPlayerAProb();
        printResults();

    }
}