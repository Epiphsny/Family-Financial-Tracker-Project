package team05.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import team05.fft.Os;

// Author hypnotics-dev devhypnotics@proton.me
/** BuyerBD */
public class BuyerDB extends DB<Buyer> {

  public BuyerDB(String db) {
    super(db);
  }

  public BuyerDB() {
    super("fft.db");
  }

  @Override
  public void newRows(Buyer[] tables) {
    String sql = "INSERT INTO buyers(buyername) VALUES(?)";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Buyer i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setString(1, i.toString());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Added " + rows + " rows in buyers");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to add buyers, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public void removeRow(Buyer[] tables) {
    String sql = "DELETE FROM buyers WHERE buyerid = ?";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Buyer i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setInt(1, i.getPk());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Removed " + rows + " rows in buyers");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to remove buyers, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public Buyer[] query(String query) {
    String sql = "SELECT buyername,buyerid FROM buyers WHERE " + query;
    ArrayList<Buyer> arr = new ArrayList<Buyer>();
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(new Buyer(rs.getInt("buyerid"), rs.getString("buyername")));
      }

    } catch (SQLException e) {
      Os.loggerErr("ERROR: query " + query + " failed");
      Os.loggerErr(e.getMessage());
    }
    return arr.toArray(new Buyer[arr.size()]);
  }

  public ArrayList<Buyer> getBuyers() {
    String sql = "SELECT buyername,buyerid FROM buyers";
    ArrayList<Buyer> arr = new ArrayList<>();
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(new Buyer(rs.getInt("buyerid"), rs.getString("buyername")));
      }
    } catch (Exception e) {
      Os.loggerErr("ERROR: failed to fetch BUYERS");
      Os.loggerErr(e.getMessage());
    }
    return arr;
  }

  public void updateRow(Buyer[] set) {
    String sql = "UPDATE buyers SET buyername = ? WHERE buyerid = ?";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Buyer i : set) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setString(1, i.toString());
        pstmt.setInt(2, i.getPk());
        pstmt.executeUpdate();
        rows++;
      }
      Os.logger("Updated " + rows + " rows");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to update buyers starting with " + set[0].toValue());
      Os.loggerErr(e.getMessage());
    }
  }

  public String getBuyerName(int fk) {
    String sql = "SELECT buyername FROM buyers WHERE buyerid = ?";
    try {
      PreparedStatement pstmt = connector.prepareStatement(sql);
      pstmt.setInt(1, fk);
      ResultSet rs = pstmt.executeQuery();
      return rs.getString("buyername");

    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to find buyername for " + fk);
      Os.loggerErr(e.getMessage());
    }
    return "" + fk;
  }

  public String getBuyerName(Buyer fk) {
    String sql = "SELECT buyername FROM buyers WHERE buyerid = ?";
    try {
      PreparedStatement pstmt = connector.prepareStatement(sql);
      pstmt.setInt(1, fk.getPk());
      ResultSet rs = pstmt.executeQuery();
      return rs.getString("buyername");

    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to find buyername for " + fk.toValue());
      Os.loggerErr(e.getMessage());
    }
    return "" + fk;
  }

  public ArrayList<String> getBuyerNames() {
    String sql = "SELECT buyername FROM buyers";
    ArrayList<String> arr = new ArrayList<>();
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(rs.getString("buyername"));
      }
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to get buyers");
      Os.loggerErr(e.getMessage());
    }
    return arr;
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
