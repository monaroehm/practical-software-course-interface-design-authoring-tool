package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayedGame {
    private final Database db = Database.getInstance();
    private int game_id;
    private String date;
    private int duration;
    private int score;
    private int User_user_id;

    PlayedGame(ResultSet rs) throws SQLException {
        this.game_id = rs.getInt("game_id");
        this.date = rs.getString("time");
        this.duration = rs.getInt("duration");
        this.score = rs.getInt("score");
        this.User_user_id = rs.getInt("User_user_id");
    }

    PlayedGame(int game_id, String date, int duration, int score, int User_user_id) {
        this.game_id = game_id;
        this.date = date;
        this.duration = duration;
        this.score = score;
        this.User_user_id = User_user_id;
    }

    /**
     * Updates the playedGame if it already exists and creates it otherwise. Assumes an
     * autoincrement id column.
     */
    public void save() throws SQLException {
        String sql = "UPDATE `played_Games` SET `duration` = ?, `score` = ?, `time` = ? WHERE game_id = ?";
        PreparedStatement stmt = db.conn.prepareStatement(sql);
        stmt.setInt(1, this.duration);
        stmt.setInt(2, this.score);
        stmt.setString(3, this.date);
        stmt.setInt(4, this.game_id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Delete the played game from the database
     */
    public void delete() throws SQLException {
        String s = "DELETE FROM `played_Games` WHERE game_id = ?";
        PreparedStatement stmt = db.conn.prepareStatement(s);
        stmt.setInt(1, this.game_id);
        stmt.executeUpdate();
        stmt.close();
    }

    public int getGame_id() {
        return game_id;
    }
    public void setGame_id(int game_id) { this.game_id = game_id; }

    public String getDate() { return date;}
    public void setDate(String date) { this.date = date; }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) { this.duration = duration; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getUser_id() {
        return User_user_id;
    }
    public void setUser_id(int user_id) { this.User_user_id = user_id; }
}
