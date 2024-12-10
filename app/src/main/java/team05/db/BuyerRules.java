package team05.db;

import javafx.beans.property.SimpleStringProperty;
import team05.fft.*;

// Author hypnotics-dev devhypnotics@proton.me
/** BuyerRules */
public class BuyerRules implements Table {

  private String regex;
  private int fpk;
  private String name;

  public BuyerRules(String regex, Buyer fpk) {
    this.regex = DB.toRegex(regex);
    this.fpk = fpk.getPk();
  }

  public BuyerRules(String regex, int fpk) {
    this.regex = DB.toRegex(regex);
    this.fpk = fpk;
  }

  public BuyerRules(String regex, String name) {
    this.regex = DB.toRegex(regex);
    this.name = name;
  }

  public String getPk() {
    return regex;
  }

  public int getBuyer() {
    return fpk;
  }

  public SimpleStringProperty ruleProperty() {
    return new SimpleStringProperty(regex);
  }

  public SimpleStringProperty nameProperty() {
    return new SimpleStringProperty(name);
  }

  @Override
  public String toValue() {
    return String.format("( %s, %s)", regex, fpk);
  }
}
