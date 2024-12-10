package team05.fft;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import team05.db.TransactionDB;

// Author hypnotics-dev devhypnotics@proton.me
// Author Justin Babineau jbabine1
public class Transaction implements Table {

  private final int ID;
  private final String BUYER;
  private final ArrayList<String> CATEGORY;
  private final long EPOCH; // EPOCH in UNIX seconds
  private final double VAL;
  private final double BAL;
  private final String DESC;
  private final int fileId;

  /**
   * Creates Transaction for reading by {@link TransactionDB}
   *
   * @param date The day the {@link Transaction} took place
   * @param desc The description of the {@link Transaction}
   * @param val The change made to balance by the {@link Transaction}
   * @param bal The current balance of the account
   */
  public Transaction(
      final LocalDate date,
      final String desc,
      final double val,
      final double bal,
      final int fileId) {
    EPOCH = date.toEpochDay();
    this.DESC = desc;
    this.VAL = val;
    this.BAL = bal;
    this.ID = -1;
    this.BUYER = "";
    CATEGORY = null;
    this.fileId = fileId;
  }

  /**
   * Creates Transaction for reading by {@link TransactionDB}
   *
   * @param date The day the {@link Transaction} took place in epoch time
   * @param desc The description of the {@link Transaction}
   * @param val The change made to balance by the {@link Transaction}
   * @param bal The current balance of the account
   */
  public Transaction(final long date, final String desc, final double val, final double bal) {
    EPOCH = date;
    this.DESC = desc;
    this.VAL = val;
    this.BAL = bal;
    this.ID = -1;
    this.BUYER = "";
    this.CATEGORY = null;
    fileId = -1;
  }

  public Transaction(
      final int id, final long date, final String desc, final double val, final double bal) {
    EPOCH = date;
    this.DESC = desc;
    this.VAL = val;
    this.BAL = bal;
    this.ID = id;
    this.BUYER = "";
    this.CATEGORY = null;
    fileId = -1;
  }

  /**
   * Creates Transaction for reading by a JavaFX UI
   *
   * @param epoch The day the {@link Transaction} took place
   * @param id The {@link Transaction}s ID found in {@link TransactionDB}
   * @param buyer The buyer ID found in TODO: Name of Buyer DB goes here
   * @param category The category the {@link Transaction} belongs to
   * @param desc The description of the {@link Transaction}
   * @param val The change made to balance by the {@link Transaction}
   * @param bal The current balance of the account
   */
  public Transaction(
      final long epoch,
      final int id,
      final String buyer,
      final String category,
      final double val,
      final double bal,
      final String desc) {
    this.ID = id;
    this.EPOCH = epoch;
    this.BUYER = buyer;
    this.CATEGORY = new ArrayList<>();
    CATEGORY.add(category);
    this.VAL = val;
    this.BAL = bal;
    this.DESC = desc;
    fileId = -1;
  }

  public int getPk() {
    return ID;
  }

  public String getBuyer() {
    return BUYER;
  }

  public String getCategory() {
    StringBuilder str = new StringBuilder();
    if (CATEGORY.isEmpty()) return str.toString();
    if (CATEGORY.size() > 1) {
      for (String string : CATEGORY) {
        str.append(string);
        str.append(" , ");
      }
      str.replace(str.length() - 3, str.length() - 1, "");
      return str.toString();
    }
    return CATEGORY.getFirst();
  }

  public long getDate() {
    return EPOCH;
  }

  public double getVal() {
    return VAL;
  }

  public double getBal() {
    return BAL;
  }

  public String getDesc() {
    return DESC;
  }

  public int getFile() {
    return fileId;
  }

  public void addCategory(String cat) {
    CATEGORY.add(cat);
  }

  public SimpleStringProperty dateProperty() {
    return new SimpleStringProperty(
        Instant.ofEpochSecond(EPOCH * 60 * 60 * 24)
            .atZone(ZoneId.of("Z"))
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
  }

  public SimpleStringProperty descProperty() {
    return new SimpleStringProperty(DESC);
  }

  public SimpleDoubleProperty valProperty() {
    return new SimpleDoubleProperty(VAL);
  }

  public SimpleDoubleProperty balProperty() {
    return new SimpleDoubleProperty(BAL);
  }

  public SimpleStringProperty catProperty() {
    StringBuilder str = new StringBuilder();
    if (CATEGORY.isEmpty()) return new SimpleStringProperty("");
    if (CATEGORY.size() > 1) {
      for (String string : CATEGORY) {
        str.append(string);
        str.append(" , ");
      }
      str.replace(str.length() - 3, str.length() - 1, "");
      return new SimpleStringProperty(str.toString());
    }
    return new SimpleStringProperty(CATEGORY.getFirst());
  }

  public SimpleStringProperty buyProperty() {
    return new SimpleStringProperty(BUYER);
  }

  @Override
  public String toValue() {
    return String.format("(%d %s %.2f %.2f)", EPOCH, DESC, VAL, BAL);
  }

  @Override
  public String toString() {
    return String.format("%d %s %.2f %.2f", EPOCH, DESC, VAL, BAL);
  }
}
