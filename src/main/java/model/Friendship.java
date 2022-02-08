package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Friendship {
    private final Database db = Database.getInstance();
    private int user_id;
    private int friend_id;
    private final String user_name;
    private final String friend_name;

    Friendship(ResultSet rs) throws SQLException {
        this.user_id = rs.getInt("User_User_Id");
        this.friend_id = rs.getInt("User_User_Id1");
        this.user_name = db.getUserById(user_id).getUsername();
        this.friend_name = db.getUserById(friend_id).getUsername();
    }

    public Friendship(int user_id, int friend_id) throws SQLException {
        this.user_id = user_id;
        this.friend_id = friend_id;
        this.user_name = db.getUserById(user_id).getUsername();
        this.friend_name = db.getUserById(friend_id).getUsername();
    }

    /**
     * Updates the relationship, between to users, if it already exists and creates it otherwise.
     */
    public void save() throws SQLException {
        String sql = "UPDATE User_has_User_as_Friend SET User_user_id1=? WHERE User_user_id=?";
        PreparedStatement stmt = db.conn.prepareStatement(sql);
        stmt.setInt(1, this.friend_id);
        stmt.setInt(2, this.user_id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Delete the friendship relationship from the database
     */
    public void delete() throws SQLException {
        String sql = "DELETE FROM User_has_User_as_Friend WHERE User_user_id = ? AND User_user_id1 = ?";
        PreparedStatement stmt = db.conn.prepareStatement(sql);
        stmt.setInt(1, this.user_id);
        stmt.setInt(2, this.friend_id);
        stmt.executeUpdate();
        stmt.close();
    }

    public User getFriendAsUser() throws SQLException {
        return db.getUserById(this.friend_id);
    }

    public int getFriend_id() {
        return friend_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

}
