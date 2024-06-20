package uk.ac.soton.comp1206.ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Multimedia {

  private static MediaPlayer audio;
  private static MediaPlayer music;
  private static boolean playing = false;

  /**
   * The logger of the class for printing information to console
   */
  private static final Logger logger = LogManager.getLogger(Multimedia.class);

  /**
   * Play an audio
   * @param fileName name of the file to play
   */
  public static void playAudioFile(String fileName) {
    String file = Multimedia.class.getResource("/sounds/" + fileName).toExternalForm();
    try {
      // Play audio
      audio = new MediaPlayer(new Media(file));
      logger.info("Playing \"{}\" sound", fileName);
      audio.play();
    }
    catch (Exception e) {
      logger.error("Cannot load audio");
    }
  }

  /**
   * Play music
   * @param fileName name of the file to play
   */
  public static void playMusicFile(String fileName) {
    String file = Multimedia.class.getResource("/music/" + fileName).toExternalForm();
    try {
      // Stop previous music
      if (playing) {
        music.stop();
      }
      // Play new music
      music = new MediaPlayer(new Media(file));
      music.setOnEndOfMedia(() -> music.seek(Duration.ZERO));
      logger.info("Playing \"{}\" music", fileName);
      music.play();
      playing = true;
    }
    catch (Exception e) {
      logger.error("Cannot load music");
    }
  }

}
