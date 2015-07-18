package com.pflance.mytetris;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class Piece extends RelativeLayout {
    //private String color; // color-code each number (include later, single color for now)

    public int color;
    public boolean inUse = false;
    public LayoutParams params;
    public int x; // X position on grid relative to bottom left corner of layout
    public int y; // Y position on grid relative to bottom left corner of layout
    private ViewFlipper layout_flipper;
    public int number; // Could be 0-6, see spec
    public int width; // Pieces's width in blocks (22dp x 22dp)
    public int height; // Pieces's height in blocks (22dp x 22dp)
    private int states; // Number of possible rotated states of the Piece
    public int state; // The rotated state of the Piece
    private int flipper_id; // Holds the ViewFlipper that holds the roations
    private int layout_id; // The layout id for the piece
    public boolean[][] grid; // holds true or false for occupied or vacant, respectively

    // Default Constructors
    public Piece(Context context){
        super(context);
        number = 2;
        state = 5;

        color = R.color.blue;
        width = 4;
        height = 4;
        states = 4;
        flipper_id = R.id.piece_2_flipper;
        layout_id = R.layout.piece_2;


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
        layout_flipper.setDisplayedChild(0);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.bottom_gridbar);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.left_gridbar);
        x = 4;
        y = -1; // above the board

        setLayoutParams(params);

        // State == 5, which is larger than any kind of state. This will auto set every piece to state 0
        Rotate(1, false);
    }
    public Piece(Context context, AttributeSet attrs){
        super(context, attrs);
        number = 2;
        state = 5;

        color = R.color.blue;
        width = 4;
        height = 4;
        states = 4;
        flipper_id = R.id.piece_2_flipper;
        layout_id = R.layout.piece_2;

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
        layout_flipper.setDisplayedChild(0);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.bottom_gridbar);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.left_gridbar);
        x = 4;
        y = -1; // above the board

        setLayoutParams(params);

        // State == 5, which is larger than any kind of state. This will auto set every piece to state 0
        Rotate(1, false);
    }
    public Piece(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        number = 2;
        state = 5;

        color = R.color.blue;
        width = 4;
        height = 4;
        states = 4;
        flipper_id = R.id.piece_2_flipper;
        layout_id = R.layout.piece_2;

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
        layout_flipper.setDisplayedChild(0);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.bottom_gridbar);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.left_gridbar);
        x = 4;
        y = -1; // above the board

        setLayoutParams(params);

        // State == 5, which is larger than any kind of state. This will auto set every piece to state 0
        Rotate(1, false);
    }
    // Custom Constructor
    public Piece(Context context, int num){
        super(context);
        number = num;
        state = 5;

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
        x = 4;
        y = -1; // above the board

        // State == 5, which is larger than any kind of state. This will auto set every piece to state 0
        Rotate(1, false);
    }

    public void Rotate(int direction, boolean revert){
        // direction is 1 for clockwise and -1 for counter-clockwise
        if (revert) {
            state -= direction;
        } else {
            state += direction;
        }
        if (state < 0) state = (states-1);
        else if (state >= states) state = 0;

        for (int i=0; i<width; ++i){
            for (int j=0; j<height; ++j){
                grid[i][j] = false;
            }
        }

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

        //Log.d("ROTATE", "Rotated piece# " + number + " to state# " + state + ".");

        if (revert) return;

        boolean shifted_x = false;
        boolean shifted_y = false;
        int shift_x = 0; // defaults so we avoid a null exception
        int shift_y = 0;

        // BEFORE WE FLIP TO THE NEXT STATE, we must check to fit the piece on the board properly
        if (x < 0) {
            int k = x * -1;
            // We should check left boundaries and shift the piece right so that it fits on the board correctly
            for (int i=0; i<k; ++i) {
                for (int j=0; j<height; ++j) {
                    if (grid[i][j]) {
                        shifted_x = true;
                        shift_x = k - i;
                        i = k; // will kick out of second loop
                        break;
                    }
                }
            }
        } else if ((x + width - 1) > 9) {
            int k = (x + width - 1) - 9; // How many blocks width-wise to check, rightmost
            // We should check right boundaries and shift the piece right so that it fits on the board correctly
            for (int i= (width - 1); i >= (width - k); --i) {
                for (int j=0; j<height; ++j) {
                    if (grid[i][j]) {
                        shifted_x = true;
                        shift_x = -k;
                        i = 0; // will kick out of second loop
                        break;
                    }
                }
            }

        }

        // ALSO CHECK FOR BOTTOM BAR BOUNDARY
        if (y > 19) {
            int k = y - 19;
            // We should check bottom boundaries and shift the piece up so that it fits on the board correctly
            for (int i= (height - 1); i >= k; --i) {
                for (int j=0; j<width; ++j) {
                    if (grid[j][i]) {
                        shifted_y = true;
                        shift_y = i - (height - k); // ???
                        i = 0; // will kick out of second loop
                        break;
                    }
                }
            }
        }

        // The piece has been rotated. temp values hold the rotated x and y values
        for (int i=0; i<width; ++i){
            for (int j=0; j<height; ++j){
                if (grid[i][j]){
                    int temp_x = x + shift_x;
                    int temp_y = y + shift_y;
                    //Log.d("Rotate", "Are we rotating into a piece? x + i = " + (temp_x + i) + " and (y - height + j + 1) = " + (temp_y - height + j + 1) + ".");
                    // Check board pieces relative to the piece x and y positions
                    // NEXT TO DO, call Rotate(direction, true); to revert state, then return
                    if (((temp_x + i) < 10) && ((temp_x + i) >= 0) && ((temp_y - height + j + 1) < 19) && ((temp_y - height + j + 1) >= 0)) {
                        if (Board.grid[(temp_x + i)][temp_y - height + j + 1]) {
                            //Log.d("Rotate", "Yeah dude!!!!");
                            Rotate(direction, true);
                            return;
                        }
                    }
                }
            }
        }


        if (shifted_x) x += shift_x;
        if (shifted_y) y += shift_y;

        params.setMargins(MyTetrisMain.dpTopx(22 * x), 0, 0, MyTetrisMain.dpTopx(418 - (y * 22)));
        setLayoutParams(params);
        layout_flipper.setDisplayedChild(state);
    }

    public void canShift(CharSequence direction) {

        boolean[] traced = new boolean[height];
        for (int i=0; i<height; ++i) traced[i] = false;

        if (direction.equals("left")) {
            for (int i=0; i<width; ++i) {
                for (int j = 0; j < height; ++j) {
                    // Is the searched block filled?
                    if (grid[i][j] && !traced[j]) { // Yes
                        // If the block on the board to the left is filled, we return false. Otherwise we continue
                        traced[j] = true;

                        Log.d("SHIFT LEFT", "x= " + x + ". y= " + y + ". (x + i) + 1 = " + (x + i - 1) + ". (y - height + j + 1) = " + (y - height + j + 1) + ".");

                        if ((x+i)-1 < 0 || ((y - height + j + 1) >= 0 && Board.grid[(x + i) - 1][y - height + j + 1])) return; // Stop the piece, it hit something

                        // Check for actualy grid pieces

                    }
                    // We don't care about the block if it isn't filled
                }
            }
            --x;
        } else { // direction == "right"
            for (int i = (width - 1); i >= 0; --i) {
                for (int j = (height - 1); j >= 0; --j) {
                    // Is the searched block filled?
                    if (grid[i][j] && !traced[j]) { // Yes
                        // If the block on the board to the left is filled, we return false. Otherwise we continue
                        traced[j] = true;

                        Log.d("SHIFT RIGHT", "x= " + x + ". y= " + y + ". (x + i) + 1 = " + (x + i + 1) + ". (y - height + j + 1) = " + (y - height + j + 1) + ".");

                        if ((x+i)+1 > 9 || ((y - height + j + 1) >= 0 && Board.grid[(x + i) + 1][y - height + j + 1])) return; // Stop the piece, it hit something

                        // Check for actualy grid pieces

                    }
                    // We don't care about the block if it isn't filled
                }
            }
            ++x;
        }
    }

    public boolean drop() {
        // Drops the piece
        if (!MyTetrisMain.paused) {
            boolean[] traced = new boolean[width];
            for (int i=0; i<width; ++i) traced[i] = false;
            //Log.d("Drop", "What is the y?: " + y);
            for (int i = (height - 1); i >= 0; --i) {
                for (int j = 0; j < width; ++j) {
                    // Is the searched block filled?
                    if (grid[j][i] && !traced[j]) { // Yes
                        // If the block on the board to the left is filled, we return false. Otherwise we continue
                        traced[j] = true;
                        //Log.d("Drop", "i: " + i + " j: " + j);
                        if ((y - height + i + 1) >= 19 || ((y - height + i + 2) >= 0 && Board.grid[x + j][(y - height + i + 1) + 1])) {
                            // Place the piece
                            return false;
                        }

                    }
                    // We don't care about the block if it isn't filled
                }
            }

            ++y;
            params.setMargins(MyTetrisMain.dpTopx(22 * x), 0, 0, MyTetrisMain.dpTopx(418 - (y * 22)));
            setLayoutParams(params);
            return true;

        }
        return false;
    }

    public int getLayoutId() { return layout_id; }
}

