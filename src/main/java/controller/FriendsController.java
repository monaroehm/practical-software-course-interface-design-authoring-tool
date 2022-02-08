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
import model.Friendship;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class FriendsController implements Initializable {
    @FXML
    private TableView friendsTable;
    @FXML
    private TextField userIdInput;
    @FXML
    private TextField friendIdInput;


    private final Database db = Database.getInstance();
    private ObservableList<Friendship> friendsData = FXCollections.observableArrayList();

    /**
     * Gets {@link Friendship}s from database and parses them into table.
     *
     * @throws SQLException
     */
    public void setTableFromDB() throws SQLException {
        friendsData = db.getAllFriendships();
        friendsTable.setItems(friendsData);
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
        friendsTable.setEditable(true);
    }

    /**
     * Gets input values from application.
     * Creates new friendship in DB, and adds it to the currently shown table.
     *
     * @throws SQLException
     */
    public void addFriendshipButtonPushed() throws SQLException {
        if (userIdInput.getText().isEmpty() || friendIdInput.getText().isEmpty()) return;
        try {
            int user = Integer.parseInt(userIdInput.getText());
            int friend = Integer.parseInt(friendIdInput.getText());

            userIdInput.clear();
            friendIdInput.clear();

            for (Friendship f : friendsData) {
                if ((f.getUser_id() == user && f.getFriend_id() == friend) || (f.getFriend_id() == user && f.getUser_id() == friend))
                    return;
            }

            if (db.getUserById(user) != null && db.getUserById(friend) != null)
                friendsData.add(db.createFriendship(user, friend));
        } catch (NumberFormatException e) {
            userIdInput.clear();
            friendIdInput.clear();
        }
    }

    /**
     * Handles use of delete button.
     * Deletes the currently selected table entry from the table and the DB.
     * Shows user dialog with additional information to confirm deletion.
     *
     * @throws SQLException
     */
    public void deleteFriendshipButtonPushed() throws SQLException {
        Friendship friendshipSelected = (Friendship) friendsTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Friendship");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this friendship?\n\n\tid: " + friendshipSelected.getUser_id()
                + " (" + friendshipSelected.getUser_name() + ")" + "\n\tid: " + friendshipSelected.getFriend_id()
                + " (" + friendshipSelected.getFriend_name() + ")");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            friendsData.remove(friendshipSelected);
            friendshipSelected.delete();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    /**
     * Handles use of back button. Loads menu stage.
     */
    public void backButtonPushed() {
        Stage stage = (Stage) friendsTable.getScene().getWindow();
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
