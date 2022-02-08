package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserHasItem {
    private final Database database;
    private int user_id;
    private int item_id;
    private String user_name;
    private String item_name;
    private int count;

    public UserHasItem(ResultSet rs) throws SQLException {
        this.database = Database.getInstance();
        this.user_id = rs.getInt("User_user_id");
        this.item_id = rs.getInt("Item_item_id");
        this.count = rs.getInt("Anzahl");
        this.user_name = database.getUserById(user_id).getUsername();
        this.item_name = database.getItemByID(item_id).getName();
    }

    public UserHasItem(int user_id, int item_id, int count) throws SQLException {
        this.database = Database.getInstance();
        this.user_id = user_id;
        this.item_id = item_id;
        this.count = count;
        this.user_name = database.getUserById(user_id).getUsername();
        this.item_name = database.getItemByID(item_id).getName();
    }

    /**
     * Updates the item count of the user if it already exists and creates it otherwise.
     */
    public void save() throws SQLException {
        String sql = "UPDATE `User_has_Item` SET `Anzahl` = ? WHERE User_user_id = ? AND Item_item_id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setInt(1, this.count);
        stmt.setInt(2, this.user_id);
        stmt.setInt(3, this.item_id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Delete the item of the user from the database
     */
    public void delete() throws SQLException {
        String sql = "DELETE FROM `User_has_Item` WHERE User_user_id = ? AND Item_item_id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setInt(1, this.user_id);
        stmt.setInt(2, this.item_id);
        stmt.executeUpdate();
        stmt.close();
    }

    public String getItem_name() {
        return item_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUser_name() {
        return user_name;
    }
}
