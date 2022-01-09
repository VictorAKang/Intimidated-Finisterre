package Solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Solver {
    /**
        0-9 number on cell
        -2 flag
        -1 closed cell
     */
    private int[][] board;
    private int longSideLength;
    private int shortSideLength;
    private int numBombs;
    private ArrayList<Coordinates> frontier;

    private ArrayList<Coordinates> certainNumberCells;
    private ArrayList<Coordinates> toFlag;

    public Solver(int[][] board, int longSideLength, int shortSideLength, int numBombs) {
        if (shortSideLength < 1 || longSideLength < 1) throw new RuntimeException("Side must be larger than 0");
        if (numBombs < 1) throw new RuntimeException("There must be at least a single bomb");
        if (board.length != longSideLength) throw new RuntimeException("Board has side length different from what was used");
        for (int i = 0; i < longSideLength; i++)
            if (board[i].length != shortSideLength)
                throw new RuntimeException("Board has side length different from what was used");

        this.board = board;
        this.longSideLength = longSideLength;
        this.shortSideLength = shortSideLength;
        this.numBombs = numBombs;
//        frontier = new HashMap<>();
        this.frontier = new ArrayList<>();

        this.certainNumberCells = new ArrayList<Coordinates>();
        this.toFlag = new ArrayList<Coordinates>();
    }

    public Coordinates step() {
//        if (frontier.isEmpty()) return new Solver.Coordinates(1,1);
        if (frontier.size() == 0) return new Coordinates(longSideLength - 1,shortSideLength - 1);

//        Set<Map.Entry<Solver.Coordinates, Integer>> frontierPoints = frontier.entrySet();
        findBombs();
        return search();
    }

    public Map<Coordinates, Integer> getDifference(int[][] newBoard) {
        Map<Coordinates, Integer> returnMap = new HashMap<Coordinates, Integer>();

        for (int i = 0; i < longSideLength; i++)
            for (int j = 0; j < shortSideLength; j++)
                if (newBoard[i][j] != board[i][j] && board[i][j] != -2)
                    returnMap.put(new Coordinates(i,j), newBoard[i][j]);

        return returnMap;
    }

    public void update(Coordinates[] coordinates, int[] values) {
        if (coordinates.length != values.length) throw new RuntimeException("Number of elements must match");
        for (int i: values)
            if (i == -1) throw new RuntimeException("Solver.Solver failed...");

        int preUpdateFrontierSize = frontier.size();
        for (int i = 0; i < coordinates.length; i++) {
            board[coordinates[i].x()][coordinates[i].y()] = values[i];
            frontier.add(coordinates[i]);
        }
        for (int i = 0; i < preUpdateFrontierSize; i++)
            updateFrontierElement(frontier.get(i));
    }

    public Coordinates[] getFlags() {
        Coordinates[] toReturn = new Coordinates[toFlag.size()];
        for (int i = 0; i < toFlag.size(); i++)
            toReturn[i] = toFlag.get(i);
        toFlag.clear();
        return toReturn;
    }

    private void updateFrontierElement(Coordinates coordinate) {
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
                if (coordinate.x() + i >= 0 && coordinate.x() + i < longSideLength && coordinate.y() + j >= 0
                        && coordinate.y() + j < shortSideLength && !(i == 0 && j == 0)
                        && board[coordinate.x() + i][coordinate.y() + j] == -1) return;
        frontier.remove(coordinate);
    }

    private void findBombs() {
        for (int k = frontier.size() - 1; k >= 0; k--) {
            Coordinates coordinate = frontier.get(k);
            int numOpenCells = 0;
            int numBombCells = 0;
            int numSurroundingCells = 0;
            ArrayList<Coordinates> closedCells = new ArrayList<Coordinates>();
            for (int i = -1; i < 2; i++)
                for (int j = -1; j < 2; j++)
                    if (coordinate.x() + i >= 0 && coordinate.x() + i < longSideLength && coordinate.y() + j >= 0
                            && coordinate.y() + j < shortSideLength && !(i == 0 && j == 0)) {
                        numSurroundingCells++;
                        if (board[coordinate.x() + i][coordinate.y() + j] == -2) numBombCells++;
                        else if (board[coordinate.x() + i][coordinate.y() + j] > -1) numOpenCells++;
                        else closedCells.add(new Coordinates(coordinate.x() + i, coordinate.y() + j));
                    }
//            if (numBombCells + numOpenCells + board[coordinate.x()][coordinate.y()] == numSurroundingCells) {
            if (numBombCells + closedCells.size() == board[coordinate.x()][coordinate.y()]) {
                for (Coordinates bombCoordinates : closedCells)
                    board[bombCoordinates.x()][bombCoordinates.y()] = -2;
                toFlag.addAll(closedCells);
                frontier.remove(coordinate);
            }
        }
    }

//    private Solver.Coordinates search(Set<Map.Entry<Solver.Coordinates, Integer>> set) {
    private Coordinates search() {
//        Map.Entry<Solver.Coordinates, Integer>[] aux = (Map.Entry<Solver.Coordinates, Integer>[]) set.toArray();
        if (!certainNumberCells.isEmpty()) {
//            Coordinates poppedCoordinate = certainNumberCells.get(0);
//            certainNumberCells.remove(0);
//            return poppedCoordinate;
            return certainNumberCells.remove(0);
        }

        for (Coordinates coordinate: frontier) {
            int numAdjacentBombs = 0;
            ArrayList<Coordinates> closedCells = new ArrayList<Coordinates>();
            for (int i = -1; i < 2; i++)
                for (int j = -1; j < 2; j++)
                    if (coordinate.x() + i >= 0 && coordinate.x() + i < longSideLength && coordinate.y() + j >= 0
                            && coordinate.y() + j < shortSideLength && !(i == 0 && j == 0)) {
                        if (board[coordinate.x() + i][coordinate.y() + j] == -2) numAdjacentBombs++;
                        else if (board[coordinate.x() + i][coordinate.y() + j] == -1)
                            closedCells.add(new Coordinates(coordinate.x() + i, coordinate.y() + j));
                    }
            if (numAdjacentBombs == board[coordinate.x()][coordinate.y()] && !closedCells.isEmpty()) {
                Coordinates poppedCoordinate = closedCells.remove(0);
//                closedCells.remove(0);
                certainNumberCells.addAll(closedCells);
                return poppedCoordinate;
            }
        }

        Coordinates aux = frontier.get(0);
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
                if (aux.x() + i >= 0 && aux.x() + i < longSideLength && aux.y() + j >= 0
                        && aux.y() + j < shortSideLength && !(i == 0 && j == 0)
                        && board[aux.x() + i][aux.y() + j] == -1) return new Coordinates(aux.x() + i, aux.y()+ j);

        return new Coordinates(1,1);
    }
}
