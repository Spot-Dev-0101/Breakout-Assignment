import javafx.scene.paint.Color;

public class GameState{

    public Model model;

    // Enum of possible states the game can be in
    public enum states {
        NONE,                   // init
        MENU,                   // The menu when the game starts
        PRESSPLAY,              // When the player presses the play button
        PLAYING,                // While the game is running
        ALLBRICKSDESTROYED,     // When all bricks have been destroyed
        BOSSPLAYING,            // While the boss level is rrunning
        GAMEOVER,               // The player has lost the game
        WIN                     // The player has won the game
    }
    public states currentState = states.NONE;   // The current state of the game
    private states lastState;                   // The previous state

    public GameState(Model m){
        model = m;
    }

    //Constantly run checking what's going on in the game to decide when to move to a new state
    public void checkState(){
        switch(currentState){
            case NONE: {
                // This is the init state so move to the menu
                System.out.println(1);
                enterState(states.MENU);
                break;
            }
            case MENU: {
                //wait for the play button to be pressed
                if(model.view.pressPlayButton.isPressed()){
                    System.out.println(3);
                    enterState(states.PRESSPLAY);
                }
                break;
            }
            case PLAYING:{
                // wait for all the bricks to be destroyed
                if(model.bricksAlive <= 0){
                    exitState(states.PLAYING);
                    enterState(states.ALLBRICKSDESTROYED);
                }
                break;
            }
            case BOSSPLAYING:{
                // Wait for the boss to be killed
                if(model.boss.lives <= 0){
                    exitState(states.BOSSPLAYING);
                    enterState(states.WIN);
                    break;
                } else{
                    for(GameObj bullet : model.boss.bullets){
                        if(model.bat.hitBy(bullet)){
                            exitState(states.BOSSPLAYING);
                            enterState(states.GAMEOVER);
                            break;
                        }
                    }
                }
                // Check if each leg has lost it's life
                if(model.boss.leg_left_top.lives <= 0){
                    model.boss.leg_left_top.visible = false;
                }
                if(model.boss.leg_left_bottom.lives <= 0){
                    model.boss.leg_left_bottom.visible = false;
                }
                if(model.boss.leg_right_top.lives <= 0){
                    model.boss.leg_right_top.visible = false;
                }
                if(model.boss.leg_right_bottom.lives <= 0){
                    model.boss.leg_right_bottom.visible = false;
                }

                // If all the bosses legs have been destroyed change the bosses health, add more bullets and change it's color
                if(!model.boss.leg_left_top.visible && !model.boss.leg_left_bottom.visible
                && !model.boss.leg_right_top.visible && !model.boss.leg_right_bottom.visible && model.boss.allLegsDead == false){
                    model.boss.lives = 7;
                    model.boss.addBullets(5);
                    model.boss.color = model.lerpColors(model.boss.startingColor, Color.BLACK, model.boss.lives, model.boss.startingLives);
                    model.boss.allLegsDead = true;
                }
                break;
            }
        }
    }

    //Change the state of the game
    public void enterState(states state){
        if(state != lastState) {
            lastState = currentState;
            currentState = state;
            System.out.println("Entered " + state);
            switch (state) {
                case MENU: {
                    //show the button and text
                    model.view.infoText.setVisible(false);
                    System.out.println(2);
                    break;
                }
                case PRESSPLAY: {
                    //start the game and spawn everything
                    exitState(states.MENU);
                    model.setGameRunning(true);
                    enterState(states.PLAYING);
                    model.view.infoText.setVisible(true);
                    break;
                }
                case ALLBRICKSDESTROYED: {
                    // change state to BOSSPLAYING
                    enterState(states.BOSSPLAYING);
                    break;
                }
                case BOSSPLAYING:{
                    // draw the boss and change the direction of the ball
                    model.boss.visible = true;
                    model.ball.frozen = false;
                    model.ball.dirY = -1;
                    model.ball.dirX = -1;
                    System.out.println(model.ball.dirX);
                    break;
                }
                case GAMEOVER:{
                    //show the game over screen
                    model.view.infoText.setVisible(false);
                    model.view.gameOverLabel.setVisible(true);
                    break;
                }
                case WIN:{
                    //show the win screen
                    model.view.infoText.setVisible(false);
                    model.view.winLabel.setVisible(true);
                    break;
                }
            }

        }

    }

    // Exit a state
    public void exitState(states state){
        if(currentState != lastState){
            System.out.println("Exited " + state);
            switch(state){
                case MENU:{
                    //hide all the menu elements
                    model.view.pressPlayButton.setVisible(false);
                    break;
                }
                case PLAYING:{
                    // place the ball in the center of the screen at the bottom and freeze it
                    model.ball.frozen = true;
                    model.ball.setX((model.width/2)-model.BALL_SIZE/2);
                    model.ball.setY(model.height - 100);
                    model.ball.dirX = 0;
                    model.ball.dirY = 0;
                    break;
                }
                case BOSSPLAYING:{
                    // hide the boss, ball and bat
                    model.boss.visible = false;
                    model.bat.visible = false;
                    model.ball.visible = false;
                    model.ball.frozen = true;
                    model.ball.dirY = 0;
                    model.ball.dirX = 0;
                    break;
                }
            }
        }
        lastState = currentState;
    }

}
