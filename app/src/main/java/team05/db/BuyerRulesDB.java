package team05.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import team05.fft.*;

// Author hypnotics-dev devhypnotics@proton.me
/** BuyerRulesDB */
public class BuyerRulesDB extends DB<BuyerRules> {

  private String db;

  public BuyerRulesDB(String db) {
    super(db);
    this.db = db;
  }

  public BuyerRulesDB() {
    super("fft.db");
    this.db = "fft.db";
  }

  @Override
  public void newRows(BuyerRules[] tables) {
    String sql = "INSERT INTO buyerRules(buyerRegex,buyerId) VALUES(?,?)";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (BuyerRules i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setString(1, i.getPk());
        pstmt.setInt(2, i.getBuyer());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Added " + rows + " rows in buyerRules");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to add buyerRules, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public void removeRow(BuyerRules[] tables) {
    String sql = "DELETE FROM buyerRules WHERE buyerregex = ?";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (BuyerRules i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setString(1, i.getPk());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Removed " + rows + " rows in buyerRules");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to remove buyerRules, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public BuyerRules[] query(String query) {
    String sql = "SELECT buyerregex, buyerid FROM buyerrules WHERE " + query;
    ArrayList<BuyerRules> arr = new ArrayList<BuyerRules>();
    BuyerDB bid = new BuyerDB(db);
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(
            new BuyerRules(
                // FIXME: remove the usage of DB.query() for a dedicated query method
                rs.getString("buyerregex"), bid.query("buyerid = " + rs.getInt("buyerid"))[0]));
      }
    } catch (SQLException e) {
      Os.loggerErr(e.getMessage());
      Os.loggerErr("ERROR: query " + query + " failed");
    }
    bid.close();
    return arr.toArray(new BuyerRules[arr.size()]);
  }

  public ArrayList<BuyerRules> getRules() {
    String sql =
        "SELECT buyerregex,buyername FROM buyerrules INNER JOIN buyers ON buyers.buyerid ="
            + " buyerrules.buyerid;";
    ArrayList<BuyerRules> arr = new ArrayList<>();
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(new BuyerRules(rs.getString("buyerregex"), rs.getString("buyername")));
      }
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to retrieve buyerRules");
      Os.loggerErr(e.getMessage());
    }
    return arr;
  }

  public void updateRowsWithRule(BuyerRules[] rules, String[] str) {
    String sql = "DELETE FROM buyerRules WHERE buyerregex = ?";
    String insert = "INSERT INTO buyerRules(buyerregex, buyerid) VALUES(?,?)";

    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (int i = 0; i < rules.length; i++) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setString(1, str[i]);
        pstmt.executeUpdate();
        pstmt = connector.prepareStatement(insert);
        pstmt.setString(1, rules[i].getPk());
        pstmt.setInt(2, rules[i].getBuyer());
        pstmt.executeUpdate();
        rows++;
      }
      Os.logger("SQL: Updated " + rows + " rows");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to update buyerRules starting with " + rules[0].toValue());
      Os.loggerErr(e.getMessage());
    }
  }

  public void updateRowsWithName(BuyerRules[] rules) {
    String sql = "UPDATE buyerRules SET buyerid = ? WHERE buyerregex = ?";

    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (BuyerRules buyerRules : rules) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setInt(1, buyerRules.getBuyer());
        pstmt.setString(2, buyerRules.getPk());
        pstmt.executeUpdate();
        rows++;
      }
      Os.logger("SQL: Updated " + rows + " rows");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to update buyerRules starting with " + rules[0].toValue());
      Os.loggerErr(e.getMessage());
    }
  }

  public Buyer getBuyerFromName(String name) {
    String sql = "SELECT buyerid FROM buyers WHERE buyername = ?";
    try {
      PreparedStatement pstmt = connector.prepareStatement(sql);
      pstmt.setString(1, name);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      return new Buyer(rs.getInt("buyerid"), name);
    } catch (SQLException e) {
      Os.loggerErr("ERORR: Failed to get buyerid for " + name);
      Os.loggerErr(e.getMessage());
    }
    return null;
  }

  public Buyer getBuyerFromId(int id) {
    String sql = "SELECT buyername FROM buyers WHERE buyerid = ?";
    try {
      PreparedStatement pstmt = connector.prepareStatement(sql);
      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      return new Buyer(id, rs.getString("buyername"));
    } catch (SQLException e) {
      Os.loggerErr("ERORR: Failed to get buyername for " + id);
      Os.loggerErr(e.getMessage());
    }
    return null;
  }

  public Buyer getBuyerFromRegex(String regex) {
    String sql = "SELECT buyerid FROM buyerRules WHERE buyerregex = ?";
    try {
      PreparedStatement pstmt = connector.prepareStatement(sql);
      pstmt.setString(1, regex);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      return new Buyer(rs.getInt("buyerid"), regex);
    } catch (SQLException e) {
      Os.loggerErr("ERORR: Failed to get buyerid for " + regex);
      Os.loggerErr(e.getMessage());
    }
    return null;
  }
}
