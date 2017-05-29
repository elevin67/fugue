package fugue_test;

import java.util.ArrayList;

public class Scale {
  String[] scale;
  int scaleType;
  int matches;
  String rootNote;
  int rootIndex;

  public Scale(String[] s, int st, int m, String r, int ri) {
    scale = s;
    scaleType = st;
    matches = m;
    rootNote = r;
    rootIndex = ri;
  }

  public Scale(String[] s, int st, String r, int ri) {
    scale = s;
    scaleType = st;
    matches = -1;
    rootNote = r;
    rootIndex = ri;
  }

  public String[] getScale() {
    return scale;
  }

  public int getScaleType() {
    return scaleType;
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

  @Override
  public String toString() {
    String scaleName = "";
    if(scaleType == 0) {
      scaleName = "Major";
    } else if(scaleType == 1) {
      scaleName = "Minor";
    }

    return rootNote + " " + scaleName;
  }
}
