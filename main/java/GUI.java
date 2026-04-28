import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.DecimalFormat;

public class GUI extends Application {

    // Sppinners for source and destination nodes
    private Spinner<Integer> sourceSpinner;
    private Spinner<Integer> destinationSpinner;

    private ComboBox<String> modeBox;
    private TextArea outputArea;
    private Label statusLabel;
    private Graph graph;
    private int minNodeId = 0;
    private int maxNodeId = 0;
    private final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void start(Stage stage) {

        // Buttons
        Button loadBtn = new Button("Load File");
        Button runBtn = new Button("Run");

        // Initialize spinners
        sourceSpinner = new Spinner<>();
        destinationSpinner = new Spinner<>();
        sourceSpinner.setEditable(true);
        destinationSpinner.setEditable(true);
        sourceSpinner.setDisable(true);
        destinationSpinner.setDisable(true);

        // Mode selection
        modeBox = new ComboBox<>();
        modeBox.getItems().addAll("Distance", "Time", "Both");
        modeBox.setValue("Both");

        Label title = new Label("Dijkstra Routing System");
        title.getStyleClass().add("title");

        // Hbox for title and load button
        HBox titleRow = new HBox(12, title, loadBtn);
        titleRow.setAlignment(Pos.CENTER);
        titleRow.setPadding(new Insets(0, 0, 6, 0));

        // Layout form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);

        form.add(new Label("Source:"), 0, 0);
        form.add(sourceSpinner, 1, 0);

        form.add(new Label("Destination:"), 0, 1);
        form.add(destinationSpinner, 1, 1);

        form.add(new Label("Mode:"), 0, 2);

        HBox modeRow = new HBox(10, modeBox, runBtn);
        modeRow.setAlignment(Pos.CENTER_LEFT);

        form.add(modeRow, 1, 2);

        // Output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(160);
        outputArea.setMaxWidth(620);

        Label outLabel = new Label("Main Result");
        outLabel.getStyleClass().add("section-title");

        // VBox for the card layout
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18));
        card.setMaxWidth(700);

        card.getChildren().addAll(
                titleRow,
                form,
                outLabel,
                outputArea
        );

        // Status label
        statusLabel = new Label("Ready.");
        statusLabel.getStyleClass().add("status-bar");

        // Wrap card and status in center VBox
        VBox centerWrap = new VBox(12, card, statusLabel);
        centerWrap.setAlignment(Pos.CENTER);
        centerWrap.setPadding(new Insets(20));

        StackPane root = new StackPane();
        root.getChildren().addAll(buildBackgroundLayer(), centerWrap);

        // Create scene and apply CSS
        Scene scene = new Scene(root, 1000, 700);

        File cssFile = new File("C:\\Users\\Ibtisal\\OneDrive\\Desktop\\Algorithm\\Dijkstra\\src\\main\\java\\style.css");
        if (cssFile.exists()) {
            scene.getStylesheets().add(cssFile.toURI().toString());
        } else {
            System.out.println("style.css NOT FOUND in project root");
        }

        loadBtn.setOnAction(e -> onLoadFile(stage));
        runBtn.setOnAction(e -> onRun());

        stage.setTitle("Dijkstra");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    // Background layer with optional image
    private Pane buildBackgroundLayer() {
        StackPane bg = new StackPane();
        bg.setPrefSize(1200, 800);

        try {
            File f = new File("C:\\Users\\Ibtisal\\OneDrive\\Desktop\\Algorithm\\Dijkstra\\src\\main\\java\\background.png");
            if (f.exists()) {
                Image img = new Image(f.toURI().toString());
                ImageView view = new ImageView(img);
                view.setPreserveRatio(false);
                view.fitWidthProperty().bind(bg.widthProperty());
                view.fitHeightProperty().bind(bg.heightProperty());
                view.setOpacity(0.25);
                view.setMouseTransparent(true);
                bg.getChildren().add(view);
            }
        } catch (Exception ignored) {
        }

        bg.setMouseTransparent(true);
        bg.setStyle("-fx-background-color: linear-gradient(to bottom, #f7f9fc, #e9eff7);");
        return bg;
    }

    // Handle file loading
    private void onLoadFile(Stage stage) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Graph Input File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        File file = chooser.showOpenDialog(stage);
        if (file == null) return;

        statusLabel.setText("Loading file...");
        outputArea.setText("");

        try {
            FileLoader loader = new FileLoader();
            FileLoader.LoadResult r = loader.load(file);

            graph = r.graph;
            if (graph == null) throw new IllegalArgumentException("Loaded graph is null.");

            if (graph.nodesCount() == 0) {
                throw new IllegalArgumentException("No nodes found in the file.");
            }

            minNodeId = graph.getMinNodeId();
            maxNodeId = graph.getMaxNodeId();

            sourceSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minNodeId, maxNodeId, minNodeId));
            destinationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minNodeId, maxNodeId, minNodeId));

            int defaultSrc = (r.source != null) ? r.source : minNodeId;
            int defaultDst = (r.destination != null) ? r.destination : minNodeId;

            defaultSrc = moveToNearestValidNode(defaultSrc);
            defaultDst = moveToNearestValidNode(defaultDst);

            sourceSpinner.getValueFactory().setValue(defaultSrc);
            destinationSpinner.getValueFactory().setValue(defaultDst);

            autoAdjustSpinnerValue(sourceSpinner);
            autoAdjustSpinnerValue(destinationSpinner);

            sourceSpinner.setDisable(false);
            destinationSpinner.setDisable(false);

            outputArea.setText(
                    "File loaded successfully.\n" +
                            "Nodes: " + graph.nodesCount() + "\n" +
                            "Edges: " + graph.edgesCount() + "\n"
            );

            statusLabel.setText("Loaded. Nodes: " + graph.nodesCount() + ", Edges: " + graph.edgesCount());

        } catch (Exception ex) {
            graph = null;
            sourceSpinner.setDisable(true);
            destinationSpinner.setDisable(true);

            statusLabel.setText("Load failed: " + ex.getMessage());
            outputArea.setText("Error:\n" + ex.getMessage());
        }
    }

    // Handle Run button action
    private void onRun() {

        if (graph == null) {
            statusLabel.setText("Please load a file first.");
            return;
        }

        int src = moveToNearestValidNode(sourceSpinner.getValue());
        int dst = moveToNearestValidNode(destinationSpinner.getValue());

        if (!graph.containsNode(src) || !graph.containsNode(dst)) {
            statusLabel.setText("Source/Destination must be valid node IDs from the file.");
            return;
        }

        String mode = modeBox.getValue();
        if (mode == null) {
            statusLabel.setText("Please select a mode.");
            return;
        }

        statusLabel.setText("Computing...");
        outputArea.setText("Working...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {

                Dijkstra dijkstra = new Dijkstra();

                StringBuilder mainOut = new StringBuilder();

                if ("Both".equalsIgnoreCase(mode)) {

                    PathResult r1 = dijkstra.run(graph, src, dst, "Distance");
                    PathResult r2 = dijkstra.run(graph, src, dst, "Time");

                    mainOut.append("Shortest Distance Result:\n");
                    mainOut.append("\n");
                    mainOut.append(formatResult(r1)).append("\n\n");

                    mainOut.append("Least Travel Time Result: \n");
                    mainOut.append("\n");
                    mainOut.append(formatResult(r2));

                } else {

                    PathResult r = dijkstra.run(graph, src, dst, mode);

                    mainOut.append(mode).append(" Optimal:\n");
                    mainOut.append("\n");
                    mainOut.append(formatResult(r));
                }

                final String mainText = mainOut.toString();

                javafx.application.Platform.runLater(() -> {
                    outputArea.setText(mainText);
                    statusLabel.setText("Done.");
                });

                return null;
            }
        };

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            outputArea.setText("Error:\n" + (ex == null ? "Unknown error" : ex.getMessage()));
            statusLabel.setText("Run failed.");
        });

        new Thread(task, "dijkstra-task").start();
    }

    // Format the main result output
    private String formatResult(PathResult r) {
        if (!r.isFound()) {
            return "No path found.";
        }

        int edges = Math.max(0, r.getPath().size() - 1);

        return "Path: " + r.pathAsString() + "\n"
                + "Total distance (km): " + df.format(r.getTotalDistance()) + "\n"
                + "Total time (min): " + df.format(r.getTotalTime()) + "\n"
                + "Edges count: " + edges;
    }

    // Snap to nearest valid node ID
    private int moveToNearestValidNode(int x) {
        if (graph == null) return x;

        if (x < minNodeId) x = minNodeId;
        if (x > maxNodeId) x = maxNodeId;

        if (graph.isPresent(x)) return x;

        int left = x - 1;
        int right = x + 1;

        while (left >= minNodeId || right <= maxNodeId) {
            if (left >= minNodeId && graph.isPresent(left)) return left;
            if (right <= maxNodeId && graph.isPresent(right)) return right;
            left--;
            right++;
        }

        return minNodeId;
    }

    // Auto-adjust spinner value to nearest valid node on commit
    private void autoAdjustSpinnerValue(Spinner<Integer> spinner) {
        spinner.getEditor().setOnAction(e -> updateSpinnerValue(spinner));

        spinner.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                updateSpinnerValue(spinner);
            }
        });
    }

    // Update spinner value based on typed input
    private void updateSpinnerValue(Spinner<Integer> spinner) {
        SpinnerValueFactory<Integer> vf = spinner.getValueFactory();
        if (vf == null) return;

        String text = spinner.getEditor().getText();
        if (text == null || text.trim().isEmpty()) {
            spinner.getEditor().setText(String.valueOf(spinner.getValue()));
            return;
        }

        try {
            int typed = Integer.parseInt(text.trim());
            int snapped = moveToNearestValidNode(typed);

            vf.setValue(snapped);
            spinner.getEditor().setText(String.valueOf(snapped));
        } catch (NumberFormatException ex) {
            spinner.getEditor().setText(String.valueOf(spinner.getValue()));
        }
    }
}
