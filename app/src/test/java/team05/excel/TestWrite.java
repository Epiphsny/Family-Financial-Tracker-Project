package team05.excel;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import team05.fft.Os;

//Author Eric Smith EWillCliff
/** TestWrite */
class TestWrite {

	@Test
    void writeData(){
        File outputFile = new File(Os.getDataPath().toString(), "Ledger-2024.xlsx");

        try{
        	LedgerWriter writer = new LedgerWriter();
            writer.write(outputFile.getAbsolutePath());
            assertTrue(outputFile.exists(), "Output file does not exist");
            
            FileInputStream fis = null;
            XSSFWorkbook workbook = null;

            try{
                fis = new FileInputStream(outputFile);
                workbook = new XSSFWorkbook(fis);

                assertEquals(12, workbook.getNumberOfSheets(), "Workbook does not contain 12 sheets");

                for(int i = 0; i < 12; i++){
                    String expectedSheetName = "2024-" + (i + 1);
                    assertEquals(expectedSheetName, workbook.getSheetAt(i).getSheetName(), "Sheet name wrong at index " + i);
                }
            } 
            catch(IOException e){
                fail("IOException occurred while reading the workbook: " + e.getMessage());
            } 
            finally{
                try{
                    if(workbook != null){
                        workbook.close();
                    }
                    if(fis != null){
                        fis.close();
                    }
                } 
                catch(IOException e){
                    e.printStackTrace();
                }
            }

        } 
        catch(IOException e){
            fail("IOException occurred: " + e.getMessage());
        } 
        finally{
            if(outputFile.exists()){
                outputFile.delete();
            }
        }
    }

}
