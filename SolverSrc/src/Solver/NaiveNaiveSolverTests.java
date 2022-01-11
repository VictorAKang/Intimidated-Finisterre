package Solver;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import java.util.*;


public class NaiveNaiveSolverTests {
    NaiveSolver naiveSolver;

    @Test
    void ConstructorTester () {
        int[][] board = {{-1}};
        naiveSolver = new NaiveSolver(board, 1, 1);

        assertEquals(naiveSolver.board[0][0], -1);
        assertEquals(naiveSolver.longSideLength, 1);
        assertEquals(naiveSolver.shortSideLength, 1);

        board = new int[][]{{1, 3, 2}, {-2, -1, 0}, {8, 9, 6}, {4,-1, 7}};
        naiveSolver = new NaiveSolver(board, 4, 3);

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 3; j++)
                assertEquals(naiveSolver.board[i][j], board[i][j]);
        assertEquals(naiveSolver.longSideLength, 4);
        assertEquals(naiveSolver.shortSideLength, 3);
    }

    @Test
    void updateFrontierElementTest() {
        int[][] board = {{0, 1, 1}, {1, 2, -2}, {-1, 2, -1}};
        naiveSolver = new NaiveSolver(board, 3, 3);

        Coordinates[] removedCoordinates = {new Coordinates(0, 0), new Coordinates(0, 2)};
        Coordinates[] notRemovedCoordinates = {new Coordinates(1, 0), new Coordinates(2, 1)};

        for (Coordinates removedCoordinate : removedCoordinates) {
            naiveSolver.frontier.add(removedCoordinate);
            naiveSolver.updateFrontierElement(removedCoordinate);
            assertFalse(naiveSolver.frontier.contains(removedCoordinate));
        }

        for (Coordinates notRemovedCoordinate : notRemovedCoordinates) {
            naiveSolver.frontier.add(notRemovedCoordinate);
            naiveSolver.updateFrontierElement(notRemovedCoordinate);
            assertTrue(naiveSolver.frontier.contains(notRemovedCoordinate));
        }
    }

    @Test
    void getDifferenceTest() {
        int[][] board = {{-1, -1, -1, -1},{-1, -1, -1, -1},{-1, -1, -1, -1},{-1, -1, -1, -1}};
        int[][] newBoard = {{-1, -1, 0, -1},{1, -2, -1, -1},{-1, -1, -1, -1},{-1, -1, -1, 0}};
        naiveSolver = new NaiveSolver(board,4,4);
        HashMap<Coordinates, Integer> changedMap = new HashMap<Coordinates, Integer>();
        changedMap.put(new Coordinates(0, 2), 0);
        changedMap.put(new Coordinates(1, 0), 1);
        changedMap.put(new Coordinates(1, 1), -2);
        changedMap.put(new Coordinates(3, 3), 0);

        Set<Map.Entry<Coordinates, Integer>> mappedDifference = naiveSolver.getDifference(newBoard).entrySet();
        assertEquals(changedMap.size(), mappedDifference.size());
        for (Map.Entry<Coordinates, Integer> entry: mappedDifference) {
            assertTrue(changedMap.containsKey(entry.getKey()));
            assertEquals(changedMap.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    void getDifferenceWithFlagsTest() {
        int[][] board = {{-1, -1, -1, -1},{-1, -1, -2, -1},{-1, -2, -1, -1},{-1, -2, -1, -1}};
        int[][] newBoard = {{-1, -1, 0, -1},{1, -2, -1, -1},{-1, -1, -1, -1},{-1, -1, -1, 0}};
        naiveSolver = new NaiveSolver(board,4,4);
        HashMap<Coordinates, Integer> changedMap = new HashMap<Coordinates, Integer>();
        changedMap.put(new Coordinates(0, 2), 0);
        changedMap.put(new Coordinates(1, 0), 1);
        changedMap.put(new Coordinates(1, 1), -2);
        changedMap.put(new Coordinates(3, 3), 0);

        Set<Map.Entry<Coordinates, Integer>> mappedDifference = naiveSolver.getDifference(newBoard).entrySet();
        assertEquals(changedMap.size(), mappedDifference.size());
        for (Map.Entry<Coordinates, Integer> entry: mappedDifference) {
            assertTrue(changedMap.containsKey(entry.getKey()));
            assertEquals(changedMap.get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    void updateTest() {
        int[][] board = {{-1, -1, -1, -1},{-1, -1, -1, -1},{-1, -1, -1, -1},{-1, -1, -1, -1}};
        int[][] changedBoard = {{1, -1, -1, -1},{-1, -1, -1, -1},{-1, -1, -1, -1},{-1, -1, -1, -1}};
        Coordinates[] changedCoordinates = {new Coordinates(0, 0)};
        int[] changedValues = {1};
        naiveSolver = new NaiveSolver(board, 4, 4);

        naiveSolver.update(changedCoordinates, changedValues);
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++) {
                assertEquals(board[i][j], changedBoard[i][j]);
            }
        assertEquals(changedCoordinates.length, naiveSolver.frontier.size());
        for (Coordinates coordinates: changedCoordinates)
            assertTrue(naiveSolver.frontier.contains(coordinates));

        changedBoard = new int[][]{{1, -1, -1, -1},{-1, -1, 8, 0},{7, -1, -1, -1},{-1, 5, 0, -2}};
        changedCoordinates = new Coordinates[]{new Coordinates(1, 2), new Coordinates(1, 3),
            new Coordinates(2, 0), new Coordinates(3, 1), new Coordinates(3, 2),
            new Coordinates(3, 3)};
        changedValues = new int[]{8,0,7,5,0,-2};

        naiveSolver.frontier.clear();
        naiveSolver.update(changedCoordinates, changedValues);
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++) {
                assertEquals(board[i][j], changedBoard[i][j]);
            }
        assertEquals(changedCoordinates.length, naiveSolver.frontier.size());
        for (Coordinates coordinates: changedCoordinates)
            assertTrue(naiveSolver.frontier.contains(coordinates));
    }

    @Test
    void updateWithKnownBombsTest() {
        int[][] board = {{-1, -2, -1, 3},{-2, 1, 5, -1},{0, -1, -1, -1},{-1, -1, -2, -1}};
        int[][] changedBoard = {{1, -1, -1, 3},{-1, 1, 5, -1},{0, -1, -1, -1},{-1, -1, -1, 0}};
        Coordinates[] changedCoordinates = {new Coordinates(0, 0), new Coordinates(3, 3)};
        int[] changedValues = {1, 0};
        naiveSolver = new NaiveSolver(board, 4, 4);

        naiveSolver.update(changedCoordinates, changedValues);
        changedBoard = new int[][]{{1, -2, -1, 3},{-2, 1, 5, -1},{0, -1, -1, -1},{-1, -1, -2, 0}};
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++) {
                assertEquals(board[i][j], changedBoard[i][j]);
            }
        assertEquals(naiveSolver.frontier.size(), 1);
        assertTrue(naiveSolver.frontier.contains(changedCoordinates[1]));
    }

    @Test
    void updateBoardTest() {
        int[][] board = {{-1, -2, -1, 3},{-2, 1, 5, -1},{0, -1, -1, -1},{-1, -1, -2, -1}};
        int[][] changedBoard = {{1, -1, -1, 3},{-1, 1, 5, -1},{0, -1, -1, -1},{-1, -1, -1, 0}};
        naiveSolver = new NaiveSolver(board, 4, 4);

        naiveSolver.updateBoard(changedBoard);
        changedBoard = new int[][]{{1, -2, -1, 3},{-2, 1, 5, -1},{0, -1, -1, -1},{-1, -1, -2, 0}};
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++) {
                assertEquals(board[i][j], changedBoard[i][j]);
            }
        assertEquals(naiveSolver.frontier.size(), 1);
        assertTrue(naiveSolver.frontier.contains(new Coordinates(3,3)));
    }

    @Test
    void findBombsTest() {
        int[][] board = {{3, -1, -1, 1}, {-1, -1, 3, 0}, {1, -1, 2, 1}, {-1, -1, -1, 1}};
        int[][] flaggedBoard = {{3, -2, -2, 1}, {-2, -2, 3, 0}, {1, -1, 2, 1}, {-1, -1, -1, 1}};
        Coordinates[] frontier = {new Coordinates(0, 0), new Coordinates(0,3), new Coordinates(1,2),
                new Coordinates(2, 0), new Coordinates(2,2)};
        Coordinates[] frontierPostFlagging = {new Coordinates(1,2), new Coordinates(2, 0),
                new Coordinates(2,2)};
        Coordinates[] flaggedCoordinates = {new Coordinates(0,1), new Coordinates(0,2),
                new Coordinates(1, 0), new Coordinates(1, 1)};

        naiveSolver = new NaiveSolver(board, 4, 4);
        naiveSolver.frontier.addAll(Arrays.asList(frontier));
        naiveSolver.findBombs();

        assertEquals(naiveSolver.frontier.size(), frontierPostFlagging.length);
        for (Coordinates coordinates: frontierPostFlagging)
            assertTrue(naiveSolver.frontier.contains(coordinates));
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                assertEquals(flaggedBoard[i][j], naiveSolver.board[i][j]);
            }
        assertEquals(flaggedCoordinates.length, naiveSolver.toFlag.size());
        for (Coordinates coordinates: flaggedCoordinates)
            assertTrue(naiveSolver.toFlag.contains(coordinates));
    }

    @Test
    void searchWithCertainNumberCells() {
        int[][] board = {{-1, -1, -1}, {-1, -1, -1}, {-1, -1, -1}};
        naiveSolver = new NaiveSolver(board, 3, 3);
        naiveSolver.certainNumberCells.add(new Coordinates(2,1));
        naiveSolver.certainNumberCells.add(new Coordinates(2,2));

        Coordinates nextCoordinates = naiveSolver.search();
        assertEquals(nextCoordinates.x(), 2);
        assertEquals(nextCoordinates.y(), 1);
        assertEquals(naiveSolver.certainNumberCells.size(), 1);
        assertEquals(naiveSolver.certainNumberCells.get(0).x(), 2);
        assertEquals(naiveSolver.certainNumberCells.get(0).y(), 2);
    }

    @Test
    void searchWithNoCertainMoveTest() {
        int[][] board = {{-1, -1, -1}, {-1, 1, -1}, {-1, -1, -1}};
        naiveSolver = new NaiveSolver(board, 3, 3);
        ArrayList<Coordinates> elementsToExplore = new ArrayList<Coordinates>();

        Iterator<Coordinates> iterator = new NaiveSolver.CoordinateIterable(new Coordinates(1,1),3,3)
                .iterator();
        while (iterator.hasNext())
            elementsToExplore.add(iterator.next());

        naiveSolver.frontier.add(new Coordinates(1,1));
        while (!elementsToExplore.isEmpty()) {
            Coordinates step = naiveSolver.search();
            assertTrue(elementsToExplore.contains(step));
            naiveSolver.board[step.x()][step.y()] = 1;
            elementsToExplore.remove(step);
        }
        assertEquals(naiveSolver.certainNumberCells.size(), 0);
    }

    @Test
    void searchSingleCellSearchTest() {
        int[][] board = {{1,-1},{2,-1}, {2,-1}, {1,-2}};
        naiveSolver = new NaiveSolver(board, 4, 2);
        Coordinates[] frontier = {new Coordinates(0,0), new Coordinates(1,0), new Coordinates(2,0),
                new Coordinates(3,0)};

        for (Coordinates coordinates: frontier)
            naiveSolver.frontier.add(coordinates);
        Coordinates step = naiveSolver.search();
        assertEquals(step.x(),2);
        assertEquals(step.y(),1);
        assertEquals(naiveSolver.frontier.size(), frontier.length - 1);
        assertFalse(naiveSolver.frontier.contains(new Coordinates(3,0)));
        assertEquals(naiveSolver.certainNumberCells.size(), 0);
    }

    @Test
    void searchSingleCellSearchWithCertainNumberCellsTest() {
        int[][] board = {{1,-1},{1,-2}, {1,-1}};
        naiveSolver = new NaiveSolver(board, 3, 2);
        Coordinates[] frontier = {new Coordinates(1,0)};

        for (Coordinates coordinates: frontier)
            naiveSolver.frontier.add(coordinates);
        Coordinates step = naiveSolver.search();
        assertEquals(step.x(),0);
        assertEquals(step.y(),1);
        assertEquals(naiveSolver.frontier.size(), 0);
        assertEquals(naiveSolver.certainNumberCells.size(), 1);
        assertTrue(naiveSolver.certainNumberCells.contains(new Coordinates(2,1)));
    }
}
