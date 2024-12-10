package team05.db;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

// Author hypnotics-dev devhypnotics@proton.me
/** TestBuyerDB */
public class TestBuyerDB {

  @Test
  @BeforeEach
  void init() {
    File db = DBTest.getTestFile("test.db");
    Buyer[] arr =
        new Buyer[] {
          new Buyer("kennethlay"),
          new Buyer("jeffreyskilling"),
          new Buyer("andrewfastow"),
          new Buyer("rebeccamark"),
          new Buyer("cliffordbaxter")
        };
    BuyerDB buyers = new BuyerDB(db.getName());
    buyers.newRows(arr);
    buyers.close();
  }

  @Tag("fast")
  @Test
  void add() {
    BuyerDB db = new BuyerDB("test.db");
    Buyer[] buyers = db.query("buyername = 'kennethlay'");
    assertEquals(buyers[0].toString(), "kennethlay");
    buyers = db.query("buyername = 'rebeccamark'");
    assertEquals(buyers[0].toString(), "rebeccamark");
    db.close();
  }

  @Tag("merge")
  @Test
  void rm() {
    BuyerDB db = new BuyerDB("test.db");
    db.removeRow(db.query("buyername LIKE '%n%'"));
    assertEquals(db.query("buyerid>0").length, 2);
  }
}
