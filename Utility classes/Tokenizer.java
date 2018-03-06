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


//A class representing a card number and its tokenizer
public class Tokenizer {
    private final String card;
    private final String token;
    public Tokenizer(String card,String token)
    {
        this.card=card;
        this.token=token;
    }
    public final String getToken()
    {
        return token;
    }
    public final String getCard()
    {
        return card;
    }
    @Override
    public String toString()
    {
        return String.format("card %s with %s token",card,token);
    }
}
