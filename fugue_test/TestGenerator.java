package fugue_test;

import java.util.ArrayList;

public class TestGenerator {
  public TestGenerator() {

  }

  public ArrayList<Note> makeThemeForBachFugueInCMinor() {
    ArrayList<Note> fugue = new ArrayList<>();
    fugue.add(new Note("C",.25));
    fugue.add(new Note("B",.25));
    fugue.add(new Note("C",.5));
    fugue.add(new Note("G",.5));
    fugue.add(new Note("A-",.5));
    fugue.add(new Note("C",.25));
    fugue.add(new Note("B",.25));
    fugue.add(new Note("C",.5));
    fugue.add(new Note("D",.5));
    fugue.add(new Note("G",.5));
    fugue.add(new Note("C",.25));
    fugue.add(new Note("B",.25));
    fugue.add(new Note("C",.5));
    fugue.add(new Note("D",.5));
    fugue.add(new Note("F",.25));
    fugue.add(new Note("G",.25));
    fugue.add(new Note("A-",.5));
    fugue.add(new Note("G",.25));
    fugue.add(new Note("F",.25));
    fugue.add(new Note("E-",.5));

    return fugue;
  }

  public static void main(String[] args) {
    Generator generator = new Generator();
    TestGenerator tester = new TestGenerator();
    ArrayList<Note> phrase = tester.makeThemeForBachFugueInCMinor();

    // test break by measure
    ArrayList<ArrayList<Note>> phraseMeasures = generator.breakByMeasure(phrase,4);
    System.out.println("Should be 2 measures: " + phraseMeasures.size() + " measures");

    // test parseMeasure
    Measure measure1 = generator.parseMeasure(phraseMeasures.get(0));
    System.out.println(measure1);

    // test translate, tonify
    Measure translatedMeasure1 = generator.translate(measure1, 5, true);
    System.out.println(translatedMeasure1);
  }
}
