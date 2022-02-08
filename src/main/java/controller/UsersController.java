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
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    @FXML private TableView usersTable;
    @FXML private TableColumn<User, String> username;
    @FXML private TableColumn<User, String> password;
    @FXML private TableColumn<User, Integer> score;
    @FXML private TableColumn<User, Integer> highscore;
    @FXML private TableColumn<User, Integer> gamesPlayed;
    @FXML private TableColumn<User, Integer> profilePicture;
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;

    private ObservableList<User> usersData = FXCollections.observableArrayList();
    private final Database db = Database.getInstance();

    /**
     * Gets {@link User}s from database and parses them into table.
     *
     * @throws SQLException
     */
    public void setTableFromDB() throws SQLException {
        usersData = db.getAllUsers();
        usersTable.setItems(usersData);
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
        usersTable.setEditable(true);
        username.setCellFactory(TextFieldTableCell.forTableColumn());
        password.setCellFactory(TextFieldTableCell.forTableColumn());
        score.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        highscore.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        gamesPlayed.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        profilePicture.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    }

    /**
     * Change listeners waiting for new input value and applying it to the DB entry.
     *
     * @param editedCell - Cell edited.
     * @throws SQLException
     */
    public void changeNameCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        User userSelected = (User) usersTable.getSelectionModel().getSelectedItem();
        userSelected.setUsername(editedCell.getNewValue().toString());
        userSelected.save();
    }
    public void changePasswordCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        User userSelected = (User) usersTable.getSelectionModel().getSelectedItem();
        userSelected.setPassword(editedCell.getNewValue().toString());
        userSelected.save();
    }
    public void changeScoreCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        User userSelected = (User) usersTable.getSelectionModel().getSelectedItem();
        userSelected.setScore(Integer.parseInt(editedCell.getNewValue().toString()));
        userSelected.save();
    }
    public void changeHighscoreCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        User userSelected = (User) usersTable.getSelectionModel().getSelectedItem();
        userSelected.setHighscore(Integer.parseInt(editedCell.getNewValue().toString()));
        userSelected.save();
    }
    public void changeGamesPlayedCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        User userSelected = (User) usersTable.getSelectionModel().getSelectedItem();
        userSelected.setGamesPlayed(Integer.parseInt(editedCell.getNewValue().toString()));
        userSelected.save();
    }
    public void changeProfilePictureCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        User userSelected = (User) usersTable.getSelectionModel().getSelectedItem();
        userSelected.setProfilepicture_picture_id(Integer.parseInt(editedCell.getNewValue().toString()));
        userSelected.save();
    }


    /**
     * Gets input values from application.
     * Creates new {@link User} in DB, and adds it to the currently shown table.
     *
     * @throws SQLException
     */
    public void newPersonButtonPushed() throws SQLException {
        if(usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) return;

        String usr = usernameTextField.getText();
        String pwd = passwordTextField.getText();
        usernameTextField.clear();
        passwordTextField.clear();
        
        if(db.getUserByUsername(usr) != null) return;

        usersData.add(db.createUser(usr, pwd));
    }

    /**
     * Handles use of delete button.
     * Deletes the currently selected table entry from the table and the DB.
     * Shows user dialog with additional information to confirm deletion.
     *
     * @throws SQLException
     */
    public void deleteUserButtonPushed() throws SQLException {
        User userSelected = (User) usersTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this user?\n\n\tid: " + userSelected.getUser_id() +
                "\n\tname: " + userSelected.getUsername());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            usersData.remove(userSelected);
            userSelected.delete();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    /**
     * Handles use of back button. Loads menu stage.
     */
    public void backButtonPushed() {
        Stage stage = (Stage) usersTable.getScene().getWindow();
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
