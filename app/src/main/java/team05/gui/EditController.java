package team05.gui;

import javafx.scene.control.Tab;

// Author Benjamin Hickey benjamin-hickey
public class EditController {
  private EditModel model;
  private EditBuilder builder;

  public EditController() {
    model = new EditModel();
    builder = new EditBuilder(model);
  }

  public Tab getTab() {
    return builder.build();
  }

}