package uk.ac.soton.comp1206.ui;

import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

/**
 * The ScoresList. Holds the UI for scores of the game.
 */
public class ScoresList extends VBox {

  // List property for storing users and their scores
  private SimpleListProperty<Pair<String, Integer>> pairs;

  // Username of the player
  protected String username;

  /**
   * Create a new ScoresList
   * @param type local/online/leader
   * @param username player username
   */
  public ScoresList(String type, String username) {
    pairs = new SimpleListProperty<>();
    setAlignment(Pos.CENTER);
    setSpacing(10);
    // Title
    var lblTitle = new Label(type + " Scores");
    lblTitle.getStyleClass().add("scorelist");
    this.getChildren().add(lblTitle);
    this.username = username;
  }

  /**
   * get the list property with users and their scores
   * @return list property
   */
  public SimpleListProperty<Pair<String, Integer>> pairsProperty() {
    return pairs;
  }

  /**
   * Display the scores
   */
  public void reveal() {
    // For each user display score
    for (Pair<String, Integer> pair : pairs) {
      String labelText = pair.getKey() + " = " + pair.getValue();
      var lblPair = new Label(labelText);
      // Change style if it's the player's score
      if (pair.getKey().equals(username)) {
        lblPair.getStyleClass().add("userscore");
      }
      else {
        lblPair.getStyleClass().add("scoreitem");
      }
      getChildren().add(lblPair);
    }
  }

}
