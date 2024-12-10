package team05.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import team05.fft.Os;
import team05.fft.Transaction;

/** FileDB */
public class FileDB {

  public static int addFile(File file) {
    try {
      Connection connector = DriverManager.getConnection(getDbURI());
      String sql = "INSERT INTO files(file) VALUES(?)";
      PreparedStatement pstm = connector.prepareStatement(sql);
      pstm.setString(1, file.getName());
      pstm.executeUpdate();
      pstm = connector.prepareStatement("SELECT file, fileid FROM files");
      pstm = connector.prepareStatement("SELECT fileid FROM files WHERE file = ?");
      pstm.setString(1, file.getName());
      int id = pstm.executeQuery().getInt("fileid");
      connector.close();
      return id;
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to connect to File DB");
      Os.loggerErr(e.getMessage());
    }
    return -1;
  }

  public static int addFile(File file, String path) {
    try {
      Connection connector = DriverManager.getConnection("jdbc:sqlite:" + path);
      String sql = "INSERT INTO files(file) VALUES(?)";
      PreparedStatement pstm = connector.prepareStatement(sql);
      pstm.setString(1, file.getName());
      pstm.executeUpdate();
      pstm = connector.prepareStatement("SELECT file, fileid FROM files");
      pstm = connector.prepareStatement("SELECT fileid FROM files WHERE file = ?");
      pstm.setString(1, file.getName());
      int id = pstm.executeQuery().getInt("fileid");
      connector.close();
      return id;
    } catch (SQLException e) {
      Os.loggerErr("ERROR: Failed to connect to File DB");
      Os.loggerErr(e.getMessage());
    }
    return -1;
  }

  public static Transaction[] revertTransactions(int id, String name) {
    ArrayList<Transaction> arr = new ArrayList<>();
    try {
      String select =
          "SELECT transactionid, date, description, value, balance FROM transactions WHERE fileid ="
              + " ?";
      String remove = "DELETE FROM files WHERE file = ?";
      String removeTransactions = "DELETE FROM transactions WHERE fileid = ?";

      Connection connector = DriverManager.getConnection(getDbURI());
      PreparedStatement pstmt = connector.prepareStatement(select);
      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        arr.add(
            new Transaction(
                rs.getInt("transactionid"),
                rs.getLong("date"),
                rs.getString("description"),
                rs.getDouble("value"),
                rs.getDouble("balance")));
      }

      pstmt = connector.prepareStatement(removeTransactions);
      pstmt.setInt(1, id);
      int rows = pstmt.executeUpdate();
      pstmt = connector.prepareStatement(remove);
      pstmt.setString(1, name);
      pstmt.executeUpdate();

      Os.logger(
          "SQL: reverted "
              + rows
              + " of "
              + arr.size()
              + " transactions from file "
              + connector
                  .createStatement()
                  .executeQuery("SELECT file FROM files WHERE fileid = " + id)
                  .getString("file"));

      connector.close();

    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to revert transactions from file id " + id);
      Os.loggerErr(e.getMessage());
    }

    return arr.toArray(new Transaction[arr.size()]);
  }

  public static int getFileId(String name) {
    int ret = -1;
    try {
      String sql = "SELECT fileid FROM files WHERE file = ?";

      Connection connector = DriverManager.getConnection(getDbURI());
      PreparedStatement pstmt = connector.prepareStatement(sql);
      pstmt.setString(1, name);
      ret = pstmt.executeQuery().getInt("fileid");
      connector.close();
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to get file " + name);
      Os.loggerErr(e.getMessage());
    }
    return ret;
  }

  public static ArrayList<String> getFiles() {
    ArrayList<String> arr = new ArrayList<>();
    String sql = "SELECT file FROM files";
    try {
      Connection connector = DriverManager.getConnection(getDbURI());
      PreparedStatement pstmt = connector.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        arr.add(rs.getString("file"));
      }
      connector.close();

      return arr;
    } catch (SQLException e) {
      Os.loggerErr("ERROR: failed to get files");
      Os.loggerErr(e.getMessage());
    }
    return null;
  }

  // TODO: Make a revert method with a filter clause as well

  private static String getDbURI() {
    return "jdbc:sqlite:" + Os.getDbPath();
  }
}
