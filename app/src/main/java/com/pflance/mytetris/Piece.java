package com.pflance.mytetris;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class Piece extends RelativeLayout {

    final int DEFAULT_NUMBER = 2;
    final int START_X_POSITION = 4;
    final int START_Y_POSITION = -1; // Above the board
    final int BASE_Y_DP_POSITION = 418;
    final int BOARD_X_LEFT_BOUNDS = 0;
    final int BOARD_X_RIGHT_BOUNDS = 10;
    final int BOARD_Y_BOTTOM_BOUNDS = 19;
    final int BOARD_Y_UPPER_BOUNDS = 0;
    final int PIECE_WIDTH_HEIGHT = 22; // in dp

    private int color;                   // The color of the piece
    private boolean inUse;               // If the piece is inUse, it cannot be made the next piece
    private LayoutParams params;         // The params of a piece
    private int x;                       // X position on grid relative to bottom left corner of layout
    private int y;                       // Y position on grid relative to bottom left corner of layout
    private ViewFlipper layout_flipper;  // The layouts for each rotated piece are held in a flipper
    private int number;                  // 0-6. Determines type of piece
    private int width;                   // Piece's width in blocks (22dp x 22dp)
    private int height;                  // Piece's height in blocks (22dp x 22dp)
    private int states;                  // Number of possible rotated states of the Piece
    private int state;                   // The rotated state of the Piece
    private int flipper_id;              // Holds the ViewFlipper that holds the rotations
    private int layout_id;               // The layout id for the piece
    private boolean[][] grid;            // Holds true or false for occupied or vacant, respectively
    private Board the_board;             // The Piece will be an Observer of the Board

    // Default Constructors
    public Piece(Context context){
        super(context);
        number = DEFAULT_NUMBER;
        inUse = false;
        state = 5;
        the_board = null;

        // Set width, height, # of states, and layouts for the piece
        color = R.color.blue;
        width = 4;
        height = 4;
        states = 4;
        flipper_id = R.id.piece_2_flipper;
        layout_id = R.layout.piece_2;

        // Init the grid with width and height values
        grid = new boolean[width][height];
        for (int i=0; i<width; ++i){
            for (int j=0; j<height; ++j){
                grid[i][j] = false;
            }
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(layout_id, this);

        layout_flipper = (ViewFlipper)findViewById(flipper_id);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.bottom_gridbar);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.left_gridbar);
        setId(R.id.the_piece);
        x = START_X_POSITION;
        y = START_Y_POSITION;

        // LayoutParams are set in Rotate
        // State == 5, which is larger than any kind of state. This will auto set every piece to state 0
        Rotate(1, false);
    }
    public Piece(Context context, AttributeSet attrs){
        super(context, attrs);
        number = DEFAULT_NUMBER;
        inUse = false;
        state = 5;
        the_board = null;

        // Set width, height, # of states, and layouts for the piece
        color = R.color.blue;
        width = 4;
        height = 4;
        states = 4;
        flipper_id = R.id.piece_2_flipper;
        layout_id = R.layout.piece_2;

        // Init the grid with width and height values
        grid = new boolean[width][height];
        for (int i=0; i<width; ++i){
            for (int j=0; j<height; ++j){
                grid[i][j] = false;
            }
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(layout_id, this);

        layout_flipper = (ViewFlipper)findViewById(flipper_id);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.bottom_gridbar);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.left_gridbar);
        setId(R.id.the_piece);
        x = START_X_POSITION;
        y = START_Y_POSITION;

        // LayoutParams are set in Rotate
        // State == 5, which is larger than any kind of state. This will auto set every piece to state 0
        Rotate(1, false);
    }
    public Piece(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        number = DEFAULT_NUMBER;
        inUse = false;
        state = 5;
        the_board = null;

        // Set width, height, # of states, and layouts for the piece
        color = R.color.blue;
        width = 4;
        height = 4;
        states = 4;
        flipper_id = R.id.piece_2_flipper;
        layout_id = R.layout.piece_2;

        // Init the grid with width and height values
        grid = new boolean[width][height];
        for (int i=0; i<width; ++i){
            for (int j=0; j<height; ++j){
                grid[i][j] = false;
            }
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(layout_id, this);

        layout_flipper = (ViewFlipper)findViewById(flipper_id);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.bottom_gridbar);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.left_gridbar);
        setId(R.id.the_piece);
        x = START_X_POSITION;
        y = START_Y_POSITION;

        // LayoutParams are set in Rotate
        // State == 5, which is larger than any kind of state. This will auto set every piece to state 0
        Rotate(1, false);
    }

    /**
     * A custom constructor that sets a piece to the number specified
     *
     * @param context the context the piece needs (always main layout)
     * @param num the type of piece being created
     */
    public Piece(Context context, int num, Board main_board){
        super(context);
        number = num;
        inUse = false;
        state = 5;
        the_board = main_board;

        // Set width, height, # of states, and layouts for the piece
        switch(number) {
            case 0:
                color = R.color.orange;
                width = 3;
                height = 3;
                states = 4;
                flipper_id = R.id.piece_0_flipper;
                layout_id = R.layout.piece_0;
                break;
            case 1:
                color = R.color.green;
                width = 3;
                height = 3;
                states = 4;
                flipper_id = R.id.piece_1_flipper;
                layout_id = R.layout.piece_1;
                break;
            case 2:
                color = R.color.blue;
                width = 4;
                height = 4;
                states = 4;
                flipper_id = R.id.piece_2_flipper;
                layout_id = R.layout.piece_2;
                break;
            case 3:
                color = R.color.yellow;
                width = 2;
                height = 2;
                states = 1;
                flipper_id = R.id.piece_3_flipper;
                layout_id = R.layout.piece_3;
                break;
            case 4:
                color = R.color.pink;
                width = 3;
                height = 3;
                states = 4;
                flipper_id = R.id.piece_4_flipper;
                layout_id = R.layout.piece_4;
                break;
            case 5:
                color = R.color.red;
                width = 3;
                height = 3;
                states = 4;
                flipper_id = R.id.piece_5_flipper;
                layout_id = R.layout.piece_5;
                break;
            case 6:
                color = R.color.purple;
                width = 3;
                height = 3;
                states = 4;
                flipper_id = R.id.piece_6_flipper;
                layout_id = R.layout.piece_6;
                break;
        }

        // Init the grid with width and height values
        grid = new boolean[width][height]; // The largest width and height any piece can be. Keep?
        for (int i=0; i<width; ++i){
            for (int j=0; j<height; ++j){
                grid[i][j] = false;
            }
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(layout_id, this);

        layout_flipper = (ViewFlipper)findViewById(flipper_id);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.bottom_gridbar);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.left_gridbar);
        setId(R.id.the_piece);
        x = START_X_POSITION;
        y = START_Y_POSITION;

        // LayoutParams are set in Rotate
        // State == 5, which is larger than any kind of state. This will auto set every piece to state 0
        Rotate(1, false);
    }

    /**
     * A method to rotate a piece on the board
     *
     * @param direction 1 flips piece clockwise, -1 flips piece counter-clockwise
     * @param revert used as a check to reverse a rotation if true.
     */
    public void Rotate(int direction, boolean revert){

        // Update state of the piece
        if (revert) {
            state -= direction;
        } else {
            state += direction;
        }

        // Wrap the state from 0 to (states-1)
        if (state < 0) state = (states-1);
        else if (state >= states) state = 0;

        // Set boolean grid for new rotated piece
        setPieceGrid();

        // Just want to revert the flip, we don't need any more checking if revert is true
        if (revert) return;

        // These variables are used as holders for a potential shifted value if Rotate is not reverted
        int shift_x = 0;
        int shift_y = 0;

        // Check to see if the rotated piece goes out of horizontal bounds of the board
        if (x < BOARD_X_LEFT_BOUNDS) {

            // x is shifted past the left boundary of 0. Check Math.abs(x) blocks along width.
            int left_width_bounds = Math.abs(x);

            for (int i=0; i<left_width_bounds; ++i) {
                for (int j=0; j<height; ++j) {

                    // Search the out of bounds portion of grid on the left side for any filled block
                    if (grid[i][j]) {

                        // The piece must be shifted right
                        shift_x = left_width_bounds - i;
                        i = left_width_bounds; // will kick out of second loop
                        break;

                    }
                }
            }
        } else if ((x + width) > BOARD_X_RIGHT_BOUNDS) {

            // x is shifted past the right boundary of 9; > 10
            int right_width_bounds = x + width - BOARD_X_RIGHT_BOUNDS;

            for (int i= (width - 1); i >= (width - right_width_bounds); --i) {
                for (int j=0; j<height; ++j) {

                    // Search the out of bounds portion of grid on the right side for any filled block
                    if (grid[i][j]) {

                        // The piece must be shifted left
                        shift_x = -right_width_bounds;
                        i = 0; // will kick out of second loop
                        break;

                    }
                }
            }

        }

        // Check to see if the rotate piece goes out of the vertical bounds of the board
        if (y > BOARD_Y_BOTTOM_BOUNDS) {

            // y is shifted past the bottom boundary of 19
            int bottom_height_bounds = y - BOARD_Y_BOTTOM_BOUNDS;

            for (int i= (height - 1); i >= bottom_height_bounds; --i) {
                for (int j=0; j<width; ++j) {

                    // Search the out of bounds portion of the right from the bottom up for any filled block
                    if (grid[j][i]) {

                        // The piece must be shifted up
                        shift_y = i - (height - bottom_height_bounds);
                        i = 0; // will kick out of second loop
                        break;

                    }
                }
            }
        }

        // Check to see if the soon to be rotated piece is overlapping any block on the board
        for (int i=0; i<width; ++i){
            for (int j=0; j<height; ++j){

                // For every filled piece block, check to see if that spot relative to the board's grid is filled
                if (grid[i][j]){

                    // temps hold the shifted x/y values
                    int temp_x = x + shift_x;
                    int temp_y = y + shift_y;

                    // the x and y position on the board
                    int relative_x = temp_x + i;
                    int relative_y = temp_y - height + j + 1;

                    // Check to see if the relative positions of the piece on the board are in bounds of the board
                    if ((relative_x < BOARD_X_RIGHT_BOUNDS) && (relative_x >= BOARD_X_LEFT_BOUNDS) && (relative_y < BOARD_Y_BOTTOM_BOUNDS) && (relative_y >= BOARD_Y_UPPER_BOUNDS)) {

                        // Check to see if the board is filled on the checked block
                        if (the_board.getGrid(relative_x, relative_y)) {

                            // Revert set to true to reverse the grid of rotated piece
                            Rotate(direction, true);
                            return;
                            // Skip the setting of the piece's layout and updated x/y values

                        }
                    }
                }
            }
        }

        // Making it here means rotation is valid. Update x and y values
        x += shift_x;
        y += shift_y;

        // Set layout params and flipper value of rotated piece
        updatePosition();
        layout_flipper.setDisplayedChild(state);
    }

    /**
     * A method to shift a piece left and right on the board
     *
     * @param direction 1 shifts piece right, -1 shifts piece left
     */
    public void shift(int direction) {

        // Gets which horizontal direction the piece is being shifted
        if (direction == -1) {
            // Shifting left

            // Going down the piece and searching each row until a filled block is found
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {

                    // Check if the piece's grid[j][i] is filled
                    if (grid[j][i]) {

                        // The y position of piece on board and the x position just left of the piece on the board
                        int relative_leftmost_x = x + j + direction;
                        int relative_y = y - height + i + 1;

                        // Stops the piece if the next left spot on the board is out of bounds OR
                        // Stops the piece if the piece is in bounds for y and there is a block on the board in the way
                        if (relative_leftmost_x < BOARD_X_LEFT_BOUNDS || (relative_y >= BOARD_Y_UPPER_BOUNDS && the_board.getGrid(relative_leftmost_x, relative_y))) return;

                        // Stop the trace once we find the first block on piece filled, since we trace from left to right, we don't need to check anymore after
                        break;

                    }

                }
            }

            // Passed the shift check, move piece left one block
            x += direction;

        } else {
            // Shifting right

            // Going down the piece and searching each row until a filled block is found
            for (int i = 0; i < height; ++i) {
                for (int j = (width - 1); j >= 0; --j) {

                    // Check if the piece's grid[j][i] is filled
                    if (grid[j][i]) {

                        // The y position of piece on board and the x position just left of the piece on the board
                        int relative_rightmost_x = x + j + direction;
                        int relative_y = y - height + i + 1;

                        // Stops the piece if the next right spot on the board is out of bounds OR
                        // Stops the piece if the piece is in bounds for y and there is a block on the board in the way
                        if (relative_rightmost_x >= BOARD_X_RIGHT_BOUNDS || (relative_y >= BOARD_Y_UPPER_BOUNDS && the_board.getGrid(relative_rightmost_x, relative_y))) return;

                        // Stop the trace once we find the first block on piece filled, since we trace from right to left, we don't need to check anymore after
                        break;

                    }
                }
            }

            // Passed the shift check, move piece left one block
            x += direction;

        }
    }

    /**
     * A method that determines if the piece can be dropped or not. If it can, we update the piece's
     * params and return true. Else, we have to place the piece and return false.
     *
     * @return Returns whether or not the piece can be placed or not.
     */
    public boolean drop() {

        // Going up the piece and searching each row until a filled block is found
        for (int i = 0; i < width; ++i) {
            for (int j = (height - 1); j >= 0; --j) {

                // Check is the piece's grid[i][j] is filled
                if (grid[i][j]) {

                    // The x position of piece on the board and y position just under the piece on the board
                    int relative_x = x + i;
                    int relative_lowermost_y = y - height + j + 2;

                    // Does not drop the piece if the spot beneath the piece relative to the board is out of bounds OR
                    // Does not drop the piece if the piece is in bounds for y and there is a block on the board in the way
                    if ((relative_lowermost_y > BOARD_Y_BOTTOM_BOUNDS) || (relative_lowermost_y >= 0 && the_board.getGrid(relative_x, relative_lowermost_y))) return false;

                    // Stop the trace once we find the first block on piece filled, since we trace from bottom to top, we don't need to check anymore after
                    break;

                }
            }
        }

        // Passed drop check, move piece down one and update params
        ++y;
        updatePosition();
        return true;
    }

    /**
     * A method to update the position of the piece on the board
     */
    public void updatePosition() {
        params.setMargins(MyTetrisMain.dpTopx(PIECE_WIDTH_HEIGHT * x), 0, 0, MyTetrisMain.dpTopx(BASE_Y_DP_POSITION - (y * PIECE_WIDTH_HEIGHT)));
        setLayoutParams(params);
    }

    /**
     * A method that resets the position of the piece back to the top of the board
     */
    public void resetPosition() {
        x = 4;
        y = -1;
        state = 5;
        Rotate(1, false);
        updatePosition();
    }

    /**
     * A method that is called within Rotate only that sets the newly rotated piece's boolean grid
     */
    private void setPieceGrid() {

        // Set the piece's entire grid to false
        for (int i=0; i<width; ++i){
            for (int j=0; j<height; ++j){
                grid[i][j] = false;
            }
        }

        // Find the piece's number and state and set the grid
        switch(number){
            case 0:
                if (state == 0) {
                    grid[0][1] = true;
                    grid[1][1] = true;
                    grid[1][2] = true;
                    grid[2][2] = true;
                } else if (state == 1) {
                    grid[0][1] = true;
                    grid[0][2] = true;
                    grid[1][0] = true;
                    grid[1][1] = true;
                } else if (state == 2) {
                    grid[0][0] = true;
                    grid[1][0] = true;
                    grid[1][1] = true;
                    grid[2][1] = true;
                } else if (state == 3) {
                    grid[2][0] = true;
                    grid[1][1] = true;
                    grid[2][1] = true;
                    grid[1][2] = true;
                }
                break;
            case 1:
                if (state == 0) {
                    grid[1][1] = true;
                    grid[2][1] = true;
                    grid[0][2] = true;
                    grid[1][2] = true;
                } else if (state == 1) {
                    grid[0][0] = true;
                    grid[0][1] = true;
                    grid[1][1] = true;
                    grid[1][2] = true;
                } else if (state == 2) {
                    grid[1][0] = true;
                    grid[2][0] = true;
                    grid[0][1] = true;
                    grid[1][1] = true;
                } else if (state == 3) {
                    grid[1][0] = true;
                    grid[1][1] = true;
                    grid[2][1] = true;
                    grid[2][2] = true;
                }
                break;
            case 2:
                if (state == 0) {
                    grid[1][0] = true;
                    grid[1][1] = true;
                    grid[1][2] = true;
                    grid[1][3] = true;
                } else if (state == 1) {
                    grid[0][1] = true;
                    grid[1][1] = true;
                    grid[2][1] = true;
                    grid[3][1] = true;
                } else if (state == 2) {
                    grid[2][0] = true;
                    grid[2][1] = true;
                    grid[2][2] = true;
                    grid[2][3] = true;
                } else if (state == 3) {
                    grid[0][2] = true;
                    grid[1][2] = true;
                    grid[2][2] = true;
                    grid[3][2] = true;
                }
                break;
            case 3:
                grid[0][0] = true;
                grid[0][1] = true;
                grid[1][0] = true;
                grid[1][1] = true;
                break;
            case 4:
                if (state == 0) {
                    grid[1][1] = true;
                    grid[0][2] = true;
                    grid[1][2] = true;
                    grid[2][2] = true;
                } else if (state == 1) {
                    grid[0][0] = true;
                    grid[0][1] = true;
                    grid[1][1] = true;
                    grid[0][2] = true;
                } else if (state == 2) {
                    grid[0][0] = true;
                    grid[1][0] = true;
                    grid[1][1] = true;
                    grid[2][0] = true;
                } else if (state == 3) {
                    grid[1][1] = true;
                    grid[2][0] = true;
                    grid[2][1] = true;
                    grid[2][2] = true;
                }
                break;
            case 5:
                if (state == 0) {
                    grid[1][0] = true;
                    grid[1][1] = true;
                    grid[1][2] = true;
                    grid[2][2] = true;
                } else if (state == 1) {
                    grid[0][1] = true;
                    grid[0][2] = true;
                    grid[1][1] = true;
                    grid[2][1] = true;
                } else if (state == 2) {
                    grid[0][0] = true;
                    grid[1][0] = true;
                    grid[1][1] = true;
                    grid[1][2] = true;
                } else if (state == 3) {
                    grid[0][1] = true;
                    grid[1][1] = true;
                    grid[2][1] = true;
                    grid[2][0] = true;
                }
                break;
            case 6:
                if (state == 0) {
                    grid[0][2] = true;
                    grid[1][0] = true;
                    grid[1][1] = true;
                    grid[1][2] = true;
                } else if (state == 1) {
                    grid[0][0] = true;
                    grid[0][1] = true;
                    grid[1][1] = true;
                    grid[2][1] = true;
                } else if (state == 2) {
                    grid[1][0] = true;
                    grid[1][1] = true;
                    grid[1][2] = true;
                    grid[2][0] = true;
                } else if (state == 3) {
                    grid[0][1] = true;
                    grid[1][1] = true;
                    grid[2][1] = true;
                    grid[2][2] = true;
                }
                break;
        }
    }


    /**
     * Getter methods that return the private fields of the piece
     */
    public int getLayoutId() { return layout_id; }
    public int getColor() { return color; }
    public int getPieceX() { return x; }
    public int getPieceY() { return y; }
    public int getPieceWidth() { return width; }
    public int getPieceHeight() { return height; }
    public int getNumber() { return number; }
    public boolean getInUse() { return inUse; }
    public boolean getGridAt(int x, int y) { return grid[x][y]; }

    /**
     * Setter methods that set the private fields of the piece
     */
    public void setInUse(boolean inUse) { this.inUse = inUse; }

}

