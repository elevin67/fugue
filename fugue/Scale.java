package fugue;

import java.util.ArrayList;

public class Scale {
  int rootPitch;
  int scaleMode;
  ArrayList<String> scaleNames;
  PhraseAnalyzer phraseAnalyzer;

  public Scale(int rP, int sM) {
    rootPitch = rP;
    scaleMode = sM;
    scaleNames = new ArrayList<>();
    scaleNames.add("MAJOR");
    scaleNames.add("MINOR");
    phraseAnalyzer = new PhraseAnalyzer();
  }

  public int getRootPitch() {
    return phraseAnalyzer.getRawNote(rootPitch);
  }

  public int getScaleMode() {
    return scaleMode;
  }

  @Override
  public String toString() {
    return phraseAnalyzer.getLetterFromPitchValue(rootPitch) + " " + scaleNames.get(scaleMode);
  }
}
