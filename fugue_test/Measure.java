package fugue_test;

import java.util.ArrayList;

public class Measure {
  Scale scale;
  ArrayList<Note> notes;

  public Measure(Scale s, ArrayList<Note> n) {
    scale = s;
    notes = n;
  }

  public Scale getMode() {
    return scale;
  }

  public ArrayList<Note> getNotes() {
    return notes;
  }

  @Override
  public String toString() {
    return notes.size() + " notes in " + scale.toString();
  }
}
