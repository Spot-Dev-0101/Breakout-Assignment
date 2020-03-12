import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class Boss extends GameObj {

    public ArrayList<GameObj> bullets = new ArrayList<>();
    public int amountOfBullets = 5;         // The amount of bullets

    public int BULLET_HEIGHT = 15;          // Height of the bullet
    public int BULLET_WIDTH = 5;            // Width of the bullet

    public GameObj leg_left_top;            // Top part of the left leg
    public GameObj leg_left_bottom;         // Bottom part of the left leg

    public GameObj leg_right_top;           // Top part of the right leg
    public GameObj leg_right_bottom;        // Bottom part of the right leg

    public boolean allLegsDead = false;     // True when all the boss's legs are destroyed

    public Boss(int x, int y, Color c){
        // Create the base GameObj
        super(x-100, y-100, 200, 200, c, 25);

        // Create the 4 legs
        leg_left_top = new GameObj(x-175, y, 75, 25, c, 3);
        leg_left_bottom = new GameObj(x-175, y, 25, 125, c, 3);

        leg_right_top = new GameObj(x+100, y, 75, 25, c, 3);
        leg_right_bottom = new GameObj(x+150, y, 25, 125, c, 3);

        // Spawn the bullets
        addBullets(amountOfBullets);

    }

    public void updateBullets(){
        //Each bullet moves down. When they hit the bottom they are placed at a random pos at the top of the screen
        for(GameObj bullet : bullets){
            bullet.moveY(10);
            if(bullet.topY > 800){
                Random r = new Random();
                bullet.setY(r.nextInt((0 - -1000) + 1) + -1000);
                bullet.setX(r.nextInt((600 - 0) + 1) + 0);
            }
        }
    }

    //Spawn an amount of bullets
    public void addBullets(int amount){
        for(int i = 0; i < amount; i++){
            Random r = new Random();
            bullets.add(new GameObj(r.nextInt((600 - 0) + 1) + 0, r.nextInt((0 - -1000) + 1) + -1000, BULLET_WIDTH, BULLET_HEIGHT, Color.BLUE, 1));
        }
    }

}
