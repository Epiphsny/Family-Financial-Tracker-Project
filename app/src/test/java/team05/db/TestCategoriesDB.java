package team05.db;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

// Author hypnotics-dev devhypnotics@proton.me
/** TestCategoriesDB */
public class TestCategoriesDB {

  @Test
  @BeforeEach
  void init() {
    File db = DBTest.getTestFile("test.db");
    Categories[] cats =
        new Categories[] {
          new Categories("dex's diner", "resteraunts"),
          new Categories("passione", "resteraunts"),
          new Categories("arrakis", "spice"),
        };

    CategoryDB cat = new CategoryDB(db.getName());
    cat.newRows(cats);
    cat.close();
  }

  @Tag("fast")
  @Test
  void add() {
    CategoryDB db = new CategoryDB("test.db");
    Categories[] cats = db.query("catname = 'spice'");
    assertEquals(cats[0].getPk(), "%arrakis%");
  }

  @Tag("merge")
  @Test
  void rm() {
    CategoryDB db = new CategoryDB("test.db");
    db.removeRow(db.query("catname = 'resteraunts'"));
    assertEquals(db.query("catname LIKE '%'").length, 1);
  }
}
