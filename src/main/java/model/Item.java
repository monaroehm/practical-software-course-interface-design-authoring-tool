package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Item {
    private final Database database;
    private int item_id;
    private String name;
    private String description;
    private String path;

    Item(ResultSet rs) throws SQLException {
        this.database = Database.getInstance();
        this.item_id = rs.getInt("Item_Id");
        this.name = rs.getString("Name");
        this.description = rs.getString("Description");
        this.path = rs.getString("Path");
    }

    Item(int item_id, String name, String description, String path) throws SQLException {
        this.database = Database.getInstance();
        this.item_id = item_id;
        this.name = name;
        this.description = description;
        this.path = path;
    }

    /**
     * Updates the item if it already exists and creates it otherwise. Assumes an
     * autoincrement id column.
     */
    public void save() throws SQLException {
        String sql = "UPDATE `Item` SET `Name` = ?, Description = ?, Path = ? WHERE Item_Id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setString(1, this.name);
        stmt.setString(2, this.description);
        stmt.setString(3, this.path);
        stmt.setInt(4, this.item_id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Delete the item from the database
     */
    public void delete() throws SQLException {
        String sql = "DELETE FROM `Item` WHERE Item_Id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setInt(1, this.item_id);
        stmt.executeUpdate();
        stmt.close();
    }

    public int getItem_id() {
        return item_id;
    }
    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
