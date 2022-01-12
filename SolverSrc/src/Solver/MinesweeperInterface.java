package Solver;

import java.util.ArrayList;

public interface MinesweeperInterface {

    public void openCells(ArrayList<Coordinates> toOpen);
    public void flagCells(ArrayList<Coordinates> toFlag);
    public int[][] getBoard();
    public int getLongSideLength();
    public int getShortSideLength();
    public void reset();
}
