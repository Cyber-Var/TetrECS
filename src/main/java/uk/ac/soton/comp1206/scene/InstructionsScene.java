package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Instructions scene. Holds the UI with instructions explaining each detail of the game.
 */
public class InstructionsScene extends BaseScene {

  /**
   * The logger of the class for printing information to console
   */
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

  /**
   * Create a new Instructions scene
   * @param gameWindow the Game Window
   */
  public InstructionsScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Instructions Scene");
  }

  /**
   * Initialise the scene and handle ESC key being pressed
   */
  @Override
  public void initialise() {
    logger.info("Initialising Instructions");
    this.getScene().setOnKeyPressed(keyEvent -> {
      // Close scene if escape pressed
      if (keyEvent.getCode() == KeyCode.ESCAPE) {
        shutDown();
      }
    });
  }

  /**
   * Build the Instructions window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    // Set the game pane
    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    // Create the main stack pane
    var instructionsPane = new StackPane();
    instructionsPane.setMaxWidth(gameWindow.getWidth());
    instructionsPane.setMaxHeight(gameWindow.getHeight());
    instructionsPane.getStyleClass().add("menu-background");
    root.getChildren().add(instructionsPane);

    // Create the main border pane
    var mainPane = new BorderPane();
    instructionsPane.getChildren().add(mainPane);

    // Create a box with all components of the left of border pane
    var left = new VBox();
    left.setAlignment(Pos.CENTER);
    left.setSpacing(15);
    BorderPane.setMargin(left, new Insets(0, 0, 0, 10));

    // Create a box with all components of the center of border pane
    var center = new VBox();
    center.setAlignment(Pos.CENTER);
    center.setSpacing(15);

    // Create a box with all components of the right of border pane
    var right = new VBox();
    right.setAlignment(Pos.CENTER);
    right.setSpacing(25);
    BorderPane.setMargin(right, new Insets(0, 10, 0, 0));

    try {

      // Add components to left
      var imgGrid = new Image(String.valueOf(this.getClass().getResource("/images/grid.png")));
      var grid = new Rectangle();
      grid.setHeight(300);
      grid.setWidth(300);
      grid.setFill(new ImagePattern(imgGrid));
      left.getChildren().add(grid);

      var gridInfo = new Text("""
          Create complete rows or columns to clear them.
          The more cleared at the same time, the more points you earn!
          The bonus will multiply as you clear more in a row!
          The sides of the grid change color from green to red,
          indicating the time left before losing a life.
          """);
      gridInfo.getStyleClass().add("instructions");
      gridInfo.setTextAlignment(TextAlignment.CENTER);
      left.getChildren().add(gridInfo);

      var movementButtons = new HBox();
      movementButtons.setSpacing(20);
      left.getChildren().add(movementButtons);

      var imgArrows = new Image(String.valueOf(this.getClass().getResource("/images/arrows.png")));
      var imgWasd = new Image(String.valueOf(this.getClass().getResource("/images/wasd.png")));
      var arrows = new Rectangle();
      var wasd = new Rectangle();
      arrows.setHeight(100);
      wasd.setHeight(100);
      arrows.setWidth(150);
      wasd.setWidth(150);
      arrows.setFill(new ImagePattern(imgArrows));
      wasd.setFill(new ImagePattern(imgWasd));
      movementButtons.getChildren().addAll(arrows, wasd);

      var movementInfo = new Text("You can use your mouse to click to place\n"
          + "the tiles or use the cursor keys/WASD.");
      movementInfo.getStyleClass().add("instructions");
      movementInfo.setTextAlignment(TextAlignment.CENTER);
      left.getChildren().add(movementInfo);
      mainPane.setLeft(left);

      // Add components to center
      var lblTitle = new Label("Instructions");
      lblTitle.getStyleClass().add("pageTitle");
      // Page explanation when title clicked
      lblTitle.setOnMouseClicked(mouseEvent -> {
        // Show information alert
        Alert info = new Alert(AlertType.INFORMATION, """
            This is the Instructions page!
            See All Pieces -> to view all possible pieces used in the game.
            Back -> to return to the previous page.""");
        info.setTitle("Information");
        info.setHeaderText("Instructions page");
        info.showAndWait();
      });
      center.getChildren().add(lblTitle);

      var imgCurrentGrid = new Image(String.valueOf(this.getClass().getResource("/images/currentGrid.png")));
      var currentGrid = new Rectangle();
      currentGrid.setHeight(120);
      currentGrid.setWidth(120);
      currentGrid.setFill(new ImagePattern(imgCurrentGrid));
      center.getChildren().add(currentGrid);

      var currentGridInfo = new Text("""
          This shows the current piece.
          The circle represents what part of the piece
          will be placed. Click on it to ROTATE it.""");
      currentGridInfo.getStyleClass().add("instructions");
      currentGridInfo.setTextAlignment(TextAlignment.CENTER);
      center.getChildren().add(currentGridInfo);

      var imgFollowingGrid = new Image(String.valueOf(this.getClass().getResource("/images/followingGrid.png")));
      var followingGrid = new Rectangle();
      followingGrid.setHeight(90);
      followingGrid.setWidth(90);
      followingGrid.setFill(new ImagePattern(imgFollowingGrid));
      center.getChildren().add(followingGrid);

      var followingGridInfo = new Text("This shows the next piece.\n"
          + "Click it to SWAP it with the current piece.");
      followingGridInfo.getStyleClass().add("instructions");
      followingGridInfo.setTextAlignment(TextAlignment.CENTER);
      center.getChildren().add(followingGridInfo);

      var rotateButtons = new HBox();
      rotateButtons.setSpacing(20);
      rotateButtons.setAlignment(Pos.CENTER);
      center.getChildren().add(rotateButtons);

      var imgQz = new Image(String.valueOf(this.getClass().getResource("/images/qz.png")));
      var qz = new Rectangle();
      qz.setHeight(90);
      qz.setWidth(90);
      qz.setFill(new ImagePattern(imgQz));
      rotateButtons.getChildren().add(qz);

      var imgEc = new Image(String.valueOf(this.getClass().getResource("/images/ec.png")));
      var ec = new Rectangle();
      ec.setHeight(90);
      ec.setWidth(90);
      ec.setFill(new ImagePattern(imgEc));
      rotateButtons.getChildren().add(ec);

      var rotateInfo = new Text("You can rotate left and right with Q\n"
          + "and E or Z and C or [ and ].");
      rotateInfo.getStyleClass().add("instructions");
      rotateInfo.setTextAlignment(TextAlignment.CENTER);
      center.getChildren().add(rotateInfo);
      mainPane.setCenter(center);

      // Add components to right
      var imgEsc = new Image(String.valueOf(this.getClass().getResource("/images/esc.png")));
      var esc = new Rectangle();
      esc.setHeight(40);
      esc.setWidth(40);
      esc.setFill(new ImagePattern(imgEsc));
      right.getChildren().add(esc);

      var escInfo = new Text("""
          To go back to a
          previous screen,
          press Escape.""");
      escInfo.getStyleClass().add("instructions");
      escInfo.setTextAlignment(TextAlignment.CENTER);
      right.getChildren().add(escInfo);

      var dropButtons = new HBox();
      dropButtons.setSpacing(20);
      dropButtons.setAlignment(Pos.CENTER);
      right.getChildren().add(dropButtons);

      var imgEnter = new Image(String.valueOf(this.getClass().getResource("/images/enter.png")));
      var enter = new Rectangle();
      enter.setHeight(100);
      enter.setWidth(70);
      enter.setFill(new ImagePattern(imgEnter));
      dropButtons.getChildren().add(enter);

      var imgX = new Image(String.valueOf(this.getClass().getResource("/images/x.png")));
      var x = new Rectangle();
      x.setHeight(40);
      x.setWidth(40);
      x.setFill(new ImagePattern(imgX));
      dropButtons.getChildren().add(x);

      var dropInfo = new Text("Hit Enter or X to\n"
          + "DROP a piece.");
      dropInfo.getStyleClass().add("instructions");
      dropInfo.setTextAlignment(TextAlignment.CENTER);
      right.getChildren().add(dropInfo);

      var swapButtons = new HBox();
      swapButtons.setSpacing(20);
      swapButtons.setAlignment(Pos.CENTER);
      right.getChildren().add(swapButtons);

      var imgSpace = new Image(String.valueOf(this.getClass().getResource("/images/space.png")));
      var space = new Rectangle();
      space.setHeight(40);
      space.setWidth(90);
      space.setFill(new ImagePattern(imgSpace));
      swapButtons.getChildren().add(space);

      var imgR = new Image(String.valueOf(this.getClass().getResource("/images/r.png")));
      var r = new Rectangle();
      r.setHeight(40);
      r.setWidth(40);
      r.setFill(new ImagePattern(imgR));
      swapButtons.getChildren().add(r);

      var swapInfo = new Text("Hit SPACE or R to\n"
          + "SWAP the upcoming tiles.");
      swapInfo.getStyleClass().add("instructions");
      swapInfo.setTextAlignment(TextAlignment.CENTER);
      right.getChildren().add(swapInfo);

      // Button to view all possible pieces scene
      var pieces = new Button("See All Pieces -->");
      pieces.getStyleClass().add("buttonBack");
      right.getChildren().add(pieces);
      pieces.setOnMouseClicked(mouseEvent -> {
        gameWindow.cleanup();
        gameWindow.startTiles();
      });

      // Button to go to previous page
      var btnBack= new Button("Back");
      btnBack.setAlignment(Pos.CENTER);
      btnBack.getStyleClass().add("buttonBack");
      btnBack.setOnMouseClicked(mouseEvent -> shutDown());
      right.getChildren().add(btnBack);
      mainPane.setRight(right);
    }
    catch (Exception e) {
      logger.error("Cannot load pictures");
    }

  }

  /**
   * Close the scene and start the menu page
   */
  private void shutDown(){
    logger.info("Shutting down InstructionsScene");
    // Close this window
    gameWindow.cleanup();
    // Start menu
    gameWindow.loadScene(new MenuScene(gameWindow));
  }

}
