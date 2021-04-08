package boot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


public class Deck {
    private LinkedList<Card> cards;
    private int playerNum;

    public Deck(int playerNum) {
        this.playerNum = playerNum;
        cards = new LinkedList<Card>();
    }

    //配置牌组
    public void prepareDeck() {
        //准备eeTheFuture与Nope
        Card.Function[] functionWithFiveCards = new Card.Function[]{
                Card.Function.SeeTheFuture,
                Card.Function.Nope
        };
        //将SeeTheFuture与Nope加入牌组，每种五张
        for (Card.Function function : functionWithFiveCards) {
            for (int j = 0; j < 5; j++) {
                cards.addLast(new Card(function, Card.Cat.NotCat));
            }
        }

        //将其余功能牌加入牌组，每种四张
        Card.Function[] functionWithFourCards = new Card.Function[]{
                Card.Function.Attack,
                Card.Function.Skip,
                Card.Function.Shuffle,
                Card.Function.Favor
        };
        for (Card.Function function : functionWithFourCards) {
            for (int j = 0; j < 4; j++) {
                cards.add(new Card(function, Card.Cat.NotCat));
            }
        }

        //将所有普通猫加入牌组，各有四张
        Card.Cat[] cats = new Card.Cat[]{
                Card.Cat.BeardCat,
                Card.Cat.Cattermelon,
                Card.Cat.HairyPotatoCat,
                Card.Cat.TacoCat,
                Card.Cat.RainbowRalphingCat
        };

        for (Card.Cat cat : cats) {
            for (int j = 0; j < 4; j++) {
                //将其余功能牌(除Defuse/ExplodingKitten)加入牌组，每种四张
                cards.add(new Card(Card.Function.NotFunction, cat));
            }
        }

        //将多余的命加入牌组
        addDefuse();
    }

    //洗牌
    public void shuffle() {
        Collections.shuffle(cards);
    }

    //添加炸弹
    public void addBomb() {
        Card bomb = new Card(Card.Function.NotFunction, Card.Cat.ExplodingKitten);
        for (int i = 0; i < playerNum - 1; i++) {
            cards.add(bomb);
        }
    }

    //将多余的命加入牌组
    public void addDefuse() {
        //Defuse总共只有6张,计算发给玩家后剩余Defuse数量
        int defuseLeft = 6 - playerNum;

        for (int i = 0; i < defuseLeft; i++) {
            cards.add(new Card(Card.Function.Defuse, Card.Cat.NotCat));
        }
    }

    //从牌堆顶抽取一张牌
    public Card drawCard() {
        return cards.removeFirst();
    }


    //开局每人发4张牌
    public Card[] dealCard(int a) {
        if (a != 4) {
            throw new IllegalArgumentException("Must deal 4 cards at the beginning");
        }
        Card[] ret = new Card[a];
        for (int i = 0; i < a; i++) {
            ret[i] = cards.removeLast();
        }
        return ret;
    }

    //用于获取牌堆上方n张牌的牌面信息
    public ArrayList<Card> getTopCards(int n) {
        ArrayList<Card> ret = new ArrayList();

        //如果牌堆内剩余牌的数量少于n张牌，则获取牌堆内全部牌面信息
        if (cards.size() < n) {
            for (int i = 1; i <= cards.size(); i++) {
                ret.add(cards.get(cards.size() - i));
            }
        } else {
            for (int i = 0; i < n; i++) {
                ret.add(cards.get(i));
            }
        }
        return ret;
    }


    //将炸弹按指定位置插入牌堆
    public void insertBomb(Card bomb, int index) {
        cards.add(index, bomb);
    }

    public int sizeOf(){
        return cards.size();
    }

    @Override
    public String toString() {
        return "deck: " + cards + cards.size() + " cards in the deck";
    }
}
