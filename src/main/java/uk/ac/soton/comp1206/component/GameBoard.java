package uk.ac.soton.comp1206.component;

import java.util.Set;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.HoverListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    /**
     * The logger of the class for printing information to console
     */
    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;

    private RightClickedListener rightClickedListener;

    private HoverListener hoverListener;

    private GameBlock currentBlock;
    private GamePiece currentPiece;

    protected boolean mainBoard = true;

    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}",cols,rows);

        // Set width and height of the board
        setMaxWidth(width);
        setMaxHeight(height);

        // Display grid lines
        setGridLinesVisible(true);

        //Create an array of blocks
        blocks = new GameBlock[cols][rows];

        // Store current piece
        currentPiece = GamePiece.createPiece(0);

        // Fill the board with blocks
        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x,y);
            }
        }

        // Store current block
        currentBlock = getBlock(cols / 2, rows / 2);
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block,x,y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e, block));

        // Add a mouse entered handler to the block to trigger pieceChanged method
        block.setOnMouseEntered(mouseEvent -> {
            if (mainBoard) {
                pieceChanged(currentPiece, block);
            }
        });

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);
        // Handle the right clicked event
        if ((height == 400 && event.getButton() == MouseButton.SECONDARY) ||
            (height == 150 && event.getButton() == MouseButton.PRIMARY) ||
            (height == 100 && event.getButton() == MouseButton.PRIMARY)){
            if (rightClickedListener != null) {
                rightClickedListener.rightClicked();
            }
        }
        // Handle the block clicked event
        else if(blockClickedListener != null) {
            blockClickedListener.blockClicked(block);
        }
    }

    /**
     * Set the listener to handle an event when right mouse is clicked
     * @param rightClickedListener listener to add
     */
    public void setRightClickedListener(RightClickedListener rightClickedListener) {
        this.rightClickedListener = rightClickedListener;
    }

    /*public void rightClicked() {
        if (rightClickedListener != null) {
            rightClickedListener.rightClicked();
        }
    }*/

    /**
     * Get the current block
     * @return current block
     */
    public GameBlock getCurrentBlock() {
        return currentBlock;
    }

    /*public void setCurrentBlock(GamePiece currentPiece, GameBlock currentBlock) {
        this.currentBlock = currentBlock;
        this.currentPiece = currentPiece;
    }*/

    /**
     * Set the listener to handle an event when block needs to hover
     * @param hoverListener listener to add
     */
    public void setHoverListener(HoverListener hoverListener) {
        this.hoverListener = hoverListener;
    }

    /**
     * Display a piece as hovering
     * @param gamePiece piece to hover
     * @param gameBlock block to hover
     * @param event mouse entered/exited event
     */
    public void hover(GamePiece gamePiece, GameBlock gameBlock, EventType<MouseEvent> event) {
        if (hoverListener != null) {
            // Hide previous hover
            drawHover(gamePiece, currentBlock, "hide");
            // Hide hover if mouse exited the block
            if (event == MouseEvent.MOUSE_EXITED) {
                drawHover(gamePiece, gameBlock, "hide");
            }
            // Show hover if mouse entered the block
            else if (event == MouseEvent.MOUSE_ENTERED){
                drawHover(gamePiece, gameBlock, "show");
            }
        }
    }

    /**
     * Actually draw the piece as hovering
     * @param gamePiece piece to hover
     * @param gameBlock block to hover
     * @param event mouse entered/exited event
     */
    private void drawHover(GamePiece gamePiece, GameBlock gameBlock, String event) {
        // Get all blocks of the piece
        int[][] gameBlocks = gamePiece.getBlocks();
        // Draw each block as hovering
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                if (gameBlocks[y][x] != 0) {
                    int posX = position(gameBlock.getX(), y);
                    int posY = position(gameBlock.getY(), x);
                    // If it is possible to hover, call the hover method of listener
                    if (posX < getRowCount() && posY < getColumnCount() && posX >= 0 && posY >= 0) {
                        hoverListener.hover(getBlock(posX, posY), event,
                            grid.canPlayPiece(gamePiece, gameBlock.getX(), gameBlock.getY()));
                    }
                }
            }
        }
    }

    /**
     * Calculate the x or y coordinate
     * @param posGrid piece to hover
     * @param posPiece block to hover
     * @return coordinate
     */
    private int position(int posGrid, int posPiece) {
        return posGrid + posPiece - 1;
    }

    /**
     * Place a piece
     * @param gameBlock block to place
     */
    public void dropPiece(GameBlock gameBlock) {
        logger.info("Block placed : {}" , gameBlock);
        if (blockClickedListener != null) {
            blockClickedListener.blockClicked(gameBlock);
        }
    }

    /**
     * Fade out specified blocks
     * @param blockCoordinates set of coordinates for blocks to fade out
     */
    public void fadeOut(Set<GameBlockCoordinate> blockCoordinates) {
        logger.info("Fading out blocks");
        // Call fade out method on each block in set
        for (GameBlockCoordinate blockCoordinate : blockCoordinates) {
            getBlock(blockCoordinate.getX(), blockCoordinate.getY()).fadeOut();
        }
    }

    /**
     * Handle hovering when any change is made to piece
     * @param gamePiece piece to hover
     * @param gameBlock block to hover
     */
    public void pieceChanged(GamePiece gamePiece, GameBlock gameBlock) {
        // Hide previous hover
        hover(currentPiece, currentBlock, MouseEvent.MOUSE_EXITED);
        currentBlock = gameBlock;
        currentPiece = gamePiece;
        // Show new hover
        hover(gamePiece, gameBlock, MouseEvent.MOUSE_ENTERED);
    }

}
