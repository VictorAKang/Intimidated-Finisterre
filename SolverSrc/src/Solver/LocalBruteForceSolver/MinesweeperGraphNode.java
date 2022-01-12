package Solver.LocalBruteForceSolver;

import java.util.ArrayList;

/**
 * The node of a graph which represents the minesweeper board
 */
public class MinesweeperGraphNode {
    protected ArrayList<MinesweeperGraphNode> neighbours;
    protected boolean known;
    protected boolean bomb;
    protected int value;
    protected int x;
    protected int y;

    public MinesweeperGraphNode(int x, int y) {
        neighbours = new ArrayList<>();
        known = false;
        bomb = false;
        value = -1;
        this.x = x;
        this.y = y;
    }

    public void addNeighbours(MinesweeperGraphNode node) {
        if (!neighbours.contains(node))
            neighbours.add(node);
    }

    public void addNeighbours(ArrayList<MinesweeperGraphNode> neighbourNodes) {
        for (MinesweeperGraphNode node: neighbourNodes)
            addNeighbours(node);
    }

    public int parseIntoNumber() {
        if (!known) return -1;
        if (bomb) return -2;
        return value;
    }

    public ArrayList<MinesweeperGraphNode> getNeighbours () {
        return neighbours;
    }

    public boolean getKnown() {
        return known;
    }

    public boolean getBomb() {
        return bomb;
    }

    public void setBomb(boolean isBomb) {
        bomb = isBomb;
    }

    public void setKnown(boolean isKnown) {
        known = isKnown;
    }

    public int getValue() {
        return value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
