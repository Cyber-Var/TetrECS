package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Leaderboard;
import uk.ac.soton.comp1206.ui.Multimedia;
import uk.ac.soton.comp1206.ui.ScoresList;

/**
 * The Scores scene. Holds the UI for displaying local and online scores
 */
public class ScoresScene extends BaseScene {

  /**
   * The logger of the class for printing information to console
   */
  private static final Logger logger = LogManager.getLogger(ScoresScene.class);

  /**
   * The property with the list of local scores
   */
  private SimpleListProperty<Pair<String, Integer>> localScores;

  /**
   * The ui component displaying the local scores
   */
  private ScoresList localScoresList;

  /**
   * The game that is currently being played
   */
  private final Game game;

  /**
   * The property with the list of online scores
   */
  private SimpleListProperty<Pair<String, Integer>> remoteScores;

  /**
   * The ui component displaying the online scores
   */
  private ScoresList remoteScoresList;

  /**
   * The ui component displaying the previous online scores
   */
  private List<Pair<String, Integer>> previousOnlineScores = new ArrayList<>(5);

  /**
   * The username of the player
   */
  private String username;

  /**
   * The value indicating whether a high score was achieved
   */
  private boolean high;

  /**
   * Create a new Scores scene
   * @param gameWindow the Game Window
   * @param game the current game
   * @param username username of the player
   * @param scores scores of multiplayer game players
   * @param high value that indicates whether a high score was achieved
   */
  public ScoresScene(GameWindow gameWindow, Game game, String username, List<Pair<String, Integer>> scores, boolean high) {
    super(gameWindow);

    this.high = high;

    logger.info("Creating Scores Scene");

    // Local scores
    ObservableList<Pair<String, Integer>> localScoresObservable =
        FXCollections.observableArrayList(new ArrayList<>(5));
    localScores = new SimpleListProperty(localScoresObservable);
    if (scores.size() > 0) {
      localScoresList = new Leaderboard(username, scores);
    }
    else {
      localScoresList = new ScoresList("Local", username);
    }
    localScoresList.pairsProperty().bind(localScores);

    // Online scores
    ObservableList<Pair<String, Integer>> remoteScoresObservable =
        FXCollections.observableArrayList(previousOnlineScores);
    remoteScores = new SimpleListProperty<>(remoteScoresObservable);
    remoteScoresList = new ScoresList("Remote", username);
    remoteScoresList.pairsProperty().bind(remoteScores);

    this.game = game;
    this.username = username;
  }

  /**
   * Initialise the scene and handle ESC key being pressed
   */
  @Override
  public void initialise() {
    logger.info("Initialising Scores");
    game.cancelLoopTimer();
    Multimedia.playMusicFile("end.wav");
    // Close scene when escape pressed
    this.getScene().setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ESCAPE) {
        shutDown();
      }
    });
    // High score achieved alert
    if (high) {
      Alert alert = new Alert(AlertType.INFORMATION,"You've bit the highest score!!!");
      alert.showAndWait();
      writeOnlineScore(new Pair<>(username, game.scoreProperty().get()));
    }
  }

  /**
   * Build the Scores window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    // Set game pane
    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    // Setting local scores
    List<Pair<String, Integer>> storedScores = loadScores();
    for (Pair<String, Integer> pair : storedScores) {
      changeLocalScore(pair);
    }
    changeLocalScore(new Pair<>(username, game.scoreProperty().get()));

    // Add listener to communicator
    var communicator = gameWindow.getCommunicator();
    communicator.addListener(communication -> {
      String[] message = communication.split(" ");
      // High scores
      if (message[0].equals("HISCORES")) {
        Platform.runLater(() -> {
          int count = 0;
          for (Pair<String, Integer> pair : loadOnlineScores(communication)) {
            changeRemoteScore(pair);
            if (count >= 5) {
              break;
            }
            count++;
          }
          remoteScoresList.reveal();
        });
      }
    });
    gameWindow.getCommunicator().send("HISCORES UNIQUE");

    // Create main VBox
    var mainBox = new VBox();
    mainBox.setAlignment(Pos.CENTER);
    mainBox.setSpacing(50);
    root.getChildren().add(mainBox);
    mainBox.getStyleClass().add("menu-background");

    // Display scores
    localScoresList.reveal();
    remoteScoresList.reveal();
    mainBox.getChildren().addAll(localScoresList, remoteScoresList);

    // Button for returning to previous page
    var btnBack = new Button("Back");
    btnBack.getStyleClass().add("buttonBack");
    btnBack.setOnMouseClicked(mouseEvent -> shutDown());
    VBox.setMargin(btnBack, new Insets(0, 50, 50, 685));
    mainBox.getChildren().add(btnBack);

    writeScores();
  }

  /**
   * Close the scene and start the menu page
   */
  public void shutDown() {
    logger.info("Shutting down ScoresScene");
    // Close this window
    gameWindow.cleanup();
    // Start menu
    gameWindow.loadScene(new MenuScene(gameWindow));
  }

  /**
   * Add a username-score pair to the list of local scores
   * @param pair pair to add
   */
  public void changeLocalScore(Pair<String, Integer> pair) {
    if (localScores.size() >= 5) {
      localScores.remove(4);
    }
    localScores.add(pair);
    localScores = sortScores(localScores);
  }

  /**
   * Add a username-score pair to the list of online scores
   * @param pair to add
   */
  public void changeRemoteScore(Pair<String, Integer> pair) {
    if (remoteScores.size() >= 5) {
      remoteScores.remove(4);
    }
    remoteScores.add(pair);
    remoteScores = sortScores(remoteScores);
  }

  /**
   * Sort pairs in descending order based on the scores
   * @param scores list of scores to sort
   * @return sorted list
   */
  private SimpleListProperty<Pair<String, Integer>> sortScores(SimpleListProperty<Pair<String, Integer>> scores) {
    scores.sort((o1, o2) -> {
      if (o1.getValue() > o2.getValue()) {
        return -1;
      } else if (o1.getValue() < o2.getValue()) {
        return 1;
      }
      return 0;
    });
    return scores;
  }

  /**
   * Load local scores from the file
   * @return list of local scores
   */
  public List<Pair<String, Integer>> loadScores() {
    logger.info("Loading local scores");
    List<Pair<String, Integer>> storedScores = new ArrayList<>();
    try {
      File file = new File("scores.txt");
      if (!file.exists()) {
        file.createNewFile();
        return makeUpScores();
      }
      // Read scores from file
      BufferedReader reader = new BufferedReader(new FileReader("scores.txt"));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] pair = line.split(" = ");
        storedScores.add(new Pair<>(pair[0], Integer.valueOf(pair[1])));
      }
      reader.close();
    }
    catch (Exception e) {
      storedScores = makeUpScores();
    }
    return storedScores;
  }

  /**
   * Sort pairs in descending order based on the scores
   * @param message message received from server
   * @return sorted list of online scpres
   */
  public List<Pair<String, Integer>> loadOnlineScores(String message) {
    logger.info("Loading online scores");
    List<Pair<String, Integer>> storedScores = new ArrayList<>();
    String[] part = message.split(" ");
    String[] pairs = part[1].split("\n");
    // Get scores from message received
    for (String pair : pairs) {
      if (storedScores.size() >= 5) {
        return storedScores;
      }
      String name = pair.split(":")[0];
      String score = pair.split(":")[1];
      storedScores.add(new Pair<>(name, Integer.valueOf(score)));
    }
    return storedScores;
  }

  /**
   * Create a list with sample scores if the score.txt file is empty
   * @return sorted list
   */
  public List<Pair<String, Integer>> makeUpScores() {
    List<Pair<String, Integer>> newScores = new ArrayList<>();
    String[] letters = new String[]{"A", "B", "C", "D", "E"};
    // Create scores
    for (int i = 1; i <= 5; i++) {
      newScores.add(new Pair<>("Player " + letters[5-i], i * 1000));
    }
    return newScores;
  }

  /**
   * Write local scores to file
   */
  public void writeScores() {
    try {
      logger.info("Writing local scores");
      FileOutputStream fos = new FileOutputStream("scores.txt");
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
      // Write scores to file
      for (Pair<String, Integer> pair : localScores) {
        writer.write(pair.getKey() + " = " + pair.getValue());
        writer.newLine();
      }
      writer.close();
    }
    catch (Exception e) {
      logger.error("Cannot write scores");
    }
  }

  /**
   * Write online scores to the server
   */
  public void writeOnlineScore(Pair<String, Integer> pair) {
    logger.info("Writing online scores");
    var largest = previousOnlineScores.get(previousOnlineScores.size() - 1);
    var communicator = gameWindow.getCommunicator();
    // Write scores online
    if (largest.getValue() <= game.scoreProperty().get()) {
      communicator.send("HISCORE " + pair.getKey() + ":" + game.scoreProperty().get());
    }
    communicator.send("HISCORES");
  }

}
