package boot;

import java.util.LinkedList;

public class Hand {
    private LinkedList<Card> cards;

    public Hand(LinkedList<Card> cards) {
        this.cards = cards;
    }

    public void shuffleHand(){
        //功能待实现
    }

    public LinkedList<Card> getCards() {
        return cards;
    }

    public void setCards(LinkedList<Card> cards) {
        this.cards = cards;
    }
}
