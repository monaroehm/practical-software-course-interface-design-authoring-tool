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
import model.Message;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class MessageController implements Initializable {
    @FXML
    private TableView messagesTable;
    @FXML
    private TableColumn<Message, Integer> user_id_sender;
    @FXML
    private TableColumn<Message, Integer> user_id_receiver;
    @FXML
    private TableColumn<Message, String> message_text;
    @FXML
    private TableColumn<Message, String> time;
    @FXML
    private TableColumn<Message, Integer> message_id;
    @FXML
    private TextField messageTextField;
    @FXML
    private TextField senderTextField;
    @FXML
    private TextField receiverTextField;


    private ObservableList<Message> messagesData = FXCollections.observableArrayList();
    private final Database db = Database.getInstance();

    /**
     * Gets {@link Message}s from database and parses them into table.
     *
     * @throws SQLException
     */
    public void setTableFromDB() throws SQLException {
        messagesData = db.getAllMessages();
        messagesTable.setItems(messagesData);
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
        messagesTable.setEditable(true);
        message_text.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * Change listeners waiting for new input value and applying it to the DB entry.
     *
     * @param editedCell - Cell edited.
     * @throws SQLException
     */
    public void changeMessageCellEvent(TableColumn.CellEditEvent editedCell) throws SQLException {
        Message messageSelected = (Message) messagesTable.getSelectionModel().getSelectedItem();
        messageSelected.setMessage_text(editedCell.getNewValue().toString());
        messageSelected.save();
    }

    /**
     * Gets input values from application.
     * Creates new {@link Message} in DB, and adds it to the currently shown table.
     *
     * @throws SQLException
     */
    public void newMessageButtonPushed() throws SQLException {
        if (senderTextField.getText().isEmpty() || receiverTextField.getText().isEmpty()
                || messageTextField.getText().isEmpty()) return;

        try {
            int sender_id = Integer.parseInt(senderTextField.getText());
            int receiver_id = Integer.parseInt(receiverTextField.getText());
            String msg = messageTextField.getText();
            senderTextField.clear();
            receiverTextField.clear();
            messageTextField.clear();

            if ((sender_id == receiver_id)) return;
            if ((db.getUserById(sender_id) == null || db.getUserById(receiver_id) == null)) return;

            messagesData.add(db.createMessage(sender_id, receiver_id, msg));
        } catch (NumberFormatException e) {
            senderTextField.clear();
            receiverTextField.clear();
            messageTextField.clear();
        }
    }


    /**
     * Handles use of delete button.
     * Deletes the currently selected table entry from the table and the DB.
     * Shows user dialog with additional information to confirm deletion.
     *
     * @throws SQLException
     */
    public void deleteMessageButtonPushed() throws SQLException {
        Message messageSelected = (Message) messagesTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this message?\n\n\tid: " + messageSelected.getMessage_id()
                + "\n\tmsg: " + messageSelected.getMessage_text());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            messagesData.remove(messageSelected);
            messageSelected.delete();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    /**
     * Handles use of back button. Loads menu stage.
     */
    public void backButtonPushed() {
        Stage stage = (Stage) messagesTable.getScene().getWindow();
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
