package team05.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** TestWhere */
public class TestWhere {

  @Tag("fast")
  @Test
  void filterByRule() {
    Where where = new Where(true);
    where.setRule("Esper");
    assertEquals(where.toString(), "WHERE description LIKE '%Esper%'");
  }

  @Tag("fast")
  @Test
  void noWhere() {
    Where where = new Where();
    assertEquals(where.toString(), "");
  }

  @Tag("merge")
  @Test
  void dateBuyer() {
    Where where = new Where(LocalDate.parse("2023-10-01"), LocalDate.parse("2023-12-31"));
    where.setBuyerFilter(new Buyer[] {new Buyer("Ethan")});
    assertEquals(
        where.toString(), "WHERE date >= 19631 AND date <= 19722 AND buyername IN ( 'Ethan')");
  }

  @Tag("merge")
  @Test
  void complex() {
    assertEquals(
        complexWhere().toString(),
        "WHERE date >= 19631 AND date <= 19722 AND buyername IN ( 'Sam','Martha') AND"
            + " catfilter.catname IN ( 'Entertainment','Shopping') AND description LIKE"
            + " '%Amazon%'");
  }

  @Tag("merge")
  @Test
  void showOutliers() {
    System.out.println(complexWhere().showOutliers().toString());
    assertEquals(
        complexWhere().showOutliers().toString(),
        "WHERE date >= 19631 AND date <= 19722 AND buyername IN ( 'Sam','Martha') AND"
            + " catfilter.catname IN ( 'Entertainment','Shopping') AND description LIKE '%Amazon%'"
            + " AND transactions.transactionid IN (SELECT transactionid FROM outlierFilter)");
  }

  @Tag("merge")
  @Test
  void showNonAssignedTransactions() {
    assertEquals(
        complexWhere().showUnassigned().setBuyerFilter(null).toString(),
        "WHERE date >= 19631 AND date <= 19722 AND catfilter.catname IN ("
            + " 'Entertainment','Shopping') AND description LIKE '%Amazon%' AND"
            + " transactions.transactionid NOT IN (SELECT transactionid FROM outlierFilter) AND"
            + " transactions.transactionid NOT IN (SELECT transactionid FROM rules)");
  }

  private Where complexWhere() {
    Where where = new Where(LocalDate.parse("2023-10-01"), LocalDate.parse("2023-12-31"));
    where
        .setBuyerFilter(new Buyer[] {new Buyer("Sam"), new Buyer("Martha")})
        .setCatsFilter(
            new Categories[] {
              new Categories("Placehodler", "Entertainment"),
              new Categories("Placeholder", "Shopping")
            })
        .setRule("Amazon");
    return where;
  }
}
