import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

public class WinnerController {

    @FXML
    private Button niceButton;

    @FXML
    private Label winBid;

    @FXML
    private Label winItem;

    @FXML
    private Label winUser;

    
    public void setWinnerText(Winner win) {
    	Platform.runLater(() -> {
    		winUser.setText(win.username);
        	winItem.setText(win.item.name);
        	winBid.setText(win.bid.toString());
	    });
    }
}
