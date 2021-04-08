package boot;

import java.util.Objects;

public class Card {
    //所有功能牌
    enum Function {
        SeeTheFuture, Nope, Attack, Skip, Shuffle, Favor, Defuse, NotFunction;
    }

    //所有普通猫猫牌
    enum Cat {
        TacoCat, HairyPotatoCat, Cattermelon, BeardCat, RainbowRalphingCat, ExplodingKitten, NotCat;
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
        if (function.equals(Function.NotFunction)) {
            return "" + cat;
        }
        return "" + function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return function == card.function &&
                cat == card.cat;
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, cat);
    }
}
