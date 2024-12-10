package team05.db;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

// Author hypnotics-dev devhypnotics@proton.me
/** TestBuyerRulesDB */
public class TestBuyerRulesDB {

  @Test
  @BeforeEach
  void init() {
    File db = DBTest.getTestFile("test.db");
    Buyer[] bar =
        new Buyer[] {
          new Buyer(1, "kennethlay"), new Buyer(2, "jeffreyskilling"), new Buyer(3, "andrewfastow")
        };

    BuyerRules[] arr =
        new BuyerRules[] {
          new BuyerRules("enron", bar[0]),
          new BuyerRules("bush", bar[0]),
          new BuyerRules("lehman", bar[1]),
          new BuyerRules("agi", bar[1]),
          new BuyerRules("california electricity", bar[2])
        };

    BuyerDB buyers = new BuyerDB(db.getName());
    BuyerRulesDB rules = new BuyerRulesDB(db.getName());
    buyers.newRows(bar);
    rules.newRows(arr);

    buyers.close();
    rules.close();
  }

  @Tag("fast")
  @Test
  void add() {
    BuyerRulesDB db = new BuyerRulesDB("test.db");
    BuyerRules[] rules = db.query("buyerid = 1");
    Buyer kay = new Buyer(1, "kennethlay");
    BuyerRules[] arr = new BuyerRules[] {new BuyerRules("enron", kay), new BuyerRules("bush", kay)};
    for (int i = 0; i < rules.length; i++) {
      assertEquals(arr[i].getBuyer(), rules[i].getBuyer());
    }
    db.close();
  }

  @Tag("merge")
  @Test
  void rm() {
    BuyerRulesDB db = new BuyerRulesDB("test.db");
    db.removeRow(db.query("buyerid > 1"));
    assertEquals(db.query("buyerid > 0").length, 2);
  }
}
