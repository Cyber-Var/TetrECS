package uk.ac.soton.comp1206.scene;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The Lobby scene. Holds the UI for the list of channels to connect to and the chat when already in a channel
 */
public class LobbyScene extends BaseScene {

  /**
   * The logger of the class for printing information to console
   */
  private static final Logger logger = LogManager.getLogger(LobbyScene.class);

  /**
   * The box containing all available channels that are currently available
   */
  private VBox channelsBox;

  /**
   * The Communicator used for communicating with the server
   */
  private Communicator communicator;

  /**
   * The Timer of the lobby
   */
  private Timer timer = new Timer();

  /**
   * The box containing all components of the left of border pane
   */
  private VBox left;

  /**
   * The value indicating whether the user is currently in a channel
   */
  private boolean inChannel = false;

  /**
   * The label with the name of the channel which the user is in
   */
  private Label lblChannelName;

  /**
   * The label containing a string with all the users connected to the channel
   */
  private Label lblUsers;

  /**
   * The box containing all messages in the chat
   */
  private VBox chatBox;

  /**
   * The button used to start the multiplayer game
   */
  private Button btnStart;

  /**
   * Create a new Lobby scene
   * @param gameWindow the Game Window
   */
  public LobbyScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Lobby Scene");
  }

  /**
   * Initialise the scene and handle server messages received
   */
  @Override
  public void initialise() {
    logger.info("Initialising Lobby");
    Multimedia.playMusicFile("game.wav");

    // Create communicator
    communicator = gameWindow.getCommunicator();
    // Set actions to be taken every 10 seconds
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        communicator.send("LIST");
      }
    }, 10, 10000);
    // Add listener to handle communications
    communicator.addListener(communication -> network(communication));

    // Close scene when escape pressed
    this.getScene().setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ESCAPE) {
        shutDown();
      }
    });
  }

  /**
   * Build the Lobby window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    // Set game pane
    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    // Create main stack pane
    var lobbyPane = new StackPane();
    lobbyPane.setMaxWidth(gameWindow.getWidth());
    lobbyPane.setMaxHeight(gameWindow.getHeight());
    lobbyPane.getStyleClass().add("menu-background");
    root.getChildren().add(lobbyPane);

    // Create main border pane
    var mainPane = new BorderPane();
    lobbyPane.getChildren().add(mainPane);

    // Create a box with all components of the right of borderpane
    var right = new VBox();
    right.setAlignment(Pos.CENTER);
    right.setSpacing(20);
    VBox.setVgrow(right, Priority.ALWAYS);

    // Explanation of the page when title clicked
    var lblTitle = new Label("Lobby");
    lblTitle.getStyleClass().add("pageTitle");
    lblTitle.setOnMouseClicked(mouseEvent -> {
      // Show information alert
      Alert info = new Alert(AlertType.INFORMATION, """
          This is the Lobby page!
          Add -> enter your channel name and click "Add" to create a new channel.
          Click any of the channel names to join a channel
          ~~~~~~~~~~~~~ When in a channel ~~~~~~~~~~~~~
          Send -> enter your message and click "Send" to send a message to the chat.
          Change -> enter a nickname and click "Change" to change your nickname.
          Start -> to start the game (only visible if you are the host.
          Back -> to return to the previous page.""");
      info.setTitle("Lobby");
      info.setHeaderText("Lobby page");
      info.showAndWait();
    });
    right.getChildren().add(lblTitle);

    // Create a box for creating a new channel
    var newChannelBox = new HBox();
    newChannelBox.setAlignment(Pos.CENTER);
    newChannelBox.setSpacing(5);

    // Field for inputting new channel name
    var txtNewChannel = new TextField("New channel name:");
    txtNewChannel.setMaxWidth(130);
    // Enter -> create new channel
    txtNewChannel.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER) {
        addNewChannel(txtNewChannel.getText());
        txtNewChannel.clear();
      }
    });
    // Button Add clicked -> create new channel
    var btnNewChannel = new Button("Add");
    btnNewChannel.getStyleClass().add("buttonBack");
    btnNewChannel.setOnMouseClicked(mouseEvent -> {
      addNewChannel(txtNewChannel.getText());
      txtNewChannel.clear();
    });

    newChannelBox.getChildren().addAll(txtNewChannel, btnNewChannel);

    var lblChannels = new Label("Join Channels");
    lblChannels.getStyleClass().add("heading");

    // Create a box for all existing channels
    channelsBox = new VBox();
    channelsBox.setAlignment(Pos.CENTER);
    channelsBox.setSpacing(20);

    right.getChildren().addAll(newChannelBox, lblChannels, channelsBox);

    // Create a box for all components of the left of border pane
    left = new VBox();
    left.setAlignment(Pos.CENTER);
    left.setSpacing(15);

    // Create a box for the name of the channel and its users
    var channelNameBox = new HBox();
    channelNameBox.setAlignment(Pos.CENTER);
    channelNameBox.setSpacing(5);
    lblChannelName = new Label("");
    lblChannelName.getStyleClass().add("channelName");
    lblUsers = new Label("");
    lblUsers.getStyleClass().add("playerBox");
    lblUsers.getStyleClass().add("instructions");
    channelNameBox.getChildren().addAll(lblChannelName, lblUsers);

    // Create a chat scroll pane
    var chatPane = new ScrollPane();
    chatPane.setPrefHeight(400);
    chatPane.setPrefWidth(500);
    chatPane.getStyleClass().add("scroller");

    // Create a box for chat messages
    chatBox = new VBox();
    chatBox.setAlignment(Pos.TOP_LEFT);
    chatBox.setSpacing(5);
    chatPane.setContent(chatBox);

    // Field for inputting messages
    var newMessageBox = new HBox();
    newMessageBox.setAlignment(Pos.CENTER);
    newMessageBox.setSpacing(5);
    var txtMessage = new TextField();
    txtMessage.setMaxWidth(450);
    // Enter -> send message
    txtMessage.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER) {
        communicator.send("MSG " + txtMessage.getText());
        txtMessage.clear();
      }
    });
    var btnSend = new Button("Send");
    btnSend.getStyleClass().add("buttonBack");
    // Button Send clicked -> send message
    btnSend.setOnMouseClicked(mouseEvent -> {
      communicator.send("MSG " + txtMessage.getText());
      txtMessage.clear();
    });
    newMessageBox.getChildren().addAll(txtMessage, btnSend);

    // Create box for changing nickname
    var nicknameBox = new HBox();
    nicknameBox.setAlignment(Pos.CENTER);
    nicknameBox.setSpacing(10);
    var txtNewNickname = new TextField("New nickname: ");
    txtNewNickname.setMaxWidth(100);
    // Enter -> change nickname
    txtNewNickname.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER) {
        communicator.send("NICK " + txtNewNickname.getText());
        txtNewNickname.clear();
      }
    });
    var btnChange = new Button("Change");
    btnChange.getStyleClass().add("buttonBack");
    // Button Change clicked -> change nickname
    btnChange.setOnMouseClicked(mouseEvent -> {
      communicator.send("NICK " + txtNewNickname.getText());
      txtNewNickname.clear();
    });
    nicknameBox.getChildren().addAll(txtNewNickname, btnChange);

    // Button for starting the game (if host)
    btnStart = new Button("Start");
    btnStart.getStyleClass().add("buttonBack");
    btnStart.setOnMouseClicked(mouseEvent -> communicator.send("START"));
    btnStart.setVisible(false);
    btnStart.setDisable(true);

    // Button for exiting channel
    var btnExitChat = new Button("Exit Chat");
    btnExitChat.getStyleClass().add("buttonBack");
    btnExitChat.setOnMouseClicked(mouseEvent -> {
      communicator.send("PART");
      btnStart.setVisible(false);
      btnChange.setDisable(true);
    });

    left.getChildren().addAll(channelNameBox, chatPane, newMessageBox,
        nicknameBox, btnStart, btnExitChat);
    left.setVisible(false);
    left.setDisable(true);

    mainPane.setRight(right);
    mainPane.setLeft(left);

    // Button for returning to previous page
    var btnBack = new Button("Back");
    btnBack.getStyleClass().add("buttonBack");
    btnBack.setOnMouseClicked(mouseEvent -> shutDown());
    BorderPane.setMargin(btnBack, new Insets(0, 50, 20, 685));
    mainPane.setBottom(btnBack);

  }

  /**
   * Add a new channel to the server
   * @param channelName the name of the channel to add
   */
  private void addNewChannel(String channelName) {
    var lblNewChannel = new Label(channelName);
    lblNewChannel.getStyleClass().add("channel");
    communicator.send("CREATE " + channelName);
    communicator.send("LIST");
    channelsBox.getChildren().add(lblNewChannel);
  }

  /**
   * Handle the messages received from the server
   * @param communication message received from the server
   */
  private void network(String communication) {
    Platform.runLater(() -> {
      String[] packet = communication.split(" ");
      String request = packet[0];
      String content = "";
      if (packet.length > 1) {
        content = packet[1];
      }
      switch (request) {
        // List of existing channels
        case "CHANNELS":
          channelsBox.getChildren().clear();
          if (!content.equals("")) {
            for (String channel : content.split("\n")) {
              var lblChannel = new Label(channel);
              lblChannel.getStyleClass().add("channel");
              lblChannel.setOnMouseClicked(mouseEvent -> communicator.send("JOIN " + channel));
              channelsBox.getChildren().add(lblChannel);
            }
          }
          break;
          // Join message received
        case "JOIN":
          if (!inChannel) {
            for (Node channel : channelsBox.getChildren()) {
              String channelName = ((Label) channel).getText();
              lblChannelName.setText(channelName);
              inChannel = true;
              left.setVisible(true);
              left.setDisable(false);
            }
            if (!lblUsers.getText().equals("Users: ")) {
              communicator.send("USERS");
            }
          }
          break;
          // Error occurred
        case "ERROR":
          Alert error = new Alert(AlertType.ERROR, content);
          error.showAndWait();
          break;
          // Nickname changed
        case "NICK":
          if (content.contains(":")) {
            String users = lblUsers.getText();
            String[] usernames = content.split(":");
            lblUsers.setText(users.replace(usernames[0], usernames[1]));
          }
          break;
          // Start game (only host)
        case "START":
          gameWindow.cleanup();
          gameWindow.loadScene(new MultiplayerScene(gameWindow));
          break;
          // Channel exited
        case "PARTED":
          left.setVisible(false);
          left.setDisable(true);
          inChannel = false;
          break;
          // List of users in a channel
        case "USERS":
          if (!content.equals("")) {
            String users = "";
            for (String username : content.split("\n")) {
              users += " |" + username + "|";
            }
            lblUsers.setText(users);
          }
          break;
          // Message received
        case "MSG":
          Multimedia.playAudioFile("message.wav");
          var message = new Label(content);
          message.getStyleClass().setAll("messages");
          chatBox.getChildren().add(message);
          break;
          // User is the host
        case "HOST":
          btnStart.setVisible(true);
          btnStart.setDisable(false);
          break;
      }
    });
  }

  /**
   * Close the scene and start the menu page
   */
  private void shutDown() {
    logger.info("Shutting down LobbyScene");
    inChannel = false;
    // Close this window
    gameWindow.cleanup();
    // Stop timer
    timer.cancel();
    communicator.send("QUIT");
    // Start menu
    gameWindow.loadScene(new MenuScene(gameWindow));
  }

}
