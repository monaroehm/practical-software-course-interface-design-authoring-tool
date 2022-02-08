package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Message {
    private final Database database;
    private final int user_id_sender;
    private final int user_id_receiver;
    private String message_text;
    private final String time;
    private final int message_id;

    public Message (ResultSet rs) throws SQLException {
        this.database = Database.getInstance();
        this.user_id_sender = rs.getInt("User_id_sender");
        this.user_id_receiver = rs.getInt("User_id_receiver");
        this.message_text = rs.getString("Message_text");
        this.time = rs.getString("Time");
        this.message_id = rs.getInt("Message_id");
    }
    public Message (int user_id_sender, int user_id_receiver, String message_text, String time, int message_id){
        this.database = Database.getInstance();
        this.user_id_sender = user_id_sender;
        this.user_id_receiver = user_id_receiver;
        this.message_text = message_text;
        this.time = time;
        this.message_id = message_id;
    }

    /**
     * Updates the message if it already exists and creates it otherwise. Assumes an
     * autoincrement id column.
     */
    public void save() throws SQLException {
        String sql = "UPDATE `User_User_Message` SET `Message_text` = ? WHERE Message_id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setString(1, this.message_text);
        stmt.setInt(2, this.message_id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Delete the message from the database
     */
    public void delete() throws SQLException {
        String sql = "DELETE FROM `User_User_Message` WHERE Message_Id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setInt(1, this.message_id);
        stmt.executeUpdate();
        stmt.close();
    }

    public int getUser_id_sender() {
        return user_id_sender;
    }

    public int getUser_id_receiver() {
        return user_id_receiver;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getTime() {
        return time;
    }

    public int getMessage_id() {
        return message_id;
    }
}
