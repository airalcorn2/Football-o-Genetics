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

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class GeneticGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    Stats stats;
    File gamesDirectory, intFile, fumbleFile, rawData;
    Genetics evolve;
    private JCheckBox distanceCheckBox;
    private JTextField drivesTextField;
    private JFileChooser fileChooser;
    private JTextField generationsTextField;
    private JTextField popTextField;
    private JComboBox<String> qb1ComboBox;
    private JComboBox<String> qb2ComboBox;
    private JComboBox<String> rb1ComboBox;
    private JComboBox<String> rb2ComboBox;
    private JTextField teamTextField;

    public GeneticGUI() {

        initComponents();

    }

    private void initComponents() {

        fileChooser = new JFileChooser();
        JButton openDirectoryButton = new JButton();
        drivesTextField = new JTextField();
        generationsTextField = new JTextField();
        JButton statsButton = new JButton();
        qb1ComboBox = new JComboBox<String>();
        qb2ComboBox = new JComboBox<String>();
        rb1ComboBox = new JComboBox<String>();
        rb2ComboBox = new JComboBox<String>();
        JLabel qb1Label = new JLabel();
        JLabel qb2Label = new JLabel();
        JButton optimizeButton = new JButton();
        JLabel rb1Label = new JLabel();
        JLabel rb2Label = new JLabel();
        teamTextField = new JTextField();
        popTextField = new JTextField();
        distanceCheckBox = new JCheckBox();
        JButton openFumbleButton = new JButton();
        JButton clearButton = new JButton();
        JButton openIntButton = new JButton();
        JButton strategyButton = new JButton();
        JButton openRawDataButton = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        teamTextField.setText("Target Team");

        openIntButton.setText("Interceptions");
        openIntButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openIntButtonActionPerformed();
            }
        });

        openFumbleButton.setText("Fumbles");
        openFumbleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFumbleButtonActionPerformed();
            }
        });

        openDirectoryButton.setText("Games Folder");
        openDirectoryButton
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        openDirectoryButtonActionPerformed();
                    }
                });

        openRawDataButton.setText("Raw Data");
        openRawDataButton
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        openRawDataButtonActionPerformed();
                    }
                });

        statsButton.setText("Get Stats");
        statsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statsButtonActionPerformed();
            }
        });

        qb1Label.setText("QB A");
        qb1ComboBox.setModel(new DefaultComboBoxModel<String>(
                new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        qb2Label.setText("QB B");
        qb2ComboBox.setModel(new DefaultComboBoxModel<String>(
                new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        rb1Label.setText("RB A");
        rb1ComboBox.setModel(new DefaultComboBoxModel<String>(
                new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        rb2Label.setText("RB B");
        rb2ComboBox.setModel(new DefaultComboBoxModel<String>(
                new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        drivesTextField.setText("Drives");

        generationsTextField.setText("Generations");

        popTextField.setText("Population Size");

        distanceCheckBox.setText("Use Distance?");

        optimizeButton.setText("Run Optimization");
        optimizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optimizeButtonActionPerformed();
            }
        });

        strategyButton.setText("Provide Strategy");
        strategyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strategyButtonActionPerformed();
            }
        });

        clearButton.setText("Start Over");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed();
            }
        });

        GroupLayout layout = new GroupLayout(
                getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.TRAILING)
                                                .addComponent(
                                                        statsButton,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                .addComponent(
                                                        teamTextField,
                                                        GroupLayout.Alignment.LEADING)
                                                .addComponent(
                                                        optimizeButton,
                                                        GroupLayout.Alignment.LEADING,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                .addGroup(
                                                        GroupLayout.Alignment.LEADING,
                                                        layout.createSequentialGroup()
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.TRAILING,
                                                                                false)
                                                                                .addComponent(
                                                                                        popTextField,
                                                                                        GroupLayout.Alignment.LEADING)
                                                                                .addComponent(
                                                                                        drivesTextField,
                                                                                        GroupLayout.Alignment.LEADING,
                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                        115,
                                                                                        GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING,
                                                                                false)
                                                                                .addComponent(
                                                                                        generationsTextField)
                                                                                .addComponent(
                                                                                        distanceCheckBox)))
                                                .addGroup(
                                                        GroupLayout.Alignment.LEADING,
                                                        layout.createSequentialGroup()
                                                                .addGap(9, 9, 9)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.TRAILING)
                                                                                .addComponent(
                                                                                        qb1Label)
                                                                                .addComponent(
                                                                                        qb2Label))
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.TRAILING)
                                                                                .addComponent(
                                                                                        qb1ComboBox,
                                                                                        GroupLayout.Alignment.LEADING,
                                                                                        0,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        qb2ComboBox,
                                                                                        GroupLayout.Alignment.LEADING,
                                                                                        0,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)))
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addGap(11, 11,
                                                                        11)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING)
                                                                                .addComponent(
                                                                                        rb2Label)
                                                                                .addComponent(
                                                                                        rb1Label))
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING)
                                                                                .addComponent(
                                                                                        rb1ComboBox,
                                                                                        0,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        rb2ComboBox,
                                                                                        0,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)))
                                                .addComponent(
                                                        strategyButton,
                                                        GroupLayout.Alignment.LEADING,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                .addComponent(
                                                        clearButton,
                                                        GroupLayout.Alignment.LEADING,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                .addGroup(
                                                        GroupLayout.Alignment.LEADING,
                                                        layout.createSequentialGroup()
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.TRAILING,
                                                                                false)
                                                                                .addComponent(
                                                                                        openDirectoryButton,
                                                                                        GroupLayout.Alignment.LEADING,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        openIntButton,
                                                                                        GroupLayout.Alignment.LEADING,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        118,
                                                                                        Short.MAX_VALUE))
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING)
                                                                                .addComponent(
                                                                                        openFumbleButton,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(
                                                                                        openRawDataButton,
                                                                                        GroupLayout.Alignment.TRAILING,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE))))
                                .addContainerGap()));
        layout.setVerticalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(teamTextField,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                                .addComponent(openFumbleButton)
                                                .addComponent(openIntButton))
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        openDirectoryButton)
                                                .addComponent(openRawDataButton))
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statsButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        qb1ComboBox,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(qb1Label))
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        qb2ComboBox,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(qb2Label))
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        rb1ComboBox,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(rb1Label))
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        rb2ComboBox,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(rb2Label))
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        drivesTextField,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(
                                                        generationsTextField,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                                .addComponent(popTextField)
                                                .addComponent(distanceCheckBox))
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(optimizeButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(strategyButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearButton).addContainerGap()));

        this.setResizable(false);
        pack();

    }

    private void openIntButtonActionPerformed() {

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            intFile = fileChooser.getSelectedFile();

        } else {

            System.out.println("File access cancelled by user.");

        }
    }

    private void openFumbleButtonActionPerformed() {

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            fumbleFile = fileChooser.getSelectedFile();

        } else {

            System.out.println("File access cancelled by user.");

        }
    }

    private void openDirectoryButtonActionPerformed() {

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            gamesDirectory = fileChooser.getSelectedFile();

        } else {

            System.out.println("File access cancelled by user.");

        }
    }

    private void openRawDataButtonActionPerformed() {

        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            rawData = fileChooser.getSelectedFile();

        } else {

            System.out.println("File access cancelled by user.");

        }
    }

    private void statsButtonActionPerformed() {

        if (rawData == null) {

            stats = new Stats();
            stats.homeTeam = teamTextField.getText();
            stats.gamesPath = gamesDirectory.getParent();
            stats.cleanData();
            stats.readInData();
            new File(gamesDirectory.getParent()).delete();

        }

        fillInPlayerComboBoxes();

    }

    void fillInPlayerComboBoxes() {

        try {

            ArrayList<String> qbs = new ArrayList<String>();

            Scanner csv = new Scanner(intFile);

            while (csv.hasNextLine()) {

                String[] lineParts = csv.nextLine().split(",");
                qbs.add(lineParts[0]);

            }

            csv.close();

            String[] theseQbs = new String[qbs.size() + 1];

            for (int i = 0; i < qbs.size(); i++)
                theseQbs[i] = qbs.get(i);
            theseQbs[qbs.size()] = "None";

            qb1ComboBox.setModel(new DefaultComboBoxModel<String>(
                    theseQbs));
            qb2ComboBox.setModel(new DefaultComboBoxModel<String>(
                    theseQbs));

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }

        try {

            ArrayList<String> rbs = new ArrayList<String>();

            Scanner csv = new Scanner(fumbleFile);

            while (csv.hasNextLine()) {

                String[] lineParts = csv.nextLine().split(",");
                rbs.add(lineParts[0]);

            }

            csv.close();

            String[] theseRbs = new String[rbs.size() + 1];

            for (int i = 0; i < rbs.size(); i++)
                theseRbs[i] = rbs.get(i);
            theseRbs[rbs.size()] = "None";

            rb1ComboBox.setModel(new DefaultComboBoxModel<String>(
                    theseRbs));
            rb2ComboBox.setModel(new DefaultComboBoxModel<String>(
                    theseRbs));

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }

    }

    private void optimizeButtonActionPerformed() {

        evolve = new Genetics();

        if (rawData == null) {

            evolve.data = stats.data;
            evolve.numberDrives = stats.numberDrives;
            evolve.rootDir = new File(gamesDirectory.getParent()).getParent();

        } else {

            evolve.data = rawData;
            evolve.rootDir = rawData.getParent();

        }

        evolve.intData = intFile;
        evolve.fumbleData = fumbleFile;

        evolve.playerA = new Hashtable<String, String>();
        evolve.playerA.put("QB", (String) qb1ComboBox.getSelectedItem());
        evolve.playerA.put("RB", (String) rb1ComboBox.getSelectedItem());
        evolve.playerB = new Hashtable<String, String>();

        if (qb2ComboBox.getSelectedItem() != "None")
            evolve.playerB.put("QB", (String) qb2ComboBox.getSelectedItem());
        if (rb2ComboBox.getSelectedItem() != "None")
            evolve.playerB.put("RB", (String) rb2ComboBox.getSelectedItem());

        evolve.generations = Integer.parseInt(generationsTextField.getText());
        evolve.popSize = Integer.parseInt(popTextField.getText());
        evolve.trials = Integer.parseInt(drivesTextField.getText());

        evolve.useDistance = distanceCheckBox.isSelected();

        evolve.runAnalysis();

    }

    private void clearButtonActionPerformed() {

        this.dispose();
        new GeneticGUI().setVisible(true);

    }

    private void strategyButtonActionPerformed() {
        // TODO add your handling code here:
    }
}