package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.*;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    /**
     * The gameBoard on which this block exists
     */
    private final GameBoard gameBoard;

    /**
     * The width of this block
     */
    private final double width;

    /**
     * The height of this block
     */
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Temporary value used to store the value that this block had before hovering
     */
    private int tempValue = 0;

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()], true);
        }
        //Draw a circle in the middle of the board if the board represents a piece
        if (x == 1 && y == 1 && gameBoard instanceof PieceBoard) {
            drawCircle();
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill
        //color = Color.rgb(102, 102, 102, 0.5);
        gc.setFill(Color.rgb(102, 102, 102, 0.5));
        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.strokeRect(0,0,width,height);

        //Store the value before hovering
        tempValue = getValue();
    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     * @param tile indicates whether to draw a triangle in this block which illustrates a tile
     */
    private void paintColor(Color colour, boolean tile) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        gc.setFill(colour);
        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);

        //Display the block as a tile if it is not hovering
        if (tile) {
            //Draw the brighter triangle inside block
            if (!colour.brighter().equals(colour)) {
                gc.setFill(colour.brighter());
                gc.fillPolygon(new double[]{0, 0, width}, new double[]{0, height, height}, 3);
            }
            //Draw the lighter triangle inside block
            else {
                gc.setFill(colour.darker());
                gc.fillPolygon(new double[]{0, width, width}, new double[]{0, 0, height}, 3);
            }
        }

        //Store the value before hovering
        tempValue = getValue();
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bindBidirectional((Property<Number>) input);
    }

    /**
     * Draw a circle inside this block
     */
    public void drawCircle() {
        var gc = getGraphicsContext2D();
        gc.setFill(Color.rgb(204, 204, 204, 0.5));
        gc.fillOval(width/4, height/4, width/2, height/2);
    }

    /**
     * Display this block as hovering
     * @param green indicates whether to draw ths block green (means can place) or red (cannot place)
     */
    public void hover(boolean green) {
        //Store the value before hovering
        tempValue = getValue();
        // Display the block as green if it can be placed
        if (green) {
            paintColor(Color.rgb(0, 204, 0, 0.5), false);
        }
        // Display the block as red if it cannot be placed
        else {
            paintColor(Color.rgb(255, 0, 0, 0.5), false);
        }
    }

    /**
     * Stop this block from hovering
     */
    public void quitHover() {
        value.setValue(tempValue);
        paint();
    }

    /**
     * Display this block as fading out when a line is completed
     */
    public void fadeOut() {
        //Paint the block as empty at first
        paintEmpty();
        paintColor(Color.WHITE, false);
        //Create animation
        AnimationTimer animationTimer = new AnimationTimer() {
            /**
             * Initial opacity
             */
            private double opacity = 1;
            @Override
            public void handle(long l) {
                //Paint the block with less opacity until it gets to 0
                if (opacity > 0) {
                    paintColor(Color.rgb(255, 255,255, opacity), false);
                }
                // When opacity is 0, stop the transition
                else {
                    paintEmpty();
                    this.stop();
                }
                //Decrease opacity until it gets to 0
                opacity -= 0.1;
            }
        };
        //Start the animation
        animationTimer.start();
    }

}
