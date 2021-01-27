package boot;

public class Cards {
    enum Function {
        SeeTheFuture, Nope, Attack, Skip, Shuffle, Favor, ExplodingKitten, Defuse;

        private static final Function[] functions = Function.values();

        public static Function getFunctions(int i) {
            return Function.functions[i];
        }
    }

    enum Cat {
        TacoCat, HairyPotatoCat, Cattermelon, BeardCat, RainbowRalphingCat;

        private static final Cat[] cats = Cat.values();

        public static Cat getCats(int i) {
            return Cat.cats[i];
        }
    }
}
