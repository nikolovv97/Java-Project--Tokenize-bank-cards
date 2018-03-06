package TokenizeBankCards;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author t430s
 */

//implementation of the server(client will be able to use only overriden methods while server will be able to use all of the methods)
public final class TokenizeBankCardsServerInterfaceImp extends UnicastRemoteObject
       implements TokenizeBankCardsServerInterface{
    private final  List<Tokenizer> tokensList;//List containing the tokenizers(serialized in a xml file when quitting the server)
    private final List<User> userList;//List containing the users(serialized in a xml file when quitting the server)
    private final XStream xstream;//use the Xstream library to serialize objects into xml files
    private final Random generator;
    private static final File CARDSFILE=new File("cards.xml");//File in which we serialize the tokenizers
    private static final File USERSFILE=new File("users.xml");//File in which we serialize the users
    
    
    //a property to bind the ListView of the users in the GUI to the userList
    private final ListProperty<User> userListProperty=new SimpleListProperty();
    //a property to bind the ListView of the users in the GUI to the tokensList
    private final ListProperty<Tokenizer> tokensListProperty=new SimpleListProperty();
    
    //bind properties
    public void bindProperties(ListView<User> lstUsers,ListView<Tokenizer> lstCards)
    {
        lstUsers.itemsProperty().bind(userListProperty);
        lstCards.itemsProperty().bind(tokensListProperty);
    }
    //constructor-upon creating a new server take the data from the CARDSFILE and the USERFILE
    //If this is the first time a server is being run-create new Lists
    public TokenizeBankCardsServerInterfaceImp()throws RemoteException,FileNotFoundException{
        generator=new Random();
        xstream=new XStream();
        
        if(CARDSFILE.exists())
            tokensList=(List<Tokenizer>) xstream.fromXML(CARDSFILE);
        else tokensList=new ArrayList<>();

        if(USERSFILE.exists())
            userList=(List<User>) xstream.fromXML(USERSFILE);
        else userList=new ArrayList<>();
    }
    
    //deletes user and updates ListView of the GUI
    public void deleteUser(User user)
    {
        userList.remove(user);
        updateUsersTable();
    }
    
    //method used inside 
    private boolean validateCard(String card)
    {
        
        if(card==null || card.length()!=16)return false;
        
        char []arr =card.toCharArray();
        if(arr[0]!='3' && arr[0]!='4' &&
                arr[0]!='5' && arr[0]!='6')return false;
        int sum=0;
        int digit=0;
        
        for(int i=0;i<16;i++)
        {
            if(arr[i]<48 || arr[i]>57)return false;
            digit=(int)arr[i]-48;
            if(i%2==0)
            {
                digit*=2;
                if(digit>9)digit-=9;
                
            }
            sum+=digit;
        }
        
        return sum%10==0;
    }
    
    //used when clicking the button Register a new User
    public boolean registerNewUser(User user)
    {
        if (userList.stream().anyMatch(x->x.getUsername().equals(user.getUsername())))
        {
            return false;
        }
        //if able to correct - add the new user to the database
        userList.add(user);
        updateUsersTable();
        
        return true;
    }
    
    //upon closing serialize the two lists in the XML files
    public void closeServer()
    {
        Formatter output;
        try {
             output=new Formatter(CARDSFILE);
             output.format(xstream.toXML(tokensList));
             output.flush();
             
             output=new Formatter(USERSFILE);
             output.format(xstream.toXML(userList));
             output.flush();
             output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TokenizeBankCardsServerInterfaceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    //use these two function whenever the database is being changed.It makes the ListViews change too
    //updates the tokens ListView
    public void updateTokensTable()
    {
        tokensListProperty.set(FXCollections.observableArrayList(tokensList)); 
    }
    //updates the user ListView
    public void updateUsersTable()
    {
        userListProperty.set(FXCollections.observableArrayList(userList)); 
    }
    
    //Serialize in txt file the list with tokens and cards sorted by tokens
    public void sortByTokensInTxt()
    {
       List<String> sorted=tokensList.stream().sorted(Comparator.comparing(Tokenizer::getToken)).
               map(tokenizer->tokenizer.toString()).collect(Collectors.toList());
        try {
            Files.write(Paths.get("sortByTokens.txt"),sorted,Charset.defaultCharset());
        } catch (IOException ex) {
            Logger.getLogger(TokenizeBankCardsServerInterfaceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Serialize in txt file the list with tokens and cards sorted by cards
    public void sortByCardsInTxt()
    {
        List<String> sorted=tokensList.stream().sorted(Comparator.comparing(Tokenizer::getCard)).
               map(tokenizer->tokenizer.toString()).collect(Collectors.toList());
        try {
            Files.write(Paths.get("sortByCards.txt"),sorted,Charset.defaultCharset());
        } catch (IOException ex) {
            Logger.getLogger(TokenizeBankCardsServerInterfaceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Client invocation method.Used whenever you try to register a new token of a Card
    //If new card is registered,update the ListView in the server GUI
    @Override
    public String registerToken(String card)throws RemoteException
    {
        if(!validateCard(card))
        {
            return null;
        }
        int arr[]=new int[16];
        int cardArr[]=new int[16];
        for(int i=0;i<card.length();i++)
        {
            cardArr[i]=(int)card.charAt(i)-48;
        }
        
        int generated=3;
        int sum=0;
        while(true)
        {    
            while(generated>=3 && generated<=6)
            {
                generated=generator.nextInt(10);
            }
            arr[0]=generated;
            sum+=arr[0];
            for(int i=1;i<12;i++)
            {
                generated=cardArr[i];
                while(generated==cardArr[i])
                {
                    generated=generator.nextInt(10);
                }
                arr[i]=generated;
                sum+=arr[i];
            }
            for(int i=12;i<16;i++)
            {
               arr[i]=cardArr[i];
               sum+=arr[i];
            }
            generated=arr[11];
            while(sum%10==0)
            {
                sum-=generated;
                generated=generator.nextInt(10);
                sum+=generated;
            }
            arr[11]=generated;
            String token="";
            for(int i=0;i<16;i++)
            {
                token+=(char)(arr[i]+'0');
            }
            final String result=token;
            if(tokensList.stream().noneMatch(x->x.getToken().equals(result)))
            {
                Tokenizer newToken=new Tokenizer(card,token);
                tokensList.add(newToken);
                updateTokensTable();
                return token;
            }
        }
            //if valid add the new tokenizer
    }
    
    
    //second invokation method used whenever the client wants to receive a card number by passing a token number
    @Override
    public String getCardNumber(String token)throws RemoteException
    {
        Tokenizer current;
        ListIterator<Tokenizer> it= tokensList.listIterator();
        while(it.hasNext())
        {
            current=it.next();
            if(token.equals(current.getToken()))return current.getCard();
        }
        
        return null;
    }
    
    
    //third invokation method used whenever the client tries to log in  
    @Override
    public User checkLogIn(String username,String password)throws RemoteException
    {
        Iterator<User> it =userList.stream().
                filter(x->x.getUsername().equals(username) && x.getPassword().equals(password)).
                iterator();
        if(it.hasNext())
        {
            return it.next();
        }
        
        return null;   
    }
}
