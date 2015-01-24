package SeaBattle;

import java.lang.Math;
/**
 * Created by britvin on 06.10.14.
 */
public class Pair {
    private int x;
    private int y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pair() {

    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public Pair getDifference(Pair b) {
        int xDifference = Math.abs(this.x - b.getY());
        int yDifference = Math.abs(this.y - b.getY());
        return new Pair(xDifference, yDifference);
    }

    public boolean equals(Pair b) {
        return (this.x == b.getX()) && (this.y == b.getY());
    }
}
