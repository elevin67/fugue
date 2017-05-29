package fugue_test;

import java.util.*;
import java.util.ArrayList;

public class Generator {
  ArrayList<String> alphabet;
  ArrayList<String> noteSteps;
  // may be phased back in later
  // String[] IONIAN = ["1","2","3","4","5","6","7"];
  // String[] DORIAN = ["1","2","3-","4","5","6","7-"];
  // String[] PHRYGIAN = ["1","2-","3-","4","5","6","7-"];
  // String[] LYDIAN = ["1","2","3","4+","5","6","7"];
  // String[] MIXOLYDIAN = ["1","2","3","4","5","6","7-"];
  // String[] AEOLIAN = ["1","2","3-","4","5","6-","7-"];
  // String[] LOCRIAN = ["1","2-","3-","4","5-","6-","7-"];
  // String[] IONIAN = {"","","","","","",""};
  // String[] DORIAN = {"","","-","","","","-"};
  // String[] PHRYGIAN = {"","-","-","","","","-"};
  // String[] LYDIAN = {"","","","+","","",""};
  // String[] MIXOLYDIAN = {"","","","","","","-"};
  // String[] AEOLIAN = {"","","-","","","-","-"};
  // String[] LOCRIAN = {"","-","-","","-","-","-"};
  int[] MAJOR = {0,2,2,1,2,2,2};
  int[] MINOR = {0,2,1,2,2,1,2};
  // ArrayList<String[]> scales;
  // String[] scaleNames;
  ArrayList<int[]> major_minor;

  public Generator() {
    // may want to move this into an array
    // scales = new ArrayList<>();
    // scales.add(IONIAN);
    // scales.add(DORIAN);
    // scales.add(PHRYGIAN);
    // scales.add(LYDIAN);
    // scales.add(MIXOLYDIAN);
    // scales.add(AEOLIAN);
    // scales.add(LOCRIAN);

    // scaleNames = new String[7];
    // scaleNames[0] = "IONIAN";
    // scaleNames[1] = "DORIAN";
    // scaleNames[2] = "PHRYGIAN";
    // scaleNames[3] = "LYDIAN";
    // scaleNames[4] = "MIXOLYDIAN";
    // scaleNames[5] = "AEOLIAN";
    // scaleNames[6] = "LOCRIAN";

    major_minor = new ArrayList<>();
    major_minor.add(MAJOR);
    major_minor.add(MINOR);

    alphabet = new ArrayList<>();
    alphabet.add("A");
    alphabet.add("B");
    alphabet.add("C");
    alphabet.add("D");
    alphabet.add("E");
    alphabet.add("F");
    alphabet.add("G");

    noteSteps = populateNoteSteps();
  }

  // main methods
  // earliest at top

  // occupies the arraylist noteSteps with all the possible notes
  // works
  public ArrayList<String> populateNoteSteps() {
    ArrayList<String> noteSteps = new ArrayList<>();

    for(int i = 0; i < alphabet.size(); i++) {
      String note = alphabet.get(i);
      if(!note.equals("C") && !note.equals("F")) {
        noteSteps.add(note + "-");
      }
      noteSteps.add(note);

    }

    return noteSteps;
  }

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

    if(!measure.isEmpty()) {
      phraseByMeasure.add(measure);
    }

    return phraseByMeasure;
  }

  // returns a Measure object containing the scale most applicable to the measure, with it's pertaining root
  // works, updated for major_minor
  public Measure parseMeasure(ArrayList<Note> measure) {
    ArrayList<String> notes = notesInMeasure(measure);
    ArrayList<Scale> possibleScales = new ArrayList<>();
    int max = 0;

    // loops over all notes
    for(int i = 0; i < notes.size(); i++) {
      String note = notes.get(i);
      ArrayList<String[]> noteScales = getScales(note);
      int count = 0;


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
          possibleScales.add(new Scale(scale, h, count, note, getIndexOfNote(note, measure)));
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
    int scaleType = measureScale.getScaleType();
    int rootIndex = measureScale.getRootIndex();
    String oldRoot = notes.get(rootIndex).getNote();
    String shiftedRoot = getShift(oldRoot, shift, measureScale);

    for(int i = 0; i < notes.size(); i++) {
      Note note = notes.get(i);
      String noteLetter = note.getNote();
      Scale noteScale = new Scale(getScale(noteLetter,scaleType),scaleType,noteLetter,rootIndex);
      String shiftedLetter = getShift(noteLetter, shift, noteScale);
      shiftedNotes.add(new Note(shiftedLetter, note.getLength()));
    }

    if(tonify) {
      Scale tonedScale = new Scale(getScale(shiftedRoot, measureScale.getScaleType()), measureScale.getScaleType(), shiftedRoot, rootIndex);
      shiftedNotes = tonify(shiftedNotes, tonedScale);
      return (new Measure(tonedScale, shiftedNotes));
    }

    return parseMeasure(shiftedNotes);
  }

  // shifts the given phrase into the given scale
  // put on hold, maybe deprecated
  public ArrayList<Note> tonify(ArrayList<Note> phrase, Scale scale) {
    String scaleRoot = scale.getRoot();
    int scaleIndex = scale.getScaleType();
    ArrayList<String> scaleNotes = convertStringArrayToArrayList(getScale(scaleRoot,scaleIndex));
    ArrayList<Note> tonedPhrase = copyNotesIntoArrayList(phrase);

    for(int i = 0; i < phrase.size(); i++) {
      if(!scaleNotes.contains(phrase.get(i).getNote())) {
        Note nearestNote = findNearestNote(phrase.get(i), scaleNotes);
        tonedPhrase.set(i, nearestNote);
      }
    }

    return tonedPhrase;
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
  // works, adjusted for major_minor
  private ArrayList<String[]> getScales(String note) {
    // ArrayList<String[]> noteScales = new ArrayList<>();
    // int index = notes.indexOf(note);
    // String[] ionian = getIonian(index);
    // noteScales.add(ionian);
    //
    // for(int i = 1; i < scales.size(); i++) {
    //   String[] scale = new String[7];
    //   String[] mode = scales.get(i);
    //   for(int j = 0; j < scale.length; j++) {
    //     scale[j] = ionian[j] + mode[j];
    //   }
    //   noteScales.add(scale);
    // }
    //
    // return noteScales;

    ArrayList<String[]> noteScales = new ArrayList<>();
    int index = noteSteps.indexOf(note);

    for(int i = 0; i < major_minor.size(); i++) {
      String[] scale = getScale(note,i);
      noteScales.add(scale);
    }

    return noteScales;
  }

  // for getScales, getScale, finds the ionian scale of a note
  // works, deprecated
  // public String[] getIonian(int index) {
  //   String[] ionian = new String[7];
  //   for(int i = 0; i < notes.size(); i++) {
  //     if(index >= notes.size()) {
  //       index = 0;
  //     }
  //     ionian[i] = notes.get(index);
  //     index++;
  //   }
  //
  //   return ionian;
  // }

  // for parseMeasure, gets the index of a note in a measure
  // works, for finding root index in measure
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
  // needs to be update for major_minor
  // need to make it so it does the signified shift within the specified scale
  public String getShift(String note, int shift, Scale scale) {
    ArrayList<String> scaleNotes = convertStringArrayToArrayList(scale.getScale());
    int index = scaleNotes.indexOf(note);

    for(int i = 1; i < shift; i++) {
      index++;
      if(index >= scaleNotes.size()) {
        index = 0;
      }
    }

    return scaleNotes.get(index);
  }

  // for tonify, gets a single scale for a single note
  // needs work, look at interval between each note of original phrase then adjust? adjusted for major_minor
  public String[] getScale(String note, int scaleIndex) {
    // String[] ionian = getIonian(index);
    // String[] scaleChanges = scales.get(scaleIndex);
    // String[] scale = new String[7];
    //
    // for(int i = 0; i < ionian.length; i++) {
    //   scale[i] = ionian[i] + scale[i];
    // }
    //
    // return scale;

    int[] scaleSteps = major_minor.get(scaleIndex);
    int index = noteSteps.indexOf(note);
    String[] scale = new String[7];

    for(int i = 0; i < scale.length; i++) {
      index = index + scaleSteps[i];
      if(index >= noteSteps.size()) {
        index = index - noteSteps.size();
      }
      scale[i] = noteSteps.get(index);
    }

    return scale;
  }

  // for tonify, converts an array into an ArrayList
  public ArrayList<String> convertStringArrayToArrayList(String[] array) {
    ArrayList<String> arrayList = new ArrayList<>();
    for(int i = 0; i < array.length; i++) {
      arrayList.add(array[i]);
    }
    return arrayList;
  }

  // for tonify, finds the nearest note in a scale
  public Note findNearestNote(Note note, ArrayList<String> scale) {
    String letter = note.getNote();
    int letterIndex = noteSteps.indexOf(letter);

    int minDistance = 0;
    int distance = 0;
    int nearestIndex = 0;

    // check forwards
    for(int i = letterIndex; i < noteSteps.size(); i++) {
      distance++;
      if(scale.contains(noteSteps.get(i))) {
        nearestIndex = i;
        minDistance = distance;
      }
    }

    distance = 0;
    // check backwards
    for(int i = letterIndex; i > 0; i--) {
      distance++;
      if(scale.contains(noteSteps.get(i))) {
        if(distance < minDistance) {
          nearestIndex = i;
          minDistance = distance;
        }
      }
    }

    String nearestLetter = noteSteps.get(nearestIndex);
    return (new Note(nearestLetter, note.getLength()));
  }
}
