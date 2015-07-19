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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Random;


public class MyTetrisMain extends Activity implements GestureDetector.OnGestureListener, View.OnTouchListener {

    public static int score = 0;
    public static TextView score_view;
    public static int goal = 5; // += 5 every level
    public static TextView goal_view;
    public static int displayed_goal = goal; // updated every row clear and level up
    public static int level = 1;
    public static TextView level_view;
    public static Random random = new Random(); // The random generator for the pieces
    public static boolean held = false; // Determines if a piece has just been recently held or not
    public static Piece[] all_pieces = new Piece[7]; // Holds all possible pieces
    public static int drop_speed = 500; // The drop_speed that the piece is set to always
    public static int actual_drop_speed = 500; // The actual drop speed the piece should be
    private static RelativeLayout the_held_piece_view;
    private static RelativeLayout the_next_piece_view;
    private static Handler pieceDropHandler;
    private static Runnable pieceDropRunnable;
    public static Resources resources;
    public boolean isScrolling = false;
    public int origin_point;
    public int end_point;
    public static Board the_board;
    public static boolean paused = false;
    public int screen_width_dp;
    private GestureDetector gestureDetector;
    public static RelativeLayout mainLayout;
    public PopupWindow pausePopUp;
    public static PopupWindow gameOverPopUp;
    public Button return_button;
    public static Piece the_piece;
    public static Piece the_held_piece = null;
    public static Piece the_next_piece;
    public static Context main_context;
    private static boolean game_over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get context
        main_context = getBaseContext();

        // Set up Resources to convert px to dp
        resources = main_context.getResources();

        // Set up all pieces
        for (int i=0; i<7; ++i){
            all_pieces[i] = new Piece(getBaseContext(), i);
        }

        // Set the content view
        setContentView(R.layout.activity_my_tetris_main);

        // Set up score and goal TextViews
        level_view = (TextView)findViewById(R.id.the_level);
        level_view.setText(Integer.toString(level));
        goal_view = (TextView)findViewById(R.id.the_goal);
        goal_view.setText(Integer.toString(displayed_goal));
        score_view = (TextView)findViewById(R.id.the_score);
        score_view.setText(Integer.toString(score));

        // Sets up the Gesture Listener and detector
        gestureDetector = new GestureDetector(getApplicationContext(), this);
        View view = findViewById(R.id.main_layout);
        view.setOnTouchListener(this);
        mainLayout = (RelativeLayout)view;

        // Set up the held piece view
        the_held_piece_view = (RelativeLayout)findViewById(R.id.held_piece_image);
        RelativeLayout.LayoutParams held_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        the_held_piece_view.setLayoutParams(held_params);
        held_params.addRule(RelativeLayout.ABOVE, R.id.pause_button);
        held_params.addRule(RelativeLayout.RIGHT_OF, R.id.right_gridbar);
        held_params.setMargins(dpTopx(26), 0, 0, dpTopx(60));
        the_held_piece_view.setLayoutParams(held_params);

        // Set up the next piece view
        the_next_piece_view = (RelativeLayout)findViewById(R.id.next_piece_image);
        RelativeLayout.LayoutParams next_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        the_next_piece_view.setLayoutParams(next_params);
        next_params.addRule(RelativeLayout.ABOVE, R.id.pause_button);
        next_params.addRule(RelativeLayout.RIGHT_OF, R.id.right_gridbar);
        next_params.setMargins(dpTopx(26), 0, 0, dpTopx(230));
        the_next_piece_view.setLayoutParams(next_params);
        the_next_piece = choosePiece();
        // Set next image
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        inflater.inflate(the_next_piece.getLayoutId(), the_next_piece_view);
        the_next_piece.inUse = true;

        // Get the screen width to set up tapping to rotate
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size.x, getResources().getDisplayMetrics());

        // Init the board
        the_board = (Board)findViewById(R.id.the_board);
        the_board.main_view = view;
        the_board.main_context = main_context;

        // Set the current piece to one of the pieces stored
        the_piece = choosePiece();
        the_piece.state = 5;
        the_piece.Rotate(1, false);
        mainLayout.addView(the_piece);
        the_piece.inUse = true;

        pieceDropHandler = new Handler();

        pieceDropRunnable = new Runnable() {
            @Override
            public void run() {
                //do something
                if (!the_piece.drop() && !paused) {
                    the_board.placePiece(the_piece);
                    if (game_over) {
                        game_over = false;
                        pieceDropHandler.removeCallbacks(pieceDropRunnable);
                    } else {
                        mainLayout.removeView(the_piece);
                        the_piece = the_next_piece;
                        the_next_piece_view.removeAllViews();
                        the_next_piece = choosePiece();
                        // Set next image
                        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
                        inflater.inflate(the_next_piece.getLayoutId(), the_next_piece_view);
                        the_next_piece.inUse = true;
                        the_piece.x = 4;
                        the_piece.y = -1;
                        the_piece.state = 5;
                        the_piece.Rotate(1, false);
                        the_piece.params.setMargins(dpTopx(22 * the_piece.x), 0, 0, dpTopx(418 - (the_piece.y * 22)));
                        mainLayout.addView(the_piece);
                        drop_speed = actual_drop_speed;
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
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        if (isScrolling) {
            //Log.d("onTouch------>", "Stopped scrolling");
            isScrolling = false;
        }
        return false;
    }
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

        /* This is where moving the peice will be implemented */

        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffY) > Math.abs(diffX)) {
            // Do Vertical action
            if (Math.abs(diffY) > dpTopx(44) && Math.abs(velocityY) > 100) {
                if (diffY > 0) {
                    drop_speed = 0;
                    pieceDropHandler.removeCallbacks(pieceDropRunnable);
                    pieceDropHandler.postDelayed(pieceDropRunnable, drop_speed);
                } else {
                    if (!held) {
                        held = true;
                        holdPiece();
                    }
                }
            }
        } else {
            // Do horizontal action

            if (!isScrolling) {
                //Log.d("onScroll", "Start Scrolling!");
                origin_point = (int)e1.getX();
                isScrolling = true;
            }

            end_point = (int)e2.getX();
            if (origin_point - end_point >= dpTopx(20)) {
                the_piece.canShift("left");
                origin_point = end_point;
            } else if (end_point - origin_point >= dpTopx(20)) {
                the_piece.canShift("right");
                origin_point = end_point;
            }

            the_piece.params.setMargins(dpTopx(22*the_piece.x), 0, 0, dpTopx(418 - (the_piece.y * 22)));
            the_piece.setLayoutParams(the_piece.params);

        }

        return false;
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e){
        /* Called when the user presses and releases the screen instantaneously */
        // e holds details of point where pressed
        int touch_x_dp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, e.getRawX(), getResources().getDisplayMetrics());
        int screen_center = screen_width_dp / 2;

        if (touch_x_dp <= screen_center) the_piece.Rotate(-1, false);
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

    @Override
    public void onPause() {
        super.onPause();
        if (!paused) {
            popPauseMenu(mainLayout);
        }
    }

    public void popPauseMenu(View view){
        paused = true;
        LayoutInflater popUp_inflater = LayoutInflater.from(getBaseContext());
        View popUpView = popUp_inflater.inflate(R.layout.pause_menu, null, false);
        pausePopUp = new PopupWindow(popUpView, -1, -1, true);
        pausePopUp.showAtLocation(findViewById(R.id.main_layout), Gravity.CENTER, 0, 0);
        return_button = (Button)popUpView.findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paused = false;
                pausePopUp.dismiss();
            }
        });
    }
    public static int dpTopx(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public void holdPiece() {
        Piece temp_piece = the_piece;
        boolean nulled = (the_held_piece == null);
        int layout_id = 0;

        switch(the_piece.number){
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

        if (nulled) {
            Log.d("HOLD", "No held piece yet");
            the_held_piece = temp_piece;
            the_piece = the_next_piece;
            the_next_piece_view.removeAllViews();
            the_next_piece = choosePiece();
            // Set next image
            LayoutInflater inflater = LayoutInflater.from(getBaseContext());
            inflater.inflate(the_next_piece.getLayoutId(), the_next_piece_view);
            the_next_piece.inUse = true;
        } else {
            Log.d("HOLD", "Recover held piece");
            // Recover held piece
            the_held_piece_view.removeAllViews();
            the_piece = the_held_piece;
            the_held_piece = temp_piece;
        }

        the_piece.state = 5;
        the_piece.x = 3;
        the_piece.y = -1;
        the_piece.params.setMargins(dpTopx(22 * the_piece.x), 0, 0, dpTopx(418 - (the_piece.y * 22)));
        the_piece.Rotate(1, false);
        mainLayout.addView(the_piece);
        the_held_piece.inUse = false;
        the_piece.inUse = true;

        // Set held image
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        inflater.inflate(layout_id, the_held_piece_view);

        pieceDropHandler.removeCallbacks(pieceDropRunnable);
        pieceDropHandler.postDelayed(pieceDropRunnable, drop_speed);
    }

    public static Piece choosePiece() {
        int the_number;
        while(true) {
            the_number = random.nextInt(7);
            if (!all_pieces[the_number].inUse) {
                all_pieces[the_number].inUse = true;
                return all_pieces[the_number];
            }
        }
    }

    public static void updateNumbers(){
        --displayed_goal;
        score += level * 100;
        if (displayed_goal == 0) {
            // Level up
            goal += 5;
            displayed_goal = goal;
            ++level;
            level_view.setText(Integer.toString(level));
            if (actual_drop_speed > 100) {
                actual_drop_speed -= 50;
            }
        }
        goal_view.setText(Integer.toString(displayed_goal));
        score_view.setText(Integer.toString(score));
    }

    public static void gameOver(Context context, View view) {
        game_over = true;
        LayoutInflater popUp_inflater = LayoutInflater.from(context);
        View popUpView = popUp_inflater.inflate(R.layout.game_over_menu, null, false);
        gameOverPopUp = new PopupWindow(popUpView, -1, -1, true);
        gameOverPopUp.showAtLocation(view, Gravity.CENTER, 0, 0);

        // Others
        Button new_game_button = (Button)popUpView.findViewById(R.id.new_game_button);
        new_game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paused = false;
                held = false;

                // Reset board
                for (int i=0; i<10; ++i){
                    for (int j=0; j<20; ++j) {
                        Board.grid[i][j] = false;
                        Board.blocks[i][j].setBackgroundColor(resources.getColor(R.color.transparent));
                    }
                }

                // Reset score, level, goal
                level = 1;
                level_view.setText("" + level);
                goal = 5;
                goal_view.setText("" + goal);
                score = 0;
                score_view.setText("" + score);

                // Reset drop speed
                actual_drop_speed = 500;
                drop_speed = actual_drop_speed;

                // Set all 7 pieces to not in use
                for (int i=0; i<7; ++i) {
                    all_pieces[i].inUse = false;
                }

                // Reset held piece
                the_held_piece = null;
                the_held_piece_view.removeAllViews();

                // Reset next piece
                the_next_piece_view.removeAllViews();
                the_next_piece = choosePiece();
                LayoutInflater inflater = LayoutInflater.from(main_context);
                inflater.inflate(the_next_piece.getLayoutId(), the_next_piece_view);
                the_next_piece.inUse = true;

                // Reset the piece
                mainLayout.removeView(the_piece);
                the_piece = choosePiece();
                the_piece.x = 4;
                the_piece.y = -1;
                the_piece.state = 5;
                the_piece.Rotate(1, false);
                the_piece.params.setMargins(dpTopx(22 * the_piece.x), 0, 0, dpTopx(418 - (the_piece.y * 22)));
                the_piece.setLayoutParams(the_piece.params);
                the_piece.inUse = true;
                mainLayout.addView(the_piece);

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

        TextView level_value = (TextView)popUpView.findViewById(R.id.game_over_level_value);
        level_value.setText("" + level);

        TextView score_value = (TextView)popUpView.findViewById(R.id.game_over_score_value);
        score_value.setText("" + score);
    }

}
