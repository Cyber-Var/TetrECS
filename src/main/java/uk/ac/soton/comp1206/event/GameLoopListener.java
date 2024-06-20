package uk.ac.soton.comp1206.event;

/**
 * The Game Loop listener is used to handle the event when the timer of the game goes out. It passes the
 * amount of milliseconds for a round
 */
public interface GameLoopListener {

  /**
   * Handle a game loop event
   * @param milliseconds the milliseconds to delay
   */
  public void gameLoop(int milliseconds);

}
