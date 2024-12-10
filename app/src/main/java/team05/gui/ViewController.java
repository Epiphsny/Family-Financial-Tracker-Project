package team05.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

// Author Benjamin Hickey benjamin-hickey
public class ViewController {
  private ViewBuilder builder;
  private ViewModel model;
  private SimpleStringProperty rulesTextFieldProperty;

  public ViewController(Stage stage) {
    model = new ViewModel();
    builder = new ViewBuilder(stage, rulesTextFieldProperty, model);
  }

  public Tab getTab() {
    return builder.build();
  }
}