package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Hover listener is used to handle the event when a mouse of the user enters the grid. It passes the
 * GameBlock to hover
 */
public interface HoverListener {

  /**
   * Handle a hover event
   * @param gameBlock the block to hover
   * @param event indicates whether to hover a block or to stop hovering it
   * @param green indicates whether to display a block in green or red color
   */
  public void hover(GameBlock gameBlock, String event, boolean green);

}
