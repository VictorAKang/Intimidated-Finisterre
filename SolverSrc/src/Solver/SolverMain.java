package Solver;

import Solver.LocalBruteForceSolver.LocalBruteForceSolver;
import Solver.NaiveSolver.NaiveSolver;

import java.util.ArrayList;
import java.util.Scanner;

public class SolverMain {
    SolverAdapter solver;
    MinesweeperInterface game;

    public SolverMain() {
        game = new SimpleMinesweeper();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        int mode = scanner.nextInt();
        chooseMode(mode);

        while (true) {
            int action = scanner.nextInt();
            if (action == 0) {
                game.reset();
                chooseMode(mode);
            }

            step();
        }
    }

    protected void chooseMode(int mode) {
        if (mode == 0)
            useNaiveSolver();
        else
            useLocalBruteForceSolver(mode);
    }

    protected void step() {
        ArrayList<Coordinates> nextSteps = new ArrayList<>();
        nextSteps.add(solver.step());
        nextSteps.addAll(solver.getNextSteps());

        game.openCells(nextSteps);
        game.flagCells(solver.getFlags());

        solver.updateBoard(game.getBoard());
    }

    protected void useNaiveSolver() {
        solver = new NaiveSolver(game.getBoard(), game.getLongSideLength(), game.getShortSideLength());
    }

    protected void useLocalBruteForceSolver(int searchWidth) {
        solver = new LocalBruteForceSolver(searchWidth, game.getLongSideLength(), game.getShortSideLength());
    }

    protected void resetGame() {
        game.reset();
    }
}
