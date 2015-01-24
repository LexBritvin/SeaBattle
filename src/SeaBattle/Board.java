package SeaBattle;

import SeaBattle.Ships.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by britvin on 07.10.14.
 */
public class Board {
    private ArrayList<Ship> ships;
    private ArrayList<Ship> shipsTemp;

    private byte fourDeckShipsCount;
    private byte threeDeckShipsCount;
    private byte twoDeckShipsCount;
    private byte oneDeckShipsCount;

    private int boardSize;

    private CellState[][] boardData;
    private CellState[][] boardDataUnknown;

    public Board() {
        this.boardSize = 10;
        this.fourDeckShipsCount = 0;
        this.threeDeckShipsCount = 0;
        this.twoDeckShipsCount = 0;
        this.oneDeckShipsCount = 0;
        this.ships = new ArrayList<Ship>();
        this.boardData = new CellState[10][10];
        this.boardDataUnknown = new CellState[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.boardData[i][j] = CellState.empty;
                this.boardDataUnknown[i][j] = CellState.empty;
            }
        }
        ships.add(new FourDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(0, 0), new Pair(0, 1), new Pair(0, 2), new Pair(0, 3)))));
        ships.add(new ThreeDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(2, 0), new Pair(2, 1), new Pair(2, 2)))));
        ships.add(new ThreeDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(4, 0), new Pair(4, 1), new Pair(4, 2)))));
        ships.add(new TwoDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(6, 0), new Pair(6, 1)))));
        ships.add(new TwoDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(6, 3), new Pair(6, 4)))));
        ships.add(new TwoDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(6, 6), new Pair(6, 7)))));
        ships.add(new OneDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(8, 0)))));
        ships.add(new OneDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(8, 2)))));
        ships.add(new OneDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(8, 4)))));
        ships.add(new OneDeckShip(new ArrayList<Pair>(Arrays.asList(new Pair(8, 6)))));
        this.fourDeckShipsCount = 1;
        this.threeDeckShipsCount = 2;
        this.twoDeckShipsCount = 3;
        this.oneDeckShipsCount = 4;
        for (int i = 0; i < ships.size(); i++) {
            ships.get(i).drawShip(boardData);
        }
    }

    public boolean isValidRange(ArrayList<Pair> range) {
        int length = range.size();
        if (length == 0) {
            return false;
        }


        int[] shipCoordinatesX = new int[length];
        int[] shipCoordinatesY = new int[length];
        for (int i = 0; i < range.size(); i++) {
            Pair temp = range.get(i);
            shipCoordinatesX[i] = temp.getX();
            shipCoordinatesY[i] = temp.getY();
        }

        Arrays.sort(shipCoordinatesX);
        Arrays.sort(shipCoordinatesY);
        int x0 = shipCoordinatesX[length - 1];
        int y0 = shipCoordinatesY[length - 1];
        boolean isHorizontal = false;
        boolean isVertical = false;
        for (int i = 0; i < length - 1; i++) {
            if (x0 != shipCoordinatesX[i]) {
                isVertical = true;
                break;
            }
        }

        for (int i = 0; i < length - 1; i++) {
            if (y0 != shipCoordinatesY[i]) {
                isHorizontal = true;
                break;
            }
        }
        int[] coordinates = null;
        if (isHorizontal && isVertical) {
            return false;
        }
        coordinates = isVertical ? shipCoordinatesX : coordinates;
        coordinates = isHorizontal ? shipCoordinatesY : coordinates;
        for (int i = 0; i < length - 1; i++) {
            int c1 = coordinates[i] + 1;
            int c2 = coordinates[i + 1];
            if (c1 != c2) {
                return false;
            }
        }
        ArrayList<Pair> near = nearbyCells(range);
        if (near == null) {
            return false;
        }

        return true;
    }
    public void createShip(ArrayList<Pair> ship) {
        if (!isValidRange(ship)) {
            return;
        }

        switch (ship.size()) {
            case 1:
                if (this.oneDeckShipsCount < 4) {
                    this.ships.add(new OneDeckShip(ship));
                    this.oneDeckShipsCount++;
                }
                else {
                    return;
                }
                break;

            case 2:
                if (this.twoDeckShipsCount < 3) {
                    this.ships.add(new TwoDeckShip(ship));
                    this.twoDeckShipsCount++;
                }
                else {
                    return;
                }
                break;

            case 3:
                if (this.threeDeckShipsCount < 2) {
                    Ship s = new ThreeDeckShip(ship);
                    this.ships.add(s);
                    this.threeDeckShipsCount++;
                }
                else {
                    return;
                }
                break;

            case 4:
                if (this.fourDeckShipsCount < 1) {
                    this.ships.add(new FourDeckShip(ship));
                    this.fourDeckShipsCount++;
                }
                else {
                    return;
                }
                break;
            default:
                return;
        }
        if (!ships.isEmpty()) {
            this.ships.get(this.ships.size() - 1).drawShip(boardData);
        }
    }

    private boolean isInArrayList(ArrayList<Pair> list, int x, int y) {
        for (int i = 0; i < list.size(); i++) {
            int a = list.get(i).getX();
            int b = list.get(i).getY();
            if ((a == x) && (b == y)) {
                return true;
            }
        }
        return false;
    }
    private ArrayList<Pair> nearbyCells(ArrayList<Pair> range) {
        ArrayList<Pair> result = new ArrayList<Pair>();
        for (int iterator = 0; iterator < range.size(); iterator++) {
            int pointX = range.get(iterator).getX();
            int pointY = range.get(iterator).getY();
            int ix = (pointX < 1) ? pointX : pointX - 1;
            int iy = (pointY < 1) ? pointY : pointY - 1;
            int countX = (pointX < 1) || (pointX == 9) ? 2 : 3;
            int countY = (pointY < 1) || (pointY == 9) ? 2 : 3;
            for (int i = ix; i < ix + countX; i++) {
                for (int j = iy; j < iy + countY; j++) {
                    if (isInArrayList(range, i, j)) {
                        continue;
                    }
                    if ((boardData[i][j] != CellState.empty) && (boardData[i][j] != CellState.miss)) {
                        return null;
                    }
                    result.add(new Pair(i, j));
                }
            }
        }
        return result;
    }

    public CellState[][] getBoardData() {
        return boardData;
    }

    public CellState[][] getBoardDataUnknown() {
        return boardDataUnknown;
    }

    public boolean isReady() {
        return fourDeckShipsCount == 1 && threeDeckShipsCount == 2 && twoDeckShipsCount == 3 && oneDeckShipsCount == 4;
    }

    public boolean attack(Pair cell) {
        int x = cell.getX();
        int y = cell.getY();
        if (boardData[x][y] == CellState.empty) {
            boardData[x][y] = CellState.miss;
            boardDataUnknown[x][y] = CellState.miss;
            return false;
        }
        if (isShip(boardData[x][y])) {
            int delta = -1;
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).contains(cell)) {
                    delta = i;
                    ships.get(i).getAttacked();
                    break;
                }
            }
            boardData[x][y] = CellState.shot;
            boardDataUnknown[x][y] = CellState.shot;
            if (ships.get(delta).isDead()) {
                ArrayList<Pair> nearby = nearbyCells(ships.get(delta).getCoordinates());
                for (int i = 0; i < nearby.size(); i++) {
                    boardData[nearby.get(i).getX()][nearby.get(i).getY()] = CellState.miss;
                    boardDataUnknown[nearby.get(i).getX()][nearby.get(i).getY()] = CellState.miss;
                }
                ships.get(delta).drawShip(boardData);
                ships.get(delta).drawShip(boardDataUnknown);
                ships.remove(delta);
            }
            return true;
        }
        return false;
    }
    private boolean isShip(CellState cell) {
        return cell == CellState.ship1 || cell == CellState.ship2
                || cell == CellState.ship3 || cell == CellState.ship4 || cell == CellState.ship;
    }

    public boolean isEmpty() {
        return ships.isEmpty();
    }
}
