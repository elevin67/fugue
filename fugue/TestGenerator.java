package fugue;

import java.util.ArrayList;

public class TestGenerator {
  public TestGenerator() {

  }

  public static void main(String[] args) {
    Generator generator = new Generator();
    ArrayList<Note> phrase = new ArrayList<>();
    phrase.add(new Note("A",1));
    phrase.add(new Note("B",1));
    phrase.add(new Note("C",1));
    phrase.add(new Note("D",1));
    phrase.add(new Note("E",1));
    phrase.add(new Note("F",1));
    phrase.add(new Note("G",1));
    phrase.add(new Note("A",1));

    // test break by measure
    ArrayList<ArrayList<Note>> phraseMeasures = generator.breakByMeasure(phrase,4);
    System.out.println("Should be 2 measures: " + phraseMeasures.size() + " measures");

    // test parseMeasure
    Scale firstMeasureScale = generator.parseMeasure(phraseMeasures.get(0));
    System.out.println("Should be ionian(0): " + firstMeasureScale.getScale());
  }
}
