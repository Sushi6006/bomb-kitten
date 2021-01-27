package boot;

import Uno.UnoCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Game {
    private int currentPlayer; //当前回合玩家index
    private int playerLeft; //当前回合存活玩家数量
    private String[] playerIds; //存储玩家id
    private LinkedList<Cards> tempPile;
    private Deck deck;
    private ArrayList<ArrayList<Cards>> playerHand; //所有玩家手牌
    private ArrayList<Cards> stockpile; //弃牌堆

    public Game(String[] pids){
        int playerNum = pids.length;

        //准备牌组
        deck = new Deck(playerNum);
        //准备弃牌堆
        stockpile = new ArrayList<Cards>();

        playerIds = pids;
        currentPlayer = 0;

        //存储所有玩家手牌
        playerHand = new ArrayList<ArrayList<Cards>>();

        //洗牌后发给每名玩家4张牌
        deck.shuffle();
        for (int i = 0; i < pids.length; i++) {
            ArrayList<Cards> hand = new ArrayList<Cards>(Arrays.asList(deck.drawCard(4)));
            Cards defuse = new FunctionCards(Cards.Function.Defuse);
            hand.add(defuse);
            playerHand.add(hand);
        }

        //放入炸弹，再次洗牌，游戏准备完毕。
        deck.addBomb();
        deck.shuffle();
    }

    //当存活玩家为1名时游戏结束
    public boolean isGameOver(){
        return playerLeft == 1;
    }

    //获取当前回合玩家信息
    public String getCurrentPlayer(){
        return this.playerIds[this.currentPlayer];
    }

    //获取之前玩家信息
    public String getPreviousPlayer(int i) {
        int index = this.currentPlayer - i;
        if (index == -1) {
            index = playerIds.length - 1;
        }
        return this.playerIds[index];
    }

    //获取全部玩家信息
    public String[] getPlayers() {
        return playerIds;
    }

    //获取玩家手牌信息
    public ArrayList<Cards> getPlayerHand(String pid){
        int index = Arrays.asList(playerIds).indexOf(pid);
        return playerHand.get(index);
    }

    //获取玩家手牌数
    public int getPlayerHandSize(String pid){
        return getPlayerHand(pid).size();
    }

    //检查玩家是否还有手牌
    public boolean hasEmptyHand(String pid) {
        return getPlayerHand(pid).isEmpty();
    }

    //检查是否轮到该玩家回合,还需要完善打出nope的case
    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException {
        if (this.playerIds[this.currentPlayer] != pid) {
            throw new InvalidPlayerTurnException("It is not " + pid + " 's turn", pid);
        }
    }

    public void 













}

class InvalidPlayerTurnException extends Exception {
    String playerId;

    public InvalidPlayerTurnException(String message, String pid) {
        super(message);
        playerId = pid;
    }

    public String getPid() {
        return playerId;
    }
}
