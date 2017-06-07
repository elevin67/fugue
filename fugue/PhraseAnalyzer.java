package fugue;

import jm.JMC;
import jm.music.data.*;
import jm.music.tools.*;
import jm.util.*;
import java.util.Vector;
import java.awt.event.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.ArrayList;

public class PhraseAnalyzer {

  // int[] MAJOR = {0,2,2,1,2,2,2};
  // int[] MINOR = {0,2,1,2,2,1,2};
  final int[] MAJOR = {0,2,4,5,7,9,11};
  final int[] MINOR = {0,2,3,5,7,8,10};
  ArrayList<Integer[]> scaleModes;
  ArrayList<String> scaleModeNames;

  public PhraseAnalyzer() {
    scaleModes = new ArrayList<>();
    scaleModes.add(MAJOR);
    scaleModes.add(MINOR);
    scaleModeNames = new ArrayList<>();
    scaleModeNames.add("MAJOR");
    scaleModeNames.add("MINOR")
  }

  // main functions analyze a whole phrase or just one note within a phrase

  public void analyzePhrase(Phrase phrase) {
    String analysis = "";
    ArrayList<Integer> rawPhrase = getRawNotesInPhrase(phrase);
    Scale scale = getScale(rawPhrase);
  }

  // will need to change input, a function that analyzes the counterpoint notes and the original phrase simultaneously
  public CounterNote analyzeNoteInPhrase(Phrase phrase, int noteIndex, ArrayList<Note> counterNotes, CounterNote prevCounterNote) {
    ArrayList<Integer> rawPhrase = getRawNotesInPhrase(phrase);
    Scale scale = getScale(rawPhrase);
    int motion = getMotion(rawPhrase,noteIndex);
  }

  // gets the interval between two notes, where a is the root
  public int getInterval(int a, int b) {
    if(a > b) {
      a -= 12;
    }

    return b - a;
  }

  // secondary functions used in main functions

  // gets the scale that matches the phrase most
  public Scale getScale(ArrayList<Integer> phrase) {
    ArrayList<Integer[]> possibleScales = new ArrayList<>();
    int max = 0;

    // loops over all notes
    for(int i = 0; i < phrase.length; i++) {
      int note = phrase.get(i);
      ArrayList<Integer[]> noteScales = getNoteScales(note);
      int count = 0;

      // loop over all scales for that note
      for(int j = 0; j < noteScales.size(); j++) {
        int[] scale = noteScales.get(j);
        count = 0;

        // compares notes to selected scale
        for(int k = 0; k < scale.length; k++) {
          if(phrase.contains(scale[k])) {
            count++;
          }
        }

        // if that scale has the largest number of matches
        if(count > max) {
          max = count;
          possibleScales.add(new Scale(note,j));
        }
      }
    }
    return possibleScales.get(possibleScales.size() - 1);
  }

  // used in getScale, gets all the scales (just MAJOR, MINOR right now) for a note
  private ArrayList<Integer[]> getNoteScales(int note) {
    ArrayList<Integer[]> scales = new ArrayList<>();
    for(int i = 0; i < scaleModes.size(); i++) {
      int[] mode = scaleModes.get(i);
      int[] scale = new int[mode.length];
      for(int j = 0; j < mode.length; j++) {
        scale[i] = getRawNote(note + mode[i]);
      }
      scales.add(scale);
    }
    return scales;
  }

  // gets the type of motion occuring at the specified note index, only in context of itself
  // motions: ascending(0), contrary(1), oblique(2)
  private int getPhraseMotion(Phrase phrase, int noteIndex) {
    if()
  }

  // gets the motion compared to the subject
  // motions: parallel, contrary, oblique
  private int getCounterpointMotion(int phraseMotion) {

  }

  // utility functions

  private ArrayList<Integer> getRawNotesInPhrase(Phrase phrase) {
    ArrayList<Integer> rawNotes = new ArrayList<>();
    for(int i = 0; i < phrase.size(); i++) {
      rawNotes.add(phrase.getNote(i).getPitch());
    }
    return rawNotes;
  }

  private int getRawNote(int pitch) {
    return (pitch % 12);
  }
}
