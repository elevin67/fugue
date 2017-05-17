package fugue;

import java.util.*;
import java.util.ArrayList;

public class Generator {
  ArrayList<String> notes;
  // may be phased back in later
  // String[] IONIAN = ["1","2","3","4","5","6","7"];
  // String[] DORIAN = ["1","2","3-","4","5","6","7-"];
  // String[] PHRYGIAN = ["1","2-","3-","4","5","6","7-"];
  // String[] LYDIAN = ["1","2","3","4+","5","6","7"];
  // String[] MIXOLYDIAN = ["1","2","3","4","5","6","7-"];
  // String[] AEOLIAN = ["1","2","3-","4","5","6-","7-"];
  // String[] LOCRIAN = ["1","2-","3-","4","5-","6-","7-"];
  String[] IONIAN = {"","","","","","",""};
  String[] DORIAN = {"","","-","","","","-"};
  String[] PHRYGIAN = {"","-","-","","","","-"};
  String[] LYDIAN = {"","","","+","","",""};
  String[] MIXOLYDIAN = {"","","","","","","-"};
  String[] AEOLIAN = {"","","-","","","-","-"};
  String[] LOCRIAN = {"","-","-","","-","-","-"};
  ArrayList<String[]> scales;
  ArrayList<String> scaleNames;

  public Generator() {
    // may want to move this into an array
    scales = new ArrayList<>();
    scales.add(IONIAN);
    scales.add(DORIAN);
    scales.add(PHRYGIAN);
    scales.add(LYDIAN);
    scales.add(MIXOLYDIAN);
    scales.add(AEOLIAN);
    scales.add(LOCRIAN);

    // may want to move this into an array
    scaleNames = new ArrayList<>();
    scaleNames.add("IONIAN");
    scaleNames.add("DORIAN");
    scaleNames.add("PHRYGIAN");
    scaleNames.add("LYDIAN");
    scaleNames.add("MIXOLYDIAN");
    scaleNames.add("AEOLIAN");
    scaleNames.add("LOCRIAN");

    notes = new ArrayList<>();
    notes.add("A");
    notes.add("B");
    notes.add("C");
    notes.add("D");
    notes.add("E");
    notes.add("F");
    notes.add("G");
  }

  // returns a Scale object containing the scale most applicable to the measure, with it's pertaining root
  public Scale parseMeasure(ArrayList<Note> measure) {
    ArrayList<String> notes = notesInMeasure(measure);
    ArrayList<Scale> possibleScales = new ArrayList<>();

    // loops over all notes
    for(int i = 0; i < notes.size(); i++) {
      String note = notes.get(i);
      ArrayList<String[]> noteScales = getScales(note);
      int count = 0;
      int max = 0;

      // loops over all scales for that note
      for(int h = 0; h < noteScales.size(); h++) {
        count = 0;
        String[] scale = noteScales.get(h);

        // compare notes to certain scale
        for(int j = 0; j < scale.length; j++) {
          // contains method may not recognize note
          if(notes.contains(scale[j])) {
            count++;
          }
        }

        // if that scale has the largest number of matches
        if(count > max) {
          max = count;
          possibleScales.add(new Scale(h, count, note));
        }
      }

    }

    return getScaleWithMostMatches(possibleScales);
  }

  public Scale getScaleWithMostMatches(ArrayList<Scale> scales) {
    int index = 0;
    int max = 0;

    for(int i = 0; i < scales.size(); i++) {
      int numMatches = scales.get(i).getNumMatches();
      if(numMatches > max) {
        index = i;
        max = numMatches;
      }
    }

    return scales.get(index);
  }

  // works, we think
  public ArrayList<String> notesInMeasure(ArrayList<Note> measure) {
    ArrayList<String> notes = new ArrayList<String>();

    for(int i = 0; i < measure.size(); i++) {
      Note note = measure.get(i);
      if(!notes.contains(note.getNote())) {
        notes.add(note.getNote());
      }
    }

    return notes;
  }

  // works
  private ArrayList<String[]> getScales(String note) {
    ArrayList<String[]> noteScales = new ArrayList<>();
    int index = notes.indexOf(note);
    String[] ionian = getIonian(note,index);
    noteScales.add(ionian);

    for(int i = 1; i < scales.size(); i++) {
      String[] scale = new String[7];
      String[] mode = scales.get(i);
      for(int j = 0; j < scale.length; j++) {
        scale[j] = ionian[j] + mode[j];
      }
      noteScales.add(scale);
    }

    return noteScales;
  }

  // works
  public String[] getIonian(String note, int index) {
    String[] ionian = new String[7];
    for(int i = 0; i < notes.size(); i++) {
      if(index >= notes.size()) {
        index = 0;
      }
      ionian[i] = notes.get(index);
      index++;
    }

    return ionian;
  }

  // works
  public ArrayList<ArrayList<Note>> breakByMeasure(ArrayList<Note> phrase, double timeSignature) {
    double count = 0;
    ArrayList<ArrayList<Note>> phraseByMeasure = new ArrayList<>();
    ArrayList<Note> measure = new ArrayList<>();
    for(int i = 0; i < phrase.size(); i++) {
      Note note = phrase.get(i);
      count += note.getLength();
      measure.add(note);
      if(count >= timeSignature) {
        phraseByMeasure.add(copyNotesIntoArrayList(measure));
        measure.clear();
        count = 0;
      }
    }

    return phraseByMeasure;
  }

  // works
  public ArrayList<Note> copyNotesIntoArrayList(ArrayList<Note> notes) {
    ArrayList<Note> notes1 = new ArrayList<>();

    for(int i = 0; i < notes.size(); i++) {
      notes1.add(notes.get(i));
    }

    return notes1;
  }
}
