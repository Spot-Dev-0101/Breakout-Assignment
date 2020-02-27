import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class Boss extends GameObj {

    public ArrayList<GameObj> bullets = new ArrayList<>();
    public int amountOfBullets = 5;

    public int BULLET_HEIGHT = 15;
    public int BULLET_WIDTH = 5;

    public GameObj leg_left_top;
    public GameObj leg_left_bottom;

    public GameObj leg_right_top;
    public GameObj leg_right_bottom;

    public boolean allLegsDead = false;

    public Boss(int x, int y, Color c){
        super(x-125, y-125, 250, 250, c, 25);

        leg_left_top = new GameObj(x-225, y, 100, 25, c, 3);
        leg_left_bottom = new GameObj(x-225, y, 25, 150, c, 3);

        leg_right_top = new GameObj(x+100, y, 100, 25, c, 3);
        leg_right_bottom = new GameObj(x+200, y, 25, 150, c, 3);

        for(int i = 0; i < amountOfBullets; i++){
            Random r = new Random();
            bullets.add(new GameObj(r.nextInt((600 - 0) + 1) + 0, r.nextInt((0 - -1000) + 1) + -1000, BULLET_WIDTH, BULLET_HEIGHT, Color.BLUE, 1));
        }

    }

    public void updateBullets(){

        for(GameObj bullet : bullets){
            bullet.moveY(10);
            if(bullet.topY > 800){
                Random r = new Random();
                bullet.setY(r.nextInt((0 - -1000) + 1) + -1000);
                bullet.setX(r.nextInt((600 - 0) + 1) + 0);
            }
        }
    }

    public void addBullets(int amount){
        for(int i = 0; i < amount; i++){
            Random r = new Random();
            bullets.add(new GameObj(r.nextInt((600 - 0) + 1) + 0, r.nextInt((0 - -1000) + 1) + -1000, BULLET_WIDTH, BULLET_HEIGHT, Color.BLUE, 1));
        }
    }

}
