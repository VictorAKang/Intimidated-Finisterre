package Solver;

import java.util.ArrayList;

public interface SolverAdapter {

    public Coordinates step();
    public ArrayList<Coordinates> getNextSteps();
    public void updateBoard(int[][] newBoard);
    public ArrayList<Coordinates> getFlags();
}
