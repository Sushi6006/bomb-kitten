package card;

import player.Player;

public abstract class Card {
    public String name;
    public Player owner;

    public abstract void beGotten();
    public abstract void bePlayed();

    public Card(Player owner){
        this.owner = owner;
    }
}
