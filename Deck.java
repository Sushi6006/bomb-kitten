package boot;

import Uno.UnoCard;

import java.util.Collections;
import java.util.LinkedList;

public class Deck {
    private LinkedList<Cards> cards;
    private int playerNum;

    public Deck(int playerNum) {
        this.playerNum = playerNum;
    }

    //配置牌组
    public void reset(){
        //SeeTheFuture与Nope各有5张牌
        Cards.Function[] functionWithFiveCards = new Cards.Function[]{
                Cards.Function.SeeTheFuture,
                Cards.Function.Nope
        };

        for (Cards.Function function : functionWithFiveCards) {
            for (int j = 0; j < 5; j++) {
                //将SeeTheFuture与Nope加入牌组，每种五张
                cards.add(new FunctionCards(function));
            }
        }

        //其余功能牌各有四张
        Cards.Function[] functionWithFourCards = new Cards.Function[]{
                Cards.Function.Attack,
                Cards.Function.Skip,
                Cards.Function.Shuffle,
                Cards.Function.Favor
        };

        //将其余功能牌加入牌组，每种四张
        for (Cards.Function function : functionWithFourCards) {
            for (int j = 0; j < 4; j++) {
                cards.add(new FunctionCards(function));
            }
        }

        //将所有普通猫加入牌组，各有四张
        Cards.Cat[] cats = Cards.Cat.values();
        for (Cards.Cat cat : cats) {
            for (int j = 0; j < 4; j++) {
                //将其余功能牌(除Defuse/ExplodingKitten)加入牌组，每种四张
                cards.add(new CatCards(cat));

            }
        }

        //将多余的命加入牌组
        addDefuse();
    }

    //洗牌
    public void shuffle(){
        Collections.shuffle(cards);
    }

    //添加炸弹
    public void addBomb(){
        Cards bomb = new FunctionCards(Cards.Function.ExplodingKitten);

        for (int i = 0; i < playerNum-1; i++) {
            cards.add(bomb);
        }
    }

    //将多余的命加入牌组
    public void addDefuse(){
        Cards.Function[] life = new Cards.Function[]{
                Cards.Function.Defuse
        };
        //命总共只有6张
        int defuseLeft = 6 - playerNum;
        for (int i = 0; i < defuseLeft; i++) {
            cards.add(new FunctionCards(life[0]));
        }
    }

    //从牌堆顶抽取一张牌
    public Cards drawCard() {
        return cards.getLast();
    }

    //用于开局时发牌的函数
    public Cards[] drawCard(int a) {
        if (a != 4) {
            throw new IllegalArgumentException("Must draw 4 cards");
        }
        Cards[] ret = new Cards[a];
        for (int i = 0; i < a; i++) {
            ret[i] = cards.removeLast();
        }
        return ret;
    }

    //用于seeTheFuture观察并返回牌堆顶三张牌
    public LinkedList<Cards> seeTopThreeCards(){
        LinkedList<Cards> ret = null;
        if(cards.size() < 3){
            for (int i = 1; i <= cards.size(); i++) {
                ret.add(cards.get(cards.size()-i));
            }
        }else {
            for (int i = 1; i < 4; i++) {
                ret.add(cards.get(cards.size()-i));
            }
        }
        return ret;
    }



















}
