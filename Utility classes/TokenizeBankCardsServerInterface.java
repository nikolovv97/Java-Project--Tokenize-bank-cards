package TokenizeBankCards;


import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author t430s
 */

//The interface of the server with methods which the client will be able to invoke 
public interface TokenizeBankCardsServerInterface extends Remote{
    String registerToken(String card)throws RemoteException;
    
    String getCardNumber(String token)throws RemoteException;
    
    User checkLogIn(String username,String password)throws RemoteException;
   
}
