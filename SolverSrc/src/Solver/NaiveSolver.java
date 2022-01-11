package Solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A naive solver for minesweeper.
 *
 * Determines if a cell is a bomb by looking at the contents of a single cell at a time and checking if the adjacent cells
 * are bombs
 */
public class NaiveSolver {
    /**
        0-9 number on cell
        -2 flag
        -1 closed cell
     */
    protected int[][] board;
    protected int longSideLength;
    protected int shortSideLength;
    protected ArrayList<Coordinates> frontier;

    protected ArrayList<Coordinates> certainNumberCells;
    protected ArrayList<Coordinates> toFlag;

    /**
     *
     * @param board Matrix representation of the board to be solved, must match the given lengths with x being the longer axis
     * @param longSideLength The size of the long side of the board, must be non-zero positive number
     * @param shortSideLength The size of the short side of the board, must be non-zero positive number
     */
    public NaiveSolver(int[][] board, int longSideLength, int shortSideLength) {
        if (shortSideLength < 1 || longSideLength < 1) throw new RuntimeException("Side must be larger than 0");
        if (board.length != longSideLength) throw new RuntimeException("Board has side length different from what was used");
        for (int i = 0; i < longSideLength; i++)
            if (board[i].length != shortSideLength)
                throw new RuntimeException("Board has side length different from what was used");

        this.board = board;
        this.longSideLength = longSideLength;
        this.shortSideLength = shortSideLength;
        this.frontier = new ArrayList<>();

        this.certainNumberCells = new ArrayList<Coordinates>();
        this.toFlag = new ArrayList<Coordinates>();
    }

    /**
     * Gives the coordinates of the next cell to be opened
     *
     * @return the Coordinates of the next cell to be opened
     */
    public Coordinates step() {
        if (frontier.size() == 0) return new Coordinates(longSideLength - 1,shortSideLength - 1);
        findBombs();
        return search();
    }

    /**
     * Generates a map of cell coordinates that have different values between the field board and the parameter newBoard
     * to the value in newBoard
     * Cells that have value -2 (mines) in the field board are not added to the map
     *
     * @param newBoard the new board that is being compared
     *
     * @return a map of the difference between field and parameter
     */
    protected Map<Coordinates, Integer> getDifference(int[][] newBoard) {
        Map<Coordinates, Integer> returnMap = new HashMap<Coordinates, Integer>();

        for (int i = 0; i < longSideLength; i++)
            for (int j = 0; j < shortSideLength; j++)
                if (newBoard[i][j] != board[i][j] && board[i][j] != -2)
                    returnMap.put(new Coordinates(i,j), newBoard[i][j]);

        return returnMap;
    }

    /**
     * Updates the solver with the new state of the board
     *
     * @param newBoard The new state of the board
     */
    public void updateBoard(int[][] newBoard) {
        Map<Coordinates, Integer> difference = getDifference(newBoard);
        Coordinates[] changedCoordinates = new Coordinates[difference.size()];
        int[] changedValues = new int[difference.size()];

        int counter = 0;
        for (Map.Entry<Coordinates, Integer> entry: difference.entrySet()) {
            changedCoordinates[counter] = entry.getKey();
            changedValues[counter] = entry.getValue();
            counter++;
        }

        update(changedCoordinates, changedValues);
    }

    /**
     * Updates the object with the changed cells
     *
     * @param coordinates The list of coordinates of the cells that have been changed
     * @param values The list of values of the cells that have been changed
     */
    protected void update(Coordinates[] coordinates, int[] values) {
        for (int i = 0; i < coordinates.length; i++) {
            board[coordinates[i].x()][coordinates[i].y()] = values[i];
            frontier.add(coordinates[i]);
        }
        for (int i = frontier.size() - 1; i >= 0; i--)
            updateFrontierElement(frontier.get(i));
    }

    public Coordinates[] getFlags() {
        Coordinates[] toReturn = new Coordinates[toFlag.size()];
        for (int i = 0; i < toFlag.size(); i++)
            toReturn[i] = toFlag.get(i);
        toFlag.clear();
        return toReturn;
    }

    /**
     * removes the cell from the frontier if it has no closed cells adjacent to it
     *
     * @param coordinate The coordinate of the cell being analyzed
     */
    protected void updateFrontierElement(Coordinates coordinate) {
        Iterator<Coordinates> iterator = new CoordinateIterable(coordinate, longSideLength, shortSideLength).iterator();
        while (iterator.hasNext()) {
            Coordinates currentCoordinates = iterator.next();
            if (board[currentCoordinates.x()][currentCoordinates.y()] == -1) return;
        }
        frontier.remove(coordinate);
    }

    /**
     * Finds and marks down bombs
     * Adds findings to toFlag field so minesweeper game can be flagged as well
     */
    protected void findBombs() {
        for (int k = frontier.size() - 1; k >= 0; k--) {
            Coordinates coordinate = frontier.get(k);
            int numOpenCells = 0;
            int numBombCells = 0;
            ArrayList<Coordinates> closedCells = new ArrayList<Coordinates>();
            Iterator<Coordinates> iterator = new CoordinateIterable(coordinate, longSideLength, shortSideLength).iterator();
            while (iterator.hasNext()) {
                Coordinates currentCoordinates = iterator.next();
                if (board[currentCoordinates.x()][currentCoordinates.y()] == -2) numBombCells++;
                else if (board[currentCoordinates.x()][currentCoordinates.y()] > -1) numOpenCells++;
                else closedCells.add(new Coordinates(currentCoordinates.x(), currentCoordinates.y()));
            }
            if (numBombCells + closedCells.size() == board[coordinate.x()][coordinate.y()]) {
                for (Coordinates bombCoordinates : closedCells)
                    board[bombCoordinates.x()][bombCoordinates.y()] = -2;
                toFlag.addAll(closedCells);
                frontier.remove(coordinate);
            }
        }
    }

    /**
     * Searches the frontier for a certain mine free cell
     *
     * @return The coordinates of the solver's guess of a mine free cell
     */
    protected Coordinates search() {
        if (!certainNumberCells.isEmpty()) {
            return certainNumberCells.remove(0);
        }

        for (Coordinates coordinate: frontier) {
            int numAdjacentBombs = 0;
            ArrayList<Coordinates> closedCells = new ArrayList<Coordinates>();
            Iterator<Coordinates> iterator = new CoordinateIterable(coordinate, longSideLength, shortSideLength)
                    .iterator();
            while(iterator.hasNext()) {
                Coordinates current = iterator.next();
                if (board[current.x()][current.y()] == -2) numAdjacentBombs++;
                else if (board[current.x()][current.y()] == -1)
                    closedCells.add(new Coordinates(current.x(), current.y()));
            }
            if (numAdjacentBombs == board[coordinate.x()][coordinate.y()] && !closedCells.isEmpty()) {
                Coordinates poppedCoordinate = closedCells.remove(0);
                certainNumberCells.addAll(closedCells);
                frontier.remove(coordinate);
                return poppedCoordinate;
            }
        }

        Coordinates aux = frontier.get(0);

        Iterator<Coordinates> iterator = new CoordinateIterable(aux,longSideLength,shortSideLength).iterator();
        while (iterator.hasNext()) {
            Coordinates current = iterator.next();
            if (board[current.x()][current.y()] == -1) return new Coordinates(current.x(), current.y());
        }

        return new Coordinates(0,0);
    }


    /**
     * Helper class to iterate through adjacent cells
     */
    protected static class AdjacentCellIterator implements Iterator<Coordinates> {

        private Coordinates coordinate;
        private int longSide, shortSide;
        private int i, j;

        public AdjacentCellIterator(Coordinates coordinate, int longSide, int shortSide) {
            this.coordinate = coordinate;
            this.longSide = longSide;
            this.shortSide = shortSide;
            i = -1;
            j = -1;
        }

        @Override
        public boolean hasNext() {
            boolean first = true;
            for (int auxI = i; auxI < 2; auxI++) {
                for (int auxJ = -1 ; auxJ < 2; auxJ++) {
                    if (first) {auxJ = j; first = false;}
                    if (checkInGrid(auxI, auxJ) && auxJ < 2)
                        return true;
                }
            }
            return false;
        }

        @Override
        public Coordinates next() {
            while (!checkInGrid(i, j) || i > 1 || j > 1) {
                j++;
                if (j > 1) {
                    j = -1;
                    i++;
                }
            }
            j++;
            return new Coordinates(coordinate.x() + i, coordinate.y() + j - 1);
        }

        private boolean checkInGrid(int i, int j) {
            return coordinate.x() + i >= 0 && coordinate.x() + i < longSide && coordinate.y() + j >= 0
                    && coordinate.y() + j < shortSide && !(i == 0 && j == 0);
        }
    }

    protected static class CoordinateIterable implements Iterable<Coordinates> {
        private Coordinates coordinate;
        private int longSide, shortSide;

        public CoordinateIterable (Coordinates coordinate, int longSide, int shortSide) {
            this.coordinate = coordinate;
            this.longSide = longSide;
            this.shortSide = shortSide;
        }

        @Override
        public Iterator<Coordinates> iterator() {
            return new AdjacentCellIterator(coordinate, longSide, shortSide);
        }

    }
}
