package team05.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import team05.fft.Os;
import team05.fft.Transaction;

// Author hypnotics-dev devhypnotics@proton.me
/** OutlierDB */
public class OutlierDB extends DB<Outlier> {

  private String dbName;

  public OutlierDB(String dbName) {
    super(dbName);
    this.dbName = dbName;
  }

  public OutlierDB() {
    super("fft.db");
    this.dbName = "fft.db";
  }

  @Override
  public void newRows(Outlier[] tables) {
    String sql = "INSERT INTO outliers(transactionid,buyerid) VALUES(?,?)";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Outlier i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setInt(1, i.getPk());
        pstmt.setInt(2, i.getBuyer());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Added " + rows + " rows in outliers");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to add outliers, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public void removeRow(Outlier[] tables) {
    String sql = "DELETE FROM outliers WHERE transactionid = ?";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Outlier i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setInt(1, i.getPk());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Removed " + rows + " rows in outliers");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to remove outliers, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  public void removeRow(Transaction[] tables) {
    String sql = "DELETE FROM outliers WHERE transactionid = ?";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Transaction i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setInt(1, i.getPk());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Removed " + rows + " rows in outliers");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to remove outliers, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public Outlier[] query(String query) {
    String sql = "SELECT transactionid, buyerid FROM outliers WHERE " + query;
    TransactionDB tid = new TransactionDB(dbName);
    BuyerDB bid = new BuyerDB(dbName);
    ArrayList<Outlier> arr = new ArrayList<Outlier>();
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(
            // FIXME: remove the usage of DB.query() for a dedicated query method
            new Outlier(
                tid.query("transactionid = " + rs.getInt("transactionid"))[0],
                bid.query("buyerid = " + rs.getInt("buyerid"))[0]));
      }
    } catch (SQLException e) {
      Os.loggerErr(e.getMessage());
      Os.loggerErr("ERROR: query " + query + " failed");
    }
    tid.close();
    bid.close();
    return arr.toArray(new Outlier[arr.size()]);
  }

  public boolean exists(Transaction table) {
    String sql = "SELECT transactionId FROM outliers WHERE transactionId = " + table.getPk();
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      return rs.next();
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to query outliers with transaction id " + table.getPk());
      Os.loggerErr(e.getMessage());
    }
    return true;
  }
}
