package team05.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import team05.db.FileDB;
import team05.db.TransactionDB;
import team05.fft.Transaction;

// Author Justin Babineau jbabine1
// Author hypnotics-dev devhypnotics@proton.me
public class XLSXReader implements DataReader {

  public void read(String path) throws IOException {
    FileInputStream file = new FileInputStream(new File(path));
    XSSFWorkbook workbook = new XSSFWorkbook(file);
    XSSFSheet sheet = workbook.getSheetAt(0);

    double debit = 0;
    double credit = 0;
    double balance = 0;
    String description = "";
    Date dateString = null;
    LocalDate date = null;
    int fileId = FileDB.addFile(new File(path));
    if (fileId < 0) {
      workbook.close();
      file.close();
      return;
    }

    ArrayList<Transaction> list = new ArrayList<Transaction>();

    for (Row row : sheet) {

      for (int cn = 0; cn < row.getLastCellNum(); cn++) {
        if (cn == 0)
          dateString =
              (row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).getDateCellValue();
        else if (cn == 1)
          description =
              (row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).getStringCellValue();
        else if (cn == 2)
          debit =
              (row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).getNumericCellValue();
        else if (cn == 3)
          credit =
              (row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).getNumericCellValue();
        else
          balance =
              (row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).getNumericCellValue();
      }

      date = dateString.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

      if (debit == 0) list.add(new Transaction(date, description, (credit * -1), balance, fileId));
      else list.add(new Transaction(date, description, debit, balance, fileId));
    }
    TransactionDB tDB = new TransactionDB();
    tDB.newRows(list.toArray(new Transaction[list.size()]));
    tDB.close();
    file.close();
    workbook.close();
  }
}
