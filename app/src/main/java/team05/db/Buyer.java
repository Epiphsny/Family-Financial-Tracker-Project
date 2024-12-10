package team05.db;

import javafx.beans.property.SimpleStringProperty;
import team05.fft.*;

// Author hypnotics-dev devhypnotics@proton.me
/** Buyer */
public class Buyer implements Table {

  private String name;
  private int id;

  public Buyer(String name) {
    this.name = name;
    id = -1;
  }

  public Buyer(int id, String name) {
    this.name = name;
    this.id = id;
  }

  @Override
  public String toValue() {
    return "(" + name + ")";
  }

  @Override
  public String toString() {
    return name;
  }

  public SimpleStringProperty nameProperty() {
    return new SimpleStringProperty(name);
  }

  public int getPk() {
    if (id < 0) {
      Os.logger("WARNING: Returning invalid PK for Buyer: " + name);
    }
    return id;
  }
}
