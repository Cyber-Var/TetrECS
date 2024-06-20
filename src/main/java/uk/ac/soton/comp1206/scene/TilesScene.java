package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Tiles scene. Holds the UI with pictures of all possible pieces
 */
public class TilesScene extends BaseScene {

  /**
   * The logger of the class for printing information to console
   */
  private static final Logger logger = LogManager.getLogger(TilesScene.class);

  /**
   * Create a new Tiles scene
   * @param gameWindow the Game Window
   */
  public TilesScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Tiles Scene");
  }

  /**
   * Initialise the scene and handle ESC key being pressed
   */
  @Override
  public void initialise() {
    logger.info("Initialising Tiles");
    // Close scene if escape pressed
    this.getScene().setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ESCAPE) {
        shutDown();
      }
    });
  }

  /**
   * Build the Tiles window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    // Set game pane
    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    // Create main stack pane
    var tilesPane = new StackPane();
    tilesPane.setMaxWidth(gameWindow.getWidth());
    tilesPane.setMaxHeight(gameWindow.getHeight());
    tilesPane.getStyleClass().add("menu-background");
    root.getChildren().add(tilesPane);

    // Create main border pane
    var mainPane = new BorderPane();
    tilesPane.getChildren().add(mainPane);

    // Create a box with all components of the top of border pane
    var top = new HBox();
    top.setAlignment(Pos.CENTER);
    top.setSpacing(30);
    BorderPane.setMargin(top,new Insets(60, 30, 0, 30));

    // Create a box with all components of the center of border pane
    var center = new HBox();
    center.setAlignment(Pos.CENTER);
    center.setSpacing(30);

    // Create a box with all components of the bottom of border pane
    var bottom = new HBox();
    bottom.setAlignment(Pos.CENTER);
    bottom.setSpacing(30);

    var bottomPieces = new VBox();
    bottomPieces.setAlignment(Pos.CENTER);
    bottomPieces.setSpacing(30);
    BorderPane.setMargin(bottomPieces,new Insets(0, 30, 60, 30));

    try {

      // Top pieces
      var imgPiece0 = new Image(String.valueOf(this.getClass().getResource("/images/piece0.png")));
      var piece0 = new Rectangle();
      piece0.setHeight(120);
      piece0.setWidth(120);
      piece0.setFill(new ImagePattern(imgPiece0));
      top.getChildren().add(piece0);

      var imgPiece1 = new Image(String.valueOf(this.getClass().getResource("/images/piece1.png")));
      var piece1 = new Rectangle();
      piece1.setHeight(120);
      piece1.setWidth(120);
      piece1.setFill(new ImagePattern(imgPiece1));
      top.getChildren().add(piece1);

      var imgPiece2 = new Image(String.valueOf(this.getClass().getResource("/images/piece2.png")));
      var piece2 = new Rectangle();
      piece2.setHeight(120);
      piece2.setWidth(120);
      piece2.setFill(new ImagePattern(imgPiece2));
      top.getChildren().add(piece2);

      var imgPiece3 = new Image(String.valueOf(this.getClass().getResource("/images/piece3.png")));
      var piece3 = new Rectangle();
      piece3.setHeight(120);
      piece3.setWidth(120);
      piece3.setFill(new ImagePattern(imgPiece3));
      top.getChildren().add(piece3);

      var imgPiece4 = new Image(String.valueOf(this.getClass().getResource("/images/piece4.png")));
      var piece4 = new Rectangle();
      piece4.setHeight(120);
      piece4.setWidth(120);
      piece4.setFill(new ImagePattern(imgPiece4));
      top.getChildren().add(piece4);

      // Center pieces
      var imgPiece5 = new Image(String.valueOf(this.getClass().getResource("/images/piece5.png")));
      var piece5 = new Rectangle();
      piece5.setHeight(120);
      piece5.setWidth(120);
      piece5.setFill(new ImagePattern(imgPiece5));
      center.getChildren().add(piece5);

      var imgPiece6 = new Image(String.valueOf(this.getClass().getResource("/images/piece6.png")));
      var piece6 = new Rectangle();
      piece6.setHeight(120);
      piece6.setWidth(120);
      piece6.setFill(new ImagePattern(imgPiece6));
      center.getChildren().add(piece6);

      var imgPiece7 = new Image(String.valueOf(this.getClass().getResource("/images/piece7.png")));
      var piece7 = new Rectangle();
      piece7.setHeight(120);
      piece7.setWidth(120);
      piece7.setFill(new ImagePattern(imgPiece7));
      center.getChildren().add(piece7);

      var imgPiece8 = new Image(String.valueOf(this.getClass().getResource("/images/piece8.png")));
      var piece8 = new Rectangle();
      piece8.setHeight(120);
      piece8.setWidth(120);
      piece8.setFill(new ImagePattern(imgPiece8));
      center.getChildren().add(piece8);

      var imgPiece9 = new Image(String.valueOf(this.getClass().getResource("/images/piece9.png")));
      var piece9 = new Rectangle();
      piece9.setHeight(120);
      piece9.setWidth(120);
      piece9.setFill(new ImagePattern(imgPiece9));
      center.getChildren().add(piece9);

      // Bottom pieces
      var imgPiece10 = new Image(String.valueOf(this.getClass().getResource("/images/piece10.png")));
      var piece10 = new Rectangle();
      piece10.setHeight(120);
      piece10.setWidth(120);
      piece10.setFill(new ImagePattern(imgPiece10));
      bottom.getChildren().add(piece10);

      var imgPiece11 = new Image(String.valueOf(this.getClass().getResource("/images/piece11.png")));
      var piece11 = new Rectangle();
      piece11.setHeight(120);
      piece11.setWidth(120);
      piece11.setFill(new ImagePattern(imgPiece11));
      bottom.getChildren().add(piece11);

      var imgPiece12 = new Image(String.valueOf(this.getClass().getResource("/images/piece12.png")));
      var piece12 = new Rectangle();
      piece12.setHeight(120);
      piece12.setWidth(120);
      piece12.setFill(new ImagePattern(imgPiece12));
      bottom.getChildren().add(piece12);

      var imgPiece13 = new Image(String.valueOf(this.getClass().getResource("/images/piece13.png")));
      var piece13 = new Rectangle();
      piece13.setHeight(120);
      piece13.setWidth(120);
      piece13.setFill(new ImagePattern(imgPiece13));
      bottom.getChildren().add(piece13);

      var imgPiece14 = new Image(String.valueOf(this.getClass().getResource("/images/piece14.png")));
      var piece14 = new Rectangle();
      piece14.setHeight(120);
      piece14.setWidth(120);
      piece14.setFill(new ImagePattern(imgPiece14));
      bottom.getChildren().add(piece14);
    }
    catch (Exception e) {
      logger.error("Cannot load pictures");
    }

    // Button for returning to previous page
    var btnBack= new Button("Back");
    btnBack.setAlignment(Pos.CENTER);
    btnBack.getStyleClass().add("buttonBack");
    btnBack.setOnMouseClicked(mouseEvent -> shutDown());

    bottomPieces.getChildren().addAll(bottom, btnBack);
    mainPane.setTop(top);
    mainPane.setCenter(center);
    mainPane.setBottom(bottomPieces);
  }

  /**
   * Close the scene and start the instructions page
   */
  private void shutDown(){
    logger.info("Shutting down TilesScene");
    gameWindow.cleanup();
    gameWindow.loadScene(new InstructionsScene(gameWindow));
  }

}
