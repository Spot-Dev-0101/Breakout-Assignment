public class GameState{

    public Model model;

    public enum states {
        NONE,
        MENU,
        PRESSPLAY,
        PLAYING,
        ALLBRICKSDESTROYED
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
                    //move on to something new
                    model.view.mouseBatControl = true;
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
            }
        }
        lastState = currentState;
    }

}
