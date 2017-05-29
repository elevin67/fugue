import jm.JMC;
import jm.music.data.*;
import jm.util.*;
import jm.music.tools.*;

public final class RowYourBoat implements JMC {
    public static void main(String[] args) {
	Score score = new Score("Row Your Boat");
	Part flute = new Part("Flute", FLUTE, 0);
	Part trumpet = new Part("Trumpet", TRUMPET, 1);
	Part clarinet = new Part("Clarinet", CLARINET, 2);
	int[] pitchArray = {C4,C4,C4,D4,E4,E4,D4,E4,F4,G4,C5,C5,C5,G4,G4,G4,E4,E4,E4,C4,C4,C4,G4,F4,E4,D4,C4};
	double[] rhythmArray = {C,C,CT,QT,C,CT,QT,CT, QT,M, QT, QT, QT, QT, QT,QT, QT, QT, QT, QT, QT, QT, CT, QT, CT, QT,M};
	Phrase phrase1 = new Phrase(0.0);
	phrase1.addNoteList(pitchArray, rhythmArray);
	Phrase phrase2 = phrase1.copy();
	phrase2.setStartTime(4.0);
	Phrase phrase3 = phrase1.copy();
	phrase3.setStartTime(8.0);

	Mod.transpose(phrase1, 12);
	Mod.transpose(phrase3, -12);
	
	Mod.repeat(phrase1, 1);
	Mod.repeat(phrase2, 1);
	Mod.repeat(phrase3, 1);

	flute.addPhrase(phrase1);
	trumpet.addPhrase(phrase2);
	clarinet.addPhrase(phrase3);

	score.addPart(flute);
	score.addPart(trumpet);
	score.addPart(clarinet);

	Write.midi(score, "rowboat.mid");
    }
}