package team05.fft;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper functions relating to the operaiting systems and file I/O, Error code constants are also
 * stored here
 */
// Author hypnotics-dev devhypnotics@proton.me
// Author Justin Babineau jbabine1
// Author Eric Smith EWillCliff
public class Os {

  public static final int LOGIC_ERROR = 1; // Generic logic error
  public static final int SQL_ERROR = 2; // Error relating to SQL Statments
  public static final int EXCEL_ERROR = 3; // Error relating to excel files
  public static final int FS_ERROR = 4; // Error relating to the file system
  public static final int CLOSE_ERROR = 5; // Failed to close an sql connection

  /**
   * Returns a path to app data as per various Os standards
   *
   * <ul>
   *   <li><code>%APPDATA%\\fft-S2T5\\ for Windows (get user)
   *   <li><code>$XDG_DATA_HOME/fft-S2T5/ for Linux
   *   <li><code>~/Library/Application Support/fft-S2T5/ for Mac
   * </ul>
   */
  public static Path getDataPath() {
    String path = System.getProperty("os.name").toLowerCase();
    File dir;
    if (path.indexOf("win") >= 0) {
      dir =
          Paths.get(
                  "C:", "Users", System.getProperty("user.name"), "AppData", "Roaming", "fft-S2T5")
              .toFile();
    } else if (path.indexOf("mac") >= 0) {

      dir =
          Paths.get(System.getProperty("user.home"), "Library", "Application Support", "fft-S2T5")
              .toFile();
    } else { // assumes that only mac, windows and linux can be used
      path = System.getenv("XDG_DATA_HOME");
      if (path == null) {
        path = Paths.get(System.getenv("HOME"), ".local", "state", "fftS2T5").toString();
      }
      dir = new File(path);
    }
    if (!dir.exists()) {
      if (!dir.mkdir()) {
        System.err.println("Failed to create Directory, " + dir.toString());
        System.exit(FS_ERROR);
      }
    }
    return dir.toPath();
  }

  public static String getDbPath(String db) {
    return getDataPath().resolve(db).toString();
  }

  public static String getDbPath() {
    return getDataPath().resolve("fft.db").toString();
  }

  // TODO: Implement a way to log errors and runtime events
  public static void loggerErr(String message) {
    // FIXME: place holder, prints to STDERR instead of logs
    if (message.length() > 0) {
      System.err.println(message);
      return;
    }
    // Real method goes here
  }

  public static void logger(String message) {
    // FIXME: place holder, prints to STDERR instead of logs
    if (message.length() > 0) {
      System.out.println(message);
      return;
    }
    // Real method goes here
  }
}
