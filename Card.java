package boot;

public class Card {
    //所有功能牌
    enum Function {
        SeeTheFuture, Nope, Attack, Skip, Shuffle, Favor, Defuse, NotFunction;

        private static final Function[] functions = Function.values();

        public static Function getFunctions(int i) {
            return Function.functions[i];
        }
    }

    //所有普通猫猫牌
    enum Cat {
        TacoCat, HairyPotatoCat, Cattermelon, BeardCat, RainbowRalphingCat, ExplodingKitten, NotCat;

        private static final Cat[] cats = Cat.values();

        public static Cat getCats(int i) {
            return Cat.cats[i];
        }
    }

    private final Function function;
    private final Cat cat;

    public Card(Function function, Cat cat) {
        this.function = function;
        this.cat = cat;
    }

    public Function getFunction() {
        return function;
    }

    public Cat getCat() {
        return cat;
    }

    @Override
    public String toString() {
        if (function == Function.NotFunction) {
            return "Card: " + cat;
        }
        return "Card: " + function;
    }
}
