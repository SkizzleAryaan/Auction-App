import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ErrorController {

    @FXML
    private TextArea errorText;

    public void setErrorText(ErrorMessage em) {
    	Platform.runLater(() -> {
        	errorText.setText(em.errorMsg);
        	
	    });
    }
}
