package boot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class Game {
    private int currentPlayerIndex; //当前回合玩家index
    private static ArrayList<String> playerIds; //存储玩家id
    private static Deck deck;
    private static ArrayList<ArrayList<Card>> playerHand; //所有玩家手牌
    private LinkedList<Card> stockpile; //弃牌堆
    private boolean underAttack; //记录当前玩家是否被上家Attack
    private int bombNeededBack; //用于记录已翻开(被defuse抵消掉的)却未爆炸的炸弹数


    public static void main(String[] args) {
        ArrayList<String> player = new ArrayList<>();
        player.add("alex");
        player.add("bob");
        player.add("charles");
        player.add("dan");
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


        //System.out.println("player hand: " + playerHand);

        //放入炸弹，再次洗牌，游戏准备完毕。
        deck.addBomb();
        deck.shuffle();

        //System.out.println(deck);
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
        Card bomb = new Card(Card.Function.NotFunction, Card.Cat.ExplodingKitten);
        return card.contains(bomb);
    }


    //玩家摸牌动作
    public void playerDraw(String pid) {
        //检查是否为当前玩家回合
        //checkPlayerTurn(pid);

        //获取当前回合玩家手牌信息
        ArrayList<Card> hand = getPlayerHand(pid);

        //检查牌堆顶一张牌是否是炸弹，如果是炸弹则判断玩家手牌中是否有Defuse
        if (gotBombed(deck.getTopCards(1))) {

            //用于接下来判断手牌中是否拥有炸弹的参考量
            Card defuse = new Card(Card.Function.Defuse, Card.Cat.NotCat);

            //如果当前玩家手牌中有Defuse则删除一张手牌中的Defuse并放入弃牌堆
            if (hand.contains(defuse)) {
                //将玩家手中的defuse移入废牌堆
                hand.remove(defuse);
                stockpile.addLast(new Card(Card.Function.Defuse, Card.Cat.NotCat));

                //将牌堆最上方的炸弹移除牌堆
                deck.drawCard();

                //未爆弹计数器增加一
                bombNeededBack++;

                //如玩家为underAttack状态，currentPlayerIndex保持不变
                if (underAttack) {
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

                //由于被炸死玩家已被移除则直接用currentPlayerIndex与当前玩家数取模
                currentPlayerIndex = currentPlayerIndex % playerIds.size();

                //提示当前玩家出局，这个功能需要看gui怎么操作再来完善
                System.out.println(pid + "被炸死了！");
            }
        } else {
            //如果该玩家在当前回合被攻击，牌堆顶部不是炸弹，则摸一张牌后继续回合
            if (underAttack) {
                hand.add(deck.drawCard());
                underAttack = false;
            } else {
                //如果顶部一张牌不是炸弹，则玩家从牌堆顶部抽取一张牌，结束当前回合
                hand.add(deck.drawCard());
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
            throws InvalidPlayerTurnException{
        //检查是否为当前玩家回合，nope的case需要单独考虑因为任何玩家都可以在任何时刻打出nope(还没有implement)
        checkPlayerTurn(pid);

        //获取当前回合玩家手牌信息
        ArrayList<Card> pHand = getPlayerHand(pid);


        //如果打出的牌数量为1张，则说明打出的是功能牌
        if (cardPlay.size() == 1) {
            Card card = cardPlay.get(0);

            if (card.getFunction() == Card.Function.Shuffle) {
                //从玩家手牌中移除该牌
                pHand.remove(Card.Function.Shuffle);

                //如玩家打出的牌为Shuffle，则将牌组重新洗牌
                deck.shuffle();

                //将该牌加入弃牌堆
                stockpile.add(new Card(Card.Function.Shuffle, Card.Cat.NotCat));

            } else if (card.getFunction() == Card.Function.Skip) {
                pHand.remove(Card.Function.Skip);
                //如果是被上家Attack的状态下打出一张Skip则underAttack状态调整为false(剩余一次摸牌可以视作结束回合时的正常摸牌)
                if (underAttack) {
                    stockpile.add(new Card(Card.Function.Skip, Card.Cat.NotCat));
                    underAttack = false;
                } else {
                    //如非underAttack状态下玩家打出的牌为Skip。则跳过自身当前回合
                    currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();
                    stockpile.add(new Card(Card.Function.Skip, Card.Cat.NotCat));
                }

            } else if (card.getFunction() == Card.Function.SeeTheFuture) {
                pHand.remove(Card.Function.SeeTheFuture);

                //获取牌堆顶部三张牌面信息
                ArrayList<Card> topThreeCards = deck.getTopCards(3);

                //需要在gui向玩家输出三张牌的牌面信息
                System.out.println(topThreeCards);

            } else if (card.getFunction() == Card.Function.Nope) {
                pHand.remove(Card.Function.Nope);
                

            } else if (card.getFunction() == Card.Function.Attack) {
                pHand.remove(Card.Function.Attack);

                //如果打出Attack，则直接结束自身回合，强制下一位玩家连续进行两个回合
                currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();
                stockpile.add(new Card(Card.Function.Attack, Card.Cat.NotCat));

                //如打出Attack时自身没有被施加underAttack状态，则将下一位玩家的underAttack状态调整为true
                if (!underAttack) {
                    underAttack = true;
                }

            } else if (card.getFunction() == Card.Function.Favor) {
                pHand.remove(Card.Function.Favor);

                //后期需要改成玩家输入的值
                String targetPlayer = "alex";

                //获取该目标玩家手牌信息
                ArrayList<Card> targetHand = getPlayerHand(targetPlayer);

                //后期需要改成目标玩家自选的牌的index
                int targetChoice = 0;

                //从目标玩家处获取一张目标玩家自选手牌加入自身牌组
                pHand.add(targetHand.remove(targetChoice));

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

