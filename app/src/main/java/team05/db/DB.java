package team05.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.Pragma;
import team05.fft.*;

// Author hypnotics-dev devhypnotics@proton.me
/** An interface for interacting with the DB */
public abstract class DB<E extends Table> {
  // TODO: Create a guide to enforcing sql relationships

  /** Database Connector */
  protected Connection connector;

  /** Create a DB object by opening an connector */
  public DB(String dbName) {
    try {
      File dbFile = new File(Os.getDbPath(dbName));

      // Set sqlite PRAMGMA here
      SQLiteConfig config = new SQLiteConfig();
      config.setPragma(Pragma.CASE_SENSITIVE_LIKE, "false");
      config.setPragma(Pragma.FOREIGN_KEYS, "ON");

      boolean create = !dbFile.exists();
      connector =
          DriverManager.getConnection("jdbc:sqlite:" + dbFile.toString(), config.toProperties());

      if (create) {

        String file =
            "CREATE TABLE IF NOT EXISTS files(fileId INTEGER PRIMARY KEY, file STRING NOT NULL)";
        String transactions =
            "CREATE TABLE IF NOT EXISTS Transactions ("
                // if we pass in NULL to an artificial ID field it will auto increment as per
                // https://www.sqlite.org/autoinc.html
                + "transactionId INTEGER PRIMARY KEY, date INTEGER NOT NULL, description TEXT NOT"
                + " NULL, fileid INTEGER NOT NULL, value REAL NOT NULL, balance REAL NOT NULL,"
                + " FOREIGN KEY (fileid) REFERENCES files(fileId) ON DELETE CASCADE)";
        String buyer =
            "CREATE TABLE IF NOT EXISTS buyers ( buyerId INTEGER PRIMARY KEY, buyerName TEXT NOT"
                + " NULL)";
        String catRule =
            "CREATE TABLE IF NOT EXISTS categories ( catregex TEXT PRIMARY KEY, catname TEXT NOT"
                + " NULL)";
        String buyerRule =
            "CREATE TABLE IF NOT EXISTS buyerRules ( buyerregex TEXT PRIMARY KEY, buyerID INTEGER"
                + " NOT NULL, FOREIGN KEY (buyerID) REFERENCES buyers (buyerID) ON DELETE CASCADE)";
        String buyerOutlier =
            "CREATE TABLE IF NOT EXISTS outliers (transactionID INTEGER PRIMARY KEY, buyerID"
                + " INTERGER NOT NULL, FOREIGN KEY (transactionID) REFERENCES transactions"
                + " (transactionId) ON DELETE CASCADE, FOREIGN KEY (buyerID) REFERENCES buyers"
                + " (buyerId) ON DELETE CASCADE)";

        connector.createStatement().execute(file);
        connector.createStatement().execute(transactions);
        connector.createStatement().execute(buyer);
        connector.createStatement().execute(catRule);
        connector.createStatement().execute(buyerRule);
        connector.createStatement().execute(buyerOutlier);
      }
    } catch (SQLException e) {
      Os.loggerErr(e.getMessage());
      Os.loggerErr("ERROR: failed to create database");
      System.exit(Os.SQL_ERROR);
    }
  }

  /** Adds rows to the datebase */
  public abstract void newRows(E[] tables);

  /** Removes rows from the database */
  public abstract void removeRow(E[] tables);

  /** Returns an object array generate by your given query */
  public abstract E[] query(String query);

  /** Closes the connection to the DB */
  public void close() {
    try {
      connector.close();
    } catch (SQLException e) {
      Os.loggerErr(e.getMessage());
      System.exit(Os.CLOSE_ERROR);
    }
  }

  public static String toRegex(String val) {
    if (val.charAt(0) != '%') {
      return '%' + val + '%';
    }
    return val;
  }

  public static void createTables(String path) {
    try {
      File dbFile = new File(Os.getDbPath(path));

      // Set sqlite PRAMGMA here
      SQLiteConfig config = new SQLiteConfig();
      config.setPragma(Pragma.CASE_SENSITIVE_LIKE, "false");
      config.setPragma(Pragma.FOREIGN_KEYS, "ON");

      boolean create = !dbFile.exists();
      String connection = "jdbc:sqlite:" + dbFile.toString();
      Connection connector = DriverManager.getConnection(connection, config.toProperties());

      if (create) {

        String file =
            "CREATE TABLE IF NOT EXISTS files(fileId INTEGER PRIMARY KEY, file STRING NOT NULL)";
        String transactions =
            "CREATE TABLE IF NOT EXISTS Transactions ("
                // if we pass in NULL to an artificial ID field it will auto increment as per
                // https://www.sqlite.org/autoinc.html
                + "transactionId INTEGER PRIMARY KEY, date INTEGER NOT NULL, description TEXT NOT"
                + " NULL, fileid INTEGER NOT NULL, value REAL NOT NULL, balance REAL NOT NULL,"
                + " FOREIGN KEY (fileid) REFERENCES files(fileId) ON DELETE CASCADE)";
        String buyer =
            "CREATE TABLE IF NOT EXISTS buyers ( buyerId INTEGER PRIMARY KEY, buyerName TEXT NOT"
                + " NULL)";
        String catRule =
            "CREATE TABLE IF NOT EXISTS categories ( catregex TEXT PRIMARY KEY, catname TEXT NOT"
                + " NULL)";
        String buyerRule =
            "CREATE TABLE IF NOT EXISTS buyerRules ( buyerregex TEXT PRIMARY KEY, buyerID INTEGER"
                + " NOT NULL, FOREIGN KEY (buyerID) REFERENCES buyers (buyerID) ON DELETE CASCADE)";
        String buyerOutlier =
            "CREATE TABLE IF NOT EXISTS outliers (transactionID INTEGER PRIMARY KEY, buyerID"
                + " INTERGER NOT NULL, FOREIGN KEY (transactionID) REFERENCES transactions"
                + " (transactionId) ON DELETE CASCADE, FOREIGN KEY (buyerID) REFERENCES buyers"
                + " (buyerId) ON DELETE CASCADE)";

        connector.createStatement().execute(file);
        connector.createStatement().execute(transactions);
        connector.createStatement().execute(buyer);
        connector.createStatement().execute(catRule);
        connector.createStatement().execute(buyerRule);
        connector.createStatement().execute(buyerOutlier);

        connector.close();
      }
    } catch (SQLException e) {
      Os.loggerErr(e.getMessage());
      Os.loggerErr("ERROR: failed to create database");
      System.exit(Os.SQL_ERROR);
    }
  }
}
