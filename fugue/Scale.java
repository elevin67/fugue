package fugue;

import java.util.ArrayList;

public class Scale {
  int scale;
  int matches;
  String rootNote;

  public Scale(int s, int m, String r) {
    scale = s;
    matches = m;
    rootNote = r;
  }

  public int getScale() {
    return scale;
  }

  public int getNumMatches() {
    return matches;
  }

  public String getRoot() {
    return rootNote;
  }
}
