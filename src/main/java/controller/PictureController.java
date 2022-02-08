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
import model.Picture;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class PictureController implements Initializable {
    @FXML private TableView pictureTable;
    @FXML private TableColumn<Picture, Integer> picture_id;
    @FXML private TableColumn<Picture, String> path;
    @FXML private TextField pathTextField;

    private ObservableList<Picture> pictureData = FXCollections.observableArrayList();
    private final Database db = Database.getInstance();

    /**
     * Gets {@link Picture}s from database and parses them into table.
     *
     * @throws SQLException
     */
    public void setTableFromDB() throws SQLException {
        pictureData = db.getAllPictures();
        pictureTable.setItems(pictureData);
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
        pictureTable.setEditable(true);
        path.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * Change listener waiting for new input value and applying it to the DB entry.
     *
     * @param editedCell - Cell edited.
     * @throws SQLException
     */
    public void changePathCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        Picture pictureSelected = (Picture) pictureTable.getSelectionModel().getSelectedItem();
        pictureSelected.setPath(editedCell.getNewValue().toString());
        pictureSelected.save();
    }

    /**
     * Gets input values from application.
     * Creates new Picture in DB, and adds it to the currently shown table.
     *
     * @throws SQLException
     */
    public void newPictureButtonPushed() throws SQLException {
        if (pathTextField.getText().isEmpty()) return;

        String path = pathTextField.getText();
        pathTextField.clear();

        for (Picture p : db.getAllPictures()) {
            if (p.getPath().equals(path)) return;
        }

        pictureData.add(db.createPicture(path));
    }

    /**
     * Handles use of delete button.
     * Deletes the currently selected table entry from the table and the DB.
     * Shows user dialog with additional information to confirm deletion.
     *
     * @throws SQLException
     */
    public void deletePictureButtonPushed() throws SQLException {
        Picture pictureSelected = (Picture) pictureTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Picture");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this picture?\n\n\tid: " + pictureSelected.getPicture_id() +
                "\n\tpath: " + pictureSelected.getPath());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            pictureData.remove(pictureSelected);
            pictureSelected.delete();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    /**
     * Handles use of back button. Loads menu stage.
     */
    public void backButtonPushed() {
        Stage stage = (Stage) pictureTable.getScene().getWindow();
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

