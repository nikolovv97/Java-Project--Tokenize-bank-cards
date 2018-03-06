package server;

import TokenizeBankCards.Rights;
import TokenizeBankCards.TokenizeBankCardsServerInterfaceImp;
import TokenizeBankCards.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;

public class ServerFXMLDocumentController extends TabPane{

    public ServerFXMLDocumentController()
    {
        FXMLLoader fxmlLoader =new FXMLLoader(
                                    getClass().getResource("/server/ServerFXMLDocument.fxml"));
        
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(ServerFXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private TokenizeBankCardsServerInterfaceImp server;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private CheckBox chkCanRegisterToken;

    @FXML
    private CheckBox chkCanGetCard;

    @FXML
    private Button btnRegisterNewUser;

    @FXML
    private Button btnDeleteUser;

    @FXML
    private ListView lstUsers;

    @FXML
    private ListView lstCards;

    @FXML
    private Button btnSortByTokens;

    @FXML
    private Button btnSortByCards;
    
    @FXML
    private Button btnQuit;

    
    //deletes user from the database and updates the listview
    //if nothing is selected returns a message
    @FXML
    void btnDeleteUserClicked(ActionEvent event) {
            int inx=lstUsers.getSelectionModel().getSelectedIndex();
            if(inx!=-1)
            {
                User newUser=(User) lstUsers.getSelectionModel().getSelectedItem();
                //lstUsers.getItems().remove(inx);
                server.deleteUser(newUser);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "You havent selected anything");
            }
            
    }

    //tries to register a new user
    //with rights according to the check box
    //if input is invalid returns a message
    //if user is already registered inputs a message
    //if registration successful returns a message and updates Table
    @FXML
    void btnRegisterNewUserClicked(ActionEvent event) {
        
        String username=txtUsername.getText();
        String password=txtPassword.getText();
        if(username.equals("") || password.equals(""))
        {
            JOptionPane.showMessageDialog(null,"Username or Password not inputed!");
            return;
        }
        Rights rights=Rights.NONE;
        if(chkCanRegisterToken.isSelected())rights=Rights.CANREGISTER;
        if(chkCanGetCard.isSelected())
        {
            if(rights.equals(Rights.CANREGISTER))rights=Rights.BOTH;
            else rights=Rights.CANREAD;
        }
        User newUser=new User(username,password);
        newUser.setRights(rights);
        if(server.registerNewUser(newUser))
        {   
            JOptionPane.showMessageDialog(null,"Successfuly registered a new user");
            //server.updateUsersTable();
        }
        
        else
        {
            JOptionPane.showMessageDialog(null,"User already registered");
        }
        txtUsername.clear();
        txtPassword.clear();
        chkCanRegisterToken.setSelected(false);
        chkCanGetCard.setSelected(false);
        
    }

    //outputs elements in the listview in a text file sorted by card numbers
    @FXML
    void btnSortByCardsClicked(ActionEvent event) {
        server.sortByCardsInTxt();
        JOptionPane.showMessageDialog(null,"Outputed in a txt file successful");
    }
    //outputs elements in the listview in a text file sorted by card numbers
    @FXML
    void btnSortByTokensClicked(ActionEvent event) {
        server.sortByTokensInTxt();
        JOptionPane.showMessageDialog(null,"Outputed in a txt file successful");
    }
    
    //upon quiting serialize the database in xml files
    @FXML
    void btnQuitClicked(ActionEvent event) {
        server.closeServer();
        System.exit(0);
    }
    
    //sets the ListViews not editable
    //creates references for the userList and the tokensList and the properties for them locally just to bind them
    //to the listviews
    //initializes the server and registeres it
    //pass the lists and the properties of the server to the newly created references
    //binds the listviews with the lists
    @FXML
    void initialize()  {
        lstUsers.setEditable(false);
        lstCards.setEditable(false);
        
        
        Registry registry;
        try {
            server=new TokenizeBankCardsServerInterfaceImp();
            registry = LocateRegistry.createRegistry(1099);
            registry.rebind("TokenizeBankCardsServerInterfaceImp", server);
        } catch (RemoteException | FileNotFoundException ex) {
            Logger.getLogger(ServerFXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        server.bindProperties(lstUsers, lstCards);
        server.updateUsersTable();
        server.updateTokensTable();
            
        
        assert txtUsername != null : "fx:id=\"txtUsername\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert txtPassword != null : "fx:id=\"txtPassword\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert chkCanRegisterToken != null : "fx:id=\"chkCanRegisterToken\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert chkCanGetCard != null : "fx:id=\"chkCanGetCard\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert btnRegisterNewUser != null : "fx:id=\"btnRegisterNewUser\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert btnDeleteUser != null : "fx:id=\"btnDeleteUser\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert lstUsers != null : "fx:id=\"lstUsers\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert lstCards != null : "fx:id=\"lstCards\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert btnSortByTokens != null : "fx:id=\"btnSortByTokens\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert btnSortByCards != null : "fx:id=\"btnSortByCards\" was not injected: check your FXML file 'FXMLDocument.fxml'.";

    }
}
