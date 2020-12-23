package player;
import card.Card;
import java.util.ArrayList;

public abstract class Player {

    /*player's status*/
    private boolean isDead;
    private int diffuse;
    //name of player
    private String name;

    /*attributes about cards*/
    //player
    private ArrayList<Card> cards;

    /*abstract classes*/
    public abstract void getCard();
    public abstract void playCard();
    //main method of players, which update player's status per frame
    public abstract void run();

    public Player(){
        //set original diffuse as 1
        this.diffuse = 1;
    }


    //getter and setter
    public boolean isDead() {
        return isDead;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void minusDiffuse() {
        this.diffuse --;
    }

    public void plusDiffuse(){
        this.diffuse ++;
    }
}
