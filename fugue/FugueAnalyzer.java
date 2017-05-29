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

public class FugueAnalyzer extends Frame implements JMC {
    static Score s = new Score();
    int start = 0;
    int end = 0;


  public FugueAnalyzer() {
	   FileDialog fd;
     Frame f = new Frame();

     for(;;) {
       // open a MIDI file
       fd = new FileDialog(f,"Open MIDI file or choose cancel to finish.",FileDialog.LOAD);
       fd.setVisible(true);
       // break out when user presses cancel
       if(fd.getFile() == null) {
         break;
       }
       Read.midi(s,fd.getDirectory() + fd.getFile());
       s.setTitle(fd.getFile());

       // the following will need to be moved into a different function
       // got subject, need to analyze without rests
       Phrase subject = getSubject(s);
       ArrayList<Note> subjectArrayList = convertPhraseToArrayList(subject);

       ArrayList<CounterNote> counterpoint = analyzeSubjectCounterpoint(s, subjectArrayList);
     }
   }

   private Phrase getSubject(Score score) {
     double earliestStartTime = 1000000000.0;
     int startIndex = 0;
     double lastStartTime = 0.0;
     Phrase subject = new Phrase();
     Phrase firstPhrase = new Phrase();
     int index = 0;

     Enumeration enum1 = score.getPartList().elements();
     while(enum1.hasMoreElements()) {
       Part nextPt = (Part)enum1.nextElement();
       Phrase phrase1 = nextPt.getPhrase(0);
       double startTime = phrase1.getNoteStartTime(1);

       if(startTime < earliestStartTime) {
         lastStartTime = earliestStartTime;
         earliestStartTime = startTime;
         startIndex = index;
       }
       index++;
     }

     // may need to change something with lastStartTime
     subject = score.getPart(startIndex).getPhrase(0).copy(0,lastStartTime-1);
     return subject;
   }

   private ArrayList<Note> convertPhraseToArrayList(Phrase phrase) {
     ArrayList<Note> notes = new ArrayList<Note>();
     for(int i = 0; i < phrase.size(); i++) {
       notes.add(phrase.getNote(i));
     }

     while(true) {
       if(notes.get(0).isRest()) {
         notes.remove(0);
       } else if(notes.get(notes.size()-1).isRest()) {
         notes.remove(notes.size()-1);
       } else {
         break;
       }
     }
     return notes;
   }

   // going to add more than just the note to the ArrayList, will need to make new object
   private ArrayList<CounterNote> analyzeSubjectCounterpoint(Score score, ArrayList<Note> phrase) {
     ArrayList<CounterNote> counterpoint = new ArrayList<>();

     Enumeration enum1 = s.getPartList().elements();
     int partIndex = 0;
     while(enum1.hasMoreElements()) {
       Part nextPt = (Part)enum1.nextElement();
       Enumeration enum2 = nextPt.getPhraseList().elements();
       int phraseIndex = 0;
       while(enum2.hasMoreElements()) {
         Phrase nextPhr = (Phrase)enum2.nextElement();
         Enumeration enum3 = nextPhr.getNoteList().elements();
         int noteIndex = 0;
         while(enum3.hasMoreElements()) {
           Note nextNote = (Note)enum3.nextElement();
           if(nextNote.getRhythmValue() == phrase.get(0).getRhythmValue()) {
             if(containsSubject(partIndex,phraseIndex,noteIndex,score,phrase)) {
               // if it contains the subject, need to get the notes on that time interval from other parts
               counterpoint.addAll(addSubjectCounterPoint(score, start, end, phraseIndex, partIndex));
             }
           }
           noteIndex++;
         }
         phraseIndex++;
       }
       partIndex++;
     }

     return counterpoint;
   }

    private boolean containsSubject(int partIndex, int phraseIndex, int noteIndex, Score score, ArrayList<Note> subject) {
      Part part = score.getPart(partIndex);
      Phrase phrase = part.getPhrase(phraseIndex);
      int index = noteIndex + 1;
      Note prevNote = subject.get(noteIndex);
      int matches = 0;
      double threshold = subject.size() * 3.0/4.0;

      for(int i = 0; i < subject.size(); i++) {
        if(!(index >= phrase.size())) {
          Note note = phrase.getNote(index);
          Note compNote = subject.get(index);
          if(note.getRhythmValue() == compNote.getRhythmValue()) {
            matches++;
          }
          index++;
          prevNote = compNote;
        } else {
          break;
        }
      }
      if(matches >= threshold) {
        start = noteIndex;
        end = index;
        return true;
      }
      return false;
    }

    // going to add more than just the notes and referenceNote to CounterNote, will modify subject and second for loop
    private ArrayList<CounterNote> addSubjectCounterPoint(Score score, int startIndex, int endIndex, int phraseIndex, int partIndex) {
      Part subjectPart = score.getPart(partIndex);
      Phrase subjectPhrase = subjectPart.getPhrase(phraseIndex);
      double startTime = subjectPhrase.getNote(startIndex).getSampleStartTime();
      double endTime = subjectPhrase.getNote(endIndex).getSampleStartTime();
      Phrase subject = subjectPhrase.copy(startTime,endTime);
      ArrayList<Part> counterpointParts = new ArrayList<>();

      // add all the other phrases concurrent with the subject
      for(int i = 0; i < score.size(); i++) {
        if(i == partIndex) {
          continue;
        }
        Part part = score.getPart(i);
        counterpointParts.add(part.copy(startTime, endTime));
      }

      // turn this into a second function
      ArrayList<CounterNote> counterpoint = new ArrayList<>();
      // get all the counterpoints from each phrase into our data structure
      for(int i = 0; i < counterpointParts.size(); i++) {
        Part counterPart = counterpointParts.get(i);

        // loop over every note in the countersubject
        for(int j = 0; j < subject.size(); j++) {
          Note note = subject.getNote(j);
          double noteStart = note.getSampleStartTime();
          double noteEnd = noteStart + note.getRhythmValue();
          Phrase counter = counterPart.copy(noteStart,noteEnd).getPhrase(0);

          // will need to find other stuff in here to add to CounterNote, will find here

          counterpoint.add(new CounterNote(convertPhraseToArrayList(counter),note));
        }
      }

      return counterpoint;
    }

   public static void main(String[] args) {
     new FugueAnalyzer();
   }
}
