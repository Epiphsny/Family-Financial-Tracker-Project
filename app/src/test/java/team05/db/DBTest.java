package team05.db;

import java.io.File;
import team05.fft.Os;

// Author hypnotics-dev devhypnotics@proton.me
public class DBTest {

  protected static final String DBNAME = "test.db";

  public static File getTestFile(String file) {
    File db = new File(Os.getDbPath(file));
    if (db.exists()) db.delete();
    return db;
  }
}
