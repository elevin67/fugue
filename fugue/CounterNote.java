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

public class CounterNote implements JMC {
  ArrayList<Note> counterNotes;
  Note referenceNote;
  int[] intervals;
  int species;
  Scale scale;
  PhraseAnalyzer analyzer;
  // will be found in analyzeNoteInPhrase
  boolean consonant;
  int referenceNoteMotion;
  int counterNoteMotion;

  public CounterNote(ArrayList<Note> cN, Note rN, Scale s, int rm, int cm) {
    analyzer = new PhraseAnalyzer();
    counterNotes = cN;
    referenceNote = rN;
    intervals = findIntervals();
    species = findSpecies();
    scale = s;
    consonant = analyzer.checkConsonance(intervals,scale);
    referenceNoteMotion = rm;
    counterNoteMotion = cm;
  }

  public ArrayList<Note> getCounterNotes() {
    return counterNotes;
  }

  public Note getReferenceNote() {
    return referenceNote;
  }

  public int[] getIntervals() {
    return intervals;
  }

  public int getSpecies() {
    return species;
  }

  private int[] findIntervals() {
    int[] counterIntervals = new int[counterNotes.size()];
    for(int i = 0; i < counterNotes.size(); i++) {
      counterIntervals[i] = analyzer.getInterval(referenceNote.getPitch(),counterNotes.get(i).getPitch());
    }
    return counterIntervals;
  }

  private int findSpecies() {
    return counterNotes.size();
  }

  @Override
  public String toString() {
    return scale.toString() + " " + species + " " + consonant;
  }
}
