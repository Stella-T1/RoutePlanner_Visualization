package RoutePlanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.*;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Polygon;

public class Visualization extends Application {
    private AppServices services;
    private ComboBox<String> startCombo;
    private ComboBox<String> endCombo;
    private ListView<String> attractionsList;
    private TextArea resultArea;
    private ScrollPane mapScrollPane;  //scroll pane


    private static class AppServices {
        private final AttractionManager attractionManager = new AttractionManager();
        private final RoadNetwork roadNetwork = new RoadNetwork();
        private final RoutePlanner routePlanner = new RoutePlanner(attractionManager, roadNetwork);

        public void initializeData(String attractionsFile, String roadsFile) {
            attractionManager.loadFromCSV(attractionsFile);
            roadNetwork.loadFromCSV(roadsFile);
        }

        public ObservableList<String> getCitySuggestions() {
            return FXCollections.observableArrayList(roadNetwork.getCities());
        }

        // The method of obtaining the list of scenic spots
        public ObservableList<String> getAttractionSuggestions() {
            return FXCollections.observableArrayList(attractionManager.getAttractions().keySet());
        }

        public PathResults calculateRoute(String start, String end, List<String> attractions) {
            return routePlanner.planRoute(start, end, attractions, new DijkstraAlgorithm(roadNetwork));  //choose Dijkstra or DFS algorithm
        }
    }



    @Override
    public void init() throws Exception {
        services = new AppServices();
        services.initializeData("attractions.csv", "roads.csv");  //load data
    }


    @Override
    public void start(Stage primaryStage) {
        // Main rolling container
        ScrollPane mainScroll = new ScrollPane();
        mainScroll.setFitToWidth(true);
        mainScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setMinWidth(780);

        // initial components
        startCombo = new ComboBox<>(services.getCitySuggestions());
        endCombo = new ComboBox<>(services.getCitySuggestions());
        attractionsList = new ListView<>(services.getAttractionSuggestions());
        attractionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(150);
        resultArea.setWrapText(true);

        // Map area
        mapScrollPane = new ScrollPane();
        mapScrollPane.setFitToWidth(true);
        mapScrollPane.setPrefViewportHeight(600);
        mapScrollPane.setStyle("-fx-background: white;");

        // Assembly interface
        mainContainer.getChildren().addAll(
                new Label("Route Planner"),
                createInputForm(),
                new Separator(),
                createResultSection(),
                new Separator(),
                createMapSection()
        );

        mainScroll.setContent(mainContainer);
        Scene scene = new Scene(mainScroll, 800, 600);  // Fixed window size
        primaryStage.setTitle("Path Planner v2.0 with Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private VBox createResultSection() {
        VBox section = new VBox(5);
        section.getChildren().addAll(
                new Label("Result:"),
                resultArea
        );
        return section;
    }

    private VBox createMapSection() {
        VBox section = new VBox(5);
        section.getChildren().addAll(
                new Label("Route Visualization:"),
                mapScrollPane
        );
        section.setMinHeight(600);
        return section;
    }


    private GridPane createInputForm() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        grid.add(createComboRow("Start City*:", startCombo), 0, 0);
        grid.add(createComboRow("End City*:", endCombo), 0, 1);
        grid.add(createAttractionSelector(), 0, 2);

        Button searchButton = new Button("Search Route");
        searchButton.setOnAction(e -> handleSearch());
        grid.add(searchButton, 0, 3);

        return grid;
    }

    private HBox createComboRow(String labelText, ComboBox<String> comboBox) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.setMinWidth(100);
        comboBox.setPrefWidth(300);
        row.getChildren().addAll(label, comboBox);
        return row;
    }

    private VBox createAttractionSelector() {
        VBox container = new VBox(5);

        HBox labelBox = new HBox(5);
        Label mainLabel = new Label("Attractions:");
        Label optionalLabel = new Label("(optional: Ctrl or Shift + Click to multi-select)");
        optionalLabel.setStyle("-fx-text-fill: #666666;");
        labelBox.getChildren().addAll(mainLabel, optionalLabel);

        // Configure ListView
        attractionsList = new ListView<>(services.getAttractionSuggestions());
        attractionsList.setPrefHeight(120);
        attractionsList.setPlaceholder(new Label("No attractions available"));

        // Key: Set to multi-selection mode (by default, Ctrl and Shift are supported for multi-selection)
        attractionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        container.getChildren().addAll(labelBox, attractionsList);
        return container;
    }



    private void handleSearch() {
        String start = startCombo.getValue();
        String end = endCombo.getValue();
        List<String> attractions = attractionsList.getSelectionModel().getSelectedItems();

        if (invalidInput(start, end)) {
            showAlert("Error", "Please select start and end cities");
            return;
        }

        try {
            PathResults results = services.calculateRoute(start, end, attractions);
            showResults(results);

            List<String> requiredCities = services.routePlanner.getAttractionMgr().getCitiesFor(attractions);
            MapCanvas mapCanvas = new MapCanvas(
                    services.roadNetwork,
                    results,
                    requiredCities,
                    services.attractionManager
            );

            mapScrollPane.setContent(mapCanvas);
        } catch (Exception ex) {
            showAlert("Calculation Error", ex.getMessage());
        }

    }

    private boolean invalidInput(String start, String end) {
        return start == null || end == null || start.isEmpty() || end.isEmpty();
    }

    private void showResults(PathResults results) {
        StringJoiner path = new StringJoiner(" -> ");
        results.getPath().forEach(path::add);

        String output = String.format("""
            Total Distance: %d miles
            Optimal Path:
            %s
            Attractions Visited: %s
            """,
                results.getDistance(),
                path.toString(),
                String.join(", ", attractionsList.getSelectionModel().getSelectedItems()));

        resultArea.setText(output);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);

        alert.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
class MapCanvas extends Pane {
    private static final double CANVAS_WIDTH = 1200;
    private static final double CANVAS_HEIGHT = 800;
    private final Map<String, Point2D> cityCoordinates = new HashMap<>();
    private final RoadNetwork roadNetwork;
    private final Translate translate = new Translate();
    private final Scale scale = new Scale(1, 1);
    private final AttractionManager attractionManager;
    private Point2D mouseAnchor;


    // Constructor parameter list
    public MapCanvas(RoadNetwork roadNetwork, PathResults results, List<String> requiredCities, AttractionManager attractionManager) {
        this.roadNetwork = roadNetwork;
        this.attractionManager = attractionManager;
        setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        getTransforms().addAll(translate, scale);

        generateCityCoordinates(roadNetwork.getCities());
        drawNetwork(roadNetwork);
        drawPath(results.getPath());
        drawCities(roadNetwork.getCities(), results, requiredCities);
        setupMouseControls();
    }

    private void setupMouseControls() {
        // Translation processing
        setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mouseAnchor = new Point2D(event.getX(), event.getY());
            }
        });

        setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY && mouseAnchor != null) {
                double deltaX = (event.getX() - mouseAnchor.getX()) * scale.getX();
                double deltaY = (event.getY() - mouseAnchor.getY()) * scale.getY();
                translate.setX(translate.getX() + deltaX);
                translate.setY(translate.getY() + deltaY);
                mouseAnchor = new Point2D(event.getX(), event.getY());
            }
        });

        // Scroll wheel scaling processing
        addEventFilter(ScrollEvent.SCROLL, event -> {
            double zoomFactor = 1.05;
            if (event.getDeltaY() < 0) {
                zoomFactor = 1 / zoomFactor;
            }

            Point2D scenePoint = new Point2D(event.getX(), event.getY());
            Point2D localPoint = sceneToLocal(scenePoint);

            scale.setX(scale.getX() * zoomFactor);
            scale.setY(scale.getY() * zoomFactor);

            // Keep the zoom center point
            Point2D newScenePoint = localToScene(localPoint);
            translate.setX(translate.getX() + (scenePoint.getX() - newScenePoint.getX()));
            translate.setY(translate.getY() + (scenePoint.getY() - newScenePoint.getY()));

            event.consume();
        });
    }

    private void generateCityCoordinates(Set<String> cities) {
        // Smaller circular layouts generate coordinates
        List<String> cityList = new ArrayList<>(cities);
        double centerX = CANVAS_WIDTH / 2;
        double centerY = CANVAS_HEIGHT / 2;
        double radius = Math.min(CANVAS_WIDTH, CANVAS_HEIGHT) * 0.35;

        for (int i = 0; i < cityList.size(); i++) {
            double angle = 2 * Math.PI * i / cityList.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            cityCoordinates.put(cityList.get(i), new Point2D(x, y));
        }
    }


    private void drawNetwork(RoadNetwork roadNetwork) {
        Set<Pair<String, String>> drawnEdges = new HashSet<>();
        for (String city : roadNetwork.getCities()) {
            for (RoadNetwork.Edge edge : roadNetwork.getNeighbors(city)) {
                String neighbor = edge.getCity();
                Pair<String, String> edgePair = new Pair<>(city, neighbor);
                if (!drawnEdges.contains(edgePair)) {
                    Point2D start = cityCoordinates.get(city);
                    Point2D end = cityCoordinates.get(neighbor);
                    drawEdge(start, end, Color.LIGHTGRAY, 1, edge.distance, false);
                    drawnEdges.add(edgePair);
                    drawnEdges.add(new Pair<>(neighbor, city));
                }
            }
        }
    }
    private void drawPath(List<String> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            Point2D start = cityCoordinates.get(from);
            Point2D end = cityCoordinates.get(to);
            int distance = roadNetwork.getDistanceBetweenCities(from, to);
            drawEdge(start, end, Color.RED, 2, distance, true);
        }
    }


    private void drawEdge(Point2D start, Point2D end, Color color, double strokeWidth, int distance, boolean isPath) {
        double x1 = start.getX();
        double y1 = start.getY();
        double x2 = end.getX();
        double y2 = end.getY();

        // Adjust the endpoints of the line segments to avoid the city nodes
        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        double nodeRadius = 8;

        double adjustedX1 = x1 + Math.cos(angle) * nodeRadius;
        double adjustedY1 = y1 + Math.sin(angle) * nodeRadius;
        double adjustedX2 = x2 - Math.cos(angle) * nodeRadius;
        double adjustedY2 = y2 - Math.sin(angle) * nodeRadius;

        // Draw line segments
        Line line = new Line(adjustedX1, adjustedY1, adjustedX2, adjustedY2);
        line.setStroke(color);
        line.setStrokeWidth(strokeWidth);
        getChildren().add(line);

        // Draw arrows
        drawArrow(adjustedX1, adjustedY1, adjustedX2, adjustedY2, color);

        // Draw distance labels
        if (isPath) {
            double midX = (adjustedX1 + adjustedX2) / 2;
            double midY = (adjustedY1 + adjustedY2) / 2;
            Text distanceText = new Text(midX + 5, midY + 5, String.valueOf(distance));
            distanceText.setFill(color);
            distanceText.setFont(Font.font(10));
            getChildren().add(distanceText);
        }
    }

    private void drawArrow(double x1, double y1, double x2, double y2, Color color) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        double arrowLength = 10;
        double arrowWidth = Math.PI / 6;

        // Arrow tip coordinates
        double tipX = x2;
        double tipY = y2;

        // Calculate the points on both sides of the arrow
        double leftX = tipX - arrowLength * Math.cos(angle - arrowWidth);
        double leftY = tipY - arrowLength * Math.sin(angle - arrowWidth);
        double rightX = tipX - arrowLength * Math.cos(angle + arrowWidth);
        double rightY = tipY - arrowLength * Math.sin(angle + arrowWidth);

        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(tipX, tipY, leftX, leftY, rightX, rightY);
        arrow.setFill(color);
        getChildren().add(arrow);
    }


    private void drawCities(Set<String> cities, PathResults results, List<String> requiredCities) {
        List<String> resultPath = results.getPath();

        for (String city : cities) {
            Point2D location = cityCoordinates.get(city);
            Circle circle = new Circle(location.getX(), location.getY(), 8);

            if (city.equals(resultPath.get(0))) {
                circle.setFill(Color.BLUE); // starting point
            } else if (city.equals(resultPath.get(resultPath.size() - 1))) {
                circle.setFill(Color.GREEN); // destination
            } else if (requiredCities.contains(city)) {
                circle.setFill(Color.YELLOW); // attractions
            } else {
                circle.setFill(Color.GRAY); // common nodes
            }

            // Tooltip: Display the attractions included in the current city
            List<String> attractionsInCity = new ArrayList<>();
            for (Map.Entry<String, String> entry : attractionManager.getAttractions().entrySet()) {
                if (entry.getValue().equals(city)) {
                    attractionsInCity.add(entry.getKey());
                }
            }
            String tooltipText = attractionsInCity.isEmpty()
                    ? city
                    : city + "\nAttractions: " + String.join(", ", attractionsInCity);
            Tooltip.install(circle, new Tooltip(tooltipText));

            Text label = new Text(city);

            double offsetX = location.getX() < CANVAS_WIDTH / 2 ? -93 : 8;
            double offsetY = -5;

            label.setX(location.getX() + offsetX);
            label.setY(location.getY() + offsetY);
            label.setFont(Font.font(13));

            getChildren().addAll(circle, label);

        }

        Button playButton = new Button("▶ Play Path");
        playButton.setLayoutX(250);
        playButton.setLayoutY(80);
        playButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        playButton.setOnAction(e -> animatePath(resultPath));
        getChildren().add(playButton);
    }


    // Highlight lines of the animation playback path
    private void animatePath(List<String> path) {
        new Thread(() -> {
            for (int i = 0; i < path.size() - 1; i++) {
                String from = path.get(i);
                String to = path.get(i + 1);
                Point2D start = cityCoordinates.get(from);
                Point2D end = cityCoordinates.get(to);

                Platform.runLater(() -> drawEdge(start, end, Color.PURPLE, 3, -1, false));

                try {
                    Thread.sleep(1000); //Pause for one second between each paragraph
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }



    // Assist the Pair class
    private static class Pair<T, U> {
        final T first;
        final U second;

        Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return first.equals(pair.first) && second.equals(pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }
}






