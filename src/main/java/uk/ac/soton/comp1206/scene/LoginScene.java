package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The Login scene. Holds the UI for creating a username in order to view the player scores
 */
public class LoginScene extends BaseScene {

  /**
   * The logger of the class for printing information to console
   */
  private static final Logger logger = LogManager.getLogger(LoginScene.class);

  /**
   * The game that is currently being played
   */
  private Game game;

  /**
   * The place for inputting the new username
   */
  private TextField txtUsername;

  /**
   * Create a new Login scene
   * @param gameWindow the Game Window
   * @param game the game that has just been played (stores the score of the user)
   */
  public LoginScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.game = game;
    logger.info("Creating Login Scene");
  }

  /**
   * Initialise the scene and handle ESC key being pressed
   */
  @Override
  public void initialise() {
    Multimedia.playMusicFile("end.wav");
    game.cancelLoopTimer();
    logger.info("Initialising Login");
    // Close scene if escape pressed
    this.getScene().setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ESCAPE) {
        gameWindow.cleanup();
        gameWindow.loadScene(new MenuScene(gameWindow));
      }
    });
  }

  /**
   * Build the Login window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    // Set game pane
    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    // Create main stack pane
    var loginPane = new StackPane();
    loginPane.setMaxWidth(gameWindow.getWidth());
    loginPane.setMaxHeight(gameWindow.getHeight());
    loginPane.getStyleClass().add("menu-background");
    root.getChildren().add(loginPane);

    // Create main border pane
    var mainPane = new BorderPane();
    loginPane.getChildren().add(mainPane);

    // Box with components for login
    var loginBox = new VBox();
    loginBox.setAlignment(Pos.CENTER);
    loginBox.setSpacing(30);

    var lblUsername = new Label("Enter your username:");
    lblUsername.getStyleClass().add("enter");

    // Field to input username
    txtUsername = new TextField();
    txtUsername.setMaxWidth(250);
    // Enter -> check username
    txtUsername.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER) {
        checkUsername(txtUsername.getText());
      }
    });

    var btnEnter = new Button("Enter");
    btnEnter.getStyleClass().add("buttonBack");
    // Button Enter clicked -> check username
    btnEnter.setOnMouseClicked(mouseEvent -> checkUsername(txtUsername.getText()));

    loginBox.getChildren().addAll(lblUsername, txtUsername, btnEnter);
    mainPane.setCenter(loginBox);

    // Button for returning to previous page
    var btnBack = new Button("Back");
    btnBack.getStyleClass().add("buttonBack");
    btnBack.setOnMouseClicked(mouseEvent -> {
      logger.info("Shutting down LoginScene");
      gameWindow.cleanup();
      gameWindow.loadScene(new MenuScene(gameWindow));
    });
    BorderPane.setMargin(btnBack, new Insets(0, 50, 50, 685));
    mainPane.setBottom(btnBack);

  }

  /**
   * Check whether the entered username is correct and take action
   * @param username username to check
   */
  private void checkUsername(String username) {
    // If username is not valid, show error alert
    if (!username.matches("([a-z]|[A-Z])+[0-9]*")) {
      logger.info("Invalid username");
      Alert error = new Alert(Alert.AlertType.ERROR,"Invalid username\n"
          + "(only letters and numbers allowed, with numbers at the end).");
      error.showAndWait();
    }
    // If username is valid, show scores
    else {
      logger.info("Shutting down LoginScene (valid username)");
      gameWindow.cleanup();
      gameWindow.loadScene(new ScoresScene(gameWindow, game, username, new ArrayList<>(), false));
    }
  }

}
