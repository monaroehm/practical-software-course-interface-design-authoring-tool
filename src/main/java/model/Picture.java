package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Picture {
    private final Database database;
    private int picture_id;
    private String path;

    public Picture(ResultSet rs) throws SQLException {
        this.database = Database.getInstance();
        this.picture_id = rs.getInt("Picture_id");
        this.path = rs.getString("Path");
    }
    public Picture(int picture_id, String path){
        this.database = Database.getInstance();
        this.picture_id = picture_id;
        this.path = path;
    }

    /**
     * Updates the profilepicture if it already exists and creates it otherwise. Assumes an
     * autoincrement id column.
     */
    public void save() throws SQLException {
        String sql = "UPDATE `Profilepicture` SET `Path` = ? WHERE Picture_id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setString(1, this.path);
        stmt.setInt(2, this.picture_id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Delete the profilepicture from the database
     */
    public void delete() throws SQLException {
        String sql = "DELETE FROM `Profilepicture` WHERE Picture_id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setInt(1, this.picture_id);
        stmt.executeUpdate();
        stmt.close();
    }

    public int getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(int picture_id) {
        this.picture_id = picture_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
