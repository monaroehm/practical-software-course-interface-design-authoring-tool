package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserHasAchievement {
    private final Database database;
    private int user_id;
    private int achievement_id;
    private String user_name;
    private String achievement_name;


    public UserHasAchievement(ResultSet rs) throws SQLException {
        this.database = Database.getInstance();
        this.user_id = rs.getInt("User_user_id");
        this.achievement_id = rs.getInt("Achievement_achievement_id");
        this.user_name = database.getUserById(user_id).getUsername();
        this.achievement_name = database.getAchievementByID(achievement_id).getName();
    }
    public UserHasAchievement(int user_id, int achievement_id) throws SQLException {
        this.database = Database.getInstance();
        this.user_id = user_id;
        this.achievement_id = achievement_id;
        this.user_name = database.getUserById(user_id).getUsername();
        this.achievement_name = database.getAchievementByID(achievement_id).getName();
    }

    /**
     * Delete the achievement of the user from the database
     */
    public void delete() throws SQLException {
        String sql = "DELETE FROM `User_has_Achievement` WHERE User_user_id = ? AND Achievement_achievement_id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setInt(1, this.user_id);
        stmt.setInt(2, this.achievement_id);
        stmt.executeUpdate();
        stmt.close();
    }

    public int getUser_id() {
        return user_id;
    }

    public int getAchievement_id() {
        return achievement_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getAchievement_name() {
        return achievement_name;
    }
}
