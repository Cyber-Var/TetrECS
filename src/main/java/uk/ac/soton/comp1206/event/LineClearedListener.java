package uk.ac.soton.comp1206.event;

import java.util.Set;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * The Line Cleared listener is used to handle the event when the player completes a full line of
 * blocks on the grid. It passes the set of GameBlockCoordinates that correspond to the blocks to clear
 */
public interface LineClearedListener {

  /**
   * Handle a line cleared event
   * @param blockCoordinates the coordinates of blocks to clear
   */
  public void fadeOut(Set<GameBlockCoordinate> blockCoordinates);

}
