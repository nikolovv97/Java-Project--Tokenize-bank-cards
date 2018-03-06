package client;

import client.ClientFXMLDocumentController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;

public class FXMLDocumentController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ClientFXMLDocumentController pnlClientComponent;

    @FXML
    void initialize() {
        assert pnlClientComponent != null : "fx:id=\"pnlClientComponent\" was not injected: check your FXML file 'FXMLDocument.fxml'.";

    }
}
