import jm.JMC;
import jm.util.*;
import jm.music.data.*;

// a short example which generates a melody's retrograde and inversion

public final class Directions implements JMC {
    
    public static void main(String[] args) {
	Phrase melody = new Phrase("Original");
	for(int i = 0; i < 12; i++) {
	    Note n = new Note(C4 + (int)(Math.random()*18), QUAVER);
	    melody.addNote(n);
	}

	Score melScore = new Score();
	Part melPart = new Part();
	melPart.addPhrase(melody);
	melScore.addPart(melPart);

	View.show(melScore);

	// reverse the phrase
	Phrase backwards = new Phrase("Retrograde");
	for(int i = melody.size(); i > 0; i--) {
	    backwards.addNote(melody.getNote(i-1));
	}
	
	Score backScore = new Score();
	Part backPart = new Part();
	backPart.addPhrase(backwards);
	backScore.addPart(backPart);

	View.show(backScore, 50,20);

	// invert the melody
	// Make a copy of the melody
	Phrase flip = melody.copy();
	flip.setTitle("Inversion");
	// get the first pitch
	int firstNote = flip.getNote(0).getPitch();
	for(int i = 0; i < flip.size(); i++) {
	    // change the pitch to invert each around the first note
	    flip.getNote(i).setPitch(firstNote - (melody.getNote(i).getPitch() - firstNote));
	}

	Score flipScore = new Score();
	Part flipPart = new Part();
	flipPart.addPhrase(flip);
	flipScore.addPart(flipPart);

	View.show(flipScore, 100,40);
    }
}
