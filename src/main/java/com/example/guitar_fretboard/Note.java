package com.example.guitar_fretboard;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Note {
    private String name;
    private int noteNum;
    Note(int noteNum) {
        this.name = FretboardController.NOTE_NAMES[noteNum % 12];
        this.noteNum = noteNum;
    }

    public int getOctave() {
        return noteNum / 12;
    }

    public String getName() {
        return name;
    }

    public int getNoteNameNum() {
        return noteNum % 12;
    }

    public int getNoteNum() {
        return noteNum;
    }

    public int getIntervalNum(int root) {
        return (noteNum - root) % 12;
    }

    public String getNameWithOctave() {
        return name + noteNum / 12;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Note plus(Note other) {
        return new Note(this.noteNum + other.getNoteNum());
    }

    public void draw(double x, double y, double r, Color c, GraphicsContext context, String text) {
        context.setFill(c);
        context.fillOval(x - r, y - r, r * 2, r * 2);
        context.setFill(Color.WHITE);
        context.setTextAlign(TextAlignment.CENTER);
        context.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, r*1.5));

        context.fillText(text, x, y+(r/2), r*2);
        context.strokeText(text, x, y+(r/2), r*2);
    }
}
