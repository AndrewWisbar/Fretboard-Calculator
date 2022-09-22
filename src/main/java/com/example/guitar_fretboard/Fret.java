package com.example.guitar_fretboard;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Fret {
    private final double x;
    private final double y;
    private final double w;
    private final double h;

    private final int fretNum;
    private final Note[] notes;

    Fret(double _x, double _y, double _w, double _h, int _fretNum, int[] tuning) {
        x = _x;
        y = _y;
        w = _w;
        h = _h;
        fretNum = _fretNum;
        notes = new Note[tuning.length];
        for(int i = 0; i < tuning.length; i++) {
            notes[i] = new Note(tuning[i] + fretNum);
        }
    }

    public void draw(GraphicsContext context) {
        context.setFill(Color.BLACK);
        context.fillRect(x - w/2, y, w, h);
    }

    public void highlightByNoteName(int[] targetNotes, GraphicsContext context) {
        for(int i = 0; i < notes.length; i++) {
            for(int note : targetNotes) {
                if(notes[i].getNoteNameNum() == note) {
                    double nH = h / 6;
                    notes[i].draw(x - h / 6,
                               y + (nH * (notes.length - i - 1)) + (nH * 0.5),
                               nH * 0.45, FretboardController.colors[note],
                                  context,
                                  FretboardController.NOTE_NAMES[note]);
                }
            }
        }
    }

    public void highlightByInterval(int[] targetNotes, int root, GraphicsContext context) {
        for(int i = 0; i < notes.length; i++) {
            for(int note : targetNotes) {
                if(notes[i].getNoteNameNum() == note) {
                    double nH = h / 6;
                    notes[i].draw(x - h / 6,
                               y + (nH * (notes.length - i - 1)) + (nH * 0.5),
                               nH * 0.45,
                                  FretboardController.colors[notes[i].getIntervalNum(root)],
                                  context,
                                  FretboardController.INTERVAL_NAMES[notes[i].getIntervalNum(root)]);
                }
            }
        }
    }
}
