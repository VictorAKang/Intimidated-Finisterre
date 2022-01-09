import ModelMinesweeper.Grid;
import Solver.Solver;
import Solver.Coordinates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        int[][] b = {{2, 3, 2},{-2, -2,-2},{-1, -1,-1}};
//        int sideLength = 3;
//        int numBombs = 1;
        Grid grid = new Grid();
        Scanner scanner = new Scanner(System.in);
        try {
            int[][] board = grid.parseGridIntoSolverMatrix();
            Solver s = new Solver(board, Grid.LONG_SIDE, Grid.SHORT_SIDE, Grid.NUM_MINES);
            Coordinates nextToOpen;

            while (true) {
                int stub = scanner.nextInt();
                nextToOpen = s.step();
                System.out.println(nextToOpen.x() + " " + nextToOpen.y());
                grid.openCell(nextToOpen.y(), nextToOpen.x());
                Coordinates[] toFlag = s.getFlags();
                for (Coordinates coordinate: toFlag)
                    grid.flagCell(coordinate.x(), coordinate.y());
                grid.drawGrid();
                board = grid.parseGridIntoSolverMatrix();

                Map<Coordinates, Integer> difference = s.getDifference(board);
                Coordinates[] changedCoordinates = new Coordinates[difference.size()];
                int[] changedValues = new int[difference.size()];

                int counter = 0;
                for (Map.Entry<Coordinates, Integer> entry: difference.entrySet()) {
                    changedCoordinates[counter] = entry.getKey();
                    changedValues[counter] = entry.getValue();
                    counter++;
                }

                s.update(changedCoordinates, changedValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
