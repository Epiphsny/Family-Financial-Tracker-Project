package team05.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.beans.property.SimpleStringProperty;
import team05.db.Buyer;
import team05.db.BuyerDB;

// Author Benjamin Hickey benjamin-hickey
public class TestGui {
  EditModel editModel;
  BuyerDB buyDB;

  @BeforeEach
  void setup() {
    editModel = new EditModel();
    buyDB = new BuyerDB();
  }

  @Test
  void addBuyer() {
    editModel.setBuyerNameProperty("George");
    assertEquals("George", editModel.buyerNameProperty().get());
    String name = editModel.buyerNameProperty().get();
    editModel.addBuyerName();
    Buyer buyer = buyDB.getBuyerFromName(name);
    assertEquals("George", buyer.toString()); 
  }

  @Test
  void editBuyer() {
    editModel.setSelectedBuyerNameProperty(new SimpleStringProperty("George"));
    editModel.setNewBuyerNameProperty("Karen");
    String name = editModel.newBuyerNameProperty().get();
    editModel.updateBuyerName();
    assertEquals("Karen", buyDB.getBuyerFromName(name).toString());
  }

  @Test
  void remBuyer() {
    editModel.setSelectedBuyerNameProperty(new SimpleStringProperty("Karen"));
    String name = editModel.newBuyerNameProperty().get();
    editModel.delBuyer();
    editModel.updateBuyerName();
    assertNotEquals("Karen", buyDB.getBuyerFromName(name).toString());
  }

  @AfterEach
  void tearDown() {
    buyDB.close();
  }
}
