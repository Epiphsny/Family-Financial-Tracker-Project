package team05.db;

import javafx.beans.property.SimpleStringProperty;
import team05.fft.*;

// Author hypnotics-dev devhypnotics@proton.me
/** Categories */
public class Categories implements Table {

  String regex;
  String name;

  public Categories(String regex, String name) {
    this.name = name;
    this.regex = DB.toRegex(regex);
  }

  public String getPk() {
    return regex;
  }

  public String getName() {
    return name;
  }

  public SimpleStringProperty ruleProperty() {
    return new SimpleStringProperty(regex);
  }

  public SimpleStringProperty nameProperty() {
    return new SimpleStringProperty(name);
  }

  @Override
  public String toValue() {
    return "(" + name + ")";
  }
}
