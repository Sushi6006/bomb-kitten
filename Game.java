package boot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class Game {
    private int currentPlayerIndex; //当前回合玩家index
    private ArrayList<String> playerIds; //存储玩家id
    private Deck deck;
    private ArrayList<ArrayList<Card>> playerHand; //所有玩家手牌
    private LinkedList<Card> stockpile; //弃牌堆
    private boolean underAttack; //记录当前玩家是否被上家Attack
    private int bombNeededBack; //用于记录已翻开(被defuse抵消掉的)却未爆炸的炸弹数


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

        //记录玩家是否被上一位玩家attack
        underAttack = false;

        //记录当前回合有多少炸弹需要被放回牌堆
        bombNeededBack = 0;

        //由第0位玩家开始游戏
        currentPlayerIndex = 0;

        //存储所有玩家手牌
        playerHand = new ArrayList<ArrayList<Card>>();

        //洗牌后发给每名玩家4张牌并发一张Defuse
        deck.shuffle();
        for (int i = 0; i < pids.size(); i++) {
            ArrayList<Card> hand = new ArrayList<Card>(Arrays.asList(deck.dealCard(4)));
            hand.add(new Card(Card.Function.Defuse, Card.Cat.NotCat));
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


    //用于判断牌中是否有炸弹
    public boolean gotBombed(ArrayList<Card> card) {
        return card.contains(Card.Cat.ExplodingKitten);
    }


    //玩家摸牌动作
    public void playerDraw(String pid) throws InvalidPlayerTurnException {
        //检查是否为当前玩家回合
        checkPlayerTurn(pid);

        //检查牌堆顶一张牌是否是炸弹，如果是炸弹则判断玩家手牌中是否有Defuse
        if (gotBombed(deck.getTopCards(1))) {
            //获取当前回合玩家手牌信息
            ArrayList<Card> hand = getPlayerHand(pid);

            //如果当前玩家手牌中有Defuse则删除一张手牌中的Defuse并放入弃牌堆
            if (hand.contains(Card.Function.Defuse)) {
                hand.remove(Card.Function.Defuse);
                stockpile.addLast(new Card(Card.Function.Defuse, Card.Cat.NotCat));

                //将牌堆最上方的炸弹移除牌堆
                deck.drawCard();

                //未爆弹计数器增加一
                bombNeededBack++;

                //如果该玩家在当前回合被攻击，牌堆顶部为炸弹且有defuse，则先将炸弹移出牌堆,将玩家手牌中的一张Defuse丢入弃牌堆，继续回合
                if (underAttack) {
                    deck.drawCard();
                    hand.remove(Card.Function.Defuse);
                    stockpile.addLast(new Card(Card.Function.Defuse, Card.Cat.NotCat));
                    underAttack = false;

                } else {
                    //将已翻开未爆炸的炸弹全部放回牌堆
                    for (int i = 0; i < bombNeededBack; i++) {
                        deck.insertBomb(new Card(Card.Function.NotFunction, Card.Cat.ExplodingKitten), 0);

                    }

                    //将未爆炸炸弹放入牌堆后，计数器清零
                    bombNeededBack = 0;

                    //移动当前玩家指针至下一位玩家
                    currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();
                }
            } else {
                //将牌堆最上方的炸弹移除牌堆
                deck.drawCard();

                //玩家被炸死，将该玩家id从游戏中移除
                playerIds.remove(pid);
                currentPlayerIndex = currentPlayerIndex % playerIds.size();

                //提示当前玩家出局，这个功能需要看gui怎么操作再来完善
                System.out.println(pid + "被炸死了！");
            }
        } else {
            //如果该玩家在当前回合被攻击，牌堆顶部不是炸弹，则摸一张牌后继续回合
            if (underAttack) {
                getPlayerHand(pid).add(deck.drawCard());
                underAttack = false;
            } else {
                //如果顶部一张牌不是炸弹，则玩家从牌堆顶部抽取一张牌，结束当前回合
                getPlayerHand(pid).add(deck.drawCard());
                //移动当前玩家指针至下一位玩家
                currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();

                //将已翻开未爆炸的炸弹全部放回牌堆
                for (int i = 0; i < bombNeededBack; i++) {
                    deck.insertBomb(new Card(Card.Function.NotFunction, Card.Cat.ExplodingKitten), 0);

                }
            }
        }
    }

    //玩家出牌动作

    //如玩家打出的手牌为SeeTheFuture,则返回牌堆顶部三张牌的牌面信息
    //如玩家打出的手牌为Nope，则无效化其他玩家所打出的牌(这tm该怎么实现。。。)

    //若玩家打出的牌为Favor，则玩家指定一名其他玩家给其一张牌
    //若玩家打出的牌为两张相同的普通猫组合，则指定一名玩家抽取其一张手牌
    //若玩家打出的牌为三张相同的普通猫组合，则指定一名玩家所要一张手牌，牌面由该玩家指定
    //若玩家打出的牌为五张不同的普通猫组合，则可从弃牌堆内拿曲任意一张牌

    public void submitPlayerCard(String pid, ArrayList<Card> cardPlay)
            throws InvalidPlayerTurnException, InvalidCardPlayException {
        //检查是否为当前玩家回合，nope的case需要单独考虑因为任何玩家都可以在任何时刻打出nope(还没有implement)
        checkPlayerTurn(pid);

        //获取当前回合玩家手牌信息
        ArrayList<Card> hand = getPlayerHand(pid);


        //如果打出的牌数量为1张，则说明打出的是功能牌
        if (cardPlay.size() == 1) {
            Card card = cardPlay.get(0);

            if (card.getFunction() == Card.Function.Shuffle) {
                //如玩家打出的牌为Shuffle，则将牌组重新洗牌
                deck.shuffle();
                stockpile.add(new Card(Card.Function.Shuffle, Card.Cat.NotCat));

            } else if (card.getFunction() == Card.Function.Skip) {
                //如果是被上家Attack的状态下打出一张Skip则underAttack状态调整为false(剩余一次摸牌可以视作结束回合时的正常摸牌)
                if (underAttack) {
                    stockpile.add(new Card(Card.Function.Skip, Card.Cat.NotCat));
                    underAttack = false;
                } else {
                    //如玩家打出的牌为Skip。则跳过自身当前回合
                    currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();
                    stockpile.add(new Card(Card.Function.Skip, Card.Cat.NotCat));
                }

            } else if (card.getFunction() == Card.Function.SeeTheFuture) {

            } else if (card.getFunction() == Card.Function.Nope) {

            } else if (card.getFunction() == Card.Function.Attack) {
                //如果打出Attack，则直接结束自身回合，强制下一位玩家连续进行两个回合
                currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();
                stockpile.add(new Card(Card.Function.Attack, Card.Cat.NotCat));

                //如打出Attack时自身没有被施加underAttack状态，则将下一位玩家的underAttack状态调整为true
                if (!underAttack) {
                    underAttack = true;
                }

            } else if (card.getFunction() == Card.Function.Favor) {

            }
        } else if (cardPlay.size() == 2) {
            //打出的牌数量为2，说明打出的是两张猫猫牌

        } else if (cardPlay.size() == 3) {


        } else if (cardPlay.size() == 5) {

        }
    }


}

//用于限制玩家在不属于自己的回合出牌
class InvalidPlayerTurnException extends Exception {
    String playerId;

    public InvalidPlayerTurnException(String message, String pid) {
        super(message);
        this.playerId = pid;
    }

    public String getPid() {
        return playerId;
    }
}

//用于判断玩家在回合中所打出的牌是否符合规则
class InvalidCardPlayException extends Exception {
    Card card;

    public InvalidCardPlayException(String message, Card card) {
        super(message);
        this.card = card;
    }

}