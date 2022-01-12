package Solver;

import ModelMinesweeper.Grid;

import java.util.ArrayList;

public class SimpleMinesweeper implements MinesweeperInterface {
    Grid grid;

    public SimpleMinesweeper() {
        grid = new Grid();
    }

    @Override
    public void openCells(ArrayList<Coordinates> toOpen) {
        for (Coordinates coordinates: toOpen)
            grid.openCell(coordinates.y(), coordinates.x());
    }

    @Override
    public void flagCells(ArrayList<Coordinates> toFlag) {
        for (Coordinates coordinate: toFlag)
            grid.flagCell(coordinate.x(), coordinate.y());
    }

    @Override
    public int[][] getBoard() {
        grid.drawGrid();
        return grid.parseGridIntoSolverMatrix();
    }

    @Override
    public int getLongSideLength() {
        return Grid.LONG_SIDE;
    }

    @Override
    public int getShortSideLength() {
        return Grid.SHORT_SIDE;
    }

    @Override
    public void reset() {
        grid.genGrid();
    }
}
