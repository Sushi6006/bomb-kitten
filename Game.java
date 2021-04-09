package boot;

import java.util.*;


public class Game {
    private int currentPlayerIndex; //当前回合玩家index
    private ArrayList<String> playerIds; //存储玩家id
    private Deck deck;
    private ArrayList<ArrayList<Card>> playerHand; //所有玩家手牌
    private ArrayList<Card> selectedCard; //玩家所选择打出的牌
    private ArrayList<Card> stockpile; //弃牌堆
    private boolean underAttack; //记录当前玩家是否被上家Attack
    private boolean gotNoped; //用于判断当前出牌是否被nope
    private int bombNeededBack; //用于记录已翻开(被defuse抵消掉的)却未爆炸的炸弹数


    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }

    public void gamersReady() {
        Scanner sc = new Scanner(System.in);

        System.out.print("输入玩家数量(2-4): ");
        int playerNum = sc.nextInt();

        while (playerNum < 2 || playerNum > 4) {
            System.out.print("重新输入");
            playerNum = sc.nextInt();
        }

        for (int i = 1; i <= playerNum; i++) {
            System.out.print("输入" + i + "号玩家id: ");
            this.playerIds.add(sc.next());
        }
    }

    public Game() {
        //读入玩家信息
        playerIds = new ArrayList<>();
        gamersReady();

        //准备牌组
        deck = new Deck(this.playerIds.size());
        deck.prepareDeck();

        //准备弃牌堆
        stockpile = new ArrayList<>();

        //记录玩家是否被上一位玩家attack
        underAttack = false;

        //记录当前出牌是否被nope
        gotNoped = false;

        //记录当前回合有多少炸弹需要被放回牌堆
        bombNeededBack = 0;

        //由第0位玩家开始游戏
        currentPlayerIndex = 0;

        //存储所有玩家手牌
        playerHand = new ArrayList<>();

        //记录当前回合玩家打出的牌
        selectedCard = new ArrayList<>();

        //洗牌后发给每名玩家4张牌并发一张Defuse
        deck.shuffle();
        for (int i = 0; i < this.playerIds.size(); i++) {
            ArrayList<Card> hand = new ArrayList<>(Arrays.asList(deck.dealCard(4)));
            hand.add(new Card(Card.Function.Defuse, Card.Cat.NotCat));
            playerHand.add(hand);
        }

        //放入炸弹，再次洗牌，游戏准备完毕。
        deck.addBomb();
        deck.shuffle();

    }

    public void run() {
        while (!isGameOver()) {
            System.out.println(getCurrentPlayer() + "回合");
            selectCard(getCurrentPlayer());
            playCard(getCurrentPlayer());
            System.out.println("牌组剩余" + this.deck.sizeOf() + "张牌");
            System.out.println();
        }
        System.out.println(playerIds.get(0) + "获得了胜利！");
    }

    //当存活玩家为1名时游戏结束
    public boolean isGameOver() {
        return playerIds.size() == 1;
    }

    //获取当前回合玩家信息
    public String getCurrentPlayer() {
        return this.playerIds.get(this.currentPlayerIndex);
    }


    //获取全部玩家信息
    public ArrayList<String> getPlayers() {
        return playerIds;
    }

    //获取玩家手牌信息
    public ArrayList<Card> getPlayerHand(String pid) {
        return playerHand.get(this.currentPlayerIndex);
    }

    public Card getPlayerCard(ArrayList<Card> targetHand, int index) {
        return targetHand.remove(index);
    }

    //检查玩家是否还有手牌
    public boolean hasEmptyHand(String pid) {
        return getPlayerHand(pid).isEmpty();
    }

    //检查是否轮到该玩家回合,还需要完善打出nope的case
    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException {
        if (!this.playerIds.get(this.currentPlayerIndex).equals(pid)) {
            throw new InvalidPlayerTurnException("It is not " + pid + " 's turn", pid);
        }
    }

    //用于判断牌中是否有炸弹
    public boolean gotBombed(ArrayList<Card> card) {
        return card.contains(new Card(Card.Function.NotFunction, Card.Cat.ExplodingKitten));
    }

    //判断玩家所出得牌是否为全部一样的猫猫牌
    public boolean sameCatCard(ArrayList<Card> catCard) {
        Card sample = catCard.get(0);

        //判断集合中的牌均为普通猫猫牌
        for (Card card : catCard) {
            if (!card.getFunction().equals(Card.Function.NotFunction)) {
                return false;
            }
        }

        for (int i = 1; i < catCard.size(); i++) {
            if (!sample.equals(catCard.get(i))) {
                return false;
            }
        }
        return true;
    }


    //判断玩家所出得牌是否为全部一样的猫猫牌
    public boolean differentCatCard(ArrayList<Card> catCard) {
        //判断集合中的牌均为普通猫猫牌
        for (Card card : catCard) {
            if (!card.getFunction().equals(Card.Function.NotFunction)) {
                return false;
            }
        }

        //利用HashSet不允许存储重复元素的特点来判断catCard集合中的猫猫是否都是不同的品种
        HashSet<Card> set = new HashSet<>(catCard);

        return catCard.size() == set.size();
    }

    //玩家选择要出的牌
    public void selectCard(String pid) {
        System.out.println("被攻击状态: " + this.underAttack);
        Scanner sc = new Scanner(System.in);
        int cardIndex;

        //打印玩家手牌供玩家选择
        System.out.print(pid + "手牌: " + getPlayerHand(pid).toString());
        System.out.println(" 请选择您想要打出的牌(左起第一张为1)，输入0结束选择");


        while (getPlayerHand(pid).size() > 0) {
            System.out.print("选择: ");

            //计算机判定左起第一位是0，所以玩家的输入需要手动减1
            cardIndex = sc.nextInt() - 1;

            if (cardIndex < getPlayerHand(pid).size() && cardIndex > -1) {
                this.selectedCard.add(getPlayerCard(getPlayerHand(pid), cardIndex));
            } else if (cardIndex == -1) {
                break;
            } else {
                System.out.println("手牌index越界");
            }



        }
    }


    //结算当前玩家所选择打出的牌
    public void playCard(String pid) {
        //获取当前回合玩家手牌信息
        ArrayList<Card> pHand = getPlayerHand(pid);

        if (this.selectedCard.size() == 1 && this.selectedCard.get(0).getCat().equals(Card.Cat.NotCat)) {
            //如果打出的牌数量仅为一张且为功能牌

            //获取功能牌牌面
            Card card = this.selectedCard.get(0);

            if (card.getFunction().equals(Card.Function.Shuffle)) {
                System.out.println(pid + "打出: Shuffle，牌堆洗牌");

                //在结算功能牌前先询问其他玩家是否nope
                anyOneNope(pid);

                if (!gotNoped) {
                    //如果没有被nope，则发动效果
                    //如玩家打出的牌为Shuffle，则将牌组重新洗牌
                    deck.shuffle();

                } else {
                    //如果被nope了，则重置nope状态
                    System.out.println("被nope出牌无效");
                    gotNoped = false;
                }
                //将该牌加入弃牌堆
                stockpile.add(new Card(Card.Function.Shuffle, Card.Cat.NotCat));

                //玩家抽牌结束这一回合
                drawCard(pid);

            } else if (card.getFunction().equals(Card.Function.Skip)) {
                System.out.println(pid + "打出: Skip");
                anyOneNope(pid);

                if (!gotNoped) {
                    //如果是被上家Attack的状态下打出一张Skip则underAttack状态调整为false(剩余一次摸牌可以视作结束回合时的正常摸牌)
                    if (underAttack) {
                        stockpile.add(new Card(Card.Function.Skip, Card.Cat.NotCat));
                        underAttack = false;
                    } else {
                        //如非underAttack状态下玩家打出的牌为Skip。则跳过自身当前回合
                        currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();
                        System.out.println(pid + "跳过当前回合");
                    }
                } else {
                    System.out.println("被nope出牌无效");
                    gotNoped = false;
                }

                stockpile.add(new Card(Card.Function.Skip, Card.Cat.NotCat));

            } else if (card.getFunction().equals(Card.Function.SeeTheFuture)) {
                System.out.println(pid + "打出: SeeTheFuture");
                anyOneNope(pid);

                if (!gotNoped) {
                    //获取牌堆顶部三张牌面信息
                    ArrayList<Card> topThreeCards = deck.getTopCards(3);

                    //需要在gui向玩家输出三张牌的牌面信息
                    System.out.println("顶部三张牌: " + topThreeCards);
                } else {
                    System.out.println("被nope出牌无效");
                    gotNoped = false;
                }

                stockpile.add(new Card(Card.Function.SeeTheFuture, Card.Cat.NotCat));
                drawCard(pid);

            } else if (card.getFunction().equals(Card.Function.Attack)) {
                System.out.println(pid + "打出: Attack");
                anyOneNope(pid);
                if (!gotNoped) {
                    //如果打出Attack，则直接结束自身回合，强制下一位玩家连续进行两个回合
                    currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();
                    //如打出Attack时自身没有被施加underAttack状态，则将下一位玩家的underAttack状态调整为true
                    if (!underAttack) {
                        this.underAttack = true;
                    }
                } else {
                    System.out.println("被nope出牌无效");
                }

                stockpile.add(new Card(Card.Function.Attack, Card.Cat.NotCat));

            } else if (card.getFunction().equals(Card.Function.Favor)) {
                //让目标玩家自选index的方法需要进一步完善
                System.out.println(pid + "打出: Favor");
                anyOneNope(pid);
                if (!gotNoped) {
                    Scanner sc = new Scanner(System.in);

                    System.out.println("请输入想要调情的对象id");
                    String targetPlayer = sc.next();

                    //如目标玩家手牌为空则当前玩家重新选择调情目标
                    while (hasEmptyHand(targetPlayer)) {
                        //如目标玩家手牌为空，指定玩家重新选择Favor目标
                        System.out.println("该玩家无手牌，请选择其他玩家调情");
                        targetPlayer = sc.next();
                    }

                    //获取该目标玩家手牌信息
                    ArrayList<Card> targetHand = getPlayerHand(targetPlayer);

                    //需要让目标玩家选择index(还未想好怎么实现)
                    System.out.println("输入想选择牌的index，左起第一张为1");

                    //玩家输入选择的牌面index
                    int targetChoice = sc.nextInt() - 1;

                    if (targetChoice < targetHand.size() && targetChoice >= 0) {
                        //从目标玩家处获取一张目标玩家自选手牌加入自身牌组
                        pHand.add(targetHand.remove(targetChoice));

                        stockpile.add(new Card(Card.Function.Favor, Card.Cat.NotCat));
                    } else {
                        //丢出有关index的exception
                    }
                } else {
                    System.out.println("被nope出牌无效");
                }
                drawCard(pid);
            }


        } else if (this.selectedCard.size() == 2 && sameCatCard(this.selectedCard)) {
            //若玩家打出的牌为两张相同的普通猫组合，则指定一名玩家抽取其一张手牌

            //获取猫猫牌牌面
            Card card = this.selectedCard.get(0);

            Scanner sc = new Scanner(System.in);

            //需要对当前玩家屏幕输出
            System.out.println("请输入想要抽牌的对象id");
            String targetPlayer = sc.next();


            //如目标玩家手牌为空则当前玩家重新选择抽牌目标
            while (hasEmptyHand(targetPlayer)) {
                //如目标玩家手牌为空，指定玩家重新选择抽牌目标
                System.out.println("该玩家无手牌，请选择其他玩家抽牌");
                targetPlayer = sc.next();
            }

            System.out.println(pid + "打出两张" + card.toString() + "组合,抽取" + targetPlayer + "一张牌");
            anyOneNope(pid);

            if (!gotNoped) {
                //获取该目标玩家手牌信息
                ArrayList<Card> targetHand = getPlayerHand(targetPlayer);

                System.out.println("输入想选择牌的index，左起第一张为1");

                //玩家输入选择的牌面index
                int targetChoice = sc.nextInt() - 1;

                if (targetChoice < targetHand.size() && targetChoice >= 0) {
                    //从目标玩家处抽取一张手牌加入自身牌组
                    pHand.add(getPlayerCard(getPlayerHand(targetPlayer), targetChoice));

                    for (int i = 0; i < 2; i++) {
                        stockpile.add(new Card(Card.Function.NotFunction, card.getCat()));
                    }

                } else {
                    //丢出有关index的exception
                }
            } else {
                System.out.println("被nope出牌无效");
            }

            drawCard(pid);

        } else if (this.selectedCard.size() == 3 && sameCatCard(this.selectedCard)) {
            //若玩家打出的牌为三张相同的普通猫组合，则指定一名玩家所要一张手牌，牌面由该玩家指定

            //获取猫猫牌牌面
            Card card = this.selectedCard.get(0);

            Scanner sc = new Scanner(System.in);

            //需要对当前玩家屏幕输出
            System.out.println("请输入索要对象id");
            String targetPlayer = sc.next();


            //如目标玩家手牌为空则当前玩家重新选择抽牌目标
            while (hasEmptyHand(targetPlayer)) {
                //如目标玩家手牌为空，指定玩家重新选择抽牌目标
                System.out.println("该玩家无手牌，请选择其他玩家抽牌");
                targetPlayer = sc.next();
            }

            System.out.println("请输入想索要的牌面编号");
            System.out.println("1: Defuse, 2: Attack, 3:Favor, 4: SeeTheFuture...");
            int targetCardIndex = sc.nextInt();

            while (targetCardIndex < 0 || targetCardIndex > 12) {
                //都出invalidInput exception
                System.out.println("invalid input 重新输入");
                targetCardIndex = sc.nextInt();
            }
            //获取该目标玩家手牌信息
            ArrayList<Card> targetHand = getPlayerHand(targetPlayer);

            Card targetCard = wantCard(targetCardIndex, targetHand);

            System.out.println(pid + "打出三张" + card.toString() + ",索要" + targetPlayer + "的" + targetCard.toString());
            anyOneNope(pid);

            if (!gotNoped) {
                pHand.add(getPlayerCard(targetHand, targetHand.indexOf(targetCard)));
            }

            drawCard(pid);

        } else if (this.selectedCard.size() == 5 && differentCatCard(this.selectedCard)) {
            //若玩家打出的牌为五张不同的普通猫组合，则可从弃牌堆内拿曲任意一张牌
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入想从弃牌堆获取的牌面编号");
            System.out.println("1: Defuse, 2: Attack, 3:Favor, 4: SeeTheFuture...");
            int targetCardIndex = sc.nextInt();

            while (targetCardIndex < 0 || targetCardIndex > 12) {
                //甩出invalidInput exception
                System.out.println("invalid input 重新输入");
                targetCardIndex = sc.nextInt();
            }

            Card targetCard = wantCard(targetCardIndex, this.stockpile);

            //如果弃牌堆内有targetCard，则删除弃牌堆内该牌并将之加入该玩家手牌
            stockpile.remove(targetCard);
            pHand.add(targetCard);


        } else {
            //如不出牌则直接摸牌
            drawCard(pid);
            //丢出InvalidCardPLayException需要后续实现
        }

        //当前玩家出牌结束后清空selectedCard并重制gotNoped
        selectedCard.clear();
    }

    //玩家摸牌动作
    public void drawCard(String pid) {
        //获取当前回合玩家手牌信息
        ArrayList<Card> hand = getPlayerHand(pid);

        //检查牌堆顶一张牌是否是炸弹，如果是炸弹则判断玩家手牌中是否有Defuse
        if (gotBombed(deck.getTopCards(1))) {
            //摸到炸弹的情况
            System.out.println(pid + "摸到了炸弹");

            //如果当前玩家手牌中有Defuse则删除一张手牌中的Defuse并放入弃牌堆
            Card defuse = new Card(Card.Function.Defuse, Card.Cat.NotCat);

            if (hand.contains(defuse)) {
                //没被炸死的情况

                //将玩家手中的defuse移入废牌堆
                hand.remove(defuse);
                this.stockpile.add(defuse);

                //将牌堆最上方的炸弹移除牌堆
                deck.drawCard();

                //未爆弹计数器增加一
                bombNeededBack++;

                //如玩家为underAttack状态，currentPlayerIndex保持不变
                if (underAttack) {
                    underAttack = false;
                }else{
                    //将已翻开未爆炸的炸弹全部放回牌堆
                    Scanner sc = new Scanner(System.in);
                    System.out.println("需要放回" + bombNeededBack + "张炸弹");
                    for (int i = 0; i < bombNeededBack; i++) {
                        System.out.print("放回第" + i + "张炸弹, ");
                        System.out.print("输入想将炸弹放入的位置，范围从上至上为1 - " + (deck.sizeOf() + 1) + ": ");

                        int index = sc.nextInt() - 1;

                        while (index < 0 || index > deck.sizeOf()) {
                            //丢出异常,提示重新输入
                            index = sc.nextInt();
                        }
                        this.deck.insertBomb(new Card(Card.Function.NotFunction, Card.Cat.ExplodingKitten), index);
                        bombNeededBack = 0;
                        //移动当前玩家指针至下一位玩家
                        currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();
                    }
                }

            } else {
                //被炸死的情况

                //将牌堆最上方的炸弹移除牌堆
                deck.drawCard();

                //玩家被炸死，将该玩家id从游戏中移除
                this.playerIds.remove(pid);

                //由于被炸死玩家已被移除则直接用currentPlayerIndex与当前玩家数取模
                currentPlayerIndex = currentPlayerIndex % playerIds.size();

                //提示当前玩家出局，这个功能需要看gui怎么操作再来完善
                System.out.println(pid + "被炸死了！");

            }
        } else {
            //没摸到炸弹的情况
            if (underAttack) {
                //如果没摸到炸弹，且该玩家在当前回合被攻击，则摸一张牌后继续回合
                hand.add(deck.drawCard());
                underAttack = false;
            } else {
                //没有被攻击则摸一张牌结束回合
                hand.add(deck.drawCard());
                //移动当前玩家指针至下一位玩家
                currentPlayerIndex = (currentPlayerIndex + 1) % playerIds.size();
            }
            System.out.println(pid + "抽取: " + getPlayerHand(pid).get(getPlayerHand(pid).size() - 1));
        }

        System.out.println();

    }

    public void anyOneNope(String pid) {
        Scanner input = new Scanner(System.in);
        boolean playedNope = false;
        int notWish = 0;

        do {
            for (String player : this.playerIds) {
                if (!player.equals(pid) && getPlayerHand(player).contains(Card.Function.Nope)) {
                    //如玩家手牌有nope则询问玩家是否希望打出。应只对此玩家的console输出而不是对全部玩家的console输出(需要后续实现)
                    System.out.println("Enter 1 if you want to play nope, 0 if not");

                    if (input.nextInt() == 1) {
                        System.out.println(player + "打出Nope");

                        //记录有玩家打出nope
                        playedNope = true;

                        //从玩家手牌中移除nope
                        playNope(player);

                        //更新gotNoped状态
                        gotNoped ^= true;

                        if (gotNoped) {
                            System.out.println(pid + "当前出牌为被nope状态");
                        } else {
                            System.out.println(pid + "当前出牌为正常状态");
                        }

                    } else {
                        //如果该玩家不想打出nope则notwish标记加一
                        notWish++;
                    }
                } else {
                    //此method用作判定"pid"所打出的牌是否被nope，由于玩家不可以一次性打出两张牌，所以默认"pid"不打出nope
                    notWish++;
                }
            }
            if (notWish == this.playerIds.size()) {
                //如果所有玩家都不想打出nope则终止询问
                break;
            }
            //每进行完一轮完整的判定清零notwish。
            notWish = 0;
        } while (playedNope); //只要有玩家打出nope就重新进行新一轮的判定
    }

    public void playNope(String pid) {
        //确定玩家手牌中nope的index
        int nopeIndex = getPlayerHand(pid).indexOf(Card.Function.Nope);

        //调用方法从pid手牌中移除一张Nope
        getPlayerCard(getPlayerHand(pid), nopeIndex);
    }

    public Card wantCard(int targetCardIndex, ArrayList<Card> targetHand) {
        Card targetCard = null;
        do {
            switch (targetCardIndex) {
                case 1 -> targetCard = new Card(Card.Function.Defuse, Card.Cat.NotCat);
                case 2 -> targetCard = new Card(Card.Function.Attack, Card.Cat.NotCat);
                case 3 -> targetCard = new Card(Card.Function.Favor, Card.Cat.NotCat);
                case 4 -> targetCard = new Card(Card.Function.SeeTheFuture, Card.Cat.NotCat);
                case 5 -> targetCard = new Card(Card.Function.Nope, Card.Cat.NotCat);
                case 6 -> targetCard = new Card(Card.Function.Shuffle, Card.Cat.NotCat);
                case 7 -> targetCard = new Card(Card.Function.Skip, Card.Cat.NotCat);
                case 8 -> targetCard = new Card(Card.Function.NotFunction, Card.Cat.TacoCat);
                case 9 -> targetCard = new Card(Card.Function.NotFunction, Card.Cat.BeardCat);
                case 10 -> targetCard = new Card(Card.Function.NotFunction, Card.Cat.HairyPotatoCat);
                case 11 -> targetCard = new Card(Card.Function.NotFunction, Card.Cat.RainbowRalphingCat);
                case 12 -> targetCard = new Card(Card.Function.NotFunction, Card.Cat.Cattermelon);
            }
            if (!targetHand.contains(targetCard)) {
                //如目标玩家手牌中没有出牌玩家指定的牌，提示出牌玩家更换想要的牌
                System.out.println("所选牌组中无" + targetCard.toString() + ",请重新选择目标牌面");
            }
        } while (!targetHand.contains(targetCard));

        return targetCard;
    }

    //end of game class
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

