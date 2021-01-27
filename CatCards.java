package boot;

public class CatCards extends Cards{
    private final Cat cat;

    public CatCards(Cat cat) {
        this.cat = cat;
    }

    public Cat getCats() {
        return cat;
    }
}