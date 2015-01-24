package SeaBattle.Ships;

import SeaBattle.CellState;
import SeaBattle.Pair;

import java.util.ArrayList;

/**
 * Created by britvin on 07.10.14.
 */
public abstract class Ship {
    protected int health;
    public Ship() {
    }

    public boolean isDead() {
        return this.health == 0;
    }
    public void getAttacked() {
        this.health--;
    }
    public abstract void drawShip(CellState[][] board);
    public abstract boolean isValidRange(ArrayList<Pair> range);
    public abstract boolean contains(Pair cell);
    public abstract ArrayList<Pair> getCoordinates();
}
