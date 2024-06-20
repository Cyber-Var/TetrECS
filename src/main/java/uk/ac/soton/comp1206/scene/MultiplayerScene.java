package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Multiplayer scene. Holds the UI for playing the game over multiple players.
 */
public class MultiplayerScene extends ChallengeScene {

  /**
   * The list of pairs so store users and their scores
   */
  private List<Pair<String, Integer>> pairs;

  /**
   * The Communicator used for communicating with the server
   */
  private Communicator communicator;

  /**
   * The box containing all players of the game
   */
  private VBox playersBox;

  /**
   * The Timer of the multiplayer scene
   */
  private Timer everySecond;

  /**
   * The Hugh score that will be displayed
   */
  private int highScore = 0;

  /**
   * The Multiplayer game logic
   */
  private MultiplayerGame multiGame;

  /**
   * The list of players in the game
   */
  private List<String> players;

  /**
   * The label to display messages of the chat
   */
  private Label chat;

  /**
   * The logger of the class for printing information to console
   */
  private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);

  /**
   * Create a new Multiplayer scene
   * @param gameWindow the Game Window
   */
  public MultiplayerScene(GameWindow gameWindow) {
    super(gameWindow);
    pairs = new ArrayList<>();
    multi = true;

    // Create multiplayer game
    multiGame = new MultiplayerGame(5, 5);
    game = multiGame;

    players = new ArrayList<>();
    logger.info("Creating Multiplayer Scene");
  }

  /**
   * Build the Multiplayer window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    super.build();

    // Remove level and high score components
    vbScoreLevel.getChildren().remove(lblLevelHeader);
    vbScoreLevel.getChildren().remove(lblLevel);
    top.getChildren().remove(vbHighScore);
    top.setSpacing(110);

    // Chat label
    chat = new Label("Keyboard \"T\"\nto use chat");
    chat.setTextAlignment(TextAlignment.CENTER);
    chat.setTextFill(Color.GREEN);
    top.getChildren().add(chat);

    // Box with all players
    var topRight = new VBox();
    playersBox = new VBox();
    playersBox.setAlignment(Pos.CENTER);
    var lblPlayers = new Label("\"<Players> : <score> (<lives>)\"");
    lblPlayers.getStyleClass().add("players");
    playersBox.getChildren().add(lblPlayers);
    topRight.getChildren().addAll(lblPlayers, playersBox);
    top.getChildren().add(topRight);

    // Handle communications received
    setListener();

    // Create communicator
    communicator = gameWindow.getCommunicator();
    for (int i = 0; i < 5; i++) {
      communicator.send("PIECE");
    }

    // Take action every second
    everySecond = new Timer();
    communicator.send("HISCORES");
    everySecond.schedule(new TimerTask() {
      @Override
      public void run() {
        if (multiGame.getQueueListSize() < 5) {
          communicator.send("PIECE");
        }
        communicator.send("SCORES");
        communicator.send("LIVES " + game.livesProperty().get());
        communicator.send("SCORE " + game.scoreProperty().get());

        // When player dies
        if (game.livesProperty().get() < 0){
          Platform.runLater(() -> {
            communicator.send("DIE");
            gameWindow.cleanup();

            //TODO maybe delete ?
            //game.cancelLoopTimer();

            multiGame.cancelLoopTimer();
            everySecond.cancel();
            shutDown();
          });
        }

        String blockList = "";
        for (int y = 0; y < 5; y++) {
          for (int x = 0; x < 5; x++) {
            blockList += gameBoard.getBlock(x, y).getValue() + " ";
          }
        }
        communicator.send("BOARD " + blockList);

      }
    }, 10, 1000);
  }

  /**
   * Handle the messages received from the server
   */
  private void setListener() {
    gameWindow.getCommunicator().addListener(communication -> Platform.runLater(() -> {
      String[] packet = communication.split(" ");
      String request = packet[0];
      String content = "";
      if (packet.length > 1) {
        content = packet[1];
      }
      switch (request) {
        // Message received
        case "MSG":
          chat.setText(content);
          break;
          // List of scores
        case "SCORES" :
          String[] scoresInfo = content.split("\n");
          for (String player : scoresInfo) {
            String[] playerInfo = player.split(":");
            String name = playerInfo[0];
            int score = Integer.parseInt(playerInfo[1]);
            String livesDead = playerInfo[2];
            String strPlayer = name + " : " + score + " (";
            if (livesDead.equals("DEAD")) {
              strPlayer += "dead)";
            }
            else {
              strPlayer += livesDead + ")";
            }
            var lblPlayer = new Label(strPlayer);
            lblPlayer.getStyleClass().add("players");

            int index = -100;
            for (int i = 0; i < players.size(); i++) {
              if (name.equals(players.get(i))) {
                index = i;
              }
            }

            if (index == -100) {
              pairs.add(new Pair(name, score));
              players.add(name);
              playersBox.getChildren().add(lblPlayer);
            }
            else {
              playersBox.getChildren().set(index, lblPlayer);
              pairs.set(index, new Pair(name, score));
            }
          }
          break;
          // Which piece to play
        case "PIECE":
          multiGame.push(Integer.parseInt(content));
          break;
          // High scores
        case "HISCORES":
          highScore = Integer.parseInt(content.split("\n")[0].split(":")[1]);
      }
    }));
  }

  /**
   * Close the scene and start the scores page
   */
  public void shutDown() {
    logger.info("Shutting down MultiplayerScene");
    // Close this window
    gameWindow.cleanup();
    everySecond.cancel();
    communicator.send("DIE");
    // Load appropriate scene
    if (highScore < game.scoreProperty().get()) {
      // TODO
      gameWindow.loadScene(new ScoresScene(gameWindow, multiGame, "You", pairs, true));
    }
    else {
      gameWindow.loadScene(new ScoresScene(gameWindow, game, "You", pairs, false));
    }
  }

}
