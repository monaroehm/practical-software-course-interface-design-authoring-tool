package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Database;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ChartController implements Initializable {
    @FXML private ListView<String> listAll;
    @FXML private ListView<String> listSelected;
    @FXML private ComboBox<String> comboYAxis;
    @FXML private ComboBox<String> comboChartType;
    @FXML private ComboBox<String> comboXAxis;
    @FXML private ComboBox<String> comboFilterCategory;
    @FXML private TextField inputValueFilter;
    @FXML private TextField inputSearchUser;
    @FXML private Text textXAxis;
    @FXML private Pane paneChart;


    private final ObservableList<String> usersAll = FXCollections.observableArrayList();
    private final ObservableList<String> usersSelected = FXCollections.observableArrayList();
    private final Database db = Database.getInstance();
    private final List<String> chartCategories = Arrays.asList("Score", "Highscore", "Games Played", "Friends");
    private final List<String> chartTypes = Arrays.asList("Bar Chart", "Pie Chart", "Scatter Chart");

    /**
     * Gets called on scene load.
     * Fills user lists, boxes and selections with current DB data; loads default chart.
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fillAllList();
            fillComboBoxes();
            loadTable();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Updates presented user lists with backend lists; loads chart with given data.
     *
     * @throws SQLException
     */
    public void loadTable() throws SQLException {
        listAll.setItems(usersAll);
        listSelected.setItems(usersSelected);
        loadChart();
    }


    /**
     * Loads chart depending on selected chart type, category, selected users and input values.
     * Presents chart in {@link ChartController#paneChart} node.
     *
     * @throws SQLException
     */
    public void loadChart() throws SQLException {
        paneChart.getChildren().clear();
        ObservableList<User> users = FXCollections.observableArrayList();
        Chart c = null;
        final String selectedCategory = comboYAxis.getValue();
        final String selectedChartType = comboChartType.getValue();

        for (String u : usersSelected)
            users.add(db.getUserByUsername(u));

        if (!comboChartType.getValue().equals("Scatter Chart")) {
            comboXAxis.setVisible(false);
            textXAxis.setVisible(false);
        }


        if (selectedChartType.equals("Scatter Chart") && comboXAxis.getValue() == null) {
            paneChart.getChildren().clear();
            comboXAxis.setVisible(true);
            textXAxis.setVisible(true);
        } else {
            switch (selectedChartType) {
                case ("Bar Chart"):
                    c = getBarChart(users, selectedCategory);
                    break;
                case ("Pie Chart"):
                    c = getPieChart(users, selectedCategory);
                    break;
                case ("Scatter Chart"):
                    c = getScatterChart(users, comboXAxis.getValue(), comboYAxis.getValue());
                    break;
            }

            assert c != null;
            c.setPrefSize(600, 250);
            paneChart.getChildren().add(c);
        }
    }

    /**
     * Creates scalable scatter chart from given parameters.
     *
     * @param users     List of users to be represented
     * @param xCategory Selected category for x-axis
     * @param yCategory Selected category for y-axis
     * @return Node of type Chart to be presented in {@link ChartController#loadChart()}.
     * @throws SQLException
     */
    public Chart getScatterChart(ObservableList<User> users, String xCategory, String yCategory) throws SQLException {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final ScatterChart<Number, Number> chart = new ScatterChart<>(xAxis, yAxis);
        ArrayList<XYChart.Series<Number, Number>> allSeries = new ArrayList<>();
        ObservableList<User> chartUser = users.isEmpty() ? db.getAllUsers() : users;

        for (User u : chartUser) {
            XYChart.Series<Number, Number> s = new XYChart.Series<>();
            s.setName(u.getUsername());
            s.getData().add(new XYChart.Data<>(getValueFromCategory(u, xCategory), getValueFromCategory(u, yCategory)));
            allSeries.add(s);
        }

        chart.getData().addAll(allSeries);
        xAxis.setLabel(xCategory);
        yAxis.setLabel(yCategory);
        chart.setTitle(xCategory + " x " + yCategory);
        comboXAxis.setVisible(true);
        textXAxis.setVisible(true);

        return chart;
    }

    /**
     * Creates scalable pie chart from given parameters.
     *
     * @param users    List of users to be represented
     * @param category Selected category to be represented in pie chart
     * @return Node of type Chart to be presented in {@link ChartController#loadChart()}.
     * @throws SQLException
     */
    public Chart getPieChart(ObservableList<User> users, String category) throws SQLException {
        ObservableList<User> chartUser = users.isEmpty() ? db.getAllUsers() : users;
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        for (User u : chartUser)
            chartData.add(new PieChart.Data(u.getUsername(), getValueFromCategory(u, category)));

        final PieChart chart = new PieChart(chartData);
        chart.setTitle(category);

        return chart;
    }

    /**
     * Creates scalable bar chart from given parameters.
     *
     * @param users    List of users to be represented
     * @param category Selected category to be represented on the y-axis
     * @return Node of type Chart to be presented in {@link ChartController#loadChart()}.
     * @throws SQLException
     */
    public Chart getBarChart(ObservableList<User> users, String category) throws SQLException {
        ObservableList<User> chartUser = users.isEmpty() ? db.getAllUsers() : users;
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> std = new XYChart.Series<>();
        std.setName(category);

        for (User u : chartUser)
            std.getData().add(new XYChart.Data<>(u.getUsername(), getValueFromCategory(u, category)));

        xAxis.setLabel("User");
        yAxis.setLabel(category);
        chart.getData().add(std);
        chart.setTitle(category);

        return chart;
    }

    /**
     * @param u        User to get categories value from
     * @param category Category to get users value from
     * @return Value of {@link User} u in given category
     * @throws SQLException
     */
    private double getValueFromCategory(User u, String category) throws SQLException {
        switch (category) {
            case "Score":
                return u.getScore();
            case "Highscore":
                return u.getHighscore();
            case "Games Played":
                return u.getGamesPlayed();
            case "Friends":
                return u.getFriendships().size();
        }
        return 0;
    }

    /**
     * Fills all combo boxes with preset values.
     */
    private void fillComboBoxes() {
        chartCategories.forEach((c) -> {
            comboYAxis.getItems().add(c);
            comboFilterCategory.getItems().add(c);
            comboXAxis.getItems().add(c);
        });

        chartTypes.forEach((t) -> {
            comboChartType.getItems().add(t);
        });

        comboChartType.getSelectionModel().selectFirst();
        comboYAxis.getSelectionModel().selectFirst();
    }

    /**
     * Fills user list with all users registered.
     *
     * @throws SQLException
     */
    private void fillAllList() throws SQLException {
        usersAll.clear();
        db.getAllUsers().forEach(user -> usersAll.add(user.getUsername()));
    }

    /**
     * Moves user from allList into selection.
     *
     * @param user User to be moved to selected list
     * @throws SQLException
     */
    private void addUserToSelected(String user) throws SQLException {
        if (!user.isEmpty() && usersAll.contains(user) && !usersSelected.contains(user)) {
            usersAll.remove(user);
            usersSelected.add(user);
            loadTable();
        }
    }

    /**
     * Removes user from selection back to allList.
     *
     * @param user User to be removed from the selected list
     * @throws SQLException
     */
    private void removeUserFromSelected(String user) throws SQLException {
        if (!user.isEmpty() && usersSelected.contains(user) && !usersAll.contains(user)) {
            usersSelected.remove(user);
            usersAll.add(user);
            loadTable();
        }
    }

    /**
     * Listener for all list to trigger on-click list switch.
     *
     * @param arg0 Necessary mouse event
     * @throws SQLException
     */
    public void handleSelectListAll(MouseEvent arg0) throws SQLException {
        if (listAll.getSelectionModel().getSelectedItem() != null) {
            String user = listAll.getSelectionModel().getSelectedItem();
            addUserToSelected(user);
        }
    }

    /**
     * Listener for selection list to trigger on-click list switch.
     *
     * @param arg0 Necessary mouse event
     * @throws SQLException
     */
    public void handleSelectListSelected(MouseEvent arg0) throws SQLException {
        if (listSelected.getSelectionModel().getSelectedItem() != null) {
            String user = listSelected.getSelectionModel().getSelectedItem();
            removeUserFromSelected(user);
        }
    }

    /**
     * On-Select listener for {@link ChartController#comboFilterCategory}.
     * Sets input field visible and sets possible value span as prompt text.
     *
     * @throws SQLException
     */
    public void handleCategoryFilter() throws SQLException {
        final String category = comboFilterCategory.getValue();
        inputValueFilter.setPromptText("0 - " + db.getMaxFromUserCategory(category));
    }

    /**
     * Applies selected filters to user lists.
     *
     * @throws SQLException
     */
    public void handleAddButton() throws SQLException {
        if (!inputSearchUser.getText().isEmpty()) {
            addUserToSelected(inputSearchUser.getText());
            inputSearchUser.clear();
        }
        if (!inputValueFilter.getText().isEmpty()) {
            String category = comboFilterCategory.getValue();
            long inputVal = Long.parseLong(inputValueFilter.getText());
            List<User> users = db.getUsersWithValueHigherThan(category, inputVal);

            for (User u : users) {
                addUserToSelected(u.getUsername());
            }
            inputValueFilter.clear();
        }
    }

    /**
     * Resets user lists and chart to default.
     *
     * @throws SQLException
     */
    public void handleClearButton() throws SQLException {
        inputSearchUser.clear();
        usersSelected.clear();
        fillAllList();
        loadChart();
    }

    /**
     * Handles use of back button. Loads menu stage.
     */
    public void handleBackButton() {
        Stage stage = (Stage) paneChart.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert root != null;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
