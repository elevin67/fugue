package fugue;

import java.util.ArrayList;

public class Scale {
  int scale;
  int matches;
  String rootNote;
  int rootIndex;

  public Scale(int s, int m, String r, int ri) {
    scale = s;
    matches = m;
    rootNote = r;
    rootIndex = ri;
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

  public int getRootIndex() {
    return rootIndex;
  }
}
