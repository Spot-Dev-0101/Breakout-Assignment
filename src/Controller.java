
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

// The breakout controller receives KeyPress events from the GUI (via
// the KeyEventHandler). It maps the keys onto methods in the model and
// calls them appropriately
public class Controller
{
    public Model model;
    public View  view;

    // we don't really need a constructor method, but include one to print a
    // debugging message if required
    public Controller()
    {
        Debug.trace("Controller::<constructor>");
    }

    public void userMouseInteraction(MouseEvent event){
        // set the bat's x pos to the position of the mouse
        model.setBatXPos((int)event.getX());
    }
}
