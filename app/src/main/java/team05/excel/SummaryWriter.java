package team05.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import team05.db.TransactionDB;
import team05.db.Where;
import team05.fft.Transaction;

//Author Eric Smith EWillCliff
public class SummaryWriter implements DataWriter {

    private static CellStyle createHeaderCellStyle(Workbook workbook){
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createRegularCellStyle(Workbook workbook){
    	CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createBoldCellStyle(Workbook workbook){
    	CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    @Override
    public void write(String path) throws IOException{
        TransactionDB tDB = null;
        XSSFWorkbook workbook = null;
        FileOutputStream file = null;

        tDB = new TransactionDB();
        ArrayList<Transaction> transactions = tDB.getTransactions(new Where());
        ArrayList<Transaction>[] monthlyTransactions = new ArrayList[12];
        for(int i = 0; i < 12; i++){
            monthlyTransactions[i] = new ArrayList<>();
        }

        for(Transaction transaction : transactions){
        	LocalDate date = LocalDate.ofEpochDay(transaction.getDate());
            int monthIndex = date.getMonthValue() - 1;
            monthlyTransactions[monthIndex].add(transaction);
        }

        workbook = new XSSFWorkbook();
        CellStyle headerStyle = createHeaderCellStyle(workbook);
        CellStyle regularStyle = createRegularCellStyle(workbook);
        CellStyle boldStyle = createBoldCellStyle(workbook);

        ArrayList<String> buyersList = new ArrayList<>();
        ArrayList<String> categoriesList = new ArrayList<>();
        for(Transaction transaction : transactions){
            if(transaction.getBuyer() != null && !buyersList.contains(transaction.getBuyer())){
                buyersList.add(transaction.getBuyer());
            }
            if(transaction.getCategory() != null && !categoriesList.contains(transaction.getCategory())){
                categoriesList.add(transaction.getCategory());
            }
        }

        for(int i = 0; i < 12; i++){
        	String monthName = "2023-" + (i + 1);
            XSSFSheet sheet = workbook.createSheet(monthName);

            Row header = sheet.createRow(0);
            Cell categoryCell = header.createCell(0);
            categoryCell.setCellValue("Category");
            categoryCell.setCellStyle(headerStyle);

            for(int j = 0; j < buyersList.size(); j++){
                Cell buyerCell = header.createCell(j + 1);
                buyerCell.setCellValue(buyersList.get(j));
                buyerCell.setCellStyle(headerStyle);
            }

            Cell totalCell = header.createCell(buyersList.size() + 1);
            totalCell.setCellValue("Total");
            totalCell.setCellStyle(headerStyle);

            for(int j = 0; j < categoriesList.size(); j++){
                Row row = sheet.createRow(j + 1);
                Cell categoryRowCell = row.createCell(0);
                categoryRowCell.setCellValue(categoriesList.get(j));
                categoryRowCell.setCellStyle(regularStyle);

                double categoryTotal = 0;

                for(int k = 0; k < buyersList.size(); k++){
                    double buyerTotal = 0;

                    for(Transaction transaction : monthlyTransactions[i]){
                        String category = transaction.getCategory();
                        String buyer = transaction.getBuyer();
                        double amount = transaction.getVal();

                        if(category != null && buyer != null && category.equals(categoriesList.get(j)) && buyer.equals(buyersList.get(k))){
                            buyerTotal += amount;
                        }
                    }

                    Cell buyerTotalCell = row.createCell(k + 1);
                    buyerTotalCell.setCellValue(buyerTotal);
                    buyerTotalCell.setCellStyle(regularStyle);

                    categoryTotal += buyerTotal;
                }

                Cell categoryTotalCell = row.createCell(buyersList.size() + 1);
                categoryTotalCell.setCellValue(categoryTotal);
                categoryTotalCell.setCellStyle(boldStyle);
            }

            int totalsRowIndex = categoriesList.size() + 1;
            Row totalsRow = sheet.createRow(totalsRowIndex);

            Cell totalsLabelCell = totalsRow.createCell(0);
            totalsLabelCell.setCellValue("Totals");
            totalsLabelCell.setCellStyle(boldStyle);

            for(int k = 0; k < buyersList.size(); k++){
                double buyerGrandTotal = 0;

                for(int j = 0; j < categoriesList.size(); j++){
                    Row categoryRow = sheet.getRow(j + 1);
                    Cell buyerTotalCell = categoryRow.getCell(k + 1);
                    if(buyerTotalCell != null){
                        buyerGrandTotal += buyerTotalCell.getNumericCellValue();
                    }
                }

                Cell buyerGrandTotalCell = totalsRow.createCell(k + 1);
                buyerGrandTotalCell.setCellValue(buyerGrandTotal);
                buyerGrandTotalCell.setCellStyle(boldStyle);
            }

            double grandTotal = 0;
            for(int j = 0; j < categoriesList.size(); j++){
                Row categoryRow = sheet.getRow(j + 1);
                Cell categoryTotalCell = categoryRow.getCell(buyersList.size() + 1);
                if (categoryTotalCell != null) {
                    grandTotal += categoryTotalCell.getNumericCellValue();
                }
            }

            Cell grandTotalCell = totalsRow.createCell(buyersList.size() + 1);
            grandTotalCell.setCellValue(grandTotal);
            grandTotalCell.setCellStyle(boldStyle);

            for(int j = 0; j <= buyersList.size() + 1; j++){
                sheet.autoSizeColumn(j);
            }
        }
        
        file = new FileOutputStream(new File(path));
        workbook.write(file);
        workbook.close();
        tDB.close();
        file.close();
    }
}