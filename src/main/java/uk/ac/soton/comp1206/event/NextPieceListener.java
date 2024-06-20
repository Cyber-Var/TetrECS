package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next Piece listener is used to handle the event when a piece has been played and a new piece needs to
 * be displayed. It passes the GamePiece that has just been played and the next GamePiece to play
 */
public interface NextPieceListener {

  /**
   * Handle a next piece event
   * @param currentPiece the piece currently being played
   * @param followingPiece the piece that will be played after the current piece
   */
  public void nextPiece(GamePiece currentPiece, GamePiece followingPiece);

}
