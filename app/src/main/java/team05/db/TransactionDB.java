package team05.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import team05.fft.Os;
import team05.fft.Transaction;

// Author hypnotics-dev devhypnotics@proton.me
public class TransactionDB extends DB<Transaction> {

  /**
   * @see DB#DB(boolean, String)
   */
  public TransactionDB(String dbName) {
    super(dbName);
  }

  public TransactionDB() {
    super("fft.db");
  }

  @Override
  public void newRows(Transaction[] tables) {
    String sql =
        "INSERT INTO transactions(date,description, fileid, value,balance) VALUES(?,?,?,?,?)";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Transaction i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setLong(1, i.getDate());
        pstmt.setString(2, i.getDesc());
        pstmt.setInt(3, i.getFile());
        pstmt.setDouble(4, i.getVal());
        pstmt.setDouble(5, i.getBal());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Added " + rows + " rows in transaction");
    } catch (Exception e) {
      Os.loggerErr("ERROR: Failed to add transactions, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public void removeRow(Transaction[] tables) {
    String sql = "DELETE FROM transactions WHERE transactionid = ?";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Transaction i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setInt(1, i.getPk());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Removed " + rows + " rows in transaction");
    } catch (Exception e) {
      Os.loggerErr("ERROR: Failed to remove transactions, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public Transaction[] query(String query) {
    String sql =
        "SELECT transactionid, date, description, value, balance FROM transactions WHERE " + query;
    ArrayList<Transaction> arr = new ArrayList<Transaction>();
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(
            new Transaction(
                rs.getInt("transactionid"),
                rs.getLong("date"),
                rs.getString("description"),
                rs.getDouble("value"),
                rs.getDouble("balance")));
      }

    } catch (SQLException e) {
      Os.loggerErr(e.getMessage());
      Os.loggerErr("ERROR: query " + query + " failed");
    }
    return arr.toArray(new Transaction[arr.size()]);
  }

  // Full query can also be found in resources/transactions.sql
  public ArrayList<Transaction> getTransactions(Where clause) {
    String finalSeletc =
        "SELECT DISTINCT transactions.transactionid as transactionid, date, description, value,"
            + " balance, buyername,catfilter.catname FROM transactions  LEFT JOIN whole ON"
            + " whole.transactionid = transactions.transactionid LEFT JOIN buyers ON whole.buyerid"
            + " = buyers.buyerid LEFT JOIN catfilter ON transactions.transactionid ="
            + " catfilter.transactionid ";
    String whole =
        "whole AS ( SELECT outlierFilter.transactionid AS transactionid, outlierFilter.buyerid AS"
            + " buyerid, catFilter.catname AS catname FROM outlierFilter  LEFT JOIN catFilter ON"
            + " catFilter.transactionid = outlierFilter.transactionid )";
    String catFilter =
        "catFilter as (  SELECT DISTINCT transactionid, catname  FROM ( SELECT transactionid,"
            + " description, catregex, catname FROM transactions CROSS JOIN categories  )"
            + " WHERE description  LIKE catregex ),";
    String outlierFilter =
        "outlierFilter AS ( SELECT transactionid, buyerid FROM rules WHERE NOT"
            + " EXISTS ( SELECT 1  FROM outliers  WHERE rules.transactionid ="
            + " outliers.transactionid ) UNION SELECT transactionid, buyerid"
            + " FROM outliers ),";
    String rules =
        "WITH rules AS ( SELECT transactionid, buyerid FROM ( SELECT transactionid, description,"
            + " buyerregex, buyerid  FROM transactions CROSS JOIN buyerrules ) WHERE description"
            + " LIKE buyerregex ),";
    String sql = rules + outlierFilter + catFilter + whole + finalSeletc + clause.toString();
    ArrayList<Transaction> data = new ArrayList<>();
    Transaction last = null;
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        int tid = rs.getInt("transactionid");
        String catname = rs.getString("catname");
        if (last != null && last.getPk() == tid) {
          last.addCategory(catname);
        } else {
          last =
              new Transaction(
                  rs.getLong("date"),
                  tid,
                  rs.getString("buyername"),
                  catname,
                  rs.getDouble("value"),
                  rs.getDouble("balance"),
                  rs.getString("description"));
          data.add(last);
        }
      }
    } catch (SQLException e) {
      Os.loggerErr(e.getMessage());
      Os.loggerErr("Error: failed to add transaction to display list");
    }

    return data;
  }

  public long getFirstDay() {
    String sql = "SELECT date FROM transactions ORDER BY date ASC LIMIT 1";
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      return rs.getLong("date");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to retrieve first day");
      Os.loggerErr(e.getMessage());
    }
    return -1L;
  }

  public long getLastDay() {
    String sql = "SELECT date FROM transactions ORDER BY date DESC LIMIT 1";
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      return rs.getLong("date");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to retrieve last day");
      Os.loggerErr(e.getMessage());
    }
    return -1L;
  }
}
