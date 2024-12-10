package team05.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import team05.fft.*;

// Author hypnotics-dev devhypnotics@proton.me
// Author Justin Babineau jbabine1
// Author Benjamin Hickey benjamin-hickey
/** CategoryDB */
public class CategoryDB extends DB<Categories> {

  public CategoryDB(String db) {
    super(db);
  }

  public CategoryDB() {
    super("fft.db");
  }

  @Override
  public void newRows(Categories[] tables) {
    String sql = "INSERT INTO categories(catregex, catname) VALUES(?,?)";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Categories i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setString(1, i.getPk());
        pstmt.setString(2, i.getName());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Added " + rows + " rows in categories");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to add categories, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public void removeRow(Categories[] tables) {
    String sql = "DELETE FROM categories WHERE catregex = ?";
    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Categories i : tables) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setString(1, i.getPk());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Removed " + rows + " rows in categories");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to remove categories, starting with " + tables[0].toString());
      Os.loggerErr(e.getMessage());
    }
  }

  @Override
  public Categories[] query(String query) {
    String sql = "SELECT catregex, catname FROM categories WHERE " + query;
    ArrayList<Categories> arr = new ArrayList<Categories>();
    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(new Categories(rs.getString("catregex"), rs.getString("catname")));
      }
    } catch (SQLException e) {
      Os.loggerErr(e.getMessage());
      Os.loggerErr("ERROR: query " + query + " failed");
    }
    return arr.toArray(new Categories[arr.size()]);
  }

  public ArrayList<Categories> getCategories() {
    String sql = "SELECT catregex, catname FROM categories;";
    ArrayList<Categories> arr = new ArrayList<Categories>();

    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(new Categories(rs.getString("catregex"), rs.getString("catname")));
      }
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to retrieve categories");
      Os.loggerErr(e.getMessage());
    }
    return arr;
  }

  public ArrayList<String> getCategoryNames() {
    String sql = "SELECT catname FROM categories;";
    ArrayList<String> arr = new ArrayList<>();

    try {
      ResultSet rs = connector.createStatement().executeQuery(sql);
      while (rs.next()) {
        arr.add(rs.getString("catname"));
      }
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to retrieve categories");
      Os.loggerErr(e.getMessage());
    }
    return arr;
  }

  public void updateRowWithName(Categories[] categories) {
    String sql = "UPDATE categories SET catname = ? WHERE catregex = ?";

    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (Categories i : categories) {
        pstmt = connector.prepareStatement(sql);
        pstmt.setString(1, i.getName());
        pstmt.setString(2, i.getPk());
        rows += pstmt.executeUpdate();
      }
      Os.logger("SQL: Updated " + rows + " rows");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to update categories starting with " + categories[0].toValue());
      Os.loggerErr(e.getMessage());
    }
  }

  public void updateRowWithRule(Categories[] categories, String[] rules) {
    String del = "DELETE FROM categories WHERE catregex = ?";
    String insert = "INSERT INTO categories(catregex, catname) VALUES(?,?)";

    try {
      PreparedStatement pstmt;
      int rows = 0;
      for (int i = 0; i < categories.length; i++) {
        pstmt = connector.prepareStatement(del);
        pstmt.setString(1, rules[i]);
        pstmt.executeUpdate();
        pstmt = connector.prepareStatement(insert);
        pstmt.setString(1, categories[i].getPk());
        pstmt.setString(2, categories[i].getName());
        pstmt.executeUpdate();
        rows++;
      }
      Os.logger("SQL: Updated " + rows + " category rules");
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to update categories starting with " + categories[0].toValue());
      Os.loggerErr(e.getMessage());
    }
  }
}
