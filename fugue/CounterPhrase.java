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
import java.util.Hashtable;

public class CounterPhrase {
  Phrase phrase;
  Scale scale;
  PhraseAnalysis jmusic_analysis;
  PhraseAnalyzer phrase_analyzer;
  Hashtable data;

  // the actual phrase
  // different statistics compiled from the PhraseAnalysis class in JMusic
  public CounterPhrase(Phrase p) {
    phrase = p;
    phrase_analyzer = new PhraseAnalyzer();
    scale = phrase_analyzer.getScale(phrase_analyzer.getRawNotesInPhrase(phrase));
    data = retrieveData(phrase,scale);
  }

  public Hashtable retrieveData(Phrase phrase, Scale scale) {
    double duration = phrase.getEndTime();
    // need to convert phrase to ArrayList
    int root_pitch = scale.getRootPitch();
    int[] scale_pitches = convertIntegerToInt(phrase_analyzer.getNoteScale(root_pitch, scale.getScaleMode()));
    return jmusic_analysis.getAllStatistics(phrase,duration,root_pitch,scale_pitches);
  }

  public int[] convertIntegerToInt(Integer[] array) {
    int[] int_array = new int[array.length];
    for(int i = 0; i < array.length; i++) {
      int_array[i] = array[i].intValue();
    }
    return int_array;
  }


  @Override
  public String toString() {
    return phrase.length() + " notes in " + scale.toString();
  }
}
