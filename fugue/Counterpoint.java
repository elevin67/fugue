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

public class Counterpoint {
  // an object that holds a CounterPhrase and all it's subsequent CounterNotes?
  // may not actually be necessary when I think about it. We'll talk.
  CounterPhrase counter_phrase;
  ArrayList<CounterNote> counter_notes;

  public Counterpoint(CounterPhrase cP, ArrayList<CounterNote> cN) {
    counter_phrase = cP;
    counter_notes = cN;
  }

  public CounterPhrase getCounterPhrase() {
    return counter_phrase;
  }

  public ArrayList<CounterNote> getCounterNotes() {
    return counter_notes;
  }

  @Override
  public String toString() {
    return counter_phrase.toString() + " with " + counter_notes.size() + " counterpoint notes";
  }
}
