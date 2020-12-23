package card;

import player.Player;

public class Bomb extends Function{

    public Bomb(Player owner){
        super(owner);
    }

    @Override
    public void beGotten(){
        owner.minusDiffuse();
    }

    @Override
    public void bePlayed(){}

}
