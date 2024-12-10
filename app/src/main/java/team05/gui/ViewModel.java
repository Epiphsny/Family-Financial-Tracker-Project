package team05.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import team05.db.Buyer;
import team05.db.BuyerDB;
import team05.db.OutlierDB;
import team05.db.TransactionDB;
import team05.db.Where;
import team05.fft.Transaction;

// Author Benjamin Hickey benjamin-hickey
public class ViewModel {

  private SimpleStringProperty selectedItemProperty;
  private SimpleStringProperty selectedBuyerProperty;
  public ViewModel() {
    selectedItemProperty = new SimpleStringProperty("");
    selectedBuyerProperty = new SimpleStringProperty("");
  }

  public SimpleStringProperty selectedItemProperty() {
    return selectedItemProperty;
  }

  public void setSelectedItemProperty(String newItem) {
    selectedItemProperty.set(newItem);
  }

  public SimpleStringProperty selectedBuyerProperty() {
    return selectedBuyerProperty;
  }

  public void setSelectedBuyerProperty(String buyer) {
    selectedBuyerProperty.set(buyer);
  }

  public void assignTransaction(TableView<Transaction> table) {
        BuyerDB bdb = new BuyerDB();
        Buyer bid = bdb.getBuyerFromName(selectedBuyerProperty().get());
        bdb.close();
        OutlierDB odb = new OutlierDB();
        Transaction t = table.getSelectionModel().getSelectedItem();
        if (odb.exists(t)) odb.removeRow(new Transaction[] {t});
        odb.close();
  }
  
  public void refresh(TableView<Transaction> table, Where where) { 
    TransactionDB db = new TransactionDB();
    table.setItems(FXCollections.observableList(db.getTransactions(where)));
    db.close();
  }
}