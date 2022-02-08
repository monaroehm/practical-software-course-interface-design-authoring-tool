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
import javafx.util.converter.IntegerStringConverter;
import model.Database;
import model.UserHasItem;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserHasItemsController implements Initializable {
    @FXML
    private TableView itemsTable;
    @FXML
    private TableColumn<UserHasItem, Integer> count;
    @FXML
    private TextField userIDTextField;
    @FXML
    private TextField itemIDTextField;
    @FXML
    private TextField countTextField;

    private ObservableList<UserHasItem> itemsData = FXCollections.observableArrayList();
    private final Database db = Database.getInstance();

    /**
     * Gets {@link UserHasItem}s from database and parses them into table.
     *
     * @throws SQLException
     */
    public void setTableFromDB() throws SQLException {
        itemsData = db.getAllUserHasItems();
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
        count.setCellFactory(TextFieldTableCell.<UserHasItem, Integer>forTableColumn(new IntegerStringConverter()));
    }

    /**
     * Change listeners waiting for new input value and applying it to the DB entry.
     *
     * @param editedCell - Cell edited.
     * @throws SQLException
     */
    public void changeCountCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        try {
            UserHasItem itemSelected = (UserHasItem) itemsTable.getSelectionModel().getSelectedItem();
            itemSelected.setCount(Integer.parseInt(editedCell.getNewValue().toString()));
            itemSelected.save();
        } catch (NumberFormatException e) {
        }
    }

    /**
     * Gets input values from application.
     * Creates new {@link UserHasItem} in DB, and adds it to the currently shown table.
     *
     * @throws SQLException
     */
    public void newItemButtonPushed() throws SQLException {
        if (userIDTextField.getText().isEmpty() || itemIDTextField.getText().isEmpty()
                || countTextField.getText().isEmpty()) return;
        try {
            int user_id = Integer.parseInt(userIDTextField.getText());
            int item_id = Integer.parseInt(itemIDTextField.getText());
            int count = Integer.parseInt(countTextField.getText());
            userIDTextField.clear();
            itemIDTextField.clear();
            countTextField.clear();

            if (db.getUserById(user_id) == null || db.getItemByID(item_id) == null) return;
            for (UserHasItem i : itemsData) {
                if (i.getUser_id() == user_id && i.getItem_id() == item_id) return;
            }

            itemsData.add(db.createUserHasItem(user_id, item_id, count));
        } catch (NumberFormatException e) {
            userIDTextField.clear();
            itemIDTextField.clear();
            countTextField.clear();
        }
    }

    /**
     * Handles use of delete button.
     * Deletes the currently selected table entry from the table and the DB.
     * Shows user dialog with additional information to confirm deletion.
     *
     * @throws SQLException
     */
    public void deleteItemButtonPushed() throws SQLException {
        UserHasItem userHasItemSelected = (UserHasItem) itemsTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete UserHasItem connection");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this UserHasItem connection?\n\n\tuser_id: " + userHasItemSelected.getUser_id() +
                "\n\titem_id: " + userHasItemSelected.getItem_id());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            itemsData.remove(userHasItemSelected);
            userHasItemSelected.delete();
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
        assert root != null;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

