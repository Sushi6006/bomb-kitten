package boot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Game {
    private int currentPlayerIndex; //当前回合玩家index
    private ArrayList playerIds; //存储玩家id
    private Deck deck;
    private ArrayList<ArrayList<Card>> playerHand; //所有玩家手牌
    private LinkedList<Card> stockpile; //弃牌堆

    public static void main(String[] args) {
        ArrayList<String> player = new ArrayList<>();
        player.add("alex");
        player.add("bob");
        player.add("charles");
        player.add("dan");
        System.out.println(player);
        Game game = new Game(player);
    }

    public Game(ArrayList<String> pids) {
        //准备牌组
        deck = new Deck(pids.size());
        deck.prepareDeck();

        //准备弃牌堆
        stockpile = new LinkedList<>();

        //记录所有玩家id
        playerIds = pids;

        //由第0位玩家开始游戏
        currentPlayerIndex = 0;

        //存储所有玩家手牌
        playerHand = new ArrayList<ArrayList<Card>>();

        //洗牌后发给每名玩家4张牌并发一张Defuse
        deck.shuffle();
        for (int i = 0; i < pids.size(); i++) {
            ArrayList<Card> hand = new ArrayList<Card>(Arrays.asList(deck.dealCard(4)));
            hand.add(new Card(Card.Function.Defuse,Card.Cat.NotCat));
            playerHand.add(hand);
        }

        //放入炸弹，再次洗牌，游戏准备完毕。
        deck.addBomb();
        deck.shuffle();
    }

    //当存活玩家为1名时游戏结束
    public boolean isGameOver() {
        return playerIds.size() == 1;
    }

    //获取当前回合玩家信息
    public String getCurrentPlayer() {
        return (String) this.playerIds.get(this.currentPlayerIndex);
    }


    //获取全部玩家信息
    public ArrayList<String> getPlayers() {
        return playerIds;
    }

    //获取玩家手牌信息
    public ArrayList<Card> getPlayerHand(String pid) {
        int index = playerIds.indexOf(pid);
        return playerHand.get(index);
    }

    //获取玩家手牌数
    public int getPlayerHandSize(String pid) {
        return getPlayerHand(pid).size();
    }

    //检查玩家是否还有手牌
    public boolean hasEmptyHand(String pid) {
        return getPlayerHand(pid).isEmpty();
    }

    //检查是否轮到该玩家回合,还需要完善打出nope的case
    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException {
        if (this.playerIds.get(this.currentPlayerIndex) != pid) {
            throw new InvalidPlayerTurnException("It is not " + pid + " 's turn", pid);
        }
    }

    public boolean gotBombed(ArrayList<Card> card){
        return card.contains(Card.Cat.ExplodingKitten);
    }

    //玩家摸牌动作
    public void playerDraw(String pid) throws InvalidPlayerTurnException{
        //检查是否为当前玩家回合
        checkPlayerTurn(pid);


        //检查牌堆顶一张牌是否是炸弹，如果是炸弹则判断玩家手牌中是否有Defuse
        if(gotBombed(deck.getTopCards(1))){
            //获取当前回合玩家手牌信息
            ArrayList<Card> hand = getPlayerHand(pid);

            //如果当前玩家手牌中有Defuse则删除一张手牌中的Defuse并放入弃牌堆
            if(hand.contains(Card.Function.Defuse)){
                hand.remove(Card.Function.Defuse);
                stockpile.addLast(new Card(Card.Function.Defuse, Card.Cat.NotCat));
                currentPlayerIndex = (currentPlayerIndex +1) % playerIds.size();
            }else{
                //玩家如果被炸死，判断当前玩家是否是最末位index，如果是则将当前玩家index调整为0，不是末尾则当前玩家index保持不动
                if (currentPlayerIndex == playerIds.size()-1){
                    currentPlayerIndex = 0;
                }

                //玩家被炸死，将该玩家id从游戏中移除
                playerIds.remove(pid);

                //提示当前玩家出局
            }
        }else{
            getPlayerHand(pid).add(deck.drawCard());
            //移动当前玩家指针至下一位玩家

        }
    }
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
