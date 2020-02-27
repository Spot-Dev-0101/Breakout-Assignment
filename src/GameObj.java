import javafx.scene.paint.*;

// An object in the game, represented as a rectangle, with a position,
// a colour, and a direction of movement.
public class GameObj
{
    // state variables for a game object
    public boolean visible  = true;     // Can see (change to false when the brick gets hit)
    public int topX   = 0;              // Position - top left corner X
    public int topY   = 0;              // position - top left corner Y
    public int width  = 0;              // Width of object
    public int height = 0;              // Height of object
    public Color color;                // Colour of object
    public int   dirX   = 1;            // Direction X (1 or -1)
    public int   dirY   = 1;            // Direction Y (1 or -1)
    public int lives = 1;
    public int startingLives = 1;
    public Color startingColor;
    public boolean frozen = false;


    public GameObj(int x, int y, int w, int h, Color c, int l)
    {
        topX   = x;
        topY = y;
        width  = w;
        height = h;
        color = c;
        startingColor = c;
        if(l <= 0){
            l = 1;
        }
        lives = l;
        startingLives = l;
    }

    // move in x axis
    public void moveX( int units )
    {
        topX += units * dirX;
    }

    // move in y axis
    public void moveY( int units )
    {
        topY += units * dirY;
    }

    public void setX(int pos){
        topX = pos;
    }

    public void setY(int pos){
        topY = pos;
    }

    // change direction of movement in x axis (-1, 0 or +1)
    public void changeDirectionX()
    {
        dirX = -dirX;
    }

    // change direction of movement in y axis (-1, 0 or +1)
    public void changeDirectionY()
    {
        dirY = -dirY;
    }

    // Detect collision between this object and the argument object
    // It's easiest to work out if they do NOT overlap, and then
    // return the negative (with the ! at the beginning)
    public boolean hitBy( GameObj obj )
    {
        return ! ( topX >= obj.topX+obj.width     ||
                topX+width <= obj.topX         ||
                topY >= obj.topY+obj.height    ||
                topY+height <= obj.topY);

    }

    public String hitByDirection(GameObj obj){
        String result = "None";
        if(topX+10 >= obj.topX+obj.width){
            return "left";
        }
        if(topX+width-10 <= obj.topX){
            return "right";
        }
        if(topY+10 >= obj.topY+obj.height){
            return "top";
        }
        if(topY+height-10 <= obj.topY){
            return "bottom";
        }

        return result;
    }

    public boolean hitAndDirection(GameObj from, GameObj on){
        if(on.hitBy(from) && on.visible && from.visible) {
            String hitSide = on.hitByDirection(from);
            if (hitSide == "top" || hitSide == "bottom") {
                from.changeDirectionY();
            } else if (hitSide == "left" || hitSide == "right") {
                from.changeDirectionX();
            }
            //System.out.println(hitSide);
            return true;
        }
        return false;
    }

    public int dist(int a_x, int a_y, int b_x, int b_y){
        return (int)Math.sqrt(Math.pow(a_x + b_x, 2) + Math.pow(a_y + b_y, 2));
    }

}
