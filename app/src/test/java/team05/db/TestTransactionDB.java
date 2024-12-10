package team05.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import team05.fft.Os;
import team05.fft.Transaction;

// Author hypnotics-dev devhypnotics@proton.me
/** TestTransactionDB */
public class TestTransactionDB {

  @Test
  @BeforeEach
  void init() {

    File db = DBTest.getTestFile(DBTest.DBNAME);
    DB.createTables(DBTest.DBNAME);
    File file = new File("foo.txt");
    int id = FileDB.addFile(file, Os.getDbPath(DBTest.DBNAME));
    Transaction[] row =
        new Transaction[] {
          new Transaction(
              LocalDate.ofInstant(Instant.ofEpochSecond(1731689943 * 24 * 60), ZoneId.of("Z")),
              "Spice shipment #117",
              200,
              19234,
              id),
          new Transaction(
              LocalDate.ofInstant(Instant.ofEpochSecond(1731430743 * 24 * 60), ZoneId.of("Z")),
              "Dex's Diner Order #873",
              123,
              19357,
              id),
          new Transaction(
              LocalDate.ofInstant(Instant.ofEpochSecond(1730566743 * 24 * 60), ZoneId.of("Z")),
              "Enron Returns",
              -12000,
              7357,
              id),
        };

    TransactionDB bank = new TransactionDB(db.getName());
    bank.newRows(row);
    bank.close();
  }

  @Tag("fast")
  @Test
  void add() {
    TransactionDB db = new TransactionDB(DBTest.DBNAME);
    Transaction[] stmt = db.query("value > 0");
    assertEquals(stmt[0].getDesc(), "Spice shipment #117"); // Fails Array out of bounds error
    assertEquals(stmt[1].getDesc(), "Dex's Diner Order #873");
  }

  @Tag("merge")
  @Test
  void rm() {
    TransactionDB db = new TransactionDB(DBTest.DBNAME);
    db.removeRow(db.query("balance > 10000"));
    assertEquals(db.query("balance > 0").length, 1); // Fails foreign key constraint from sql
  }
}
