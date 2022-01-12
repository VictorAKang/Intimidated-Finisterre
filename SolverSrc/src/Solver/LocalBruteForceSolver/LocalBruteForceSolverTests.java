package Solver.LocalBruteForceSolver;

import Solver.Coordinates;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LocalBruteForceSolverTests {
    LocalBruteForceSolver solver;

    @Test
    public void constructor() {
        int longSide = 7;
        int shortSide = 5;
        int searchWidth = 0;
        solver = new LocalBruteForceSolver(searchWidth, longSide, shortSide);

        assertTrue(solver.frontier.isEmpty());
        assertTrue(solver.certainNumberCells.isEmpty());
        assertTrue(solver.toFlag.isEmpty());

        assertEquals(solver.searchWidth, searchWidth);
        assertEquals(solver.longSideLength, longSide);
        assertEquals(solver.shortSideLength, shortSide);

        assertEquals(solver.board.length, longSide);
        for (int i = 0; i < longSide; i++)
            assertEquals(solver.board[i].length, shortSide);

        for (int i = 0; i < longSide; i++)
            for (int j = 0; j < shortSide; j++)
                assertEquals(solver.board[i][j].parseIntoNumber(), -1);
    }

    @Test
    public void setNeighbours() {
        int longSide = 7;
        int shortSide = 5;
        solver = new LocalBruteForceSolver(0, longSide, shortSide);

        for (int x = 0; x < longSide; x++)
            for (int y = 0; y < shortSide; y++)
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        if (x + i >= 0 && x + i < longSide && y + j >= 0 && y +j < shortSide && !(i == 0 && j ==0))
                            assertTrue(solver.board[x][y].getNeighbours().contains(solver.board[x + i][y + j]));
    }

    @Test
    public void getFlags() {
        solver = new LocalBruteForceSolver(1, 4, 3);
        ArrayList<MinesweeperGraphNode> toFlagArray = new ArrayList<>();
        toFlagArray.add(solver.board[0][0]);
        toFlagArray.add(solver.board[3][1]);
        toFlagArray.add(solver.board[2][2]);
        toFlagArray.add(solver.board[1][2]);

        solver.toFlag.addAll(toFlagArray);
        assertEquals(solver.toFlag.size(), toFlagArray.size());
        ArrayList<Coordinates> resultToFlag = solver.getFlags();
        assertTrue(solver.toFlag.isEmpty());
        for (MinesweeperGraphNode node: toFlagArray) {
            assertTrue(resultToFlag.contains(new Coordinates(node.getX(), node.getY())));
            assertTrue(node.getKnown());
            assertTrue(node.getBomb());
        }
    }

    @Test
    public void updateBoard() {
        solver = new LocalBruteForceSolver(1, 5, 4);
        int[][] board = {{-1, -1, 0, -1}, {3, -2, -1, -1}, {-1, 5, 4, -2}, {-1, -1, 1, -1}, {4, 3, -1, -2}};

        Coordinates[] changeCoordinates = {new Coordinates(0, 2),
                new Coordinates(2, 1), new Coordinates(4, 0), new Coordinates(4, 1)};
        int[] values = {0, 5, 4, 3};

        Coordinates[] alreadyChangedCoordinates = {new Coordinates(1, 0), new Coordinates(2, 2),
                new Coordinates(2,3), new Coordinates(3, 2)};
        int[] alreadySetValues = {3, 4, -2, 1};

        Coordinates[] setBombs = {new Coordinates(1, 2), new Coordinates(1,1),
                new Coordinates(3, 3), new Coordinates(4, 3)};

        for (int i = 0; i < alreadyChangedCoordinates.length; i++) {
            solver.board[alreadyChangedCoordinates[i].x()][alreadyChangedCoordinates[i].y()].setKnown(true);
            solver.board[alreadyChangedCoordinates[i].x()][alreadyChangedCoordinates[i].y()].setValue(alreadySetValues[i]);
        }

        for (Coordinates coordinates: setBombs) {
            solver.board[coordinates.x()][coordinates.y()].setBomb(true);
            solver.board[coordinates.x()][coordinates.y()].setKnown(true);
        }

        solver.updateBoard(board);
        assertEquals(solver.frontier.size(), changeCoordinates.length);
        for (MinesweeperGraphNode node: solver.frontier) {
            boolean found = false;
            for (int i = 0; i < changeCoordinates.length; i++) {
                if (node.getX() == changeCoordinates[i].x() && node.getY() == changeCoordinates[i].y()) {
                    assertEquals(node.parseIntoNumber(), values[i]);
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    public void updateBoardWithFrontierBeingRemoved() {
        solver = new LocalBruteForceSolver(0, 3,3);
        int[][] board = {{1,0,1},{1,1,1},{2,-1,1}};

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                solver.board[i][j].setKnown(true);
                solver.board[i][j].setValue(1);
            }

        solver.board[0][1].setKnown(false);
        solver.board[2][1].setKnown(false);
        solver.board[2][0].setValue(2);
        solver.frontier.add(solver.board[2][0]);

        solver.updateBoard(board);
        assertEquals(solver.frontier.size(), 1);
        assertTrue(solver.frontier.contains(solver.board[2][0]));
        assertTrue(solver.board[0][1].getKnown());
        assertEquals(solver.board[0][1].getValue(), 0);
    }

//    @Test
//    public void basicSearchTest() {
//        solver = new LocalBruteForceSolver(0,4,2);
//        int[][] board = {{1,-1},{1,-1}, {2,-1}, {1,-1}};
//
//        solver.updateBoard(board);
//        Coordinates step = solver.step();
//        assertEquals(step.x(), 0);
//        assertEquals(step.y(), 1);
//        assertTrue(solver.toFlag.isEmpty());
//        assertTrue(solver.certainNumberCells.isEmpty());
//
//        solver = new LocalBruteForceSolver(1,3,3);
//        solver.updateBoard(board);
//        step = solver.step();
////        assertEquals(step.x(), );
//        assertEquals(step.y(), 1);
//        assertEquals(solver.toFlag.size(), 2);
//        assertTrue(solver.certainNumberCells.isEmpty());
//
//    }
}
