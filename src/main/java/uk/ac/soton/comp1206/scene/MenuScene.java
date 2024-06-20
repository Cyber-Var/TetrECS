package uk.ac.soton.comp1206.scene;

import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    /**
     * The logger of the class for printing information to console
     */
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        // Set game pane
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        // Create main stack pane
        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        try {
            // TetrECS image component
            var imgTetrecs = new Image(
                String.valueOf(this.getClass().getResource("/images/TetrECS.png")));
            var view = new ImageView(imgTetrecs);
            view.setFitWidth(500);
            view.setFitHeight(100);
            view.setOpacity(0.5);

            // Transition for image to move
            var transition = new PathTransition();
            transition.setDuration(Duration.millis(10000));
            transition.setNode(view);
            var path = new Path();
            path.getElements().add(new MoveTo(0f, 50f));
            path.getElements().add(new CubicCurveTo(40f, 10f, 390f, 240f, 500, 50f));
            transition.setPath(path);
            transition.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
            transition.setCycleCount(4);
            transition.setAutoReverse(true);
            transition.play();

            StackPane.setMargin(view, new Insets(50, 150, 470, 150));
            menuPane.getChildren().add(view);

        }
        catch (Exception e) {
            logger.error("Cannot load picture");
        }

        // Create main border pane
        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        // Explanation of the page when title clicked
        var lblTitle = new Label("Menu");
        lblTitle.getStyleClass().add("pageTitle");
        // Show information alert
        lblTitle.setOnMouseClicked(mouseEvent -> {
            Alert info = new Alert(AlertType.INFORMATION, """
                This is the Menu page!
                Play -> to play a Single Player game.
                Instructions -> to view instructions as to how to play the game.
                Multiplayer -> to play a Multiplayer game.""");
            info.setTitle("Information");
            info.setHeaderText("Menu page");
            info.showAndWait();
        });
        BorderPane.setAlignment(lblTitle, Pos.CENTER_RIGHT);
        mainPane.setTop(lblTitle);

        // Menu buttons
        VBox vbButtons = new VBox();
        vbButtons.setAlignment(Pos.CENTER);
        vbButtons.setSpacing(25);

        // Play single player
        var btnPlay = new Button("Play");
        btnPlay.getStyleClass().add("menuItem");

        // Show instructions
        var btnInstructions = new Button("Instructions");
        btnInstructions.getStyleClass().add("menuItem");

        // Play multiplayer
        var btnMultiplayerGame = new Button("Multiplayer Game");
        btnMultiplayerGame.getStyleClass().add("menuItem");

        vbButtons.getChildren().addAll(btnPlay, btnInstructions, btnMultiplayerGame);

        //Bind the button action to the startGame method in the menu
        btnPlay.setOnAction(this::startGame);

        //Bind the button action to the startInstructions method in the menu
        btnInstructions.setOnAction(this::startInstructions);
        mainPane.setCenter(vbButtons);

        //Bind the button action to the startMultiplayer method in the menu
        btnMultiplayerGame.setOnAction(this::startMultiplayerGame);
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        logger.info("Initialising Menu");
        Multimedia.playMusicFile("menu.mp3");
        // Close scene when escape pressed
        this.getScene().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                shutDown();
            }
        });
    }

    /**
     * Handle when the Play Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

    /**
     * Close the scene and exit
     */
    public void shutDown() {
        logger.info("Shutting down MenuScene");
        // Close this window
        gameWindow.cleanup();
        // Exit
        System.exit(0);
    }

    /**
     * Handle when the show Instructions button is pressed
     * @param event event
     */
    private void startInstructions(ActionEvent event) {
        gameWindow.startInstructions();
    }

    /**
     * Handle when the Play Multiplayer Game button is pressed
     * @param event event
     */
    private void startMultiplayerGame(ActionEvent event) {
        gameWindow.startLobby();
    }

}
