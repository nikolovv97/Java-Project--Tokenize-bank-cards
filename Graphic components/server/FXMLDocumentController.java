/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import server.ServerFXMLDocumentController;

public class FXMLDocumentController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ServerFXMLDocumentController pnlServerComponent;

    @FXML
    void initialize() {
        assert pnlServerComponent != null : "fx:id=\"pnlServerComponent\" was not injected: check your FXML file 'FXMLDocument.fxml'.";

    }
}
