package com.example.guitar_fretboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FretboardController {
    private static final float SCALE_LEN = 25.5f; // Standard fender (Jazzmaster, Strat, Tele)
    private static final float DRAW_SCALE = 25;
    private static final double FRET_SIZE = 5.0;

    //FXML Elements
    @FXML
    private ChoiceBox<Scale> scaleSelector;

    @FXML
    private ChoiceBox<String> rootSelector;

    @FXML
    private Canvas fretCanvas;

    @FXML
    private RadioButton intervalDisplayOpt, nameDisplayOpt, standardFret, fullNeckFrets, rangeFretSelect;

    @FXML
    private TextField fretRangeStart, fretRangeEnd, neckLength;

    @FXML
    private Label fretRangeEndLabel, fretRangeStartLabel;

    @FXML
    private MenuItem tuningMenuOpt, openMidiItem;

    // A note is represented by an integer, 0 is given to represent C0
    // Therefore, integer division can be used to "translate" a number to a note name + octave
    // noteNum / 12 = octave, and noteNum % 12 = note name
    // Eg.
    // 45 / 12 = 3, noteNames[45 % 12] = "A"
    // 0 = C0, 12 = C1, A3 = 45, etc.

    // An interval name can be gotten by subtracting a noteNum by the root's noteNum, and taking the remainder of
    // division by 12
    // Eg.
    // Root = 4 ("E0"), note = 25 ("C#2")
    // interval = intervalNames[(25 - 4) % 12] = "M6"

    public static final String[] INTERVAL_NAMES = {"R", "m2", "M2", "m3", "M3", "p4", "°5", "p5", "m6", "M6", "m7", "M7"};
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static Color[] colors = {Color.RED, Color.TOMATO, Color.ORANGE,
                                           Color.YELLOW, Color.PALEGREEN, Color.MEDIUMSEAGREEN, Color.MEDIUMTURQUOISE,
                                           Color.DODGERBLUE, Color.ROYALBLUE, Color.DARKVIOLET, Color.MEDIUMORCHID, Color.PALEVIOLETRED};

    // Default tuning E2, A2, D3, G3, B3, E4
    private static final int[] standardTuning = {28, 33, 38, 43, 47, 52};

    private Fret[] frets;

    public static int[] tuning = Arrays.copyOf(standardTuning, 6);

    public void populateChoiceBoxes(ArrayList<Scale> inList) {
        populateScaleList(inList);
        populateRootList();
    }

    private void populateScaleList(ArrayList<Scale> inList) {
        try {
            this.scaleSelector.getItems().addAll(inList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        this.scaleSelector.getSelectionModel().selectFirst();
    }

    private void populateRootList() {
        try {
            this.rootSelector.getItems().addAll(NOTE_NAMES);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.rootSelector.getSelectionModel().selectFirst();
    }

    /**
     * Draw the fretboard with the desired scale
     */
    public void drawResults() {
        int numFrets = 0;
        int[] targetNotes, drawFrets;

        try {
            drawFrets = getNumberOfFrets();
            numFrets = drawFrets[1] - drawFrets[0];

            double fretWidth = (fretCanvas.getWidth() * .9) / numFrets;
            frets = new Fret[numFrets + 1];

            // Get graphics context, draw neck box
            GraphicsContext context = fretCanvas.getGraphicsContext2D();
            context.clearRect(0, 0, fretCanvas.getWidth(), fretCanvas.getHeight());

            //Get Neck Dimensions
            double neckWidth = fretWidth * 3;
            double neckX = fretCanvas.getWidth() * 0.1 - fretWidth * 0.5;
            double neckY = fretCanvas.getHeight() / 2 - neckWidth / 2;


            drawNeck(context, neckX, neckY, fretWidth, numFrets, neckWidth, drawFrets[0]);


            // Get Scale from selector
            Scale scale = scaleSelector.getValue();
            int root = Arrays.asList(NOTE_NAMES).indexOf(rootSelector.getValue());
            targetNotes = scale.getNotesFromRoot(root);

            // Create the frets
            for(int i = 0; i < numFrets + 1; i++) {
                frets[i] = new Fret(fretWidth * i + neckX,
                                    fretCanvas.getHeight()/2 - neckWidth/2,
                                    5.0,
                                    neckWidth, i + drawFrets[0], standardTuning);
            }
            // Show frets
            for(Fret f : frets) {
                f.draw(context);
            }

            // Highlight appropriate notes
            for(Fret f : frets) {
                if(intervalDisplayOpt.isSelected()) {
                    f.highlightByInterval(targetNotes, root, context);
                }
                else {
                    f.highlightByNoteName(targetNotes, context);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Given a fret number calculate the distance from the nut
     *
     * Calculation taken from:
     * https://www.liutaiomottola.com/formulae/fret.htm#:~:text=a%20Scale%20Length-,About%20Historical%20Fret%20Position%20Calculation%3A%20the%20Rule%20of%2018,the%20previous%20fret%20by%2018.
     * @param fretNum the number of the current fret
     * @return X position of the fret
     */
    private double getFretPosition(int fretNum) {
        return SCALE_LEN - (SCALE_LEN / (Math.pow(2, (float) fretNum / 12)));
    }

    private void drawNeck(GraphicsContext context, double neckX, double neckY, double fretWidth, int numFrets, double neckWidth, int startFret) {
        context.setFill(Color.BLACK);
        context.strokeRect(neckX, neckY , fretWidth * numFrets, neckWidth);
        double stringWidth = neckWidth / tuning.length;

        for(int i = 0; i < tuning.length; i++) {
            context.setLineWidth(i + 1);
            context.strokeLine(neckX,
                              neckY + i * stringWidth + stringWidth * 0.5,
                              fretCanvas.getWidth(),
                              neckY + i * stringWidth + stringWidth * 0.5);
        }
        context.setLineWidth(1);
        double r = fretWidth * 0.5;
        for(int i = 1; i <= numFrets; i++) {
            int dots = i + startFret;
            if((dots % 12) % 2 == 1 && dots % 12 != 1 && dots % 12 != 11) {
                context.fillOval(neckX + i * fretWidth - fretWidth * 0.75, fretCanvas.getHeight() / 2 - r / 2, r, r);
            }
            else if (dots % 12 == 0) {
                context.fillOval(neckX + i * fretWidth - fretWidth * 0.75, fretCanvas.getHeight() / 2 - neckWidth * 0.25 - r / 2, r, r);
                context.fillOval(neckX + i * fretWidth - fretWidth * 0.75, fretCanvas.getHeight() / 2 + neckWidth * 0.25 - r / 2, r, r);
            }
        }
    }

    public void fretOptionSelect() {
        if(standardFret.isSelected()) {
            neckLength.setDisable(true);
            fretRangeStart.setDisable(true);
            fretRangeEnd.setDisable(true);
            fretRangeEndLabel.setDisable(true);
            fretRangeStartLabel.setDisable(true);
        }
        else if (fullNeckFrets.isSelected()) {
            neckLength.setDisable(false);
            fretRangeStart.setDisable(true);
            fretRangeEnd.setDisable(true);
            fretRangeEndLabel.setDisable(true);
            fretRangeStartLabel.setDisable(true);

        }
        else if (rangeFretSelect.isSelected()) {
            neckLength.setDisable(true);
            fretRangeStart.setDisable(false);
            fretRangeEnd.setDisable(false);
            fretRangeEndLabel.setDisable(false);
            fretRangeStartLabel.setDisable(false);
        }
    }

    private int[] getNumberOfFrets() throws Exception {
        int[] frets = {0, 0};
        if(standardFret.isSelected()) {
            frets[1] = 12;
        }
        else if (fullNeckFrets.isSelected()) {
            frets[1] = Integer.parseInt(neckLength.getText());
        }
        else if (rangeFretSelect.isSelected()) {
            frets[0] = Integer.parseInt(fretRangeStart.getText());
            frets[1] = Integer.parseInt(fretRangeEnd.getText());
        }
        if(frets[1] - frets[0] < 2 || frets[1] - frets[0] > 32)
            throw new Exception();

        return frets;
    }

    public void showTuningPane() throws Exception{
        System.out.println("Open Tuning Menu");
        Stage tuningStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(FretboardApplication.class.getResource("tuning_pop.fxml"));



        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        tuningStage.setTitle("Tuning");
        tuningStage.setScene(scene);
        tuningStage.show();
    }

    public void openMIDIFile() {
        Stage stage = new Stage();
        FileChooser f = new FileChooser();
        f.setTitle("Open Destination File from Location...");
        File destFile = f.showOpenDialog(stage);
        System.out.println(destFile.getName());
        //stage.show();
    }

    private String getFileExtention(String fileName) {
        String ext = "";
        int i = fileName.lastIndexOf(".");
        if(i > 0) {
            ext = fileName.substring(i + 1);
        }
        return ext;
    }
}