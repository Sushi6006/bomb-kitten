package boot;

import Uno.UnoCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Deck {
    private LinkedList<Card> cards;
    private int playerNum;

    public Deck(int playerNum) {
        this.playerNum = playerNum;
    }

    //配置牌组
    public void prepareDeck(){

        System.out.println("开始配置牌组");

        //将SeeTheFuture与Nope加入牌组，每种五张
        Card.Function[] functionWithFiveCards = new Card.Function[]{
                Card.Function.SeeTheFuture,
                Card.Function.Nope
        };
        for (Card.Function function : functionWithFiveCards) {
            for (int j = 0; j < 5; j++) {
                cards.addLast(new Card(function,null));
            }
        }
        System.out.println("testing");
        //将其余功能牌加入牌组，每种四张
        Card.Function[] functionWithFourCards = new Card.Function[]{
                Card.Function.Attack,
                Card.Function.Skip,
                Card.Function.Shuffle,
                Card.Function.Favor
        };
        for (Card.Function function : functionWithFourCards) {
            for (int j = 0; j < 4; j++) {
                cards.add(new Card(function,null));
            }
        }

        //将所有普通猫加入牌组，各有四张
        Card.Cat[] cats = Card.Cat.values();
        for (Card.Cat cat : cats) {
            for (int j = 0; j < 4; j++) {
                //将其余功能牌(除Defuse/ExplodingKitten)加入牌组，每种四张
                cards.add(new Card(null, cat));

            }
        }

        //将多余的命加入牌组
        addDefuse();

        System.out.println("配置牌组成功");
    }

    //洗牌
    public void shuffle(){
        Collections.shuffle(cards);
    }

    //添加炸弹
    public void addBomb(){
        Card bomb = new Card(null,Card.Cat.ExplodingKitten);
        for (int i = 0; i < playerNum-1; i++) {
            cards.add(bomb);
        }
    }

    //将多余的命加入牌组
    public void addDefuse(){
        //Defuse总共只有6张,计算发给玩家后剩余Defuse数量
        int defuseLeft = 6 - playerNum;

        for (int i = 0; i < defuseLeft; i++) {
            cards.add(new Card(Card.Function.Defuse,null));
        }
    }

    //从牌堆顶抽取一张牌
    public Card drawCard() {
        return cards.removeLast();
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
    public ArrayList<Card> getTopCards(int n){
        ArrayList<Card> ret = null;
        if(cards.size() < n){
            for (int i = 1; i <= cards.size(); i++) {
                ret.add(cards.get(cards.size()-i));
            }
        }else {
            for (int i = 1; i <= n; i++) {
                ret.add(cards.get(cards.size()-i));
            }
        }
        return ret;
    }



















}
