package fugue;

import java.util.ArrayList;

public class Scale {
  int rootPitch;
  int scaleMode;
  ArrayList<String> scaleNames;

  public Scale(int rP, int sM) {
    rootPitch = rP;
    scaleMode = sM;
    scaleNames = new ArrayList<>();
    scaleNames.add("MAJOR");
    scaleNames.add("MINOR");
  }

  public int getRootPitch() {
    return rootPitch;
  }

  public int getScaleMode() {
    return scaleMode;
  }

  @Override
  public String toString() {
    return rootPitch + " " + scaleNames.get(scaleMode);
  }
}
