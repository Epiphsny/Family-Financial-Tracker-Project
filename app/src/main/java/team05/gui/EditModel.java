package team05.gui;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import team05.db.Buyer;
import team05.db.BuyerDB;
import team05.db.BuyerRules;
import team05.db.BuyerRulesDB;

// Author Benjamin Hickey benjamin-hickey
//Author Eric Smith EWillCliff
public class EditModel {

  private SimpleStringProperty buyerNameProperty;
  private SimpleStringProperty newBuyerNameProperty;
  private SimpleStringProperty selectedBuyerNameProperty;

  private SimpleStringProperty buyerRuleProperty;
  private SimpleStringProperty newBuyerRuleProperty;
  private SimpleStringProperty selectedRuleBuyerProperty;

  


  public EditModel() {
    buyerNameProperty = new SimpleStringProperty("");
    newBuyerNameProperty = new SimpleStringProperty("");
    selectedBuyerNameProperty = new SimpleStringProperty("");
    buyerRuleProperty = new SimpleStringProperty("");
    newBuyerRuleProperty = new SimpleStringProperty("");
    selectedRuleBuyerProperty = new SimpleStringProperty();
  }

  public SimpleStringProperty buyerNameProperty() {
    return buyerNameProperty;
  }

  public void setBuyerNameProperty(String name) {
    buyerNameProperty.set(name);
  }

  public SimpleStringProperty selectedBuyerNameProperty() {
    return selectedBuyerNameProperty;
  }

  public void setSelectedBuyerNameProperty(SimpleStringProperty name) {
    selectedBuyerNameProperty = name;
  }

  public ArrayList<Buyer> getBuyers() {
    BuyerDB db = new BuyerDB();
    ArrayList<Buyer> buyers = db.getBuyers();
    db.close();
    return buyers;
  }

  public ArrayList<Buyer> getTableBuyers() {
    BuyerDB db = new BuyerDB("fft.db");
    ArrayList<Buyer> buyers = db.getBuyers();
    db.close();
    return buyers;
  }
  
  public ArrayList<String> getBuyerNames() {
	BuyerDB db = new BuyerDB();
	ArrayList<String> buyerNames = db.getBuyerNames();
	db.close();
	return buyerNames;
  }
  
  public void updateRowsWithName(BuyerRules[] rules) {
	 BuyerRulesDB ruleDB = new BuyerRulesDB();
	 ruleDB.updateRowsWithName(rules);
  }
  
  public Buyer getBuyerFromName(String name) {
	 BuyerRulesDB ruleDB = new BuyerRulesDB();
	 return ruleDB.getBuyerFromName(name);
  }

  public PropertyValueFactory<Buyer, String> getTableBuyersFactory() {
    return new PropertyValueFactory<Buyer, String>("name");
  }

  public void addBuyerName() {
    BuyerDB db = new BuyerDB();
      db.newRows(new Buyer[] {new Buyer(buyerNameProperty().get())});
    db.close();
    setBuyerNameProperty("");
  }

  public void updateBuyerName() {
    BuyerDB db = new BuyerDB();
    Buyer old = db.getBuyerFromName(selectedBuyerNameProperty.get());
    db.updateRow(
      new Buyer[] {
        new Buyer(old.getPk(), newBuyerNameProperty().get())
      });
    db.close();
    setNewBuyerNameProperty("");
  }

  public void delBuyer() {
    BuyerDB db = new BuyerDB();
    Buyer[] buyers = new Buyer[1];
    buyers[0] = db.getBuyerFromName(selectedBuyerNameProperty.get());
    db.removeRow(buyers);
    db.close();
  }

  public SimpleStringProperty newBuyerNameProperty() {
    return newBuyerNameProperty;
  }

  public void setNewBuyerNameProperty(String name) {
    newBuyerNameProperty.set(name);
  }

  //BuyerRules
  public SimpleStringProperty buyerRuleProperty() {
    return buyerRuleProperty;
  }

  public void setBuyerRuleProperty(String name) {
    buyerRuleProperty.set(name);
  }

  public SimpleStringProperty newBuyerRuleProperty() {
    return newBuyerRuleProperty;
  }

  public void setNewBuyerRuleProperty(String name) {
    newBuyerRuleProperty.set(name);
  }

  public SimpleStringProperty selectedRuleBuyerProperty() {
    return selectedRuleBuyerProperty;
  }

  public void setSelectedRuleBuyerProperty(String name) {
    if (name != null)
      selectedRuleBuyerProperty.set(name);
  }

  public void addBuyerRule() {
    BuyerRulesDB ruleDB = new BuyerRulesDB();
    ruleDB.newRows(
      new BuyerRules[] {new BuyerRules(buyerRuleProperty().get(), 
        ruleDB.getBuyerFromName(selectedRuleBuyerProperty().get()))});
    ruleDB.close();
    setBuyerRuleProperty("");
  }

  public ArrayList<BuyerRules> getBuyerRules() {
    BuyerRulesDB ruleDB = new BuyerRulesDB();
    ArrayList<BuyerRules> rules = ruleDB.getRules();
    ruleDB.close();
    return rules;
  }

  public void updateBuyerRule(BuyerRules old) {
    BuyerRulesDB rulesDB = new BuyerRulesDB();
    rulesDB.updateRowsWithRule(
              new BuyerRules[] {new BuyerRules(newBuyerRuleProperty().get(), rulesDB.getBuyerFromRegex(old.getPk()))},
              new String[] {old.getPk()});
    rulesDB.close();
    setNewBuyerRuleProperty("");
  }

  public void updateBuyerRuleBuyer() {
    BuyerRulesDB rulesDB = new BuyerRulesDB();
    
    setNewBuyerRuleProperty("");
    rulesDB.close();
  }

  public void delRule(BuyerRules[] rules) {
    BuyerRulesDB rulesDB = new BuyerRulesDB();
    rulesDB.removeRow(rules);
    rulesDB.close();
  }
}