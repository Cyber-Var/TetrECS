package uk.ac.soton.comp1206.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.util.Pair;

/**
 * The Leader board. Holds the UI for the scores of multiplayer game.
 */
public class Leaderboard extends ScoresList {

  // List for storing users and their scores
  private List<Pair<String, Integer>> leaderPairs = new ArrayList<>();

  /**
   * Create a new ScoresList
   * @param username player username
   * @param pairs list of users and their scores
   */
  public Leaderboard(String username, List<Pair<String, Integer>> pairs) {
    super("Leader", username);
    leaderPairs.addAll(pairs);
  }

  /**
   * Display the scores
   */
  @Override
  public void reveal() {
    // For each user display scopre
    for (Pair<String, Integer> leaderPair : leaderPairs) {
      String labelText = leaderPair.getKey() + " = " + leaderPair.getValue();
      var lblPair = new Label(labelText);
      // Change style if it's the player's score
      if (leaderPair.getKey().equals(username)) {
        lblPair.getStyleClass().add("userscore");
      }
      else {
        lblPair.getStyleClass().add("scoreitem");
      }
      getChildren().add(lblPair);
    }
  }

}
