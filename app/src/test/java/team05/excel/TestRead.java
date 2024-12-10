package team05.excel;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

// Author Justin Babineau jbabine1
/** TestBuyerDB */
public class TestRead {

  @Tag("merge")
  @Test
  void readData() {
    try {
      XLSXReader xlsxReader = new XLSXReader();
      xlsxReader.read(Paths.get("src", "test", "resources", "2023-11.xlsx").toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
