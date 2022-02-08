package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Database {

    public StringProperty host = new SimpleStringProperty();
    public StringProperty database = new SimpleStringProperty();
    public StringProperty username = new SimpleStringProperty();
    public StringProperty password = new SimpleStringProperty();
    public Connection conn;

    /**
     * constructor of Database
     */
    private Database() {}

    private final static Database instance = new Database();

    /**
     * convenient to have only one Database instance
     * @return database instance
     */
    public static Database getInstance() {
        return instance;
    }

    /**
     * Connects the user to the database.
     *
     * @return true if connection is established.
     */
    public boolean connect() {
        String url = "jdbc:mysql://";
        url += host.get();
        url += "/";
        url += database.get();
        url += "?useSSL=false";

        try {
            conn = DriverManager.getConnection(url, username.get(), password.get());
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        Connection conn = Database.getInstance().conn;
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * get all users from DB
     * @return ObservableList<User> of all users from DB
     * @throws SQLException
     */
    public ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User`");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            User user = new User(rs);
            users.add(user);
        }
        stmt.close();
        return users;
    }

    /**
     * gets the user with the user_id given from the parameter user_id
     * @param user_id user_id from specific user
     * @return user with the given user_id
     * @throws SQLException
     */
    public User getUserById(int user_id) throws SQLException {
        User user = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User` WHERE User_Id = ?");
        stmt.setInt(1, user_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            user = new User(rs);
        }
        stmt.close();
        return user;
    }

    /**
     * gets the user with the username given from the parameter username
     * @param username username from specific user
     * @return user with the given username
     * @throws SQLException
     */
    public User getUserByUsername(String username) throws SQLException {
        User user = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User` WHERE Username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            user = new User(rs);
        }
        stmt.close();
        return user;
    }

    /**
     * gets highest value of given category from the DB
     * @param category relevant category
     * @return highest value of given category
     * @throws SQLException
     */
    public long getMaxFromUserCategory(String category) throws SQLException {
        long max = 0;
        if (category.equals("Friends")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT count(User_user_id1) FROM User_has_User_as_Friend GROUP BY User_user_id");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                max = Math.max(rs.getLong(1), max);
            }
            stmt.close();
        } else {
            if (category.equals("Games Played")) category = "gamesPlayed";
            PreparedStatement stmt = conn.prepareStatement("SELECT MAX(" + category + ") FROM User");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                max = rs.getLong(1);
            }
            stmt.close();
        }
        return max;
    }

    /**
     * gets all users who has a higher value than the given parameter val in the given parameter category
     * @param category relevant category
     * @param val value for the limit
     * @return ObservableList<User> of users who has a higher value than the given parameter val in the given parameter category
     * @throws SQLException
     */
    public ObservableList<User> getUsersWithValueHigherThan(String category, Long val) throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();
        if (category.equals("Friends")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT User_user_id FROM User_has_User_as_Friend GROUP BY User_user_id HAVING count(User_user_id1) >= " + val);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(getUserById(rs.getInt(1)));
            }
            stmt.close();
        } else {
            if (category.equals("Games Played")) category = "gamesPlayed";
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM User WHERE " + category + " >= " + val);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(rs));
            }
            stmt.close();
        }
        return users;
    }

    /**
     * gets the item with the given item_id as a parameter
     * @param item_id item_id of a specific item
     * @return item with the given item_id
     * @throws SQLException
     */
    public Item getItemByID(int item_id) throws SQLException {
        Item item = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Item` WHERE `Item_Id`  = ?");
        stmt.setInt(1, item_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            item = new Item(rs);
        }
        stmt.close();
        return item;
    }

    /**
     * gets the achievement with the given achievement_id as a parameter
     * @param achievement_id achievement_if of a specific achievement
     * @return achievement with the given achievement_id
     * @throws SQLException
     */
    public Achievement getAchievementByID(int achievement_id) throws SQLException {
        Achievement achievement = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Achievement` WHERE `Achievement_Id`  = ?");
        stmt.setInt(1, achievement_id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            achievement = new Achievement(rs);
        }
        stmt.close();
        return achievement;
    }

    /**
     * gets all friendships from the DB
     * @return ObservableList<Friendship> of all friendships
     * @throws SQLException
     */
    public ObservableList<Friendship> getAllFriendships() throws SQLException {
        ObservableList<Friendship> friendships = FXCollections.observableArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User_has_User_as_Friend`");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            friendships.add(new Friendship(rs));
        }
        stmt.close();
        return friendships;
    }

    /**
     * get alls games from the DB
     * @return ObservableList<PlayedGame> of alle games
     * @throws SQLException
     */
    public ObservableList<PlayedGame> getAllGames() throws SQLException {
        ObservableList<PlayedGame> games = FXCollections.observableArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `played_Games`");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            PlayedGame game = new PlayedGame(rs);
            games.add(game);
        }
        stmt.close();
        return games;
    }
    /**
     * gets all items from the DB
     * @return ObservableList<Item> of all items
     * @throws SQLException
     */
    public ObservableList<Item> getAllItems() throws SQLException {
        ObservableList<Item> items = FXCollections.observableArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Item`");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Item item = new Item(rs);
            items.add(item);
        }
        stmt.close();
        return items;
    }

    /**
     * gets all achievements from the DB
     * @return ObservableList<Achievement> of all achievements
     * @throws SQLException
     */
    public ObservableList<Achievement> getAllAchievements() throws SQLException {
        ObservableList<Achievement> achievements = FXCollections.observableArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Achievement`");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Achievement achievement = new Achievement(rs);
            achievements.add(achievement);
        }
        stmt.close();
        return achievements;
    }

    /**
     * gets all UserHasItems from the DB
     * @return ObservableList<UserHasItem> of all userHasItems
     * @throws SQLException
     */
    public ObservableList<UserHasItem> getAllUserHasItems() throws SQLException {
        ObservableList<UserHasItem> items = FXCollections.observableArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User_has_Item`");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            UserHasItem item = new UserHasItem(rs);
            items.add(item);
        }
        stmt.close();
        return items;
    }

    /**
     * gets all pictures from the DB
     * @return ObservableList<Picture> of all pictures
     * @throws SQLException
     */
    public ObservableList<Picture> getAllPictures() throws SQLException {
        ObservableList<Picture> pictures = FXCollections.observableArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Profilepicture`");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Picture picture = new Picture(rs);
            pictures.add(picture);
        }
        stmt.close();
        return pictures;
    }

    /**
     * gets all userHasAchievements from the DB
     * @return ObservableList<UserHasAchievement> of all userHAsAchievements
     * @throws SQLException
     */
    public ObservableList<UserHasAchievement> getAllUserHasAchievements() throws SQLException {
        ObservableList<UserHasAchievement> achievements = FXCollections.observableArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User_has_Achievement`");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            UserHasAchievement userHasAchievement = new UserHasAchievement(rs);
            achievements.add(userHasAchievement);
        }
        stmt.close();
        return achievements;
    }

    /**
     * gets all messages from the DB
     * @return ObservableList<Message> of all messages
     * @throws SQLException
     */
    public ObservableList<Message> getAllMessages() throws SQLException {
        ObservableList<Message> messages = FXCollections.observableArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User_User_Message`");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Message message = new Message(rs);
            messages.add(message);
        }
        stmt.close();
        return messages;
    }

    /**
     * creates new user with given parameters and saves it in DB
     * @param username username of the new user
     * @param password password of the new user
     * @return new user
     * @throws SQLException
     */
    public User createUser(String username, String password) throws SQLException {
        User user = null;
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO `User` (Username, Score, Highscore, Password, GamesPlayed, Profilepicture_picture_id) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, username);
        stmt.setInt(2, 0);
        stmt.setInt(3, 0);
        stmt.setString(4, password);
        stmt.setInt(5, 0);
        stmt.setInt(6, 7);
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int user_id = rs.getInt(1);
            user = new User(user_id, username, password);
        }
        stmt.close();
        return user;
    }

    /**
     * creates new friendship between users with id's user_id and friend_id and saves it in DB
     * @param user_id user_id of the first user from the new friendship
     * @param friend_id user_id of the second user from the new friendship
     * @return new friendship
     * @throws SQLException
     */
    public Friendship createFriendship(int user_id, int friend_id) throws SQLException {
        Friendship f = null;
        if (this.getUserById(user_id) != null && this.getUserById(friend_id) != null) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO User_has_User_as_Friend (User_user_id, User_user_id1) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, user_id);
            stmt.setInt(2, friend_id);
            stmt.executeUpdate();

            f = new Friendship(user_id, friend_id);

            stmt.close();
        }
        return f;
    }

    /**
     * creates new game with given parameters and saves it in DB
     * @param date date of the new game
     * @param dur duration of the new game
     * @param score score of the new game
     * @param User_user_id user_id of user of the new game
     * @return new game
     * @throws SQLException
     * @throws ParseException
     */
    public PlayedGame createGame(String date, int dur, int score, int User_user_id) throws SQLException, ParseException {
        PlayedGame game = null;
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO played_Games (time, duration, score, User_user_id) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, date);
        stmt.setInt(2, dur);
        stmt.setInt(3, score);
        stmt.setInt(4, User_user_id);
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int game_id = rs.getInt(1);
            game = new PlayedGame(game_id, date, dur, score, User_user_id);
        }

        stmt.close();
        return game;
    }

    /**
     * creates new item with given parameters and saves it in DB
     * @param name name of the new item
     * @param description description of the new item
     * @param path path of the picture of the new item
     * @return new item
     * @throws SQLException
     */
    public Item createItem(String name, String description, String path) throws SQLException {
        Item item = null;
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO `Item` (`Name`, Description, Path) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, name);
        stmt.setString(2, description);
        stmt.setString(3, path);
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int item_id = rs.getInt(1);
            item = new Item(item_id, name, description, path);
        }
        stmt.close();
        return item;
    }

    /**
     * creates new userHasItem with given parameters and saves it in DB
     * @param user_id user_id of the user who has the item
     * @param item_id item_id of the item which the user has
     * @param count count of the item which the user has
     * @return new userHasItem
     * @throws SQLException
     */
    public UserHasItem createUserHasItem(int user_id, int item_id, int count) throws SQLException {
        UserHasItem item;
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO `User_has_Item` VALUES (?, ?, ?)");
        stmt.setInt(1, user_id);
        stmt.setInt(2, item_id);
        stmt.setInt(3, count);
        stmt.executeUpdate();
        item = new UserHasItem(user_id, item_id, count);
        stmt.close();
        return item;
    }

    /**
     * creates new achievements with given parameters and saves it in DB
     * @param name name of new achievement
     * @param description description of new achievement
     * @param type type of new achievement
     * @param goal goal of new achievement
     * @return new achievement
     * @throws SQLException
     */
    public Achievement createAchievement(String name, String description, String type, int goal) throws SQLException {
        Achievement achievement = null;
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO `Achievement` (`Name`, Description, `Type`, Goal) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, name);
        stmt.setString(2, description);
        stmt.setString(3, type);
        stmt.setInt(4, goal);
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int achievemnet_id = rs.getInt(1);
            achievement = new Achievement(achievemnet_id, name, description, type, goal);
        }
        stmt.close();
        return achievement;
    }

    /**
     * creates new picture with given parameters and saves it in DB
     * @param path path of the new picture
     * @return new picture
     * @throws SQLException
     */
    public Picture createPicture(String path) throws SQLException {
        Picture picture = null;
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO `Profilepicture` (`Path`) VALUE (?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, path);
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int picture_id = rs.getInt(1);
            picture = new Picture(picture_id, path);
        }
        stmt.close();
        return picture;
    }

    /**
     * creates new userHasAchievement with given parameters and saves it in DB
     * @param user_id user_id of the user who gets the achievement
     * @param achievement_id achievement_id of the achievement which the user gets
     * @return new userHasAchievement
     * @throws SQLException
     */
    public UserHasAchievement createUserHasAchievement(int user_id, int achievement_id) throws SQLException {
        UserHasAchievement userHasAchievement = null;
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO `User_has_Achievement` VALUES (?,?)");
        stmt.setInt(1, user_id);
        stmt.setInt(2, achievement_id);
        stmt.executeUpdate();
        userHasAchievement = new UserHasAchievement(user_id, achievement_id);
        stmt.close();
        return userHasAchievement;
    }

    /**
     * creates new message with given parameters and saves it in DB
     * @param user_id_sender user_id of the user who sends the message
     * @param user_id_receiver user_id of the user who receives the message
     * @param message_text text of the message which gets sent
     * @return new message
     * @throws SQLException
     */
    public Message createMessage(int user_id_sender, int user_id_receiver, String message_text) throws SQLException {
        Message message = null;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO `User_User_Message` (`User_id_sender`,`User_id_receiver`, `Message_text`, `Time` ) VALUE (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, user_id_sender);
        stmt.setInt(2, user_id_receiver);
        stmt.setString(3, message_text);
        stmt.setString(4, timeStamp);
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int message_id = rs.getInt(1);
            message = new Message(user_id_sender, user_id_receiver, message_text, timeStamp, message_id);
        }
        stmt.close();
        return message;
    }
}

