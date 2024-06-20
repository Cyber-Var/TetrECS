package uk.ac.soton.comp1206.game;

import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The MultiplayerGame class handles the game played within multiple players. Methods to manipulate the game state
 * and to handle actions made by the players are taking place inside this class.
 */
public class MultiplayerGame extends Game {

  /**
   * The logger of the class for printing information to console
   */
  private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

  /**
   * The queue to store pieces in order, received from the server
   */
  private Deque<Integer> queue = new ArrayDeque<>(5);;

  /**
   * Create a new multiplayer game with the specified rows and columns. Calls the constructor of super class.
   * @param cols number of columns
   * @param rows number of rows
   */
  public MultiplayerGame(int cols, int rows) {
    super(cols, rows);
  }

  /**
   * Initialise a new multiplayer game and set up anything that needs to be done at the start.
   * Calls the initialiseGame method of the super class
   */
  @Override
  public void initialiseGame() {
    super.initialiseGame();
    logger.info("Initialising game");
    Multimedia.playMusicFile("game_start.wav");

    // Set initial current piece
    currentPiece = spawnPiece();
    // Set initial following piece
    followingPiece = spawnPiece();

    // Call the listener nextPiece method
    if (nextPieceListener != null) {
      nextPieceListener.nextPiece(currentPiece, followingPiece);
    }
  }

  /**
   * Spawn a piece to play from the queue of pieces received from the server
   * @return piece
   */
  @Override
  public GamePiece spawnPiece() {
    try {
      // Take a piece off the queue
      return GamePiece.createPiece(queue.poll());
    }
    catch (Exception e) {
      logger.error("Cannot spawn piece");
    }
    return null;
  }

  /**
   * Push a piece to the queue
   * @param number the number of the piece to add
   */
  public void push(int number) {
    try {
      // Push the piece number onto queue
      queue.add(number);
    }
    catch (Exception e) {
      logger.error("Cannot push piece");
    }
  }

  /**
   * Get the size of the queue of pieces
   * @return size
   */
  public int getQueueListSize() {
    return queue.size();
  }

}
