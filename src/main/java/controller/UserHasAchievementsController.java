package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Database;
import model.UserHasAchievement;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserHasAchievementsController implements Initializable {
    @FXML
    private TableView achievementsTable;
    @FXML
    private TextField userIDTextField;
    @FXML
    private TextField achievementIDTextField;

    private ObservableList<UserHasAchievement> achievementsData = FXCollections.observableArrayList();
    private final Database db = Database.getInstance();

    /**
     * Gets {@link UserHasAchievement}s from database and parses them into table.
     *
     * @throws SQLException
     */
    public void setTableFromDB() throws SQLException {
        achievementsData = db.getAllUserHasAchievements();
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
    }

    /**
     * Gets input values from application.
     * Creates new {@link UserHasAchievement} in DB, and adds it to the currently shown table.
     *
     * @throws SQLException
     */
    public void newItemButtonPushed() throws SQLException {
        if (userIDTextField.getText().isEmpty() || achievementIDTextField.getText().isEmpty()) return;
        try {
            int user_id = Integer.parseInt(userIDTextField.getText());
            int achievement_id = Integer.parseInt(achievementIDTextField.getText());
            userIDTextField.clear();
            achievementIDTextField.clear();

            if(db.getUserById(user_id) == null || db.getAchievementByID(achievement_id) == null) return;
            for (UserHasAchievement a : achievementsData) {
                if (a.getUser_id() == user_id && a.getAchievement_id() == achievement_id) return;
            }

            achievementsData.add(db.createUserHasAchievement(user_id, achievement_id));
        } catch (NumberFormatException e){
            userIDTextField.clear();
            achievementIDTextField.clear();
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
        UserHasAchievement userHasAchievementSelected = (UserHasAchievement) achievementsTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete UserHasAchievement connection");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this UserHasAchievement connection?\n\n\tuser_id: " + userHasAchievementSelected.getUser_id() +
                "\n\titem_id: " + userHasAchievementSelected.getAchievement_id());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            achievementsData.remove(userHasAchievementSelected);
            userHasAchievementSelected.delete();
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
        assert root != null;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

