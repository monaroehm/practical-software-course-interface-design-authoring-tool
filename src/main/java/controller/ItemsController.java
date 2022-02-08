package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import model.Database;
import model.Item;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ItemsController implements Initializable {
    @FXML private TableView itemsTable;
    @FXML private TableColumn<Item, String> name;
    @FXML private TableColumn<Item, String> description;
    @FXML private TableColumn<Item, String> path;
    @FXML private TextField nameTextField;
    @FXML private TextField descriptionTextField;
    @FXML private TextField pathTextField;

    private ObservableList<Item> itemsData = FXCollections.observableArrayList();
    private final Database db = Database.getInstance();

    /**
     * Gets {@link Item}s from database and parses them into table.
     *
     * @throws SQLException
     */
    public void setTableFromDB() throws SQLException {
        itemsData = db.getAllItems();
        itemsTable.setItems(itemsData);
    }

    /**
     * Gets called on scene load. Sets table columns.
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setTableFromDB();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        itemsTable.setEditable(true);
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        description.setCellFactory(TextFieldTableCell.forTableColumn());
        path.setCellFactory(TextFieldTableCell.forTableColumn());
    }


    /**
     * Change listeners waiting for new input value and applying it to the DB entry.
     *
     * @param editedCell - Cell edited.
     * @throws SQLException
     */
    public void changeNameCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        Item itemSelected = (Item) itemsTable.getSelectionModel().getSelectedItem();
        itemSelected.setName(editedCell.getNewValue().toString());
        itemSelected.save();
    }

    public void changeDescriptionCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        Item itemSelected = (Item) itemsTable.getSelectionModel().getSelectedItem();
        itemSelected.setDescription(editedCell.getNewValue().toString());
        itemSelected.save();
    }

    public void changePathCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        Item itemSelected = (Item) itemsTable.getSelectionModel().getSelectedItem();
        itemSelected.setPath(editedCell.getNewValue().toString());
        itemSelected.save();
    }

    /**
     * Gets input values from application.
     * Creates new {@link Item} in DB, and adds it to the currently shown table.
     *
     * @throws SQLException
     */
    public void newItemButtonPushed() throws SQLException {
        if(nameTextField.getText().isEmpty()) return;

        String name = nameTextField.getText();
        String desc = descriptionTextField.getText();
        String path = pathTextField.getText();
        nameTextField.clear();
        descriptionTextField.clear();
        pathTextField.clear();

        for (Item i : itemsData) {
            if (i.getName().equals(name)) return;
        }

        itemsData.add(db.createItem(name, desc, path));
    }

    /**
     * Handles use of delete button.
     * Deletes the currently selected table entry from the table and the DB.
     * Shows user dialog with additional information to confirm deletion.
     *
     * @throws SQLException
     */
    public void deleteItemButtonPushed() throws SQLException {
        Item itemSelected = (Item) itemsTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this item?\n\n\tid: " + itemSelected.getItem_id() +
                "\n\tname: " + itemSelected.getName());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            itemsData.remove(itemSelected);
            itemSelected.delete();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    /**
     * Handles use of back button. Loads menu stage.
     */
    public void backButtonPushed() {
        Stage stage = (Stage) itemsTable.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
