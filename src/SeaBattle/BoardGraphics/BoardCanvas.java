package SeaBattle.BoardGraphics;

import SeaBattle.Board;
import SeaBattle.CellState;
import SeaBattle.GameState;
import SeaBattle.Pair;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;

public class BoardCanvas extends JPanel{
    private String[] letters;
    private String[] numbers;

    private Pair resolution;
    private Pair rectSize;
    private Pair borderOffset;
    private Pair[][] cells;
    private Pair cellSize;

    private CellState[][] cellsData;
    private CellState[][] cellsDataTemp;

    private PaintState paintState;
    private GameState gameState;
    private ArrayList<Pair> selectedCells;

    public BoardCanvas(CellState[][] boardData, Pair resolution) {
        int width = resolution.getX();
        int height = resolution.getY();
        setSize(width, height);

        this.resolution = resolution;
        this.borderOffset = new Pair(30, 30);
        this.rectSize = new Pair(width - 2 * borderOffset.getX(), height - 2 * borderOffset.getY());
        this.cellSize = new Pair(rectSize.getX() / 10, rectSize.getY() / 10);

        this.cells = new Pair[10][10];

        String[] letters = {"A", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "К"};
        String[] numbers = {"  1", "  2", "  3", "  4", "  5", "  6", "  7", "  8", "  9", "10"};
        this.letters = letters;
        this.numbers = numbers;

        this.gameState = GameState.initShips;
        this.paintState = PaintState.wait;

        this.cellsData = new CellState[10][10];
        this.cellsDataTemp = new CellState[10][10];
        this.selectedCells = new ArrayList<Pair>();
        updateCellData(boardData);
        initCanvasCellsPosition();
        initMouseAdapter();;

    }

    public void updateCellData(CellState[][] boardOfPlayer) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.cellsData[i][j] = boardOfPlayer[i][j];
                this.cellsDataTemp[i][j] = boardOfPlayer[i][j];
            }
        }
    }

    public void blockBoard() {
        gameState = GameState.blockedBoard;
    }

    public ArrayList<Pair> getSelectedCells() {
        return selectedCells;
    }
    public void deselectCells() {
        for (int i = 0; i < selectedCells.size(); i++) {
            cellsDataTemp[selectedCells.get(i).getX()][selectedCells.get(i).getY()] = CellState.empty;
        }
        selectedCells.clear();
        paintState = PaintState.repaintCells;
        repaint();
    }
    public void updateBoard(CellState[][] boardOfPlayer) {
        updateCellData(boardOfPlayer);
        paintState = PaintState.repaintCells;
        repaint();
    }

    private void initCanvasCellsPosition() {
        Pair currentPosition = new Pair(borderOffset.getX(), borderOffset.getY());
        for (int i = 0; i < 10; i++) {
            currentPosition.setX(this.borderOffset.getX());
            for (int j = 0; j < 10; j++) {
                this.cells[i][j] = new Pair(currentPosition.getX(), currentPosition.getY());
                currentPosition.setX(currentPosition.getX() + this.cellSize.getX());
            }
            currentPosition.setY(currentPosition.getY() + this.cellSize.getY());
        }
    }
    public void giveMove() {
        gameState = GameState.attackShip;
    }
    private void initMouseAdapter() {
        addMouseListener(new MouseAdapter() {
            private Pair identifyCell(MouseEvent me) {
                int j = me.getX();
                int i = me.getY();
                j -= borderOffset.getX();
                i -= borderOffset.getY();
                if (i < 0 || j < 0) {
                    return null;
                }
                j /= cellSize.getX();
                i /= cellSize.getY();
                if (i > 9 || j > 9) {
                    return null;
                }
                return new Pair(i, j);
            }

            public void mousePressed(MouseEvent me) {
                Pair parameter;
                switch (gameState) {
                    case initShips:
                        if ((parameter = identifyCell(me)) != null) {
                            CellState temp = cellsDataTemp[parameter.getX()][parameter.getY()];
                            if (temp == CellState.empty) {
                                if (selectedCells.size() < 4) {
                                    temp = CellState.selected;
                                    selectedCells.add(parameter);
                                }
                            } else {
                                temp = temp == CellState.ship1 || temp == CellState.ship2
                                        || temp == CellState.ship3 || temp == CellState.ship4
                                        ? CellState.ship : temp;
                                if (temp == CellState.ship) {
                                    return;
                                }
                                int index = -1;
                                for (int i = 0; i < selectedCells.size(); i++) {
                                    int x = selectedCells.get(i).getX();
                                    int y = selectedCells.get(i).getY();
                                    if ((parameter.getX() == x) && (parameter.getY() == y)) {
                                        index = i;
                                        break;
                                    }

                                }
                                selectedCells.remove(index);
                                temp = CellState.empty;
                            }
                            cellsDataTemp[parameter.getX()][parameter.getY()] = temp;
                            paintState = PaintState.repaintCells;
                            repaint();
                        }
                        break;
                    case attackShip:
                        if ((parameter = identifyCell(me)) != null) {
                            CellState temp = cellsDataTemp[parameter.getX()][parameter.getY()];
                            if (!isEmptyCell(temp)) {
                                return;
                            }
                            if (selectedCells.isEmpty()) {
                                selectedCells.add(parameter);
                                temp = CellState.selected;
                            }
                            else {
                                Pair index = selectedCells.get(0);
                                cellsDataTemp[index.getX()][index.getY()] = CellState.empty;
                                selectedCells.clear();
                                if (!index.equals(parameter)) {
                                    selectedCells.add(parameter);
                                    temp = CellState.selected;
                                }
                                else {
                                    temp = CellState.empty;
                                }
                            }
                            cellsDataTemp[parameter.getX()][parameter.getY()] = temp;
                            paintState = PaintState.repaintCells;
                            repaint();
                        }
                        break;
                }
            }
        });
    }

    public void paint(Graphics g) {
        super.paint(g);
        switch (paintState) {
            case repaintCells:
                paintCells(g, paintState);
                break;
        }
    }

    private void paintCells(Graphics g, PaintState state) {
        CellState[][] data = null;
        switch (state) {
            case initialize:
                data = cellsData;
                break;
            case repaintCells:
                data = cellsDataTemp;
                break;
        }
        // Draw all cells
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                switch (data[i][j]) {
                    case empty:
                        g.setColor(Color.white);
                        break;
                    case ship1:
                    case ship2:
                    case ship3:
                    case ship4:
                    case ship:
                        g.setColor(Color.gray);
                        break;
                    case shot1:
                    case shot2:
                    case shot3:
                    case shot4:
                    case shot:
                        g.setColor(Color.red);
                        break;
                    case miss:
                        g.setColor(Color.blue);
                        break;
                    case selected:
                        g.setColor(Color.yellow);
                        break;
                }
                g.fillRect(cells[i][j].getX(), cells[i][j].getY(), cellSize.getX(), cellSize.getY());
                g.setColor(Color.black);
                g.drawRect(cells[i][j].getX(), cells[i][j].getY(), cellSize.getX(), cellSize.getY());
            }
        }
    }
    public void paintComponent(Graphics g) {

        int width = getWidth();
        int height = getHeight();
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.black);
        Font font = new Font("Courier", Font.BOLD,12);
        g.setFont(font);
        // Draw cell names
        for (int i = 0; i < 10; i++) {
            g.drawString(this.letters[i], this.cells[0][i].getX() + this.cellSize.getX() / 2, this.borderOffset.getY() * 2 / 3);
            g.drawString(this.numbers[i], this.borderOffset.getX() / 4, this.cells[i][0].getY() + this.cellSize.getY() * 2 / 3);
        }
        paintCells(g, PaintState.initialize);

    }
    private boolean isEmptyCell(CellState cell) {
        return cell == CellState.empty || cell == CellState.selected;
    }

}