package uk.ac.soton.comp1206.scene;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    /**
     * The logger of the class for printing information to console
     */
    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);

    /**
     * The game that is currently being played
     */
    protected Game game;

    /**
     * The game board on which the current game is being played
     */
    protected GameBoard gameBoard;

    /**
     * The board displaying the current piece
     */
    private PieceBoard pieceBoard;

    /**
     * The board displaying the following piece
     */
    private PieceBoard smallPieceBoard;

    /**
     * The box containing all components of the top of border pane
     */
    protected HBox top;

    /**
     * The box containing all components of the center of border pane
     */
    protected VBox center;

    /**
     * The rectangle illustrating the timer
     */
    private Rectangle rectangle;

    /**
     * The property of the high score
     */
    private IntegerProperty highScore = new SimpleIntegerProperty();

    /**
     * The box containing ui components for score and level
     */
    protected VBox vbScoreLevel;

    /**
     * The label displaying "Level"
     */
    protected Label lblLevelHeader;

    /**
     * The label displaying the level itself
     */
    protected Label lblLevel;

    /**
     * The box containing ui components for high score
     */
    protected VBox vbHighScore;

    /**
     * The value that indicates whether the scene is a multiplayer scene
     */
    protected boolean multi = false;

    /**
     * The place for inputting a message to send to chat
     */
    protected TextField txtMessage;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        setupGame();

        // Set the game pane
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        // Create the main stack pane
        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        // Create the main border pane
        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        // Create a box with all components of the center of border pane
        center = new VBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(5);

        // Add rectangle representing the timer
        var centerPane = new StackPane();
        var board = new GameBoard(game.getGrid(), 400, 400);
        rectangle = new Rectangle(405, 405);
        centerPane.getChildren().addAll(rectangle, board);

        // Field for inputting chat messages (only available if multiplayer)
        txtMessage = new TextField();
        txtMessage.setMaxWidth(400);
        txtMessage.setVisible(false);
        txtMessage.setDisable(true);
        txtMessage.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                gameWindow.getCommunicator().send("MSG " + txtMessage.getText());
                txtMessage.clear();
                txtMessage.setVisible(false);
                txtMessage.setDisable(true);
            }
        });
        center.getChildren().addAll(centerPane, txtMessage);
        mainPane.setCenter(center);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        // Create a box with all components of the top of border pane
        top = new HBox();
        top.setSpacing(190);
        top.setAlignment(Pos.CENTER);

        // Create a box for displaying score and level
        vbScoreLevel = new VBox();
        vbScoreLevel.setSpacing(15);
        vbScoreLevel.setAlignment(Pos.CENTER);

        // Score components
        Label lblScoreHeader = new Label("Score");
        lblScoreHeader.getStyleClass().add("heading");
        Label lblScore = new Label();
        lblScore.getStyleClass().add("score");
        lblScore.textProperty().bind(game.scoreProperty().asString());

        // Level components
        lblLevelHeader = new Label("Level");
        lblLevelHeader.getStyleClass().add("heading");
        lblLevel = new Label();
        lblLevel.getStyleClass().add("score");
        lblLevel.textProperty().bind(game.levelProperty().asString());
        vbScoreLevel.getChildren().addAll(lblScoreHeader, lblScore, lblLevelHeader, lblLevel);

        // Create a box for displaying lives and multiplier
        VBox vbLivesMultiplier = new VBox();
        vbLivesMultiplier.setSpacing(15);
        vbLivesMultiplier.setAlignment(Pos.CENTER);

        // Lives components
        Label lblLivesHeader = new Label("Lives");
        lblLivesHeader.getStyleClass().add("heading");
        Label lblLives = new Label();
        lblLives.getStyleClass().add("score");
        lblLives.textProperty().bind(game.livesProperty().asString());

        // Multiplier components
        Label lblMultiplierHeader = new Label("Multiplier");
        lblMultiplierHeader.getStyleClass().add("heading");
        Label lblMultiplier = new Label();
        lblMultiplier.getStyleClass().add("score");
        lblMultiplier.textProperty().bind(game.multiplierProperty().asString());
        vbLivesMultiplier.getChildren().addAll(lblLivesHeader, lblLives, lblMultiplierHeader, lblMultiplier);

        // Create a box for displaying high score
        vbHighScore = new VBox();
        vbHighScore.setSpacing(35);
        vbHighScore.setAlignment(Pos.CENTER);

        // High score components
        var lblHighScoreHeader = new Label("High Score");
        lblHighScoreHeader.getStyleClass().add("heading");
        var lblHighScore = new Label();
        lblHighScore.getStyleClass().add("hiscore");
        getHighScore();
        lblHighScore.textProperty().bind(highScore.asString());
        vbHighScore.getChildren().addAll(lblHighScoreHeader, lblHighScore);

        top.getChildren().addAll(vbScoreLevel, vbLivesMultiplier, vbHighScore);
        mainPane.setTop(top);

        // Create a box with all components of the right of border pane
        var right = new VBox();
        right.setAlignment(Pos.CENTER);
        right.setPrefWidth(250);
        right.setSpacing(10);

        // Add piece boards and swap button to right
        var lblFollowing = new Label("Following Piece");
        lblFollowing.getStyleClass().add("piece");
        smallPieceBoard = new PieceBoard(new Grid(3, 3), 100, 100);
        right.getChildren().addAll(lblFollowing, smallPieceBoard);
        var lblCurrent = new Label("Current Piece");
        lblCurrent.getStyleClass().add("piece");
        pieceBoard = new PieceBoard(new Grid(3, 3), 150, 150);
        right.getChildren().addAll(lblCurrent, pieceBoard);
        var btnSwap = new Button("Swap");
        btnSwap.getStyleClass().add("buttonBack");
        btnSwap.setOnAction(actionEvent -> game.swapCurrentPiece());
        right.getChildren().add(btnSwap);
        mainPane.setRight(right);

        this.gameBoard = board;

        //Add a next piece listener to the game with implementation of nextPiece method
        game.setNextPieceListener((currentPiece, followingPiece) -> {
            // Change value of high score if user scores higher than it was
            if (game.scoreProperty().get() > highScore.get()) {
                highScore.bind(game.scoreProperty());
            }
            // Display current and following piece
            pieceBoard.displayPiece(currentPiece);
            smallPieceBoard.displayPiece(followingPiece);
            // Handle hovering
            gameBoard.pieceChanged(currentPiece, gameBoard.getCurrentBlock());
        });

        //Add a right clicked listener to the main board with implementation of rightClicked method
        board.setRightClickedListener(() -> {
            // Stop previous hovering
            gameBoard.hover(game.getCurrentPiece(), gameBoard.getCurrentBlock(), MouseEvent.MOUSE_EXITED);
            // Rotate current piece
            game.rotateCurrentPiece(false);
            // Show new hovering
            gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getCurrentBlock());
            // Display new current piece
            pieceBoard.displayPiece(game.getCurrentPiece());
        });

        //Add a right clicked listener to the current piece board with implementation of rightClicked method
        pieceBoard.setRightClickedListener(() -> {
            // Stop previous hovering
            gameBoard.hover(game.getCurrentPiece(), gameBoard.getCurrentBlock(), MouseEvent.MOUSE_EXITED);
            // Rotate current piece
            game.rotateCurrentPiece(false);
            // Show new hovering
            gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getCurrentBlock());
            // Display new current piece
            pieceBoard.displayPiece(game.getCurrentPiece());
        });

        //Add a right clicked listener to the following piece board with implementation of rightClicked method
        smallPieceBoard.setRightClickedListener(() -> {
            // Swap current piece with following
            game.swapCurrentPiece();
        });

        //Add a line cleared listener to the game with implementation of fadeOut method
        game.setLineClearedListener(blockCoordinates -> {
            // Fade out blocks
            gameBoard.fadeOut(blockCoordinates);
        });

        //Add a game loop listener to the game with implementation of gameLoop method
        game.setGameLoopListener(milliseconds -> {
            // Continue game if lives are >= 0
            if (game.livesProperty().get() >= 0) {
                gameLoopShow(milliseconds);
            }
            // End game otherwise
            else {
                Platform.runLater(() -> {
                    if (multi) {
                        game.cancelLoopTimer();
                        multiShutDown();
                    }
                    else {
                        shutDown(true);
                    }
                });
            }
        });

        //Add a hover listener to the main board with implementation of hover method
        gameBoard.setHoverListener((gameBlock, event, green) -> {
            // Hover the block
            game.hover(gameBlock, event, green);
        });

    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        // Start game
        game.start();
        // Set actions that will be taken when keys are pressed on keyboard
        setKeyboard();
    }

    /**
     * Close the scene and start the login or menu page
     */
    public void shutDown(boolean scores) {
        logger.info("Shutting down ChallengeScene");
        // Close this window
        gameWindow.cleanup();
        // Stop the timer
        game.cancelLoopTimer();
        // Load another scene
        if (scores) {
            gameWindow.loadScene(new LoginScene(gameWindow, game));
        }
        else {
            gameWindow.loadScene(new MenuScene(gameWindow));
        }
    }

    /**
     * Set the actions that will be taken when certain keys are pressed on the keyboard
     */
    public void setKeyboard() {
        this.getScene().setOnKeyPressed(keyEvent -> {
            // Exit game
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                shutDown(false);
            }
            // Rotate piece left
            else if (keyEvent.getCode() == KeyCode.Q ||
                keyEvent.getCode() == KeyCode.Z ||
                keyEvent.getCode() == KeyCode.OPEN_BRACKET) {
                gameBoard.hover(game.getCurrentPiece(), gameBoard.getCurrentBlock(), MouseEvent.MOUSE_EXITED);
                game.rotateCurrentPiece(true);
                gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getCurrentBlock());
                pieceBoard.displayPiece(game.getCurrentPiece());
                smallPieceBoard.displayPiece(game.getFollowingPiece());
            }
            // Rotate piece right
            else if (keyEvent.getCode() == KeyCode.E ||
                keyEvent.getCode() == KeyCode.C ||
                keyEvent.getCode() == KeyCode.CLOSE_BRACKET) {
                gameBoard.hover(game.getCurrentPiece(), gameBoard.getCurrentBlock(), MouseEvent.MOUSE_EXITED);
                game.rotateCurrentPiece(false);
                gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getCurrentBlock());
                pieceBoard.displayPiece(game.getCurrentPiece());
                smallPieceBoard.displayPiece(game.getFollowingPiece());
            }
            // Swap current piece with following
            else if (keyEvent.getCode() == KeyCode.SPACE ||
                keyEvent.getCode() == KeyCode.R) {
                game.swapCurrentPiece();
            }
            // Drop piece
            else if (keyEvent.getCode() == KeyCode.ENTER ||
                keyEvent.getCode() == KeyCode.X) {
                gameBoard.dropPiece(gameBoard.getCurrentBlock());
            }
            else {
                int x = gameBoard.getCurrentBlock().getX();
                int y = gameBoard.getCurrentBlock().getY();
                // Move piece left
                if (keyEvent.getCode().equals(KeyCode.LEFT) ||
                    keyEvent.getCode().equals(KeyCode.A)) {
                    if (x - 1 >= 0) {
                        gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getBlock(x - 1, y));
                    }
                    else {
                        gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getBlock(0, y));
                    }
                }
                // Move piece right
                else if (keyEvent.getCode().equals(KeyCode.RIGHT) ||
                    keyEvent.getCode().equals(KeyCode.D)) {
                    if (x + 1 <= gameBoard.getColumnCount() - 1) {
                        gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getBlock(x + 1, y));
                    }
                    else {
                        gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getBlock(gameBoard.getColumnCount() - 1, y));
                    }
                }
                // Move piece up
                else if (keyEvent.getCode().equals(KeyCode.UP) ||
                    keyEvent.getCode().equals(KeyCode.W)) {
                    if (y - 1 >= 0) {
                        gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getBlock(x, y - 1));
                    }
                    else {
                        gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getBlock(x, 0));
                    }
                }
                // Move piece down
                else if (keyEvent.getCode().equals(KeyCode.DOWN) ||
                    keyEvent.getCode().equals(KeyCode.S)) {
                    if (y + 1 <= gameBoard.getRowCount() - 1) {
                        gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getBlock(x, y + 1));
                    }
                    else {
                        gameBoard.pieceChanged(game.getCurrentPiece(), gameBoard.getBlock(x, gameBoard.getColumnCount() - 1));
                    }
                }
                // Use chat (only in multiplayer)
                else if (keyEvent.getCode() == KeyCode.T) {
                    if (multi) {
                        txtMessage.setVisible(true);
                        txtMessage.setDisable(false);
                    }
                }
            }
        });
    }

    /**
     * Display the timer as a rectangle that changes its color from green to red
     */
    public void gameLoopShow(int milliseconds) {
        rectangle.setStroke(Color.rgb(0, 204, 0));
        var timeline = new Timeline();
        KeyFrame frame = new KeyFrame(Duration.millis(milliseconds),
            new KeyValue(rectangle.strokeProperty(), Color.rgb(255, 0, 0), Interpolator.LINEAR));
        timeline.getKeyFrames().add(frame);
        // Play the timer animation
        timeline.play();
    }

    /**
     * Get the high score from the server
     */
    public void getHighScore() {
        // Creeate communicator
        var communicator = gameWindow.getCommunicator();
        communicator.addListener(communication -> Platform.runLater(() -> {
            // Split message
            String[] message = communication.split(" ");
            // Set high score
            if (message[0].equals("HISCORES")) {
                String pair = message[1].split("\n")[0];
                highScore.set(Integer.parseInt(pair.split(":")[1]));
            }
        }));
        communicator.send("HISCORES DEFAULT");
    }

    /**
     * Shut down the multiplayer scene
     */
    private void multiShutDown() {
        logger.info("Shutting down Multiplayer ChallengeScene");
        ((MultiplayerScene) this).shutDown();
    }

}
