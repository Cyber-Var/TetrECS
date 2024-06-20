package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player are taking place inside this class.
 */
public class Game {

    /**
     * The logger of the class for printing information to console
     */
    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * The piece currently being played
     */
    protected GamePiece currentPiece;

    /**
     * The piece that will be played after the current piece
     */
    protected GamePiece followingPiece;

    /**
     * The listener to call when a piece is played
     */
    protected NextPieceListener nextPieceListener;

    /**
     * The listener to call when a line is cleared
     */
    private LineClearedListener lineClearedListener;

    /**
     * The listener to call when timer is up
     */
    protected GameLoopListener gameLoopListener;

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * The property to store the score
     */
    private IntegerProperty score;

    /**
     * The property to store the level
     */
    private IntegerProperty level;

    /**
     * The property to store the lives
     */
    private IntegerProperty lives;

    /**
     * The property to store the multipler
     */
    private IntegerProperty multiplier;

    /**
     * The timer of the game
     */
    private Timer loopTimer;


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);

        // Set the initial current piece
        currentPiece = spawnPiece();
        // Set the initial following piece
        followingPiece = spawnPiece();

        // Set initial property values
        score = new SimpleIntegerProperty(0);
        level = new SimpleIntegerProperty(0);
        lives = new SimpleIntegerProperty(3);
        multiplier = new SimpleIntegerProperty(1);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        // Start the timer of the game
        gameLoop();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        Multimedia.playMusicFile("game_start.wav");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        // Place the current piece if it can be played
        if (grid.canPlayPiece(currentPiece, x, y)) {

            // Place current piece and handle logic that has to be done after playing it
            grid.playPiece(currentPiece, x, y);
            afterPiece();
            nextPiece();

            // Start a new loop in timer
            if (loopTimer != null) {
                loopTimer.cancel();
            }
            gameLoopListener.gameLoop(getTimerDelay());
            loopTimer = new Timer();
            loopTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    gameLoop();
                }
            }, getTimerDelay());
            Multimedia.playAudioFile("place.wav");
        }
        else {
            Multimedia.playAudioFile("fail.wav");
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Spawn a random piece to play
     * @return piece
     */
    public GamePiece spawnPiece() {
        return GamePiece.createPiece(new Random().nextInt(15));
    }

    /**
     * Set the next piece to play
     */
    public void nextPiece() {
        logger.info("Loading next piece");
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }

    /**
     * Perform actions after a piece has been played
     */
    public void afterPiece() {
        // Create lists and set to store cleared lines and blocks
        List<Integer> rowsToClear = new ArrayList<>();
        List<Integer> columnsToClear = new ArrayList<>();
        Set<Integer> toClear = new HashSet<>();

        // Identify rows that have to be cleared
        for (int y = 0; y < rows; y++) {
            boolean full = true;
            for (int x = 0; x < cols; x++) {
                if (grid.get(x, y) == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int k = 0; k < cols; k++) {
                    toClear.add(5 * y + k);
                }
                rowsToClear.add(y);
            }
        }

        // Identify columns that have to be cleared
        for (int x = 0; x < cols; x++) {
            boolean full = true;
            for (int y = 0; y < rows; y++) {
                if (grid.get(x, y) == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int k = 0; k < rows; k++) {
                    toClear.add(cols * k + x);
                }
                columnsToClear.add(x);
            }
        }

        // Clear and fade out blocks that have to be cleared
        Set<GameBlockCoordinate> blockCoordinates = new HashSet<>();
        for (int position : toClear) {
            int x = position % cols;
            int y = position / cols;
            grid.set(x, y, 0);
            blockCoordinates.add(new GameBlockCoordinate(x, y));
        }
        lineClearedListener.fadeOut(blockCoordinates);

        // Set the new score and level
        score(rowsToClear.size() + columnsToClear.size(), toClear.size());
        int newLevel = score.get() / 1000;
        if (newLevel > level.get()) {
            level.set(newLevel);
        }
    }

    /**
     * Get the score property
     * @return score property
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Get the level property
     * @return level property
     */
    public IntegerProperty levelProperty() {
        return level;
    }

    /**
     * Get the lives property
     * @return lives property
     */
    public IntegerProperty livesProperty() {
        return lives;
    }

    /**
     * Get the multiplayer property
     * @return multiplayer property
     */
    public IntegerProperty multiplierProperty() {
        return multiplier;
    }

    /**
     * Calculate and set the score
     * @param lines number of lines cleared
     * @param blocks number of blocks cleared
     */
    public void score(int lines, int blocks) {
        score.set(score.get() + lines * blocks * 10 * multiplier.get());
        // Set multiplier to 1 if no lines have been cleared
        if (lines == 0) {
            multiplier.set(1);
        }
        // Increment multiplier if at least 1 line has been cleared
        else {
            multiplier.set(multiplier.get() + 1);
        }
    }

    /**
     * Rotate the current piece being played
     * @param left indicates whether to rotate the piece left or right
     */
    public void rotateCurrentPiece(boolean left) {
        logger.info("Rotating current piece");
        // Rotate current piece
        currentPiece.rotate();
        // Rotate current piece twice more if it is a left rotation
        if (left) {
            currentPiece.rotate();
            currentPiece.rotate();
        }
        Multimedia.playAudioFile("rotate.wav");

    }

    /**
     * Set the listener to handle an event when a piece is played
     * @param nextPieceListener listener to add
     */
    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
        this.nextPieceListener.nextPiece(currentPiece, followingPiece);
    }

    /**
     * Swap the current piece with the following piece
     */
    public void swapCurrentPiece() {
        logger.info("Swapping current piece");
        // Call the listener nextPiece method
        nextPieceListener.nextPiece(followingPiece, currentPiece);
        // Swap values of current and following piece
        GamePiece tempPiece = currentPiece;
        currentPiece = followingPiece;
        followingPiece = tempPiece;
        Multimedia.playAudioFile("transition.wav");
    }

    /**
     * Get the current piece being played
     * @return current piece
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Get the piece that is following after the current piece
     * @return follownig piece
     */
    public GamePiece getFollowingPiece() {
        return followingPiece;
    }

    /**
     * Set the listener to handle an event when a line is cleared
     * @param lineClearedListener listener to add
     */
    public void setLineClearedListener(LineClearedListener lineClearedListener) {
        this.lineClearedListener = lineClearedListener;
    }

    /**
     * Hover a block over the board
     * @param gameBlock the gameblock to hover
     * @param event indicates whether to hover a block or stop hovering it
     * @param green indicates whether to color the block to green or red
     */
    public void hover(GameBlock gameBlock, String event, boolean green) {
        // Show hover
        if (event.equals("show")) {
            gameBlock.hover(green);
        }
        // Quit hover
        else {
            gameBlock.quitHover();
        }
    }

    /**
     * Calculate delay for the timer of the game
     * @return delay
     */
    public int getTimerDelay() {
        // Decrease delay depending on the level
        int delay = 12000 - 500 * level.get();
        // Cannot be decreased lower than 2500
        if (delay <= 2500) {
            return 2500;
        }
        return delay;
    }

    /**
     * Make the player lose a life every time a certain time has passed
     */
    public void gameLoop() {
        // Handle if timer delay goes out
        if (loopTimer != null) {
            loopTimer.cancel();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    // Decrement lives
                    lives.set(lives.get() - 1);
                    // Set multiplier to 1
                    multiplier.set(1);
                }
            });
            nextPiece();
        }
        // Continue looping
        gameLoopListener.gameLoop(getTimerDelay());
        loopTimer = new Timer();
        loopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    gameLoop();
                }
                catch (Exception e) {
                    logger.error("timer already cancelled");
                }
            }
        }, getTimerDelay());
    }

    /**
     * Set the listener to handle an event when the game timer goes out
     * @param gameLoopListener listener to add
     */
    public void setGameLoopListener(GameLoopListener gameLoopListener) {
        this.gameLoopListener = gameLoopListener;
    }

    /*public void bindMultiplier(ObservableValue<? extends Number> input) {
        multiplier.bindBidirectional((Property<Number>) input);
    }*/

    /**
     * Stop the timer of the game
     */
    public void cancelLoopTimer() {
        loopTimer.cancel();
    }
}
