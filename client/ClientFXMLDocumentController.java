package client;

import TokenizeBankCards.Rights;
import TokenizeBankCards.TokenizeBankCardsServerInterface;
import TokenizeBankCards.User;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javax.swing.JOptionPane;

public class ClientFXMLDocumentController extends AnchorPane{

    
    public ClientFXMLDocumentController()
    {
        FXMLLoader fxmlLoader=new FXMLLoader(
                            getClass().getResource("/client/ClientFXMLDocument.fxml"));
        
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(ClientFXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    TokenizeBankCardsServerInterface server;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblPassword;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    private Button btnLogIn;

    @FXML
    private TextField txtConnection;

    @FXML
    private Label lblCardNumber;

    @FXML
    private Label lblToken;

    @FXML
    private TextField txtCardNumber;

    @FXML
    private TextField txtToken;

    @FXML
    private Button btnRegisterToken;

    @FXML
    private Button btnGetBankCard;

    @FXML
    private Button btnQuit;

    //if client with no rights to get a bank card by token clicks this,returns a message alerting the client
    //if token doesnt match the regular expression returns a message for invalid input
    //invoke the method of the server interface
    //if it returns a null that means there is no such card with this token
    //else returns the card number matching this token
    @FXML
    void btnGetBankCardClicked(ActionEvent event) {
        if (!txtToken.isEditable()) {
            JOptionPane.showMessageDialog(null, "You dont have rights to read Card Numbers!");
            return;
        }
        
        String token = txtToken.getText();
        if(!token.matches("\\d{16}"))
        {
            JOptionPane.showMessageDialog(null,"You havent entered a proper token number");
            return;
        }
        String cardNumber;
        try {
            cardNumber = server.getCardNumber(token);
            if (cardNumber == null) {
                JOptionPane.showMessageDialog(null, "There is no card with this token!");
            }
            else
                JOptionPane.showMessageDialog(null,"Received card number:"+cardNumber);

        } catch (RemoteException ex) {
            Logger.getLogger(ClientFXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    //when a new client tries to log in set the cardNumber and Token fields not editable(we dont know what rights
    //will the new user have
    //invokes the login method of the server interface
    //if either of the fields username or password is empty returns null a message saying the input is invalid
    //if the invokation method returns a user,login is successful and set the cardNumber and token fields editable
    //according to the right the current user has
    @FXML
    void btnLogInClicked(ActionEvent event) {
        txtCardNumber.setEditable(false);
        txtToken.setEditable(false);
        User result = null;
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            result=server.checkLogIn(username, password);
            txtUsername.clear();
            txtPassword.clear();
            txtCardNumber.clear();
            txtToken.clear();
            if (result!=null) {
                
                JOptionPane.showMessageDialog(null,"Connection successful");
                
                txtConnection.setText("You are logged in as "+result.getUsername());
                if(result.getRights().equals(Rights.BOTH))
                {
                    txtCardNumber.setEditable(true);
                    txtCardNumber.setVisible(true);
                    
                    txtToken.setEditable(true);
                    txtToken.setVisible(true);
                    
                    lblToken.setVisible(true);
                    lblCardNumber.setVisible(true);
                    
                    btnGetBankCard.setVisible(true);
                    btnRegisterToken.setVisible(true);
                    
                }
                if(result.getRights().equals(Rights.CANREAD))
                {
                    txtCardNumber.setEditable(false);
                    txtCardNumber.setVisible(false);
                    lblCardNumber.setVisible(false);
                    btnGetBankCard.setVisible(true);
                    
                    btnRegisterToken.setVisible(false);
                    txtToken.setVisible(true);
                    lblToken.setVisible(true);
                    txtToken.setEditable(true);
                    
                }
                if(result.getRights().equals(Rights.CANREGISTER))
                {
                    txtToken.setVisible(false);
                    txtToken.setEditable(false);
                    lblToken.setVisible(false);
                    btnRegisterToken.setVisible(true);
                    
                    btnGetBankCard.setVisible(false);
                    txtCardNumber.setEditable(true);
                    txtCardNumber.setVisible(true);
                    lblCardNumber.setVisible(true);
                    
                }
            } else {
                JOptionPane.showMessageDialog(null,"connection failed");
                txtConnection.setText("Connection failed");
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ClientFXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //upon clicking quit closes the application
    @FXML
    void btnQuitClicked(ActionEvent event) {
        System.exit(0);
    }

    //if the field with the card number is not editable returns an alert message(either noone is logged or the current
    //client doesnt have rights to register tokens
    //use regular expression to validate the input-should be 16 digits starting with a digit between 3 and 6
    //invoke the registerToken method of the server interface
    //clear the cardNumber and token fields
    //returns a message with the result
    //if registered new token-add it to the database and update the ListView in the server
    @FXML
    void btnRegisterTokenClicked(ActionEvent event) {
        if (!txtCardNumber.isEditable()) {
            JOptionPane.showMessageDialog(null, "You dont have rights to register tokens");
            return;
        }
        String token = null;
        String cardNumber = txtCardNumber.getText();
        
        if(!cardNumber.matches("[3-6]\\d{15}"))
        {
            JOptionPane.showMessageDialog(null,"Invalid input!Try again");
            return;
        }
        try {
            token=server.registerToken(cardNumber);
            txtCardNumber.clear();
            txtToken.clear();
            if(token==null)
            {
                JOptionPane.showMessageDialog(null, "Invalid card number!Try again");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Received token:"+token);
                
            }
        } catch (RemoteException ex) {
            //Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    //upon opening a new Client set the fields of the card number and the token not editable because noone is logged
    //initializes the server interface by looking for an rmi object on the passed port
    @FXML
    void initialize() {

        txtConnection.setEditable(false);
        txtToken.setVisible(false);
        txtToken.setEditable(false);
        
        txtCardNumber.setVisible(false);
        txtCardNumber.setEditable(false);
        
        
        btnRegisterToken.setVisible(false);
        btnGetBankCard.setVisible(false);
        
        lblToken.setVisible(false);
        lblCardNumber.setVisible(false);
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (TokenizeBankCardsServerInterface) registry.lookup("TokenizeBankCardsServerInterfaceImp");
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(ClientFXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }


        assert lblUsername != null : "fx:id=\"lblUsername\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert lblPassword != null : "fx:id=\"lblPassword\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert txtPassword != null : "fx:id=\"txtPassword\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert txtUsername != null : "fx:id=\"txtUsername\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert btnLogIn != null : "fx:id=\"btnLogIn\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert txtConnection != null : "fx:id=\"txtConnection\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert lblCardNumber != null : "fx:id=\"lblCardNumber\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert lblToken != null : "fx:id=\"lblToken\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert txtCardNumber != null : "fx:id=\"txtCardNumber\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert txtToken != null : "fx:id=\"txtToken\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert btnRegisterToken != null : "fx:id=\"btnRegisterToken\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert btnGetBankCard != null : "fx:id=\"btnGetBankCard\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert btnQuit != null : "fx:id=\"btnQuit\" was not injected: check your FXML file 'FXMLDocument.fxml'.";

    }
}
