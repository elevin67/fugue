package fugue;

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
}
