package com.example.guitar_fretboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Scanner;

public class FretboardApplication extends Application {
    private FretboardController controller;
    public static final String intervalNames[] = {"R", "m2", "M2", "m3", "p4", "°5", "p5", "m6", "M6", "m7", "M7"};
    public static final String noteNames[] = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    @Override
    public void start(Stage stage) throws IOException {
        File scaleFile = new File("scale_config.txt");
        Scanner fileScanner = new Scanner(scaleFile);
        ArrayList<Scale> scales = parseScales(fileScanner);

        FXMLLoader fxmlLoader = new FXMLLoader(FretboardApplication.class.getResource("main-view.fxml"));



        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        stage.setTitle("Fretboard Calculator");
        controller = fxmlLoader.getController();
        controller.populateChoiceBoxes(scales);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private ArrayList<Scale> parseScales(Scanner fileScanner) {
        ArrayList<Scale> scales = new ArrayList<>();

        while(fileScanner.hasNext()) {
            String name;
            String line = fileScanner.nextLine();
            String[] tokens = line.split(";");
            int[] intervals = new int[tokens.length - 1];

            name = tokens[0];
            for(int i = 0; i < tokens.length - 1; i++) {
                intervals[i] = Integer.parseInt(tokens[i + 1]);
            }

            scales.add(new Scale(name, intervals));
        }

        return scales;
    }
}