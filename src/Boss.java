import javafx.scene.paint.Color;

public class Boss extends GameObj {

    public Boss(int x, int y, Color c){
        super(x-125, y-125, 250, 250, c, 10);
        System.out.println(x + " " + y);
    }

}
