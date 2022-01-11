import ModelMinesweeper.Grid;
import Solver.NaiveSolver;
import Solver.Coordinates;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Grid grid = new Grid();
        Scanner scanner = new Scanner(System.in);
        try {
            int[][] board = grid.parseGridIntoSolverMatrix();
            NaiveSolver s = new NaiveSolver(board, Grid.LONG_SIDE, Grid.SHORT_SIDE);
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

                s.updateBoard(board);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
