package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class MenuController {
    @FXML private AnchorPane menu_ap;

    /**
     * Handles use of menu button. Redirects to {@link model.User} view.
     */
    @FXML
    void redirectUsersButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/users.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles use of menu button. Redirects to {@link model.Item} view.
     */
    @FXML
    void redirectItemsButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/items.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles use of menu button. Redirects to {@link model.Friendship} view.
     */
    @FXML
    void redirectFriendsButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/friends.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles use of menu button. Redirects to {@link model.UserHasItem} view.
     */
    @FXML
    void redirectUserHasItemButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/userHasItem.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles use of menu button. Redirects to {@link model.Achievement} view.
     */
    @FXML
    void redirectAchievementButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/achievements.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles use of menu button. Redirects to {@link model.Picture} view.
     */
    @FXML
    void redirectProfilepictureButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/pictures.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles use of menu button. Redirects to chart view.
     */
    @FXML
    void redirectStatisticsButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/charts.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles use of menu button. Redirects to {@link model.PlayedGame} view.
     */
    @FXML
    public void redirectPlayedGameButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/playedGames.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles use of menu button. Redirects to {@link model.UserHasAchievement} view.
     */
    @FXML
    void redirectUserHasAchievementButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/userHasAchievement.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Handles use of menu button. Redirects to {@link model.Message} view.
     */
    @FXML
    void redirectMessagesButtonPressed() {
        Stage stage = (Stage) menu_ap.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/message.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
