package team05.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import team05.db.TransactionDB;
import team05.db.Where;
import team05.fft.Transaction;

// Author Eric Smith EWillCliff
public class LedgerWriter implements DataWriter {

  private static CellStyle createHeaderCellStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBold(true);
    style.setFont(font);
    style.setAlignment(HorizontalAlignment.CENTER);
    return style;
  }

  @Override
  public void write(String path) throws IOException {
    TransactionDB tDB = new TransactionDB();
    ArrayList<Transaction> transactions = tDB.getTransactions(new Where());

    ArrayList<Transaction>[] monthlyTransactions = new ArrayList[12];
    for (int i = 0; i < 12; i++) {
      monthlyTransactions[i] = new ArrayList<>();
    }

    for (int i = 0; i < transactions.size(); i++) {
      Transaction transaction = transactions.get(i);
      LocalDate date = LocalDate.ofEpochDay(transaction.getDate());
      int monthIndex = date.getMonthValue() - 1;
      monthlyTransactions[monthIndex].add(transaction);
    }

    XSSFWorkbook workbook = new XSSFWorkbook();

    for (int i = 0; i < 12; i++) {
      String monthName = "2023-" + (i + 1);
      XSSFSheet sheet = workbook.createSheet(monthName);

      Row header = sheet.createRow(0);
      String[] headers = {"Date", "Description", "BuyerID", "Amount", "Category"};
      for (int j = 0; j < headers.length; j++) {
        Cell cell = header.createCell(j);
        cell.setCellValue(headers[j]);
        cell.setCellStyle(createHeaderCellStyle(workbook));
      }

      int rowNum = 1;
      ArrayList<Transaction> transactionsForMonth = monthlyTransactions[i];
      for (int j = 0; j < transactionsForMonth.size(); j++) {
        Transaction transaction = transactionsForMonth.get(j);
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(LocalDate.ofEpochDay(transaction.getDate()).toString());
        row.createCell(1).setCellValue(transaction.getDesc());
        row.createCell(2).setCellValue(transaction.getBuyer());
        row.createCell(3).setCellValue(transaction.getVal());
        row.createCell(4).setCellValue(transaction.getCategory());
      }

      for (int j = 0; j < headers.length; j++) {
        sheet.autoSizeColumn(j);
      }
    }

    FileOutputStream file = new FileOutputStream(new File(path));
    workbook.write(file);
    workbook.close();
    tDB.close();
    file.close();
  }
}
