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
  PhraseAnalyzer analyzer;

  public FugueAnalyzer() {
	   FileDialog fd;
     Frame f = new Frame();
     analyzer = new PhraseAnalyzer();

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
      //  System.out.println(counterpoint.size());
      //  for(int i = 0; i < counterpoint.size(); i++) {
      //    System.out.println("Reference note: " + counterpoint.get(i).getReferenceNote());
      //    System.out.println("Counterpoint: " + counterpoint.get(i).getCounterNotes());
      //  }
     }
   }

   // main functions

   // identifies the subject at the beginning of the piece
   // works
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
     subject = score.getPart(startIndex).getPhrase(0).copy(0,lastStartTime,false,false,false);

     // trim subject so that it ends on 1,3,5 of root
     Note root = new Note();
     for(int i = 0; i < subject.size(); i++) {
       if(!subject.getNote(i).isRest()) {
         root = subject.getNote(i);
         break;
       }
     }
     ArrayList<Integer> intervals = new ArrayList<>();
     intervals.add(0);
     intervals.add(3);
     intervals.add(4);
     intervals.add(7);
     while(true) {
       Note note = subject.getNote(subject.size()-1);
       int interval = analyzer.getInterval(root.getPitch(),note.getPitch());
       if(!intervals.contains(interval)) {
         subject.removeNote(subject.size() - 1);
       } else {
         break;
       }
     }

     return subject;
   }

   // analyze counterpoint per note and general counterpoint per phrase to help eliminate possibilities when looping over options?? interesting...
   // finds all the subjects within the piece, adds the subsequent counterpoint melodies to a data structure
   // going to add more than just the note to the ArrayList, will need to make new object
   private ArrayList<CounterNote> analyzeSubjectCounterpoint(Score score, ArrayList<Note> phrase) {
     ArrayList<CounterNote> counterpoint = new ArrayList<>();

     ArrayList<Subject> subjects = findSubjects(score, phrase);
     for(int i = 0; i < subjects.size(); i++) {
       Subject subject = subjects.get(i);
       counterpoint.addAll(addSubjectCounterPoint(score,subject.getStartIndex(),subject.getEndIndex(),subject.getPhraseIndex(), subject.getPartIndex()));
     }

     return counterpoint;
   }

   // functions for analyzeSubjectCounterPoint

   // finds all the subjects in the score
   // could use a bad suffix table??
   public ArrayList<Subject> findSubjects(Score score, ArrayList<Note> subject) {
     ArrayList<Subject> subjects = new ArrayList<>();

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
           if(nextNote.getRhythmValue() == subject.get(0).getRhythmValue()) {
             if(containsSubject(partIndex,phraseIndex,noteIndex,score,subject)) {
               subjects.add(new Subject(start, end, phraseIndex, partIndex));
             }
           }
           noteIndex++;
         }
         phraseIndex++;
       }
       partIndex++;
     }

     return subjects;
   }

   // used by findSubjects
   // checks if a note is the beginning of a subject in the piece, must pass a "threshold" because every subject won't have identical rhythms
   // add a check for consistent intervals as well, use similar "threshold"
   private boolean containsSubject(int partIndex, int phraseIndex, int noteIndex, Score score, ArrayList<Note> subject) {
     Part part = score.getPart(partIndex);
     Phrase phrase = part.getPhrase(phraseIndex);
     int index = noteIndex + 1;
     Note prevNote = phrase.getNote(noteIndex);
     int matches = 0;
     int pitchMatch = 0;
     int interval = analyzer.getInterval(subject.get(0).getPitch(),prevNote.getPitch());
     double threshold = subject.size() * 8.0/10.0;
     double pitchThreshold = subject.size() * 0.5;

     for(int i = 1; i < subject.size(); i++) {
       if(!(index >= phrase.size())) {
         Note note = phrase.getNote(index);
         Note compNote = subject.get(i);
         if(note.getRhythmValue() == compNote.getRhythmValue()) {
           matches++;
         }
         if(analyzer.getInterval(compNote.getPitch(),note.getPitch()) == interval) {
           pitchMatch++;
         }
         index++;
         prevNote = compNote;
       } else {
         break;
       }
     }
     if(matches >= threshold && pitchMatch >= threshold) {
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
      double startTime = subjectPhrase.getNoteStartTime(startIndex);
      if(endIndex >= subjectPhrase.size() - 1) {
        endIndex = subjectPhrase.size() - 1;
      }
      double endTime = subjectPhrase.getNoteStartTime(endIndex);
      Phrase subject = subjectPhrase.copy(startTime,endTime,false,false,true);
      ArrayList<Part> counterpointParts = new ArrayList<>();

      // add all the other phrases concurrent with the subject
      for(int i = 0; i < score.size(); i++) {
        if(i == partIndex) {
          continue;
        }
        Part part = score.getPart(i);
        counterpointParts.add(part.copy(startTime, endTime,false,false,false));
      }

      // Score sScore = new Score();
      // Part sPart = new Part();
      // sPart.addPhrase(subject);
      // sScore.addPart(sPart);
      // Write.midi(sScore,"subject.mid");
      System.out.println("Subject");
//      System.out.println("Subject length: " + subject.getEndTime()+"\n\n\n\n");
      System.out.println(subject+"\n\n\n");


      // for(int i = 0; i < counterpointParts.size(); i++) {
      //   Score partScore = new Score();
      //   partScore.addPart(counterpointParts.get(i));
      //   Write.midi(partScore,"subject"+i+".mid");
      //   System.out.println("Counterpoint "+i);
      //   System.out.println("length: "+counterpointParts.get(i).getEndTime()+"\n\n\n\n");
      //   System.out.println(counterpointParts.get(i));
      // }



      ArrayList<CounterNote> counterpoint = getCounterpointPerNote(counterpointParts,subject);
      System.out.println("Counterpoint");
      System.out.println(counterpoint.size());
      for(int i = 0; i < counterpoint.size(); i++) {
        // System.out.println("Reference note: " + counterpoint.get(i).getReferenceNote());
        System.out.println("Counterpoint: " + counterpoint.get(i).getCounterNotes());
      }
      System.out.println("\n\n\n\n");

      return counterpoint;
    }

    // for addSubjectCounterPoint
    // this is where you get all the data for the CounterNote object
    // gets the counterpoint associated with each note in the subject, returns an ArrayList of CounterNotes
    private ArrayList<CounterNote> getCounterpointPerNote(ArrayList<ArrayList<Note>> counterpointParts, Phrase subject) {
      // convert counterPointParts into an arrayList of notes
      ArrayList<ArrayList<Note>> counterpointArrayList = convertPartsToArrayLists(counterpointParts);
      ArrayList<ArrayList<Note>> counterpointPhrases = convertPartsToArrayLists(counterpointParts);

      ArrayList<CounterNote> counterpoint = new ArrayList<>();
      // get all the counterpoints from each phrase into our data structure
      for(int i = 0; i < counterpointArrayList.size(); i++) {
        ArrayList<Note> counterPart = counterpointArrayList.get(i);
        double overflow = 0.0;

        // loop over every note in the countersubject
        // need to get the note start and end for each note in subject, may need new object
        for(int j = 0; j < subject.size(); j++) {
          Note note = subject.getNote(j);
          double rhythmValue = note.getRhythmValue();
          Phrase notes = new Phrase();
          double rhythmCount = 0.0;
          overflow = 0.0;

          while(counterPart.size() > 0) {
            rhythmCount += counterPart.get(0).getRhythmValue();
            if(rhythmCount > rhythmValue) {
              overflow = rhythmCount - rhythmValue;
              Note overflowNote = counterPart.get(0);
              if(!overflowNote.isRest()) {
                notes.add(new Note(overflowNote.getPitch(),rhythmValue));
              }
              counterPart.remove(0);
              counterPart.add(0, new Note(overflowNote.getPitch(), overflowNote.getRhythmValue() - rhythmValue));
              break;
            } else if(rhythmCount == rhythmValue) {
              if(!counterPart.get(0).isRest()) {
                notes.add(counterPart.get(0));
              }
              counterPart.remove(0);
              break;
            } else {
              if(!counterPart.get(0).isRest() && !(counterPart.get(0).getRhythmValue() == 0.0)) {
                notes.add(counterPart.get(0));
              }
              counterPart.remove(0);
            }
          }


          // will need to find other stuff in here to add to CounterNote, will find here
          if(notes.size() > 0) {
            counterpoint.add(new CounterNote(convertPhraseToArrayList(notes), note));
            if(counterpoint.size() >= 1) {
              counterpoint.add(analyzer.analyzeNoteInPhrase(subject,j,convertPhraseToArrayList(notes),counterpoint.get(counterpoint.size() - 1)));
            } else {
              counterpoint.add(analyzer.analyzeNoteInPhrase(subject,j,convertPhraseToArrayList(notes),null));
            }

          }
        }
      }

      return counterpoint;
    }

    // utility functions

    // convert an ArrayList of parts to an ArrayList of ArrayLists of notes
    public ArrayList<ArrayList<Note>> convertPartsToArrayLists(ArrayList<Part> parts) {
      ArrayList<ArrayList<Note>> counterpointArrayList = new ArrayList<>();
      for(int i = 0; i < counterpointParts.size(); i++) {
        if(counterpointParts.get(i).size() > 0) {
          counterpointArrayList.add(convertPartToArrayList(counterpointParts.get(i)));
        }
      }
    }

    // converts a Phrase object into an ArrayList of notes, gets rid of rests at beginning and end
    // works
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
      return cleanNoteArrayList(notes);
    }

    // converts a Part object to an ArrayList of notes, gets rid of rests at beginning and end
    // works
    private ArrayList<Note> convertPartToArrayList(Part part) {
      ArrayList<Note> notes = new ArrayList<>();
      for(int i = 0; i < part.size(); i++) {
        Phrase phrase = part.getPhrase(i);
        for(int j = 0; j < phrase.size(); j++) {
          notes.add(phrase.getNote(j));
        }
      }

      // while(true) {
      //   if(notes.get(0).isRest()) {
      //     notes.remove(0);
      //   } else if(notes.get(notes.size()-1).isRest()) {
      //     notes.remove(notes.size()-1);
      //   } else {
      //     break;
      //   }
      // }

      return cleanNoteArrayList(notes);
    }

    // cleans an array list of all troublesome rhythms or rests
    private ArrayList<Note> cleanNoteArrayList(ArrayList<Note> notes) {
      for(int i = 0; i < notes.size(); i++) {
        Note note = notes.get(i);
        double rhythm = note.getRhythmValue();
        if(rhythm % .25 != 0) {
          Double newRhythm = scrubNote(note);
          notes.set(i, new Note(note.getPitch(),newRhythm));
        }
      }

      return notes;
    }

    // cleans up an individual note
    private double scrubNote(Note note) {
      double rhythm = note.getRhythmValue();
      double rem = rhythm % .25;
      double newRhythm = 0;
      if(rem != 0) {
        double lower = rhythm - rem;
        double upper = lower + .25;
        double lowInt = Math.abs(lower - rhythm);
        double highInt = Math.abs(upper - rhythm);
        if(highInt < lowInt){
          newRhythm = upper;
        } else {
          newRhythm = lower;
        }
      }

      return newRhythm;
    }

   public static void main(String[] args) {
     new FugueAnalyzer();
   }
}
