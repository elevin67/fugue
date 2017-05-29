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

public class CounterNote implements JMC {
  ArrayList<Note> counterNotes;
  Note referenceNote;

  public CounterNote(ArrayList<Note> cN, Note rN) {
    counterNotes = cN;
    referenceNote = rN;
  }

  public ArrayList<Note> getCounterNotes() {
    return counterNotes;
  }

  public Note getReferenceNote() {
    return referenceNote;
  }
}
