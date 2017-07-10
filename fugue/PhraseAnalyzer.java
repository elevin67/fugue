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
  final Integer[] MAJOR = {0,2,4,5,7,9,11};
  final Integer[] MINOR = {0,2,3,5,7,8,10};
  ArrayList<Integer[]> scaleModes;
  ArrayList<String> scaleModeNames;

  public PhraseAnalyzer() {
    scaleModes = new ArrayList<>();
    scaleModes.add(MAJOR);
    scaleModes.add(MINOR);
    scaleModeNames = new ArrayList<>();
    scaleModeNames.add("MAJOR");
    scaleModeNames.add("MINOR");
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
    int referenceNoteMotion = getPhraseMotion(rawPhrase, noteIndex);
    int counterNoteMotion = getCounterpointMotion(referenceNoteMotion, counterNotes, prevCounterNote);
    CounterNote counterNote = new CounterNote(counterNotes, phrase.getNote(noteIndex), scale, referenceNoteMotion, counterNoteMotion);
    return counterNote;
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
    ArrayList<Scale> possibleScales = new ArrayList<>();
    int max = 0;

    // loops over all notes
    for(int i = 0; i < phrase.size(); i++) {
      int note = phrase.get(i);
      ArrayList<Integer[]> noteScales = getNoteScales(note);
      int count = 0;

      // loop over all scales for that note
      for(int j = 0; j < noteScales.size(); j++) {
        Integer[] scale = noteScales.get(j);
        count = 0;

        // compares notes to selected scale
        for(int k = 0; k < scale.length; k++) {
          if(phrase.contains(scale[k])) {
            count++;
          }
        }

        // if that scale has the largest number of matches
        if(count >= max) {
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
      Integer[] scale = getNoteScale(note,i);
      scales.add(scale);
    }
    return scales;
  }

  public Integer[] getNoteScale(int note, int scaleMode) {
    Integer[] mode = scaleModes.get(scaleMode);
    Integer[] scale = new Integer[mode.length];
    for(int j = 0; j < mode.length; j++) {
      scale[j] = getRawNote(note + mode[j]);
    }
    return scale;
  }

  // gets the type of motion occuring at the specified note index, only in context of itself
  // motions: ascending(0), descending(1), oblique(2)
  private int getPhraseMotion(ArrayList<Integer> phrase, int noteIndex) {
    if(phrase.size() > 1) {
      if (noteIndex == 0) {
        return getMotionOfTwoNotes(phrase.get(noteIndex), phrase.get(noteIndex + 1));
      } else if (noteIndex <= phrase.size() - 1) {
        return getMotionOfTwoNotes(phrase.get(noteIndex), phrase.get(noteIndex - 1));
      }
    }
    return -1;
  }

  private int getMotionOfTwoNotes(int a, int b) {
    if(a > b) {
      return 1;
    } else if(a < b) {
      return 0;
    } else {
      return 2;
    }
  }

  // gets the motion compared to the subject
  // motions: parallel, contrary, oblique
  // parallel = 0, contrary = 1, oblique = 2
  private int getCounterpointMotion(int phraseMotion, ArrayList<Note> counterNotes, CounterNote prev) {
    if(prev != null) {
      ArrayList<Note> prevNotes = prev.getCounterNotes();
      int motion = getMotionOfTwoNotes(prevNotes.get(prevNotes.size()-1).getPitch(),counterNotes.get(0).getPitch());
      if(phraseMotion == 2) {
        return motion;
      } else if(phraseMotion == motion) {
        return 0;
      } else {
        return 1;
      }
    }
    return -1;
  }

  public boolean checkConsonance(int[] intervals, Scale scale) {
    Integer[] mode = scaleModes.get(scale.getScaleMode());
    ArrayList<Integer> dissonants = getDissonantInMode(mode);
    for(int i = 0; i < intervals.length; i++) {
      if(dissonants.contains(intervals[i])) {
        return false;
      }
    }
    return true;
  }

  // utility functions

  public ArrayList<Integer> getRawNotesInPhrase(Phrase phrase) {
    ArrayList<Integer> rawNotes = new ArrayList<>();
    for(int i = 0; i < phrase.size(); i++) {
      rawNotes.add(phrase.getNote(i).getPitch());
    }
    return rawNotes;
  }

  private ArrayList<Integer> getConsonantInMode(Integer[] mode) {
    ArrayList<Integer> consonants = new ArrayList<>();
    consonants.add(mode[0]);
    consonants.add(mode[4]);
    consonants.add(mode[2]);
    consonants.add(mode[5]);
    return consonants;
  }

  private ArrayList<Integer> getDissonantInMode(Integer[] mode) {
    ArrayList<Integer> dissonants = new ArrayList<>();
    dissonants.add(mode[1]);
    dissonants.add(mode[6]);
    dissonants.add(mode[3]);
    return dissonants;
  }

  public String getLetterFromPitchValue(int pitch) {
    int raw_pitch = getRawNote(pitch);
    String letter;
    switch(raw_pitch) {
      case 0: letter = "C"; break;
      case 1: letter = "C#"; break;
      case 2: letter = "D"; break;
      case 3: letter = "D#"; break;
      case 4: letter = "E"; break;
      case 5: letter = "F"; break;
      case 6: letter = "F#"; break;
      case 7: letter = "G"; break;
      case 8: letter = "G#"; break;
      case 9: letter = "A"; break;
      case 10: letter = "A#"; break;
      case 11: letter = "B"; break;
      default: letter = ""; break;
    }
    return letter;
  }

  public int getRawNote(int pitch) {
    return (pitch % 12);
  }
}
