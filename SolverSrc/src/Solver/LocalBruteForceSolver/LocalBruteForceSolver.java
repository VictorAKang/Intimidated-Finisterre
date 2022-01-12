package Solver.LocalBruteForceSolver;

import Solver.Coordinates;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Brute force solver for minesweeper
 *
 * Determine bombs and guesses by brute forcing possibilities in a fixed sized area around a cell in the frontier
 */
public class LocalBruteForceSolver {
    protected MinesweeperGraphNode[][] board;
    protected int longSideLength;
    protected int shortSideLength;
    protected int searchWidth;

    protected ArrayList<MinesweeperGraphNode> frontier;
    protected ArrayList<MinesweeperGraphNode> certainNumberCells;
    protected ArrayList<MinesweeperGraphNode> toFlag;

    public LocalBruteForceSolver(int searchWidth, int longSideLength, int shortSideLength) {
        board = new MinesweeperGraphNode[longSideLength][shortSideLength];
        this.longSideLength = longSideLength;
        this.shortSideLength = shortSideLength;
        this.searchWidth = searchWidth;

        frontier = new ArrayList<>();
        certainNumberCells = new ArrayList<>();
        toFlag = new ArrayList<>();

        for (int i = 0; i < longSideLength; i++)
            for (int j = 0; j < shortSideLength; j++)
                board[i][j] = new MinesweeperGraphNode(i,j);

        setNeighbours();
    }

    /**
     * Sets the neighbours of all nodes in the board
     */
    protected void setNeighbours() {
        for (int i = 0; i < longSideLength; i++)
            for (int j = 0; j < shortSideLength; j++) {
                ArrayList<MinesweeperGraphNode> neighbours = getNeighbours(i, j);
                board[i][j].addNeighbours(neighbours);
            }
    }

    /**
     * Gets the neighbour nodes to the cell at position (x,y)
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The list of neighbour nodes
     */
    protected ArrayList<MinesweeperGraphNode> getNeighbours(int x, int y) {
        ArrayList<MinesweeperGraphNode> neighbours = new ArrayList<>();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                if (x + i >= 0 && x + i < longSideLength && y + j >= 0 && y +j < shortSideLength && !(i == 0 && j ==0))
                    neighbours.add(board[x + i][y + j]);
        return neighbours;
    }

    /**
     * Returns the coordinate of the next cell to open
     * @return The coordinates of the next cell to open
     */
    public Coordinates step() {
        MinesweeperGraphNode node = search();
        return new Coordinates(node.getX(), node.getY());
    }

    public ArrayList<Coordinates> getNextSteps() {
        ArrayList<Coordinates> returnArray = new ArrayList<>();

        for (MinesweeperGraphNode node: certainNumberCells)
            returnArray.add(new Coordinates(node.getX(), node.getY()));

        certainNumberCells.clear();
        return returnArray;
    }

    /**
     * Makes the search for the next cell to open. Guesses if necessary
     *
     * @return The node of the next cell to open
     */
    protected MinesweeperGraphNode search() {
        if (!certainNumberCells.isEmpty())
            return certainNumberCells.remove(0);

        for (MinesweeperGraphNode node: frontier) {
            bruteForceWrapper(node);
            if (!certainNumberCells.isEmpty()) {
                frontier.remove(node);
                return certainNumberCells.remove(0);
            }
        }

        for (MinesweeperGraphNode node: frontier)
            for (MinesweeperGraphNode neighbour: node.getNeighbours())
                if (!neighbour.getKnown())
                    return neighbour;
        return new MinesweeperGraphNode(0,0);
    }

    /**
     * Does brute force search around centralNode with the specified width
     * Updates toFlag and certainNumberCells according to certain findings of the search
     * @param centralNode The central node of the search
     */
    protected void bruteForceWrapper(MinesweeperGraphNode centralNode) {
        ArrayList<MinesweeperGraphNode> searchFrontier = getSearchFrontier(centralNode);
        ArrayList<MinesweeperGraphNode> searchClosedCells = getClosedNeighbourCells(searchFrontier);
        Map<MinesweeperGraphNode, Integer> bombFrequency = makeInitialBombFrequencyMap(searchClosedCells);

        int possibilities = bruteForce(searchFrontier, searchClosedCells, bombFrequency,
                searchClosedCells.size() - 1);

        for (MinesweeperGraphNode node: searchClosedCells) {
            int frequency = bombFrequency.get(node);
            if (frequency == 0)
                certainNumberCells.add(node);
            if (frequency == possibilities && !toFlag.contains(node))
                toFlag.add(node);
        }
    }

    /**
     * Does brute force search on the bomb assignments for the nodes in searchClosedCells
     * Updates bombFrequency map to reflect the frequency of when the cells were bombs
     * Returns the number of different valid assignments given current state
     * Recursive call, first call should be called with currentCellIndex at the size minus one of the searchClosedCells
     *
     * @param searchFrontier The list of nodes in the search frontier
     * @param searchClosedCells The list of cells being searched
     * @param bombFrequency The frequency map
     * @param currentCellIndex The index of the node currently being searched
     * @return The number of possible valid assignments given the current state
     */
    protected int bruteForce(ArrayList<MinesweeperGraphNode> searchFrontier,
                             ArrayList<MinesweeperGraphNode> searchClosedCells,
                             Map<MinesweeperGraphNode, Integer> bombFrequency,
                             int currentCellIndex) {
        if (isStateInvalid(searchClosedCells)) return 0;
        if (currentCellIndex < 0) {
            if (!doesStateSatisfyFrontier(searchFrontier)) return 0;
            updateBombFrequency(searchClosedCells, bombFrequency);
            return 1;
        }

        MinesweeperGraphNode currentNode = searchClosedCells.get(currentCellIndex);
        int possibilities = 0;

        currentNode.setKnown(true);
        currentNode.setBomb(true);
        possibilities += bruteForce(searchFrontier, searchClosedCells, bombFrequency, currentCellIndex - 1);
        currentNode.setBomb(false);
        possibilities += bruteForce(searchFrontier, searchClosedCells, bombFrequency, currentCellIndex - 1);
        currentNode.setKnown(false);

        return possibilities;
    }

    /**
     * Checks if the current assignment is 1. completely assigned with all closed searched nodes being assigned either
     * bomb or not bomb; 2. all nodes in the frontier have exactly the correct number of bombs adjacent to it
     * @param searchFrontier The list of nodes in the search frontier
     * @return True if the state satisfies the frontier constraints, false otherwise
     */
    protected boolean doesStateSatisfyFrontier(ArrayList<MinesweeperGraphNode> searchFrontier) {
        for (MinesweeperGraphNode node: searchFrontier) {
            if (checkAdjacentForClosedCells(node)) return false;
            int numBombs = 0;
            for (MinesweeperGraphNode neighbour: node.getNeighbours())
                if (neighbour.getBomb()) numBombs++;
            if (numBombs != node.value) return false;
        }
        return true;
    }

    /**
     * Updates bombFrequency map to have updated data on which cells were bombs
     *
     * @param searchClosedCells The list of searched cells in this search
     * @param bombFrequency The frequency map to be updated
     */
    protected void updateBombFrequency(ArrayList<MinesweeperGraphNode> searchClosedCells,
                                       Map<MinesweeperGraphNode, Integer> bombFrequency) {
        for (MinesweeperGraphNode node: searchClosedCells)
            if (node.getBomb())
                bombFrequency.put(node, bombFrequency.get(node) + 1);
    }

    /**
     * Checks if the current bomb assignment makes the board invalid
     *
     * @param searchClosedCells The nodes that are being assigned in the search, can still not be assigned
     * @return True if the current assignment does not invalidate board
     */
    protected boolean isStateInvalid(ArrayList<MinesweeperGraphNode> searchClosedCells) {
        ArrayList<MinesweeperGraphNode> visited = new ArrayList<>();

        for (MinesweeperGraphNode node: searchClosedCells)
            if (node.getKnown()) {
                for (MinesweeperGraphNode neighbour: node.getNeighbours()) {
                    if (neighbour.getKnown() && !neighbour.getBomb() && !visited.contains(neighbour) &&
                            !searchClosedCells.contains(neighbour)) {
                        int numAdjacentBombs = countAdjacentBombs(neighbour);
                        if (numAdjacentBombs > neighbour.getValue()) return true;
                        visited.add(neighbour);
                    }
                }
            }

        return false;
    }

    /**
     * Counts the number of known bombs adjacent to the parameter node
     * @param node The node to have adjacent bombs counted
     * @return The number of bombs adjacent to the parameter node
     */
    protected int countAdjacentBombs(MinesweeperGraphNode node) {
        int adjacentBombs = 0;

        for (MinesweeperGraphNode neighbours: node.getNeighbours())
            if (neighbours.getKnown() && neighbours.getBomb()) adjacentBombs++;

        return adjacentBombs;
    }

    /**
     * Returns an arrayList of number nodes with unknown neighbours that are at most distance
     * searchWidth of the centralNode
     *
     * @param centralNode The central node of the search
     * @return ArrayList of number nodes with unknown neighbours that are at most distance searchWidth
     */
    protected ArrayList<MinesweeperGraphNode> getSearchFrontier(MinesweeperGraphNode centralNode) {
        ArrayList<MinesweeperGraphNode> searchFrontier = new ArrayList<>();
        Set<MinesweeperGraphNode> toGetFrontier = new HashSet<>();

        searchFrontier.add(centralNode);
        toGetFrontier.add(centralNode);
        for (int i = 0; i < searchWidth; i++) {
            Set<MinesweeperGraphNode> auxSet = new HashSet<>();
            for (MinesweeperGraphNode node : toGetFrontier) {
                for (MinesweeperGraphNode neighbour : node.getNeighbours()) {
                    if (neighbour.getKnown() && !neighbour.getBomb() && checkAdjacentForClosedCells(neighbour) &&
                            !searchFrontier.contains(neighbour)) {
                        searchFrontier.add(neighbour);
                        auxSet.add(neighbour);
                    }
                }
            }
            toGetFrontier.clear();
            toGetFrontier.addAll(auxSet);
        }

        return searchFrontier;
    }

    /**
     * Gets the closed nodes adjacent to the frontier nodes
     * @param searchFrontier The list of nodes being searched
     * @return A list of closed nodes adjacent to the parameter nodes
     */
    protected ArrayList<MinesweeperGraphNode> getClosedNeighbourCells(ArrayList<MinesweeperGraphNode> searchFrontier) {
        ArrayList<MinesweeperGraphNode> closedNeighbourCells = new ArrayList<>();

        for (MinesweeperGraphNode node: searchFrontier)
            for (MinesweeperGraphNode neighbour: node.getNeighbours())
                if (!neighbour.getKnown() && !closedNeighbourCells.contains(neighbour))
                    closedNeighbourCells.add(neighbour);

        return closedNeighbourCells;
    }

    /**
     * Maps all nodes to 0
     * Setup for brute force search
     *
     * @param closedCells The nodes to be mapped
     * @return A map where keys are the nodes from closedCells and all map to 0
     */
    protected Map<MinesweeperGraphNode, Integer> makeInitialBombFrequencyMap(ArrayList<MinesweeperGraphNode> closedCells) {
        Map<MinesweeperGraphNode, Integer> returnMap = new HashMap<>();

        for (MinesweeperGraphNode node: closedCells)
            returnMap.put(node, 0);

        return returnMap;
    }

    /**
     * Checks the adjacent cells to the parameter node and check if any of them are closed cells
     * @param node The node being checked
     * @return Returns true if at least one adjacent cell is closed, Returns false otherwise
     */
    protected boolean checkAdjacentForClosedCells(MinesweeperGraphNode node) {
        for (MinesweeperGraphNode neighbour: node.getNeighbours())
            if (!neighbour.getKnown()) return true;
        return false;
    }

    /**
     * Parses the list of nodes to flag to a list of coordinates and returns it
     * Update nodes that were flagged to known bombs
     * @return The list of coordinates to flag as bombs
     */
    public ArrayList<Coordinates> getFlags() {
        ArrayList<Coordinates> returnArray = new ArrayList<>();
        for (MinesweeperGraphNode node: toFlag) {
            returnArray.add(new Coordinates(node.getX(), node.getY()));
            node.setKnown(true);
            node.setBomb(true);
        }
        toFlag.clear();
        return returnArray;
    }

    /**
     * Updates the board according to the new board state newBoard
     * Updates frontier, so it only contains nodes that are in fact in the frontier
     * @param newBoard The new state of the board
     */
    public void updateBoard(int[][] newBoard) {
        ArrayList<MinesweeperGraphNode> difference = getDifference(newBoard);

        for (MinesweeperGraphNode node: difference) {
            node.setKnown(true);
            node.setValue(newBoard[node.getX()][node.getY()]);
            if (!frontier.contains(node))
                frontier.add(node);
        }

        frontier.removeIf(node -> !checkAdjacentForClosedCells(node));
    }

    /**
     * Returns an array of the nodes that differ from the newBoard parameter
     * Ignores the nodes that are deemed as known bombs by the solver
     * @param newBoard The new state of the board
     * @return An array of the nodes that differ
     */
    protected ArrayList<MinesweeperGraphNode> getDifference(int[][] newBoard) {
        ArrayList<MinesweeperGraphNode> difference = new ArrayList<>();

        for (int i = 0; i < longSideLength; i++)
            for (int j = 0; j < shortSideLength; j++)
                if (newBoard[i][j] != board[i][j].parseIntoNumber() && board[i][j].parseIntoNumber() != -2)
                    difference.add(board[i][j]);

        return difference;
    }
}
