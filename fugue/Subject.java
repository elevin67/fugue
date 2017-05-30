package fugue;

import jm.JMC;
import jm.music.data.*;
import jm.music.tools.*;
import jm.util.*;
import java.util.Vector;
import java.awt.event.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.ArrayList;

public class Subject implements JMC {
  int start;
  int end;
  int phraseIndex;
  int partIndex;
  double startTime;
  double endTime;

  public Subject(int s, int e, int phI, int pI, double sT, double eT) {
    start = s;
    end = e;
    phraseIndex = phI;
    partIndex = pI;
    startTime = sT;
    endTime = eT;
  }

  public Subject(int s, int e, int phI, int pI) {
    start = s;
    end = e;
    phraseIndex = phI;
    partIndex = pI;
    startTime = -1;
    endTime = -1;
  }

  public int getStartIndex() {
    return start;
  }

  public int getEndIndex() {
    return end;
  }

  public int getPhraseIndex() {
    return phraseIndex;
  }

  public int getPartIndex() {
    return partIndex;
  }

  public double getStartTime() {
    return startTime;
  }

  public double getEndTime() {
    return endTime;
  }
}
