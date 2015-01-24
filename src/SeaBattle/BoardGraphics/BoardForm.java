package SeaBattle.BoardGraphics;

import SeaBattle.Board;
import SeaBattle.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Александр on 16.11.2014.
 */
public class BoardForm extends JFrame {
    private JLabel[] lPlayer;
    private Board[] boards;
    private BoardCanvas[][] boardCanvas;
    private JButton btnStartGame;
    private JButton[] btnCreateShip;
    private JButton[] btnAttackShip;

    public BoardForm() throws HeadlessException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Pair frameResolution = new Pair(600, 650);
        Pair boardResolution = new Pair(250, 250);
        int boardIndent = 30;
        Pair boardPosition = new Pair((frameResolution.getX() - boardResolution.getX() * 2 - boardIndent * 2) / 2, boardIndent);

        setSize(frameResolution.getX(), frameResolution.getY());
        setLayout(null);

        initBoards(frameResolution, boardResolution, boardPosition, boardIndent);
        initLabels(frameResolution, boardResolution, boardPosition, boardIndent);
        initButtons(frameResolution, boardResolution, boardPosition, boardIndent);

        setVisible(true);
    }

    private void initBoards(Pair frameResolution, Pair boardResolution, Pair boardPosition, int boardIndent) {
        boards = new Board[2];
        boardCanvas = new BoardCanvas[2][2];

        for (int i = 0; i < 2; i++) {
            boards[i] = new Board();
            for (int j = 0; j < 2; j++) {
                boardCanvas[i][j] = new BoardCanvas(j == 0 ? boards[i].getBoardData() : boards[i].getBoardDataUnknown(), boardResolution);
            }
        }

        boardCanvas[0][1].blockBoard();
        boardCanvas[1][1].blockBoard();
        boardCanvas[0][0].setLocation(boardPosition.getX(), boardPosition.getY());
        boardCanvas[0][1].setLocation(boardPosition.getX() + boardIndent + boardResolution.getX(), boardPosition.getY());
        boardCanvas[1][0].setLocation(boardPosition.getX(), boardPosition.getY() + boardIndent + boardResolution.getY());
        boardCanvas[1][1].setLocation(boardPosition.getX() + boardIndent + boardResolution.getX(), boardPosition.getY() + boardIndent + boardResolution.getY());

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                add(boardCanvas[i][j]);
            }
        }
    }
    private void initLabels(Pair frameResolution, Pair boardResolution, Pair boardPosition, int boardIndent) {
        lPlayer = new JLabel[2];
        lPlayer[0] = new JLabel("Игрок 1", JLabel.CENTER);
        lPlayer[0].setSize(50, 15);
        lPlayer[0].setLocation(frameResolution.getX() / 2 - boardIndent - 10, boardPosition.getY() / 2);

        lPlayer[1] = new JLabel("Игрок 2");
        lPlayer[1].setSize(50, 15);
        lPlayer[1].setLocation(frameResolution.getX() / 2 - boardIndent - 10, boardPosition.getY() + boardIndent / 2 + boardResolution.getY());
        add(lPlayer[0]);
        add(lPlayer[1]);
    }
    private void initButtons(Pair frameResolution, Pair boardResolution, Pair boardPosition, int boardIndent) {

        btnCreateShip = new JButton[2];
        btnAttackShip = new JButton[2];

        btnStartGame = new JButton("Начать игру");
        btnStartGame.setSize(120, 20);
        btnStartGame.setLocation(frameResolution.getX() / 2 - boardIndent * 3 + 10, boardIndent * 2 + 10 + 2 * boardResolution.getY());
        btnStartGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boardCanvas[0][0].blockBoard();
                boardCanvas[1][0].blockBoard();
                lPlayer[0].setForeground(Color.red);
                lPlayer[1].setForeground(Color.gray);
                btnStartGame.setVisible(false);
                remove(btnStartGame);
                btnAttackShip[0].setVisible(true);
                boardCanvas[0][1].giveMove();
                boardCanvas[1][1].blockBoard();
            }
        });

        for (int i = 0; i < 2; i++) {
            btnCreateShip[i] = new JButton("Создать");
            btnCreateShip[i].setSize(100, 20);
            btnCreateShip[i].setLocation(frameResolution.getX() / 8, (i + 1) * (boardIndent + boardResolution.getY()));

            btnAttackShip[i] = new JButton("Ход");
            btnAttackShip[i].setSize(100, 20);
            btnAttackShip[i].setVisible(false);
            btnAttackShip[i].setLocation(frameResolution.getX() / 2 + boardIndent * 2, (i + 1) * (boardIndent + boardResolution.getY()));
        }
        btnCreateShip[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addShip(0);
            }
        });
        btnCreateShip[1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addShip(1);
            }
        });

        btnAttackShip[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                attack(0, 1);
            }
        });
        btnAttackShip[1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                attack(1, 0);
            }
        });

        for (int i = 0; i < 2; i++) {
            add(btnCreateShip[i]);
            btnCreateShip[i].setVisible(false);
            add(btnAttackShip[i]);
        }
        add(btnStartGame);
    }

    private void addShip(int boardNumber) {
        ArrayList<Pair> ship = boardCanvas[boardNumber][0].getSelectedCells();
        if (boards[boardNumber].isValidRange(ship)) {
            boards[boardNumber].createShip(ship);
            boardCanvas[boardNumber][0].deselectCells();
            boardCanvas[boardNumber][0].updateBoard(boards[boardNumber].getBoardData());
        }
        if (boards[boardNumber].isReady()) {
            lPlayer[boardNumber].setForeground(Color.yellow);
            btnCreateShip[boardNumber].setVisible(false);
            remove(btnCreateShip[boardNumber]);
            btnCreateShip[boardNumber] = null;
        }
        if (boards[0].isReady() && boards[1].isReady()) {
            add(btnStartGame);
        }
    }

    private void attack(int boardNumber, int attackedBoard) {
        Pair cell = boardCanvas[boardNumber][1].getSelectedCells().get(0);
        boolean result = boards[attackedBoard].attack(cell);
        boardCanvas[boardNumber][1].deselectCells();
        boardCanvas[attackedBoard][0].updateBoard(boards[attackedBoard].getBoardData());
        boardCanvas[boardNumber][1].updateBoard(boards[attackedBoard].getBoardDataUnknown());
        if (boards[attackedBoard].isEmpty()) {
            boardCanvas[boardNumber][1].blockBoard();
            boardCanvas[attackedBoard][1].blockBoard();
            btnAttackShip[boardNumber].setVisible(false);
            lPlayer[boardNumber].setForeground(Color.blue);
            boardCanvas[attackedBoard][1].updateBoard(boards[boardNumber].getBoardData());
        }
        else {
            if (!result) {
                boardCanvas[boardNumber][1].blockBoard();
                boardCanvas[attackedBoard][1].giveMove();
                btnAttackShip[boardNumber].setVisible(false);
                btnAttackShip[attackedBoard].setVisible(true);
                lPlayer[boardNumber].setForeground(Color.gray);
                lPlayer[attackedBoard].setForeground(Color.red);
            }
        }
    }

}
