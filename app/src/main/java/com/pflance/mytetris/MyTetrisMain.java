package com.pflance.mytetris;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Random;


public class MyTetrisMain extends Activity implements GestureDetector.OnGestureListener, View.OnTouchListener {

    // Constants
    private static final int MAX_NUMBER_OF_PIECES = 7;     // The maximum number of pieces
    private static final int START_SCORE = 0;              // On a New Game, this is the starting score
    private static final int START_GOAL = 5;               // On a New Game, this is the starting goal (+= 5 every ++level)
    private static final int START_LEVEL = 1;              // On a New Game, this is the starting level
    private static final int START_DROP_SPEED = 500;       // On a New Game, this is the starting drop speed
    private static final int MIN_Y_SWIPE_DISTANCE = 44;    // The minimum swiping distance in dp for a swipe action
    private static final int MIN_Y_SWIPE_SPEED = 100;      // The minimum swiping speed for a swipe action
    private static final int MIN_X_SCROLL_DISTANCE = 20;   // The scrolling distance for a horizontal shift action
    private static final int MAX_DROP_SPEED = 100;         // The fastest the piece will ever drop. Lowest runnable delay.

    // Variables
    static boolean held, paused, game_over;              // Booleans that control the state of the game
    static boolean isScrolling, is_back_pressed;         // Booleans that allow/prevent actions from occurring
    int origin_point, end_point, screen_width_px;        // Used for touch/gesture listeners for touch screen functionality
    static TextView score_view, goal_view, level_view;   // TextViews of score, goal, and level to update
    static Random random;                                // Allows randomization in piece selection
    static LayoutInflater inflater;                      // A LayoutInflater used for inflating views in this Activity
    static PopupWindow gameOverPopUp;                    // The game over pop-up. Used as a global variable for activity wide dismissals
    static int displayed_goal, actual_drop_speed;        // Values used to keep track of the displayed real time goal and the actual drop speed of the piece
    static Piece[] all_pieces;                           // Holds all possible pieces, only ever creating 7 Piece objects
    static Handler pieceDropHandler;                     // A Handler which drops the piece
    static Runnable pieceDropRunnable;                   // The runnable for the Handler above
    GestureDetector gestureDetector;                     // A GestureDetector for this Activity
    PopupWindow pausePopUp;                              // The paused pop-up. Used as a global variable for activity wide dismissals

    // Fields
    private static Resources main_resources;                                             // Activity's resources
    private static Context main_context;                                                 // Activity's context
    private static int score, goal, level, drop_speed;                                   // Key values
    private static RelativeLayout the_held_piece_view, the_next_piece_view, mainLayout;  // Layouts that are handled dynamically. Piece views could be replaced with the pieces themselves.
    private static Board the_board;                                                      // The board that the game is played on
    private static Piece the_piece, the_held_piece, the_next_piece;                      // The 3 pieces that are dealt with throughout the game.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_my_tetris_main);

        // Get the screen width to set up tapping to rotate
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width_px = size.x;

        // Init the Gesture Listener and detector
        gestureDetector = new GestureDetector(getApplicationContext(), this);
        mainLayout = (RelativeLayout)findViewById(R.id.main_layout);
        mainLayout.setOnTouchListener(this);

        // Sets the random used to determine pieces
        random = new Random();

        //get context
        main_context = getBaseContext();

        // Set up Resources to convert px to dp
        main_resources = main_context.getResources();

        // Set up the LayoutInflater
        inflater = LayoutInflater.from(main_context);

        // Init the board
        the_board = (Board)findViewById(R.id.the_board);

        // Init all pieces
        all_pieces = new Piece[MAX_NUMBER_OF_PIECES];
        for (int i=0; i<MAX_NUMBER_OF_PIECES; ++i){
            all_pieces[i] = new Piece(main_context, i, the_board);
        }

        // Init the numerical value views
        level_view = (TextView)findViewById(R.id.the_level);
        goal_view = (TextView)findViewById(R.id.the_goal);
        score_view = (TextView)findViewById(R.id.the_score);

        // Init view where held piece is shown
        the_held_piece_view = (RelativeLayout)findViewById(R.id.held_piece_image);

        // Init view where next piece is shown
        the_next_piece_view = (RelativeLayout)findViewById(R.id.next_piece_image);

        // Inflate all views, set all pieces, set all variables
        resetGame();

        // Set the Handler and Runnable that drops the piece
        pieceDropHandler = new Handler();
        pieceDropRunnable = new Runnable() {
            @Override
            public void run() {

                // Check if the game isn't paused and we can no longer drop the piece
                if (!paused && !the_piece.drop()) {

                    // Drop the piece on the board
                    the_board.placePiece(the_piece);

                    // Checks if the piece placement loses the game or not
                    if (game_over) {

                        // Remove the Runnable from the Handler
                        pieceDropHandler.removeCallbacks(pieceDropRunnable);

                    } else {
                        // Game is not over

                        // Put a new piece on top of the board
                        setThePiece();

                        // Queue up a new piece
                        setNextPiece();

                        // Used for slamming piece down
                        drop_speed = actual_drop_speed;

                        // Player can hold a new piece once a piece has been placed
                        held = false;

                        pieceDropHandler.postDelayed(this, drop_speed);
                    }
                } else {
                    pieceDropHandler.postDelayed(this, drop_speed);
                }
            }
        };

        pieceDropHandler.postDelayed(pieceDropRunnable, drop_speed);

    }

    /**
     * Method for the OnTouchListener
     */
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        if (isScrolling) {
            //Log.d("onTouch------>", "Stopped scrolling");
            isScrolling = false;
        }
        return false;
    }

    /**
     * Methods for the Gesture Listener
     */
    @Override
    public boolean onDown(MotionEvent e) {
        /* Always called when the user presses down on the screen */
        // e holds details of point where pressed
        return false;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
        /* A flick-like motion where there is pressure at first, and less pressure at the end */
        // e1 holds details of point where down gesture was performed
        // e2 holds details of where swipe ends
        // velocityX and velocityY are self explanatory
        return false;
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
        /* A scroll is a fling where pressure is ~constant throughout movement */
        // e1 holds details of point where down gesture was performed
        // e2 holds details of where scrolling ends
        // velocityX and velocityY are self explanatory

        // If any pop-ups are up, do not scroll
        if (game_over || paused) return false;

        // Variables that store displacement from e1 to e2
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();

        // Check to see if vertical movement is more pronounced than horizontal movement
        if (Math.abs(diffY) > Math.abs(diffX)) {
            // Do Vertical action - Hold piece

            // Threshold for
            if (Math.abs(diffY) > dpTopx(MIN_Y_SWIPE_DISTANCE) && Math.abs(velocityY) > MIN_Y_SWIPE_SPEED) {

                // Check to see which direction has been swiped
                if (diffY > 0) {
                    // Swiped down, drop the piece and restart the Handler
                    drop_speed = 0;
                    pieceDropHandler.removeCallbacks(pieceDropRunnable);
                    pieceDropHandler.postDelayed(pieceDropRunnable, drop_speed);

                } else {
                    // Swiped up, check to see if we had previously held last piece
                    if (!held) {
                        // We didn't hold last piece, we can hold this piece
                        held = true;
                        holdPiece();
                    }
                }
            }
        } else {
            // Do horizontal action - shift piece

            // Check to see if we are starting a scroll
            if (!isScrolling) {
                // We take an origin point to get a displacement for a single shift of a piece
                origin_point = (int)e1.getX();
                isScrolling = true;
            }

            // Get the point of the end of the scroll
            end_point = (int)e2.getX();

            // Check to see which direction to shift the piece
            if (origin_point - end_point >= dpTopx(MIN_X_SCROLL_DISTANCE)) {
                // Shift left
                the_piece.shift(-1);
                origin_point = end_point;
            } else if (end_point - origin_point >= dpTopx(MIN_X_SCROLL_DISTANCE)) {
                // Shift right
                the_piece.shift(1);
                origin_point = end_point;
            }

            the_piece.updatePosition();

        }

        return false;
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e){
        /* Called when the user presses and releases the screen instantaneously */
        // e holds details of point where pressed
        int screen_center = screen_width_px / 2;

        // If any pop-ups are up, do not rotate
        if (game_over || paused) return false;

        // If on right side of screen, rotate clockwise. Else, rotate counter clockwise
        if (e.getRawX() <= screen_center) the_piece.Rotate(-1, false);
        else the_piece.Rotate(1, false);

        return false;
    }
    @Override
    public void onLongPress(MotionEvent e){
        /* Called when the user presses and holds the screen for minimum 2000 miliseconds */
        // e holds details of point where pressed
    }
    @Override
    public void onShowPress(MotionEvent e){
        /* Called when the user has pressed the screen and has not released, or performed any gestures since then */
        // e holds details of point where pressed
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        /* I do not know what this does */
        // e holds details of point where pressed

        return gestureDetector.onTouchEvent(e);
    }

    /**
     * Handle Device buttons
     */
    @Override
    public void onPause() {
        super.onPause();

        // Check to see if the game is paused, game is over, and the back button is not pressed
        if (!paused && !game_over && !is_back_pressed) {
            // Then pause the game while game is in this state
            popPauseMenu(mainLayout);
        }
    }
    @Override
    public void onBackPressed() {
        // Functionality when the device back button is pressed

        is_back_pressed = true;

        // Check to see if the game is paused
        if (paused) {
            // Dismiss the paused pop-up
            pausePopUp.dismiss();
            paused = false;
            is_back_pressed = false;
        } else {
            // Close the app

            // Dismiss the game over pop-up if it is up
            if (game_over) {
                gameOverPopUp.dismiss();
            } else {
                super.onBackPressed();
            }

            // Remove callback from Runnable
            pieceDropHandler.removeCallbacks(pieceDropRunnable);
        }
    }

    /**
     * A method that pops the Paused pop-up and places the game in a paused state
     */
    public void popPauseMenu(View view){
        // Let activity know the game is paused
        paused = true;

        // Set up pop-up window
        View popUpView = inflater.inflate(R.layout.pause_menu, null, false);
        pausePopUp = new PopupWindow(popUpView, -1, -1, false);
        pausePopUp.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        pausePopUp.setOutsideTouchable(true); // Can listen to device buttons when set to true

        // Set up Return to Game button
        Button return_button = (Button)popUpView.findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the paused state
                paused = false;
                pausePopUp.dismiss();
            }
        });
    }

    /**
     * A method that holds a piece and either swaps or makes a new piece for the actual piece
     */
    public void holdPiece() {
        Piece temp_piece = the_piece;
        int layout_id = 0;

        // Get the layout ID of the piece
        switch(the_piece.getNumber()){
            case 0: layout_id = R.layout.piece_0;
                break;
            case 1: layout_id = R.layout.piece_1;
                break;
            case 2: layout_id = R.layout.piece_2;
                break;
            case 3: layout_id = R.layout.piece_3;
                break;
            case 4: layout_id = R.layout.piece_4;
                break;
            case 5: layout_id = R.layout.piece_5;
                break;
            case 6: layout_id = R.layout.piece_6;
                break;
        }

        mainLayout.removeView(the_piece);

        // Check to see if we have held a piece of not
        if (the_held_piece == null) {
            // Set held piece to the piece, the piece to the next piece, and set a new next piece
            the_held_piece = temp_piece;
            the_piece = the_next_piece;
            setNextPiece();

        } else {

            // Swap held piece and the piece
            the_held_piece_view.removeAllViews();
            the_piece = the_held_piece;
            the_held_piece = temp_piece;
        }

        // Replace the piece back on top of the board and add it to the layout
        the_piece.resetPosition();
        the_piece.updatePosition();
        mainLayout.addView(the_piece);

        // Update piece usages
        the_held_piece.setInUse(false);
        the_piece.setInUse(true);

        // Set held image
        inflater.inflate(layout_id, the_held_piece_view);

        // To start the drop for the next piece coming with a full delay, remove and add runnable
        pieceDropHandler.removeCallbacks(pieceDropRunnable);
        pieceDropHandler.postDelayed(pieceDropRunnable, drop_speed);
    }

    /**
     * A method that selects a piece that is not in use
     *
     * @return A random Piece that is not in use at the time of the function call
     */
    public static Piece choosePiece() {
        int the_number;

        // Loop to find a piece not in use
        while(true) {
            the_number = random.nextInt(7);

            if (!all_pieces[the_number].getInUse()) {
                // This piece is not in use
                all_pieces[the_number].setInUse(true);
                return all_pieces[the_number];
            }
        }
    }

    /**
     * A method that is called every time a row is cleared
     */
    public static void updateNumbers(){

        // Decrease goal count by 1 and add points to score
        --displayed_goal;
        score += level * 100;

        // Checks to see if we level up
        if (displayed_goal == 0) {

            // Level up. Update new goal and level
            goal += 5;
            displayed_goal = goal;
            ++level;
            level_view.setText(Integer.toString(level));

            // Speed up piece drop until it maxes out at 100 speed
            if (actual_drop_speed > MAX_DROP_SPEED) {
                actual_drop_speed -= 50;
            }
        }

        // Set goal and score views
        goal_view.setText(Integer.toString(displayed_goal));
        score_view.setText(Integer.toString(score));
    }

    /**
     * A method that pops the Game Over pop-up and places the game in a Game Over state
     */
    public static void gameOver() {
        game_over = true;
        LayoutInflater popUp_inflater = LayoutInflater.from(main_context);
        View popUpView = popUp_inflater.inflate(R.layout.game_over_menu, null, false);
        gameOverPopUp = new PopupWindow(popUpView, -1, -1, false);
        gameOverPopUp.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        gameOverPopUp.setOutsideTouchable(true);

        // Others
        Button new_game_button = (Button)popUpView.findViewById(R.id.new_game_button);
        new_game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Reset all board values and set all to transparent
                for (int i=0; i < Board.BOARD_WIDTH; ++i){
                    for (int j=0; j < Board.BOARD_HEIGHT; ++j) {
                        the_board.setGrid(i, j, false);
                        the_board.setBlockColor(i, j, main_resources.getColor(R.color.transparent));
                    }
                }

                // Set all 7 pieces to not in use
                for (int i=0; i<7; ++i) {
                    all_pieces[i].setInUse(false);
                }

                resetGame();

                // And we good, start the runnable again
                pieceDropHandler.postDelayed(pieceDropRunnable, drop_speed);

                gameOverPopUp.dismiss();
            }
        });

        Button quit_button = (Button)popUpView.findViewById(R.id.quit_button);
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameOverPopUp.dismiss();
                System.exit(0);
            }
        });

        // Display score and level when the player loses
        TextView level_value = (TextView)popUpView.findViewById(R.id.game_over_level_value);
        level_value.setText(Integer.toString(level));
        TextView score_value = (TextView)popUpView.findViewById(R.id.game_over_score_value);
        score_value.setText(Integer.toString(score));

    }

    /**
     * A method that sets the actual piece
     */
    private static void setThePiece() {

        mainLayout.removeView(the_piece);
        if (the_next_piece != null) {
            the_piece = the_next_piece;
        } else {
            the_piece = choosePiece();
        }
        the_piece.resetPosition();
        the_piece.updatePosition();
        mainLayout.addView(the_piece);

    }

    /**
     * A method that sets the next piece
     */
    private static void setNextPiece() {

        // Remove any layout displayed on the view
        if (the_next_piece_view != null) {
            the_next_piece_view.removeAllViews();
        }

        // Get the next piece
        the_next_piece = choosePiece();

        // Set next image
        inflater.inflate(the_next_piece.getLayoutId(), the_next_piece_view);
        the_next_piece.setInUse(true);
    }

    /**
     * A method that sets all variables and pieces for the game to their starting value
     */
    private static void resetGame() {
        // Reset all values
        level = START_LEVEL;
        goal = START_GOAL;
        displayed_goal = goal;
        score = START_SCORE;
        drop_speed = START_DROP_SPEED;
        actual_drop_speed = drop_speed;
        is_back_pressed = false;
        isScrolling = false;
        game_over = false;
        paused = false;
        held = false;
        the_next_piece = null;
        the_held_piece = null;
        // We don't set the_piece to null since the other pieces are simply placeholders for the views

        // Set the numerical value views
        level_view.setText(Integer.toString(level));
        goal_view.setText(Integer.toString(displayed_goal));
        score_view.setText(Integer.toString(score));

        setThePiece();

        setNextPiece();

        // Reset the held piece
        the_held_piece_view.removeAllViews();

    }

    /**
     * A method takes an Integer as a dp value and converts it to pixels
     */
    public static int dpTopx(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, main_resources.getDisplayMetrics());
    }

}
