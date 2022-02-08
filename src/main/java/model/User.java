package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class User {
    private final Database database;
    private int user_id;
    private String username;
    private String password;
    private int score;
    private int highscore;
    private int gamesPlayed;
    private int profilepicture_picture_id;

    User(ResultSet rs) throws SQLException {
        this.database = Database.getInstance();
        this.user_id = rs.getInt("User_Id");
        this.username = rs.getString("Username");
        this.password = rs.getString("Password");
        this.score = rs.getInt("Score");
        this.highscore = rs.getInt("Highscore");
        this.gamesPlayed = rs.getInt("GamesPlayed");
        this.profilepicture_picture_id = rs.getInt("Profilepicture_picture_id");
    }

    public User(int user_id, String username, String password) {
        this.database = Database.getInstance();
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.score = 0;
        this.highscore = 0;
        this.gamesPlayed = 0;
        this.profilepicture_picture_id = 7;
    }

    /**
     * Updates the user if it already exists and creates it otherwise. Assumes an
     * autoincrement id column.
     */
    public void save() throws SQLException {
        String sql = "UPDATE `User` SET Username = ?, Password = ?, Score = ?, Highscore = ?, GamesPlayed = ?, Profilepicture_picture_id = ? WHERE User_Id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setString(1, this.username);
        stmt.setString(2, this.password);
        stmt.setInt(3, this.score);
        stmt.setInt(4, this.highscore);
        stmt.setInt(5, this.gamesPlayed);
        stmt.setInt(6, this.profilepicture_picture_id);
        stmt.setInt(7, this.user_id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Delete the user from the database
     */
    public void delete() throws SQLException {
        String sql = "DELETE FROM `User` WHERE User_Id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setInt(1, this.user_id);
        stmt.executeUpdate();
        stmt.close();
    }

    public ObservableList<Friendship> getFriendships() throws SQLException {
        ObservableList<Friendship> result = FXCollections.observableArrayList();
        String sql = "SELECT * FROM User_has_User_as_Friend WHERE User_user_id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setInt(1, this.user_id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Friendship f = new Friendship(rs);
            result.add(f);
        }
        stmt.close();
        return result;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws SQLException {
        this.username = username;
        this.save();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) throws SQLException {
        this.score += score;
        this.save();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getProfilepicture_picture_id() {
        return profilepicture_picture_id;
    }

    public void setProfilepicture_picture_id(int profilepicture_picture_id) {
        this.profilepicture_picture_id = profilepicture_picture_id;
    }
}
