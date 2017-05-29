package fugue_test;

import java.util.ArrayList;

public class Counterpoint {

  /*
  * CONSONANCE VS. DISSONANCE
  * consonance: unisons, octaves, 3rds, 5ths, 6ths
  * dissonance: 2nds, 7ths, major 2nd and minor 7th are soft, minor 2nd and major 7th are harsh
  * 4ths and tritones can be either depending on situation
  * may need to create both consonant and dissonant of each type of motion for each species
  * Balancing intervals:
  * 3rd and 6ths (steak)
  * 5ths and octaves (potatoes)
  * dissonance (salt and pepper)
  *
  * TYPES OF MOTION
  * parallel motion: go same direction in a scale
  * contrary motion: go in opposite directions in a scale
  * oblique motion: one voice moves while the other stays constant
  * can produce a parallel, contrary, and oblique line?
  *
  * FIVE SPECIES OF COUNTERPOINT
  * First: a single note against another single note
  * Second: two notes against one
  * Third: four notes against one
  * Fourth: all about suspensions and syncopations?
  * Fifth: florid counterpoint? combination of the first four
  * Make methods for all of these?
  *
  * AVOID
  * parallel and direct 5ths and 8ths?
  *
  * FOR MORE THAN 2 PARTS:
  * Focus on top and bottom lines
  *
  * FUTURE WORK
  * create a genetic algorithm that takes composer's work,
  * learns taste for three variables in different situations
  */

  ArrayList<Note> melody;
  boolean dissonant;
  // 0 for parallel, 1 for contrary, 2 for oblique
  int motion;
  // 0-4 species of Counterpoint
  int species;
  ArrayList<Note> counterpointMelody;

  public Counterpoint(ArrayList<Note> p, boolean d, int m, int s) {
    melody = p;
    dissonant = d;
    motion = m;
    s = species;

    counterpointMelody = generateCounterpointMelody(melody);
  }

  public ArrayList<Note> generateCounterpointMelody(ArrayList<Note> phrase) {
    ArrayList<Note> possibleNotes = new ArrayList<>();

    for(int i = 0; i < phrase.size(); i++) {
      ArrayList<Note> speciesNotes = getSpecies(phrase.get(i));
      possibleNotes = addNotesToPhrase(phrase, speciesNotes);
    }

    return null;
  }

  public ArrayList<Note> addNotesToPhrase(ArrayList<Note> phrase, ArrayList<Note> add) {
    for(int i = 0; i < add.size(); i++) {
      phrase.add(add.get(i));
    }

    return phrase;
  }

  public ArrayList<Note> getSpecies(Note note) {
    return null;
  }

  // gets all the consonant notes associated with that melody, broken down by note? maybe?
  public ArrayList<Note> getConsonant(ArrayList<Note> phrase) {
    return null;
  }

  // gets all the dissonant notes associated with that melody, broken down by note? maybe?
  public ArrayList<Note> getDissonant(ArrayList<Note> phrase) {
    return null;
  }

}
