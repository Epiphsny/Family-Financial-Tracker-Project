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
/** TestOutlierDB */
public class TestOutlierDB {

  @Test
  @BeforeEach
  void init() {
    File db = DBTest.getTestFile(DBTest.DBNAME);
    DB.createTables(DBTest.DBNAME);
    Buyer[] arr =
        new Buyer[] {
          new Buyer("andrewfastow"), new Buyer("rebeccamark"), new Buyer("cliffordbaxter")
        };
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
    TransactionDB tdb = new TransactionDB(db.getName());
    BuyerDB bdb = new BuyerDB(db.getName());
    OutlierDB odb = new OutlierDB(db.getName());
    bdb.newRows(arr);
    tdb.newRows(row);

    arr = bdb.query("buyerid > 0");
    row = tdb.query("transactionid > 0");

    Outlier[] out =
        new Outlier[] {
          new Outlier(row[0], arr[2]), new Outlier(row[1], arr[1]), new Outlier(row[2], arr[0]),
        };

    odb.newRows(out);

    tdb.close();
    bdb.close();
    odb.close();
  }

  @Tag("fast")
  @Test
  void add() {
    OutlierDB db = new OutlierDB(DBTest.DBNAME);
    BuyerDB bdb = new BuyerDB(DBTest.DBNAME);
    Outlier[] query = db.query("buyerid > 0");
    Buyer[] buyers = bdb.query("buyerid = " + query[0].getBuyer());
    assertEquals(buyers[0].toString(), "cliffordbaxter");
    db.close();
    bdb.close();
  }

  @Tag("merge")
  @Test
  void rm() {
    OutlierDB db = new OutlierDB(DBTest.DBNAME);
    TransactionDB tdb = new TransactionDB(DBTest.DBNAME);
    db.removeRow(tdb.query("transactionid > 1"));
    assertEquals(db.query("buyerid > 0").length, 1);
  }
}
