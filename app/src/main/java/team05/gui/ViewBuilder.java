package team05.gui;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Duration;
import team05.db.Buyer;
import team05.db.BuyerDB;
import team05.db.Categories;
import team05.db.CategoryDB;
import team05.db.FileDB;
import team05.db.OutlierDB;
import team05.db.TransactionDB;
import team05.db.Where;
import team05.excel.LedgerWriter;
import team05.excel.SummaryWriter;
import team05.excel.XLSXReader;
import team05.fft.Os;
import team05.fft.Transaction;

// Author hypnotics-dev devhypnotics@proton.me
// Author Justin Babineau jbabine1
// Author Eric Smith EWillCliff
// Author Benjamin Hickey Benjamin-Hickey

/** MainScene */
public class ViewBuilder implements Builder<Tab> {

  private Stage stage;
  TableView<Transaction> table = new TableView<>();
  Where where = new Where();
  private BuyerDB buyerDB;
  private SimpleStringProperty rulesTextFieldProperty;
  private ViewModel model;

  public ViewBuilder(
      Stage stage,
      SimpleStringProperty rulesTextFieldProperty,
      ViewModel model) { // Need to have stage controls in "ViewController" class
    this.stage = stage;
    this.rulesTextFieldProperty = rulesTextFieldProperty;
    this.model = model;
  }

  /*
   * Layout ==>
   * Top: filter options :: HBox :: Give Pref Height
   * Left and Center TableView :: Vbox :: Auto assigned
   * Right: Action buttons :: VBox :: Give Pref Width
   * Top: Tabs :: Tab :: Give Pref Width
   */
  @Override
  public Tab build() {
    BorderPane border = new BorderPane();
    border.setCenter(table());
    border.setRight(getActions());
    border.setTop(getFilter());
    // scene.getStylesheets().add("../../../resources/stylesheet.css");
    Tab tab = new Tab("View", border);
    return tab;
  }

  /*
   * The Center of the screen (Takes up the most real estate)
   * Issue #31
   */
  private Node table() {
    final Label label = new Label("Transactions");

    table.setEditable(false);

    final double datew = 90;
    final double descw = 250;
    final double valw = 90;
    final double balw = 95;
    final double buyw = 120;
    final double catw = 120;

    TableColumn<Transaction, SimpleStringProperty> date =
        new TableColumn<Transaction, SimpleStringProperty>("Date");
    date.setCellValueFactory(new PropertyValueFactory<Transaction, SimpleStringProperty>("date"));
    date.setPrefWidth(datew);

    TableColumn<Transaction, SimpleStringProperty> desc =
        new TableColumn<Transaction, SimpleStringProperty>("Description");
    desc.setCellValueFactory(new PropertyValueFactory<Transaction, SimpleStringProperty>("desc"));
    desc.setPrefWidth(descw);

    TableColumn<Transaction, SimpleDoubleProperty> val =
        new TableColumn<Transaction, SimpleDoubleProperty>("Value");
    val.setCellValueFactory(new PropertyValueFactory<Transaction, SimpleDoubleProperty>("val"));
    val.setPrefWidth(valw);

    TableColumn<Transaction, SimpleDoubleProperty> bal =
        new TableColumn<Transaction, SimpleDoubleProperty>("Balance");
    bal.setCellValueFactory(new PropertyValueFactory<Transaction, SimpleDoubleProperty>("bal"));
    bal.setPrefWidth(balw);

    TableColumn<Transaction, SimpleStringProperty> buy =
        new TableColumn<Transaction, SimpleStringProperty>("Buyer");
    buy.setCellValueFactory(new PropertyValueFactory<Transaction, SimpleStringProperty>("buy"));
    buy.setPrefWidth(buyw);

    TableColumn<Transaction, SimpleStringProperty> cat =
        new TableColumn<Transaction, SimpleStringProperty>("Category");
    cat.setCellValueFactory(new PropertyValueFactory<Transaction, SimpleStringProperty>("cat"));
    cat.setPrefWidth(catw);

    // Warings are from the fact that table is of type TableView<Transaction> and it's taking an
    // array of TableColumn<Transaction,?> as the type from each collum can differ
    table.getColumns().addAll(date, desc, buy, val, bal, cat);
    table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    table
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              if (newValue != null) model.setSelectedItemProperty(newValue.toString());
            });

    model.refresh(table, where);

    VBox vbox = new VBox(label, table);
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));

    return vbox;
  }

  private VBox getActions() {
    buyerDB = new BuyerDB();
    final ComboBox<String> buyers =
        new ComboBox<>(FXCollections.observableList(buyerDB.getBuyerNames())); //
    buyers.setPromptText("Buyers");
    final ComboBox<String> fileList =
        new ComboBox<>(FXCollections.observableList(FileDB.getFiles()));
    fileList.setPromptText("File");

    HBox outliers = new HBox(assignTransactionButton(), buyerComboBox()); //
    outliers.setSpacing(5);
    outliers.setPadding(new Insets(10, 0, 0, 0));
    HBox btns = new HBox(importButton(fileList), exportButton(), summaryButton(), refreshButton(buyers));
    btns.setSpacing(5);
    btns.setPadding(new Insets(10, 0, 0, 0));
    HBox reverts = new HBox(fileList, revertTransactionButton(fileList)); //
    reverts.setSpacing(5);
    reverts.setPadding(new Insets(10, 0, 0, 0));
    VBox vbox = new VBox(actionLabel(), btns, outliers, reverts, removeBuyerButton());
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    buyerDB.close();
    return vbox;
  }

  private Node actionLabel() {
    Label label = new Label("Actions:");
    return label;
  }

  private Node assignTransactionButton() {
    Button beOutilier = new Button("Assign Transaction");
    beOutilier.setOnAction(
        event -> {
          model.assignTransaction(table);
          model.refresh(table, where);
        });
    return beOutilier;
  }

  private Node buyerComboBox() {
    final ComboBox<String> buyers =
        new ComboBox<>(FXCollections.observableList(buyerDB.getBuyerNames()));
    buyers.setPromptText("Buyers");
    buyers.getSelectionModel().getSelectedItem();
    buyers
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              if (newValue != null)
                model.setSelectedBuyerProperty(buyers.getSelectionModel().getSelectedItem());
            });
    return buyers;
  }

  private Node importButton(ComboBox<String> fileList) {
    Button importButton = new Button("Import");
    importButton.setOnAction(
        event -> {
          FileChooser file = new FileChooser();
          file.setTitle("Import Bank Statment");
          File exFile = file.showOpenDialog(stage);
          try {
            XLSXReader xlsxReader = new XLSXReader();
            xlsxReader.read(exFile.toString());
          } catch (IOException e) {
            Os.loggerErr(e.getMessage());
          }
          fileList.setItems(FXCollections.observableList(FileDB.getFiles()));
          model.refresh(table, where);
        });
    return importButton;
  }

  private Node exportButton() {
    Button exportButton = new Button("Export");
    exportButton.setOnAction(
        event -> {
          FileChooser files = new FileChooser();
          files.setTitle("Export Ledger");
          files.setInitialFileName("Ledger-2023.xlsx");
          try {
            LedgerWriter ledger = new LedgerWriter();
            ledger.write(files.showSaveDialog(stage).toString());
          } catch (IOException e) {
            Os.loggerErr("ERROR: Failed to export Ledger");
            Os.loggerErr(e.getMessage());
          }
        });
    return exportButton;
  }
  
  private Node summaryButton() {
	    Button summaryButton = new Button("Summary");
	    summaryButton.setOnAction(
	        event -> {
	          FileChooser files = new FileChooser();
	          files.setTitle("Export Summary");
	          files.setInitialFileName("Spending-Summary-2023.xlsx");
	          try {
	            SummaryWriter summary = new SummaryWriter();
	            summary.write(files.showSaveDialog(stage).toString());
	          } catch (IOException e) {
	            Os.loggerErr("ERROR: Failed to export Summary");
	            Os.loggerErr(e.getMessage());
	          }
	        });
	    return summaryButton;
	  }

  private Node refreshButton(ComboBox<String> buyers) {
    Button refresh = new Button("Refresh");
    refresh.setOnAction(
        event -> {
          model.refresh(table, where);
          BuyerDB bdb = new BuyerDB();
          buyers.setItems(FXCollections.observableList(bdb.getBuyerNames()));
          bdb.close();
        });
    return refresh;
  }

  private Node removeBuyerButton() {
    Button removeOutlier = new Button("Remove Buyer");
    removeOutlier.setOnAction(
        event -> {
          OutlierDB odb = new OutlierDB();
          odb.removeRow(table.getSelectionModel().getSelectedItems().toArray(new Transaction[0]));
          odb.close();
          model.refresh(table, where);
        });
    return removeOutlier;
  }

  private Node revertTransactionButton(ComboBox<String> fileList) {
    Button revertTransactions = new Button("Revert");
    revertTransactions.setOnAction(
        event -> {
          FileDB.revertTransactions(
              FileDB.getFileId(fileList.getSelectionModel().getSelectedItem()),
              fileList.getSelectionModel().getSelectedItem());
          model.refresh(table, where);
          fileList.setItems(FXCollections.observableList(FileDB.getFiles()));
        });
    return revertTransactions;
  }

  private Node fileListComboBox() {
    ComboBox<String> fileList = new ComboBox<>(FXCollections.observableList(FileDB.getFiles()));
    fileList.setPromptText("File");
    fileList.getSelectionModel().getSelectedItem();

    return fileList;
  }

  private VBox getFilter() {
    /*
     * Start Date, End Date, Buyer, Category, (dropdown menu)
     * Rule (Text Field)
     * Show Outliers,Show Unasigned, Ignore filters (checkboxes)
     * Apply Filters (Button)
     */

    TransactionDB tdb = new TransactionDB();
    final DatePicker start = new DatePicker(LocalDate.ofEpochDay(tdb.getFirstDay()));
    final Tooltip startTip = new Tooltip("Only show transactions before this date");
    final DatePicker end = new DatePicker(LocalDate.ofEpochDay(tdb.getLastDay()));
    final Tooltip endTip = new Tooltip("Only show transactions after this date");

    tdb.close();
    buyerDB = new BuyerDB();
    final ComboBox<String> buyers = new ComboBox<>();
    buyers.setPromptText("Buyers");
    buyers.getItems().add(null);
    buyers.getItems().addAll(FXCollections.observableList(buyerDB.getBuyerNames()));
    final Tooltip buyersTip = new Tooltip("Only show transactions belonging to this buyer");
    buyerDB.close();
    CategoryDB cdb = new CategoryDB();
    final ComboBox<String> cats = new ComboBox<>();
    cats.setPromptText("Categories");
    cats.getItems().add(null);
    cats.getItems().addAll(FXCollections.observableList(cdb.getCategoryNames()));
    final Tooltip catTip = new Tooltip("Only show transactions belonging to this category");
    cdb.close();

    final TextField rules = new TextField();
    rules.setPromptText("Search");
    final Tooltip rulesTip =
        new Tooltip("Only show transactions with description LIKE the one specified");

    final ComboBox<String> files = new ComboBox<>();
    files.setPromptText("Files");
    files.getItems().add(null);
    files.getItems().addAll(FXCollections.observableList(FileDB.getFiles()));
    final Tooltip filesTip = new Tooltip("Filters by file");

    final CheckBox outliers = new CheckBox("Buyers Assigned");
    final Tooltip outliersTip = new Tooltip("Only shows transactions that you assigned to a buyer");

    final CheckBox noBuyer = new CheckBox("Buyerless");
    final Tooltip noBuyerTip = new Tooltip("Only shows transactions with no buyer");

    final CheckBox noFilter = new CheckBox("Disable");
    final Tooltip noFilterTip = new Tooltip("Diable all other filters");

    final Button btn = new Button("Apply");
    final Tooltip btnTip = new Tooltip("Applies specified filters to the Transaction View");

    final Button reset = new Button("Reset");
    final Tooltip resetTip =
        new Tooltip("Refreshes contents of filters, and resets them to their default states");

    showDelay(
        startTip,
        endTip,
        buyersTip,
        catTip,
        rulesTip,
        outliersTip,
        filesTip,
        noBuyerTip,
        noFilterTip,
        btnTip,
        resetTip);

    Tooltip.install(start, startTip);
    Tooltip.install(end, endTip);
    Tooltip.install(buyers, buyersTip);
    Tooltip.install(cats, catTip);
    Tooltip.install(rules, rulesTip);
    Tooltip.install(outliers, outliersTip);
    Tooltip.install(files, filesTip);
    Tooltip.install(noBuyer, noBuyerTip);
    Tooltip.install(noFilter, noFilterTip);
    Tooltip.install(btn, btnTip);
    Tooltip.install(reset, resetTip);

    outliers.setOnAction(
        event -> {
          noBuyer.setSelected(false);
        });

    reset.setOnAction(
        event -> {
          outliers.setSelected(false);
          noBuyer.setSelected(false);
          noFilter.setSelected(false);

          BuyerDB lbdb = new BuyerDB();
          cats.setPromptText("Buyers");
          cats.setItems(FXCollections.observableList(lbdb.getBuyerNames()));
          lbdb.close();
          CategoryDB lcdb = new CategoryDB();
          cats.setPromptText("Categories");
          cats.setItems(FXCollections.observableList(lcdb.getCategoryNames()));
          lcdb.close();
          files.setPromptText("Files");
          files.setItems(FXCollections.observableList(FileDB.getFiles()));
          TransactionDB ltdb = new TransactionDB();
          start.setValue(LocalDate.ofEpochDay(ltdb.getFirstDay()));
          end.setValue(LocalDate.ofEpochDay(ltdb.getLastDay()));
          ltdb.close();
          where = new Where();
          model.refresh(table, where);
        });

    noBuyer.setOnAction(
        event -> {
          outliers.setSelected(false);
        });

    btn.setOnAction(
        event -> {
          if (noFilter.isSelected()) {
            where = new Where();
            model.refresh(table, where);
            return;
          }
          where = new Where(start.getValue(), end.getValue());

          if (!rulesTextFieldProperty.get().equals("")) where.setRule(rulesTextFieldProperty.get());

          if (buyers.getValue() != null)
            where.setBuyerFilter(new Buyer[] {new Buyer(buyers.getValue())});

          if (cats.getValue() != null)
            where.setCatsFilter(new Categories[] {new Categories("Placeholder", cats.getValue())});

          if (files.getValue() != null)
            where.setFileIds(new Integer[] {FileDB.getFileId(files.getValue())});

          if (outliers.isSelected()) where.showOutliers();
          else if (noBuyer.isSelected()) where.showUnassigned();

          model.refresh(table, where);
        });

    HBox filters = new HBox(start, end, buyers, cats, rules, files);
    filters.setPadding(new Insets(10, 0, 0, 10));
    filters.setSpacing(5);
    HBox checks = new HBox(outliers, noBuyer, noFilter);
    checks.setPadding(new Insets(10, 0, 0, 10));
    checks.setSpacing(5);
    HBox buttons = new HBox(btn, reset);
    buttons.setPadding(new Insets(10, 0, 0, 10));
    buttons.setSpacing(5);
    VBox vbox = new VBox(filterLabel(), filters, checks, buttons);

    return vbox;
  }

  private Node filterLabel() {
    Label label = new Label("Transaction Filters");
    return label;
  }

  private void showDelay(Tooltip... tool) {
    for (Tooltip tooltip : tool) {
      tooltip.setShowDelay(new Duration(500));
    }
  }
}
