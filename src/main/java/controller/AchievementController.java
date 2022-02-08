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
import model.Achievement;
import model.Database;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AchievementController implements Initializable {
    @FXML
    private TableView achievementsTable;
    @FXML
    private TableColumn<Achievement, String> name;
    @FXML
    private TableColumn<Achievement, String> description;
    @FXML
    private TableColumn<Achievement, String> type;
    @FXML
    private TableColumn<Achievement, Integer> goal;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField typeTextField;
    @FXML
    private TextField goalTextField;

    Database db = Database.getInstance();
    private ObservableList<Achievement> achievementsData = FXCollections.observableArrayList();

    /**
     * Gets achievements from database and parses them into table.
     *
     * @throws SQLException
     */
    public void setTableFromDB() throws SQLException {
        achievementsData = db.getAllAchievements();
        achievementsTable.setItems(achievementsData);
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
        achievementsTable.setEditable(true);
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        description.setCellFactory(TextFieldTableCell.forTableColumn());
        type.setCellFactory(TextFieldTableCell.forTableColumn());
        goal.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    }

    /**
     * Change listeners waiting for new input value and applying it to the DB entry.
     *
     * @param editedCell - Cell edited.
     * @throws SQLException
     */
    public void changeNameCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        Achievement achievementSelected = (Achievement) achievementsTable.getSelectionModel().getSelectedItem();
        achievementSelected.setName(editedCell.getNewValue().toString());
        achievementSelected.save();
    }

    public void changeDescriptionCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        Achievement achievementSelected = (Achievement) achievementsTable.getSelectionModel().getSelectedItem();
        achievementSelected.setDescription(editedCell.getNewValue().toString());
        achievementSelected.save();
    }

    public void changeTypeCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        Achievement achievementSelected = (Achievement) achievementsTable.getSelectionModel().getSelectedItem();
        achievementSelected.setType(editedCell.getNewValue().toString());
        achievementSelected.save();
    }

    public void changeGoalCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        try {
            Achievement achievementSelected = (Achievement) achievementsTable.getSelectionModel().getSelectedItem();
            achievementSelected.setGoal(Integer.parseInt(editedCell.getNewValue().toString()));
            achievementSelected.save();
        } catch (NumberFormatException e) {
        }
    }

    /**
     * Gets input values from application.
     * Creates new achievement in DB, and adds it to the currently shown table.
     *
     * @throws SQLException
     */
    public void newItemButtonPushed() throws SQLException {
        if (nameTextField.getText().isEmpty() || goalTextField.getText().isEmpty()) return;
        try {
            final String newName = nameTextField.getText();
            final String newDesc = descriptionTextField.getText();
            final String newType = typeTextField.getText();
            final int newGoal = Integer.parseInt(goalTextField.getText());
            nameTextField.clear();
            descriptionTextField.clear();
            typeTextField.clear();
            goalTextField.clear();

            for (Achievement a : achievementsData) {
                if (a.getName().equals(newName))
                    return;
            }
            achievementsData.add(db.createAchievement(newName, newDesc, newType, newGoal));
        } catch (NumberFormatException e) {
            nameTextField.clear();
            descriptionTextField.clear();
            typeTextField.clear();
            goalTextField.clear();
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
        Achievement achievementSelected = (Achievement) achievementsTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Achievement");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this achievement?\n\n\tid: " + achievementSelected.getId() +
                "\n\tname: " + achievementSelected.getName());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            achievementsData.remove(achievementSelected);
            achievementSelected.delete();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    /**
     * Handles use of back button. Loads menu stage.
     */
    public void backButtonPushed() {
        Stage stage = (Stage) achievementsTable.getScene().getWindow();
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

