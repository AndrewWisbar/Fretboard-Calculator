package com.example.guitar_fretboard;

import java.util.Arrays;

public class Scale {
    private String name = "Default";
    private int intervals[];

    Scale(String name, int[] intervals) {
        this.name = name;
        this.intervals = Arrays.copyOf(intervals, intervals.length);
    }

    public String getName() {
        return name;
    }

    public int[] getIntervals() {
        return intervals;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int[] getNotesFromRoot(int root) {
        int[] targetNotes = new int[intervals.length + 1];
        targetNotes[0] = root;
        int lastNote = root;
        for(int i = 1; i < targetNotes.length; i++) {
            targetNotes[i] = (lastNote + intervals[i - 1]) % 12;
            lastNote += intervals[i - 1];
        }
        return targetNotes;
    }
}
