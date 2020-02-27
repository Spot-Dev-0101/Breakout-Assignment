import javafx.application.Platform;
import javafx.scene.paint.Color;

public class GameState{

    public Model model;

    public enum states {
        NONE,
        MENU,
        PRESSPLAY,
        PLAYING,
        ALLBRICKSDESTROYED,
        BOSSPLAYING,
        GAMEOVER,
        WIN
    }
    public states currentState = states.NONE;
    private states lastState;

    public GameState(Model m){
        model = m;
    }

    public void checkState(){
        switch(currentState){
            case NONE: {
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
                if(model.bricksAlive <= 0){
                    exitState(states.PLAYING);
                    enterState(states.ALLBRICKSDESTROYED);
                }
                break;
            }
            case BOSSPLAYING:{
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
                    enterState(states.BOSSPLAYING);
                    break;
                }
                case BOSSPLAYING:{
                    model.view.mouseBatControl = true;
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
                    model.ball.frozen = true;
                    model.ball.setX((model.width/2)-model.BALL_SIZE/2);
                    model.ball.setY(model.height - 100);
                    model.ball.dirX = 0;
                    model.ball.dirY = 0;
                    break;
                }
                case BOSSPLAYING:{
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
