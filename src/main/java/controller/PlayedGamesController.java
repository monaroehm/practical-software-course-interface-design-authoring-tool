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
import model.Friendship;
import model.PlayedGame;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class PlayedGamesController implements Initializable {
    @FXML
    private TableView playedGamesTable;
    @FXML
    private TableColumn<PlayedGame, Integer> game_id;
    @FXML
    private TableColumn<PlayedGame, String> date;
    @FXML
    private TableColumn<PlayedGame, Integer> duration;
    @FXML
    private TableColumn<PlayedGame, Integer> score;
    @FXML
    private TableColumn<PlayedGame, Integer> User_user_id;
    @FXML
    private TextField durationTextField;
    @FXML
    private TextField scoreTextField;
    @FXML
    private TextField userIDTextField;

    private final Database db = Database.getInstance();
    private ObservableList<PlayedGame> playedGamesData = FXCollections.observableArrayList();

    /**
     * Gets {@link PlayedGame}s from database and parses them into table.
     *
     * @throws SQLException
     */
    public void setTableFromDB() throws SQLException {
        playedGamesData = db.getAllGames();
        playedGamesTable.setItems(playedGamesData);
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
        playedGamesTable.setEditable(true);
        date.setCellFactory(TextFieldTableCell.forTableColumn());
        duration.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        score.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    }

    /**
     * Change listener waiting for new input value and applying it to the DB entry.
     *
     * @param editedCell - Cell edited.
     * @throws SQLException
     */
    public void changeDurationCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        PlayedGame gameSelected = (PlayedGame) playedGamesTable.getSelectionModel().getSelectedItem();

        try {
            gameSelected.setDuration(Integer.parseInt(editedCell.getNewValue().toString()));
            gameSelected.save();
        } catch (NumberFormatException ignored){}
    }

    /**
     * Change listener waiting for new input value and applying it to the DB entry.
     *
     * @param editedCell - Cell edited.
     * @throws SQLException
     */
    public void changeScoreCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        PlayedGame gameSelected = (PlayedGame) playedGamesTable.getSelectionModel().getSelectedItem();
        try {
            gameSelected.setScore(Integer.parseInt(editedCell.getNewValue().toString()));
            gameSelected.save();
        } catch (NumberFormatException ignored){}
    }

    /**
     * Change listener waiting for new input value and applying it to the DB entry.
     *
     * @param editedCell - Cell edited.
     * @throws SQLException
     */
    public void changeDateCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        PlayedGame gameSelected = (PlayedGame) playedGamesTable.getSelectionModel().getSelectedItem();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            sdf.parse(editedCell.getNewValue().toString());
        } catch (ParseException e) {
            return;
        }
        gameSelected.setDate(editedCell.getNewValue().toString());
        gameSelected.save();
    }

    /**
     * Gets input values from application.
     * Creates new {@link PlayedGame} in DB, and adds it to the currently shown table.
     *
     * @throws SQLException
     */
    public void newGameButtonPushed() throws SQLException, ParseException {
        if (durationTextField.getText().isEmpty() || scoreTextField.getText().isEmpty()
                || userIDTextField.getText().isEmpty()) return;
        try {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            int dur = Integer.parseInt(durationTextField.getText());
            int score = Integer.parseInt(scoreTextField.getText());
            int uid = Integer.parseInt(userIDTextField.getText());
            durationTextField.clear();
            scoreTextField.clear();
            userIDTextField.clear();


            for (PlayedGame g : playedGamesData) {
                if (g.getDate().equals(date)) return;
            }
            if (db.getUserById(uid) != null)
                playedGamesData.add(db.createGame(date, dur, score, uid));
        } catch (NumberFormatException e) {
            durationTextField.clear();
            scoreTextField.clear();
            userIDTextField.clear();
        }
    }

    /**
     * Handles use of delete button.
     * Deletes the currently selected table entry from the table and the DB.
     * Shows user dialog with additional information to confirm deletion.
     *
     * @throws SQLException
     */
    public void deleteGameButtonPushed() throws SQLException {
        PlayedGame selectedGame = (PlayedGame) playedGamesTable.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete played Game");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this game?\n\n\tUserID:\t" + selectedGame.getGame_id() +
                "\n\tDate:\t" + selectedGame.getDate() + "\n\tUser:\t" + db.getUserById(selectedGame.getUser_id()).getUsername());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            playedGamesData.remove(selectedGame);
            selectedGame.delete();
        } else {
            // ... played Game chose CANCEL or closed the dialog
        }
    }

    /**
     * Handles use of back button. Loads menu stage.
     */
    public void backButtonPushed() {
        Stage stage = (Stage) playedGamesTable.getScene().getWindow();
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
