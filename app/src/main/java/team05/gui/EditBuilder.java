package team05.gui;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import team05.db.Buyer;
import team05.db.BuyerRules;
import team05.db.BuyerRulesDB;
import team05.db.Categories;
import team05.db.CategoryDB;

// Author hypnotics-dev devhypnotics@proton.me
// Author Justin Babineau jbabine1
// Author Benjamin Hickey benjamin-hickey
//Author Eric Smith EWillCliff
/** Entry */
public class EditBuilder implements Builder<Tab> {

  private BorderPane border;
  private EditModel model;

  public EditBuilder(EditModel model) {
    border = new BorderPane();
    this.model = model;
  }

  @Override
  public Tab build() {

    TableView<Buyer> table = buyerOutput();
    border.setTop(getModes());
    border.setCenter(table);
    border.setRight(buyerActions(table));

    Tab tab = new Tab("Edit", border);
    return tab;
  }

  private Node getModes() {
    HBox hbox = new HBox(modeLabel(), buyerButton(), buyerRulesButton(), categoryButton());
    hbox.setSpacing(10);
    hbox.setPadding(new Insets(10, 0, 0, 10));
    return hbox;
  }

  private Node modeLabel() {
    Label label = new Label("Modes: ");
    return label;
  }

  private Node buyerButton() {
    Button buyer = new Button("Buyer");
    buyer.setOnAction(
      event -> {
        TableView<Buyer> table = buyerOutput();
        border.setCenter(table);
        border.setRight(buyerActions(table));
      });
    return buyer;
  }

  private Node buyerRulesButton() {
    Button buyerRules = new Button("Buyer Rules");
    buyerRules.setOnAction(
      event -> {
        TableView<BuyerRules> table = buyerRulesOutput();
        border.setCenter(table);
        border.setRight(buyerRulesActions(table));
      });
    return buyerRules;
  }

  private Node categoryButton() {
    Button category = new Button("Category");
    category.setOnAction(
      event -> {
        TableView<Categories> table = categoryOutput();
        border.setCenter(table);
        border.setRight(categoryActions(table));
      });
    return category;
  }

  private Node buyerActions(TableView<Buyer> table) {
    final HBox adds = new HBox(10, buyerAddButton(table), buyerNameTextField());
    adds.setSpacing(5);
    adds.setPadding(new Insets(10, 0, 0, 0));
    final HBox edits = new HBox(editBuyerButton(table), newBuyerNameTextField());
    edits.setSpacing(5);
    edits.setPadding(new Insets(10, 0, 10, 0));
    VBox vbox = new VBox(buyerModeLabel(), adds, edits, deleteBuyerButton(table));
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    return vbox;
  }

  private Node buyerModeLabel() {
    Label mode = new Label("Buyer Mode");
    return mode;
  }

  private Node buyerAddButton(TableView<Buyer> table) {
    Button add = new Button("Add");
    add.setOnAction(
    event -> {
      model.addBuyerName();
      table.setItems(FXCollections.observableList(model.getBuyers()));
    });
    return add;
  }

  private Node buyerNameTextField() {
    TextField buyerName= new TextField();
    buyerName.setPromptText("Buyer Name");
    buyerName.textProperty().bindBidirectional(model.buyerNameProperty());
    return buyerName;
  }

  private Node newBuyerNameTextField() {
    TextField newName = new TextField();
    newName.setPromptText("New Name");
    newName.textProperty().bindBidirectional(model.newBuyerNameProperty());
    return newName;
  }

  private Node editBuyerButton(TableView<Buyer> table) {
    Button edit = new Button("Edit");
    edit.setOnAction(
      event -> {
        model.updateBuyerName();
        table.setItems(FXCollections.observableList(model.getBuyers()));
      });

    return edit;
  }

  private Node deleteBuyerButton(TableView<Buyer> table) {
    Button del = new Button("Delete");
    del.setOnAction(
      event -> {
        model.delBuyer();
        table.setItems(FXCollections.observableList(model.getBuyers()));
      });
    return del;
  }


  private Node buyerRulesActions(TableView<BuyerRules> table) {
    final ComboBox<String> buyersEdit =
        new ComboBox<>(FXCollections.observableList(model.getBuyerNames()));
    buyersEdit.setPromptText("Update Buyer");

    
    final HBox adds = new HBox(addRuleButton(table), buyerRuleTextField(), addBuyerComboBox());
    adds.setSpacing(5);
    adds.setPadding(new Insets(10, 0, 0, 0));
    final HBox editsRules = new HBox(editRuleButton(table), newRuleTextField());
    editsRules.setSpacing(5);
    editsRules.setPadding(new Insets(10, 0, 10, 0));
    final HBox editsNames = new HBox(editBuyerButton(table, buyersEdit), buyersEdit);
    editsNames.setSpacing(5);
    editsNames.setPadding(new Insets(10, 0, 10, 0));
    
    final VBox vbox = new VBox(buyerRuleModeLabel(), adds, editsRules, editsNames, deleteRuleButton(table));
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    return vbox;
  }

  private Node buyerRuleModeLabel() {
    Label label = new Label("Buyer Rules Mode");
    return label;
  }

  private Node addRuleButton(TableView<BuyerRules> table) {
    Button add = new Button("Add");
    add.setOnAction(
      event -> {
        String rule = model.buyerRuleProperty().get();
        if (ruleCheck(rule, "Buyer Rules")) return;
        model.addBuyerRule();
        table.setItems(FXCollections.observableList(model.getBuyerRules()));
        });
    return add;
  }

  private Node buyerRuleTextField() {
    TextField buyerRule = new TextField();
    buyerRule.setPromptText("Rule");
    buyerRule.textProperty().bindBidirectional(model.buyerRuleProperty());
    return buyerRule;
  }

  private Node addBuyerComboBox() {
    ComboBox<String> buyers =
      new ComboBox<>(FXCollections.observableList(model.getBuyerNames()));
    buyers.setPromptText("Buyer");
    buyers.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      if (newValue != null) 
        model.setSelectedRuleBuyerProperty(buyers.getSelectionModel().getSelectedItem());
    });
    return buyers;
  }

  private Node editRuleButton(TableView<BuyerRules> table) {
    Button editRule = new Button("Edit Rule");
    editRule.setOnAction(
        event -> {
          String rule = model.newBuyerRuleProperty().get();
          if (ruleCheck(rule, "Buyer Rules")) return;
          model.updateBuyerRule(table.getSelectionModel().getSelectedItem());
          table.setItems(FXCollections.observableList(model.getBuyerRules()));
        });
    return editRule;
  }

  private Node newRuleTextField() {
    TextField newRule = new TextField();
    newRule.setPromptText("Update Rule");
    newRule.textProperty().bindBidirectional(model.newBuyerRuleProperty());
    return newRule;
  }

  private Node editBuyerButton(TableView<BuyerRules> table, ComboBox<String> buyersEdit) {//Not working
    Button editBuyer = new Button("Edit Buyer");
    editBuyer.setOnAction(
        event -> {
          ObservableList<BuyerRules> rules = table.getSelectionModel().getSelectedItems();
          BuyerRules[] br = new BuyerRules[rules.size()];
          int i = 0;
          for (BuyerRules buyerRules : rules) {
            br[i++] =
                new BuyerRules(buyerRules.getPk(), model.getBuyerFromName(buyersEdit.getValue()));
          }
          model.updateRowsWithName(br);
          model.setNewBuyerRuleProperty("");
          table.setItems(FXCollections.observableList(model.getBuyerRules()));
        });
    return editBuyer;
  }

  private Node deleteRuleButton(TableView<BuyerRules> table) {
    Button del = new Button("Delete");
    del.setOnAction(
      event -> {
        model.delRule(table.getSelectionModel().getSelectedItems().toArray(new BuyerRules[0]));
        table.setItems(FXCollections.observableList(model.getBuyerRules()));
      });
    return del;
  }

  private Node categoryActions(TableView<Categories> table) {
    CategoryDB db = new CategoryDB();
    table.setItems(FXCollections.observableList(db.getCategories()));
    final Label label = new Label("Category Mode");
    final Button add = new Button("Add");
    final TextField createRule = new TextField();
    createRule.setPromptText("Rule");
    createRule.setPrefWidth(100);
    final TextField createCat = new TextField();
    createCat.setPromptText("Name");
    createCat.setPrefWidth(100);
    final Button del = new Button("Delete");
    final Button editName = new Button("Edit Name");
    final TextField newName = new TextField();
    newName.setPromptText("Name");
    newName.setPrefWidth(100);
    final Button editRule = new Button("Edit Rule");
    final TextField newRule = new TextField();
    newRule.setPromptText("Rule");
    newRule.setPrefWidth(100);
    db.close();

    add.setOnAction(
        event -> {
          CategoryDB catDB = new CategoryDB();
          String rule = createRule.getText();
          if (ruleCheck(rule, "Categories")) return;
          Categories category = new Categories(createRule.getText(), createCat.getText());
          createRule.clear();
          createCat.clear();
          catDB.newRows(new Categories[] {category});
          table.setItems(FXCollections.observableList(catDB.getCategories()));
          catDB.close();
        });
    editName.setOnAction(
        event -> {
          CategoryDB catDB = new CategoryDB();
          catDB.updateRowWithName(
              new Categories[] {
                new Categories(
                    table.getSelectionModel().getSelectedItem().getPk(), newName.getText())
              });
          newName.clear();
          table.setItems(FXCollections.observableList(catDB.getCategories()));
          catDB.close();
        });
    editRule.setOnAction(
        event -> {
          String rule = newRule.getText();
          if (ruleCheck(rule, "Category Rules")) return;
          CategoryDB catDB = new CategoryDB();
          catDB.updateRowWithRule(
              new Categories[] {
                new Categories(rule, table.getSelectionModel().getSelectedItem().getName())
              },
              new String[] {table.getSelectionModel().getSelectedItem().getPk()});
          newName.clear();
          table.setItems(FXCollections.observableList(catDB.getCategories()));
          catDB.close();
        });
    del.setOnAction(
        event -> {
          CategoryDB catDB = new CategoryDB();
          catDB.removeRow(table.getSelectionModel().getSelectedItems().toArray(new Categories[0]));
          table.setItems(FXCollections.observableList(catDB.getCategories()));
          newRule.clear();
          catDB.close();
          ;
        });

    final HBox creates = new HBox(add, createRule, createCat);
    creates.setSpacing(5);
    creates.setPadding(new Insets(10, 0, 0, 0));
    final HBox removes = new HBox(del);
    removes.setSpacing(5);
    removes.setPadding(new Insets(10, 0, 0, 0));
    final HBox editsName = new HBox(editName, newName);
    editsName.setSpacing(5);
    editsName.setPadding(new Insets(10, 0, 0, 0));
    final HBox editsRule = new HBox(editRule, newRule);
    editsRule.setSpacing(5);
    editsRule.setPadding(new Insets(10, 0, 0, 0));

    final VBox vbox = new VBox(label, creates, editsName, editsRule, removes);
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    return vbox;
  }

  private TableView<Buyer> buyerOutput() {
    TableView<Buyer> table = new TableView<>();

    // TODO: Make buyer Col bigger by default
    table.setPlaceholder(new Label("No Buyers in System"));
    table.setEditable(false);
    table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    TableColumn<Buyer, String> buyer = new TableColumn<>("Buyers");
    buyer.setCellValueFactory(model.getTableBuyersFactory());
    table.getColumns().add(buyer);

    table.setItems(FXCollections.observableList(model.getTableBuyers()));
    
    table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {   
      if(newValue != null) //Fixes error from issue #83
          model.setSelectedBuyerNameProperty(newValue.nameProperty());
      });
    return table;
  }

  private TableView<BuyerRules> buyerRulesOutput() {
    TableView<BuyerRules> table = new TableView<>();
    table.setPlaceholder(new Label("No Buyer Rules In System"));
    table.setEditable(false);
    table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    TableColumn<BuyerRules, SimpleStringProperty> name = new TableColumn<>("Buyers");
    name.setCellValueFactory(new PropertyValueFactory<BuyerRules, SimpleStringProperty>("name"));
    TableColumn<BuyerRules, SimpleStringProperty> rule = new TableColumn<>("Rules");
    rule.setCellValueFactory(new PropertyValueFactory<BuyerRules, SimpleStringProperty>("rule"));

    table.getColumns().addAll(rule, name);
    BuyerRulesDB db = new BuyerRulesDB();
    table.setItems(FXCollections.observableList(db.getRules()));
    db.close();
    return table;
  }

  private TableView<Categories> categoryOutput() {
    TableView<Categories> table = new TableView<>();
    table.setPlaceholder(new Label("No Categories In System"));
    table.setEditable(false);
    table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    TableColumn<Categories, SimpleStringProperty> rule = new TableColumn<>("Rules");
    rule.setCellValueFactory(new PropertyValueFactory<Categories, SimpleStringProperty>("rule"));
    TableColumn<Categories, SimpleStringProperty> name = new TableColumn<>("Categories");
    name.setCellValueFactory(new PropertyValueFactory<Categories, SimpleStringProperty>("name"));

    table.getColumns().addAll(rule, name);
    CategoryDB db = new CategoryDB();
    db.close();
    return table;
  }

  private boolean ruleCheck(String rule, String group) {
    if (rule.length() < 3) {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setContentText(group + " must 3 or more characters long");
      alert.show();
      return true;
    }
    // Not sure if possible curently, but if it becomes possible just uncomment this line
    // if (rule.indexOf((int) '\n') > 0) {
    //  Alert alert = new Alert(AlertType.WARNING);
    //  alert.setContentText("Cannot have newline in a " + group);
    //  alert.show();
    //  return true;
    // }
    return false;
  }
}
