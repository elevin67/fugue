package fugue;

import java.util.ArrayList;

public class Note {
  String note;
  double length;

  public Note(String n, double l) {
    note = n;
    length = l;
  }

  public String getNote() {
    return note;
  }

  public double getLength() {
    return length;
  }

  public String noteToString(String note) {
    if(note.length() == 2) {
      if(note.substring(1) == "+") {
        return note.substring(0) + "#";
      } else if(note.substring(1) == "-") {
        return note.substring(0) + "b";
      }
    }
    return note;
  }

  public String lengthToString(double length) {
    if(length == .25) {
      return "sixteenth";
    } else if(length == .5) {
      return "eigth";
    } else if(length == 1.0) {
      return "quarter";
    } else if(length == 2.0) {
      return "half";
    } else if(length == 4.0) {
      return "whole";
    }
    
    return "";
  }
}
