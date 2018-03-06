package TokenizeBankCards;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author t430s
 */
public class User implements Serializable{
    private final String username;
    private final String password;
    private Rights rights=Rights.NONE;
    public User(String username,String password)
    {
        this.username=username;
        this.password=password;
        
    }
    public final String getUsername()
    {
        return username;
    }
    public final String getPassword()
    {
        return password;
    }
    public final void setRights(Rights rights)
    {
        this.rights=rights;
    }
    public final Rights getRights()
    {
        return rights;
    }
    @Override
    public String toString()
    {
        return String.format("%s %s %s",username,password,rights);
    }
}
