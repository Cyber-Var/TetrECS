package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A PieceBoard is a visual component to represent the visual GameBoard displaying a single piece.
 */
public class PieceBoard extends GameBoard {

  /**
   * Create a new PieceBoard, based off a given grid, with a visual width and height.
   * @param grid linked grid
   * @param width the visual width
   * @param height the visual height
   */
  public PieceBoard(Grid grid, double width, double height) {
    super(3, 3, width, height);

    //Build the PieceBoard
    build();

    // Draw a circle in the middle block of the board
    getBlock(1, 1).paint();

    // Indicate that the board is a piece board
    mainBoard = false;
  }

  /**
   * Display the piece on the grid
   * @param gamePiece piece to display
   */
  public void displayPiece(GamePiece gamePiece) {
    // Get all blocks of the piece
    var blocks = gamePiece.getBlocks();
    // Draw each block on the grid
    for (int x = 0; x <= 2; x++) {
      for (int y = 0; y <= 2; y++) {
        grid.set(x, y, blocks[x][y]);
      }
    }
    // Place the piece on the grid
    grid.playPiece(gamePiece, 1, 1);
  }

}
