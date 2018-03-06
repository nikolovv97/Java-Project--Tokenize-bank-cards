/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TokenizeBankCards;

/**
 *
 * @author t430s
 */
public enum Rights {
    CANREAD("CanRead"),
    CANREGISTER("CanRegister"),
    BOTH("Both"),
    NONE("None");
    
    private final String right;
    private Rights(String right)
    {
        this.right=right;
    }
}
//This enum serves to show what can a user do-register tokens of a card,read a card number,both or none
