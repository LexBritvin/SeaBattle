package SeaBattle.Ships;

import SeaBattle.CellState;
import SeaBattle.Pair;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by britvin on 07.10.14.
 */
public class FourDeckShip extends Ship {

    protected ArrayList<Pair> ship;

    public FourDeckShip(ArrayList<Pair> ship) {
        this.ship = ship;
        health = ship.size();
    }

    public void drawShip(CellState[][] board) {
        for (int i = 0; i < ship.size(); i++) {
            board[ship.get(i).getX()][ship.get(i).getY()] = isDead() ? CellState.shot4 : CellState.ship4;
        }
    }

    public boolean isValidRange(ArrayList<Pair> range) {
        return range.size() == 4;
    }
    public boolean contains(Pair cell) {
        int delta = -1;
        for(int i = 0; i < this.ship.size(); i++) {
            if (this.ship.get(i).equals(cell)) {
                delta = i;
                break;
            }
        }
        return delta >= 0;
    }
    public ArrayList<Pair> getCoordinates() {
        return ship;
    }
}
