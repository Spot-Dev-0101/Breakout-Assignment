import java.util.ArrayList;
import javafx.scene.paint.*;
import javafx.application.Platform;

// The model represents all the actual content and functionality of the app
// For Breakout, it manages all the game objects that the View needs
// (the bat, ball, bricks, and the score), provides methods to allow the Controller
// to move the bat (and a couple of other fucntions - change the speed or stop 
// the game), and runs a background process (a 'thread') that moves the ball 
// every 20 milliseconds and checks for collisions 
public class Model
{
    // First,a collection of useful values for calculating sizes and layouts etc.

    public int B              = 6;      // Border round the edge of the panel
    public int M              = 40;     // Height of menu bar space at the top

    public int BALL_SIZE      = 30;     // Ball side
    public int BRICK_WIDTH    = 50;     // Brick size
    public int BRICK_HEIGHT   = 30;
    public int BRICK_AMOUNT = 1;
    public int BRICK_TOPGAP = 50;
    public int BRICK_XGAP = 10;
    public int BRICK_YGAP = 10;
    public int BRICK_MAXLIVES = 1;

    public int BAT_MOVE       = 5;      // Distance to move bat on each keypress
    public int BALL_MOVE      = 3;      // Units to move the ball on each step
    public int BAT_WIDTH = 150;
    public int BAT_HEIGHT = 10;

    public int HIT_BRICK      = 50;     // Score for hitting a brick
    public int HIT_BOTTOM     = -200;   // Score (penalty) for hitting the bottom of the screen
    public int HIT_BOSS       = 25;

    // The other parts of the model-view-controller setup
    View view;
    Controller controller;
    GameState gameState;

    // The game 'model' - these represent the state of the game
    // and are used by the View to display it
    public GameObj ball;                // The ball
    public ArrayList<GameObj> bricks;   // The bricks
    public GameObj bat;                 // The bat
    public Boss boss;
    public int score = 0;               // The score
    public int bricksAlive = 99;

    // variables that control the game
    public boolean gameRunning = false;  // Set false to stop the game
    public boolean gameLoopRunning =  true;
    public boolean fast = false;        // Set true to make the ball go faster

    // initialisation parameters for the model
    public int width;                   // Width of game
    public int height;                  // Height of game

    // CONSTRUCTOR - needs to know how big the window will be
    public Model( int w, int h )
    {
        Debug.trace("Model::<constructor>");
        width = w;
        height = h;
    }

    // Initialise the game - reset the score and create the game objects
    public void initialiseGame()
    {
        score = 0;
        ball   = new GameObj(width/2, height/2, BALL_SIZE, BALL_SIZE, Color.RED, 1);
        bat    = new GameObj(width/2, height - BRICK_HEIGHT*3/2, BAT_WIDTH,
                BAT_HEIGHT, Color.GRAY, 1);
        boss = new Boss(width/2, 250, Color.RED);
        boss.visible = false;
        bricks = new ArrayList<>();
        int y = 0;
        int xCounter = 0;
        //BRICK_XGAP = width/BRICK_AMOUNT;
        for(int i = 1; i < BRICK_AMOUNT+1; i++){
            int x = (BRICK_WIDTH+BRICK_XGAP)*xCounter;
            if(x+(BRICK_WIDTH/1) >= width-BRICK_WIDTH){
                y += BRICK_HEIGHT+BRICK_YGAP;
                xCounter = 0;
                x = (BRICK_WIDTH+BRICK_XGAP)*xCounter;
            }
            xCounter += 1;
            int lives = (BRICK_MAXLIVES-(y/(BRICK_HEIGHT+BRICK_YGAP)+1)) + 1;
            //System.out.println(x + " " + y + " " + lives);
            bricks.add(new GameObj(x+(BRICK_XGAP), BRICK_TOPGAP+y, BRICK_WIDTH, BRICK_HEIGHT, lerpColors(Color.GREEN, Color.BLUE, lives-1, BRICK_MAXLIVES), lives));
        }
        bricksAlive = BRICK_AMOUNT;
        // *[1]******************************************************[1]*
        // * Fill in code to add the bricks to the arrayList            *
        // **************************************************************

    }

    // Animating the game
    // The game is animated by using a 'thread'. Threads allow the program to do
    // two (or more) things at the same time. In this case the main program is
    // doing the usual thing (View waits for input, sends it to Controller,
    // Controller sends to Model, Model updates), but a second thread runs in
    // a loop, updating the position of the ball, checking if it hits anything
    // (and changing direction if it does) and then telling the View the Model
    // changed.

    // When we use more than one thread, we have to take care that they don't
    // interfere with each other (for example, one thread changing the value of
    // a variable at the same time the other is reading it). We do this by
    // SYNCHRONIZING methods. For any object, only one synchronized method can
    // be running at a time - if another thread tries to run the same or another
    // synchronized method on the same object, it will stop and wait for the
    // first one to finish.

    // Start the animation thread
    public void startGame()
    {

        Thread t = new Thread( this::runGame );     // create a thread runnng the runGame method
        t.setDaemon(true);                          // Tell system this thread can die when it finishes
        t.start();                                  // Start the thread running
    }

    // The main animation loop

    public void runGame()
    {
        try
        {
            // set gameRunning true - game will stop if it is set false (eg from main thread)

            while (getGameLoopRunning())
            {
                gameState.checkState();
                if(getGameRunning()) {
                    updateGame();                        // update the game state
                    modelChanged();                      // Model changed - refresh screen
                }
                Thread.sleep( getFast() ? 10 : 20 ); // wait a few milliseconds
            }
        } catch (Exception e)
        {
            Debug.error("Model::runAsSeparateThread error: " + e.getMessage() );
        }
    }

    // updating the game - this happens about 50 times a second to give the impression of movement
    public synchronized void updateGame()
    {
        if(boss.visible){
            boss.updateBullets();


            if(boss.hitAndDirection(ball, boss)){
                boss.lives -= 1;
                boss.color = lerpColors(boss.startingColor, Color.BLACK, boss.lives, boss.startingLives);
                score += HIT_BOSS;
            }
            if(boss.leg_left_top.hitAndDirection(ball, boss.leg_left_top)){
                score += HIT_BOSS;
                boss.leg_left_top.lives -= 1;
                boss.leg_left_top.color = lerpColors(boss.leg_left_top.startingColor, Color.BLACK, boss.leg_left_top.lives, boss.leg_left_top.startingLives);
            }
            if(boss.leg_left_bottom.hitAndDirection(ball, boss.leg_left_bottom)){
                score += HIT_BOSS;
                boss.leg_left_bottom.lives -= 1;
                boss.leg_left_bottom.color = lerpColors(boss.leg_left_bottom.startingColor, Color.BLACK, boss.leg_left_bottom.lives, boss.leg_left_bottom.startingLives);
            }
            if(boss.leg_right_top.hitAndDirection(ball, boss.leg_right_top)){
                score += HIT_BOSS;
                boss.leg_right_top.lives -= 1;
                boss.leg_right_top.color = lerpColors(boss.leg_right_top.startingColor, Color.BLACK, boss.leg_right_top.lives, boss.leg_right_top.startingLives);
            }
            if(boss.leg_right_bottom.hitAndDirection(ball, boss.leg_right_bottom)){
                score += HIT_BOSS;
                boss.leg_right_bottom.lives -= 1;
                boss.leg_right_bottom.color = lerpColors(boss.leg_right_bottom.startingColor, Color.BLACK, boss.leg_right_bottom.lives, boss.leg_right_bottom.startingLives);
            }

        }
        // move the ball one step (the ball knows which direction it is moving in)
        if(ball.frozen == false) {
            ball.moveX(BALL_MOVE);
            ball.moveY(BALL_MOVE);
            // get the current ball possition (top left corner)
            int x = ball.topX;
            int y = ball.topY;
            // Deal with possible edge of board hit
            if (x >= width - B - BALL_SIZE) ball.changeDirectionX();
            if (x <= 0 + B) ball.changeDirectionX();
            if (y >= height - B - BALL_SIZE)  // Bottom
            {
                ball.changeDirectionY();
                addToScore(HIT_BOTTOM);     // score penalty for hitting the bottom of the screen
            }
            if (y <= 0 + M) ball.changeDirectionY();
        }

        // check whether ball has hit a (visible) brick
        boolean hit = false;

        // *[2]******************************************************[2]*
        // * Fill in code to check if a visible brick has been hit      *
        // * The ball has no effect on an invisible brick               *
        // * If a brick has been hit, change its 'visible' setting to   *
        // * false so that it will 'disappear'                          *
        // **************************************************************

        for(GameObj brick : bricks){
            if(brick.visible && brick.hitAndDirection(ball, brick)){



                if(brick.lives <= 1){
                    brick.visible = false;
                    addToScore(HIT_BRICK);
                    bricksAlive--;
                } else{
                    brick.lives--;
                    brick.color = lerpColors(brick.startingColor, Color.RED, brick.lives, brick.startingLives);
                }
            }
        }

        // check whether ball has hit the bat
        if (ball.visible && ball.hitBy(bat) ){
            System.out.println(ball.dirX + " " + bat.dirX);
            if(ball.dirX != bat.dirX && bat.dirX != 0){
                ball.changeDirectionX();
            }
            ball.changeDirectionY();
        }

    }

    public Color lerpColors(Color startColor, Color endColor, int currentvalue, int maxValue){

        float value = 1-((float)currentvalue/(float)maxValue);
        Color result = startColor.interpolate(endColor, value);
        System.out.println(value + " " + currentvalue + " " + maxValue);
        return result;
    }

    // This is how the Model talks to the View
    // Whenever the Model changes, this method calls the update method in
    // the View. It needs to run in the JavaFX event thread, and Platform.runLater
    // is a utility that makes sure this happens even if called from the
    // runGame thread
    public synchronized void modelChanged()
    {
        Platform.runLater(view::update);
    }


    // Methods for accessing and updating values
    // these are all synchronized so that the can be called by the main thread
    // or the animation thread safely

    // Change game running state - set to false to stop the game
    public synchronized void setGameRunning(Boolean value)
    {
        gameRunning = value;
    }

    // Return game running state
    public synchronized Boolean getGameRunning()
    {
        return gameRunning;
    }

    public synchronized Boolean getGameLoopRunning()
    {
        return gameLoopRunning;
    }

    // Change game speed - false is normal speed, true is fast
    public synchronized void setFast(Boolean value)
    {
        fast = value;
    }

    // Return game speed - false is normal speed, true is fast
    public synchronized Boolean getFast()
    {
        return(fast);
    }

    // Return bat object
    public synchronized GameObj getBat()
    {
        return(bat);
    }

    // return ball object
    public synchronized GameObj getBall()
    {
        return(ball);
    }

    public synchronized Boss getBoss(){return boss;}

    // return bricks
    public synchronized ArrayList<GameObj> getBricks()
    {
        return(bricks);
    }

    // return score
    public synchronized int getScore()
    {
        return(score);
    }

    // update the score
    public synchronized void addToScore(int n)
    {
        score += n;
    }



    // move the bat one step - -1 is left, +1 is right
    public synchronized void moveBat( int direction )
    {
        int dist = direction * BAT_MOVE;    // Actual distance to move
        Debug.trace( "Model::moveBat: Move bat = " + dist );
        int newBatPosX = bat.topX + dist;
        if(!(newBatPosX+bat.width > width) && !(newBatPosX-bat.width < -bat.width)){
            if(newBatPosX > bat.topX){
                bat.dirX = 1;
            } else if(newBatPosX < bat.topX){
                bat.dirX = -1;
            } else{
                bat.dirX = 0;
            }
            bat.moveX(dist);
        }

    }

    public synchronized void setBatXPos(int pos){
        //Debug.trace( "Model::moveBat: bat pos = " + pos );
        int newBatPos = pos - bat.width/2;
        if(!(newBatPos+bat.width > width) && !(newBatPos-bat.width < -bat.width)){
            if(newBatPos > bat.topX){
                bat.dirX = 1;
            } else if(newBatPos < bat.topX){
                bat.dirX = -1;
            } else{
                bat.dirX = 0;
            }
            //System.out.println(bat.dirX);
            bat.setX(newBatPos);
        } else{
            bat.dirX = 0;
        }
    }
}   
    