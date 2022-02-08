package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Achievement {
    private final Database database;
    private int id;
    private String name;
    private String description;
    private String type;
    private int goal;

    public Achievement(ResultSet rs) throws SQLException {
        this.database = Database.getInstance();
        this.id = rs.getInt("Achievement_id");
        this.name = rs.getString("Name");
        this.description = rs.getString("Description");
        this.type = rs.getString("Type");
        this.goal = rs.getInt("Goal");
    }

    public Achievement(int id, String name, String description, String type, int goal) {
        this.database = Database.getInstance();
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.goal = goal;
    }

    /**
     * Updates the achievement if it already exists and creates it otherwise. Assumes an
     * autoincrement id column.
     */
    public void save() throws SQLException {
        String sql = "UPDATE `Achievement` SET `Name` = ?, Description = ?, `Type` = ?, Goal = ? WHERE Achievement_Id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setString(1, this.name);
        stmt.setString(2, this.description);
        stmt.setString(3, this.type);
        stmt.setInt(4, this.goal);
        stmt.setInt(5, this.id);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Delete the Achievement from the Database
     */
    public void delete() throws SQLException {
        String sql = "DELETE FROM `Achievement` WHERE Achievement_Id = ?";
        PreparedStatement stmt = database.conn.prepareStatement(sql);
        stmt.setInt(1, this.id);
        stmt.executeUpdate();
        stmt.close();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }
}
