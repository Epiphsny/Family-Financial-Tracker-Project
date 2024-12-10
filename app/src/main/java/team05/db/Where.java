package team05.db;

import java.time.LocalDate;

/** Where */
public class Where {

  private LocalDate start;
  private LocalDate end;
  private String rule;
  private Buyer[] buyer;
  private Categories[] cats;
  private Integer[] files;
  private Boolean showOutlier = false;
  private Boolean showUnasigned = false;
  private Boolean nulled = false;

  /** Constructor for a WHERE clause, if nulled is false, toString returns an empty String */
  public Where(Boolean nulled) {
    this.nulled = !nulled;
  }

  /** Constructor for a Where clause that always returns an empty String */
  public Where() {
    nulled = true;
  }

  public Where(LocalDate start, LocalDate end) {
    this.start = start;
    this.end = end;
  }

  public Where setStart(LocalDate start) {
    this.start = start;
    return this;
  }

  public Where setEnd(LocalDate end) {
    this.end = end;
    return this;
  }

  public Where setRule(String rule) {
    this.rule = rule;
    return this;
  }

  public Where setBuyerFilter(Buyer[] buyer) {
    this.buyer = buyer;
    return this;
  }

  public Where setCatsFilter(Categories[] cats) {
    this.cats = cats;
    return this;
  }

  public Where setFileIds(Integer[] files) {
    this.files = files;
    return this;
  }

  public Where showOutliers() {
    showOutlier = true;
    showUnasigned = false;
    return this;
  }

  public Where showUnassigned() {
    showUnasigned = true;
    showOutlier = false;
    return this;
  }

  @Override
  public String toString() {
    if (nulled) return "";
    Boolean trail = false;
    StringBuilder builder = new StringBuilder("WHERE ");
    if (start != null) {
      // System.out.println("Start");
      builder.append("date >= ").append(start.toEpochDay()).append(" AND ");
      trail = true;
    }
    if (end != null) {
      // System.out.println("End");
      builder.append("date <= ").append(end.toEpochDay()).append(" AND ");
      trail = true;
    }
    if (buyer != null && !showUnasigned) {
      // System.out.println("Buyer");
      builder.append("buyername IN ( ");
      for (Buyer i : buyer) {
        builder.append('\'').append(i.toString()).append("',");
      }
      builder.replace(builder.length() - 1, builder.length(), "");
      builder.append(") AND ");
      trail = true;
    }
    if (cats != null) {
      // System.out.println("Cats");
      builder.append("catfilter.catname IN ( ");
      for (Categories i : cats) {
        builder.append('\'').append(i.getName()).append("',");
      }
      builder.replace(builder.length() - 1, builder.length(), "");
      builder.append(") AND ");
      trail = true;
    }
    if (files != null) {
      builder.append("fileid IN ( ");
      for (Integer i : files) {
        builder.append(i).append(" ,");
      }
      builder.replace(builder.length() - 1, builder.length(), "");
      builder.append(") AND ");
      trail = true;
    }
    if (rule != null) {
      // System.out.println("Rule");
      builder.append("description LIKE '").append(DB.toRegex(rule)).append("' AND ");
      trail = true;
    }
    if (showOutlier) {
      builder.append("transactions.transactionid IN (SELECT transactionid FROM outlierFilter)");
      trail = false;
    } else if (showUnasigned) {
      builder.append(
          "transactions.transactionid NOT IN (SELECT transactionid FROM outlierFilter)"
              + " AND transactions.transactionid NOT IN (SELECT transactionid FROM rules)");
      trail = false;
    }

    if (trail) builder.replace(builder.length() - 5, builder.length(), "");
    // System.out.println(builder.toString()); // DEBUG

    return builder.toString();
  }
}
