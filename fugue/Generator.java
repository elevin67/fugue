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
  String[] scaleNames;

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

    scaleNames = new String[7];
    scaleNames[0] = "IONIAN";
    scaleNames[1] = "DORIAN";
    scaleNames[2] = "PHRYGIAN";
    scaleNames[3] = "LYDIAN";
    scaleNames[4] = "MIXOLYDIAN";
    scaleNames[5] = "AEOLIAN";
    scaleNames[6] = "LOCRIAN";

    notes = new ArrayList<>();
    notes.add("A");
    notes.add("B");
    notes.add("C");
    notes.add("D");
    notes.add("E");
    notes.add("F");
    notes.add("G");
  }

  // main methods
  // earliest at top

  // breaks a phrase into measures, indicate timeSignature here
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

  // returns a Measure object containing the scale most applicable to the measure, with it's pertaining root
  // works
  public Measure parseMeasure(ArrayList<Note> measure) {
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
          if(notes.contains(scale[j])) {
            count++;
          }
        }

        // if that scale has the largest number of matches
        if(count > max) {
          max = count;
          possibleScales.add(new Scale(h, count, note, getIndexOfNote(note, measure)));
        }
      }

    }

    Scale scale = getScaleWithMostMatches(possibleScales);
    return (new Measure(scale, measure));
  }

  // translates measure by the indicated shift, tonifies if indicates
  public Measure translate(Measure measure, int shift, boolean tonify) {
    ArrayList<Note> notes = measure.getNotes();
    ArrayList<Note> shiftedNotes = new ArrayList<>();
    Scale measureScale = measure.getMode();
    int rootIndex = measureScale.getRootIndex();
    String shiftedRoot = shiftedNotes.get(rootIndex).getNote();

    for(int i = 0; i < notes.size(); i++) {
      Note note = notes.get(i);
      String shiftedLetter = getShift(note.getNote(), shift);
      shiftedNotes.add(new Note(shiftedLetter, note.getLength()));
    }

    if(tonify) {
      Scale tonedScale = new Scale(measureScale.getScale(),measureScale.getNumMatches(),shiftedRoot, rootIndex);
      shiftedNotes = tonify(shiftedNotes, tonedScale);
      return (new Measure(tonedScale, shiftedNotes));
    }

    return parseMeasure(shiftedNotes);
  }

  // shifts the given measure into the given scale
  public ArrayList<Note> tonify(ArrayList<Note> measure, Scale scale) {
    return null;
  }

  // assisting methods
  // organized earliest at bottom

  // for parseMeasure, finds all the notes in a measure without repeats
  // works
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

  // for parseMeasure, gets every scale from a certain root
  // works
  private ArrayList<String[]> getScales(String note) {
    ArrayList<String[]> noteScales = new ArrayList<>();
    int index = notes.indexOf(note);
    String[] ionian = getIonian(index);
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

  // for getScales, finds the ionian scale of a note
  // works
  public String[] getIonian(int index) {
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

  // for parseMeasure, gets the index of a note in a measure
  // for finding root index in measure
  public int getIndexOfNote(String note, ArrayList<Note> measure) {
    int index = 0;

    for(int i = 0; i < measure.size(); i++) {
      if(measure.get(i).getNote().equals(note)) {
        index = i;
      }
    }

    return index;
  }

  // for parseMeasure, searches arrayList for scale with most matches
  // works
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

  // for breakByMeasure, copies notes from an arrayList into a new array list
  // works
  public ArrayList<Note> copyNotesIntoArrayList(ArrayList<Note> notes) {
    ArrayList<Note> notes1 = new ArrayList<>();

    for(int i = 0; i < notes.size(); i++) {
      notes1.add(notes.get(i));
    }

    return notes1;
  }

  // for translate
  public String getShift(String note, int shift) {
    int index = notes.indexOf(note);

    for(int i = 0; i < shift; i++) {
      index++;
      if(i >= notes.size()) {
        index = 0;
      }
    }

    return notes.get(index);
  }

  // for tonify
  public String[] getScale(int index, int scaleIndex) {
    String[] ionian = getIonian(index);
    String[] scaleChanges = scales.get(scaleIndex);
    String[] scale = new String[7];

    for(int i = 0; i < ionian.length; i++) {
      scale[i] = ionian[i] + scale[i];
    }

    return scale;
  }
}
