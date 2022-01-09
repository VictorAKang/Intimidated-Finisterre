// Code from https://github.com/VictorAKang/Brain-Flexer-Time-Waster

package ModelMinesweeper;

import java.util.ArrayList;
import java.util.Random;

// represents the whole grid where the game will be played
// stores references to all cells that are part of the game
public class Grid {
    public static final int LONG_SIDE = 30; //CoordinateI
    public static final int SHORT_SIDE = 16; //CoordinateJ
    public static final int NUM_MINES = 99;
    public static final int TOTAL_NUM_CELLS = SHORT_SIDE * LONG_SIDE;

    public Cell[][] grid; //represents the field in the current state of the game

    public Grid() {
        grid = new Cell[LONG_SIDE][SHORT_SIDE];
        genGrid();
    }

    //MODIFIES: this
    //EFFECTS: returns a grid with randomly placed mines and
    //         all cells that are not mines set (correct values for adjacent bombs) to play the game
    public void genGrid() {
        ArrayList<Cell> toBeRandomized = new ArrayList<>();
        Cell referenceCell;

        for (int i = 0; i < NUM_MINES; i++) {
            referenceCell = new Cell();
            referenceCell.makeMine();

            toBeRandomized.add(referenceCell);
        }

        for (int i = 0; i < TOTAL_NUM_CELLS - NUM_MINES; i++) {
            referenceCell = new Cell();

            toBeRandomized.add(referenceCell);
        }

        ArrayList<Cell> randomizedField;
        randomizedField = shuffle(toBeRandomized);

        for (int i = 0; i < LONG_SIDE; i++) {
            for (int j = 0; j < SHORT_SIDE; j++) {
                grid[i][j] = randomizedField.get(j + (SHORT_SIDE * i));
            }
        }

        setAllNonMineCells();
    }

    //REQUIRES: input array must be of size TOTAL_NUM_CELLS
    //MODIFIES: input list
    //EFFECTS: shuffles the inputted list
    public ArrayList<Cell> shuffle(ArrayList<Cell> toBeRandomized) {
        Cell helperCell;
        Random rand = new Random();
        int swapIndex;

        for (int i = 0; i < 175; i++) {
            for (int j = 0; j < TOTAL_NUM_CELLS; j++) {
                swapIndex = rand.nextInt(TOTAL_NUM_CELLS);

                helperCell = toBeRandomized.get(swapIndex);
                toBeRandomized.set(swapIndex, toBeRandomized.get(j));
                toBeRandomized.set(j,helperCell);
            }
        }

        return toBeRandomized;
    }

    //MODIFIES: this
    //EFFECTS: set the value of adjacentMines of all nonMineCells to the correct value
    public void setAllNonMineCells() {
        for (int i = 0; i < LONG_SIDE; i++) {
            for (int j = 0; j < SHORT_SIDE; j++) {
                if (!grid[i][j].getIsMine()) {
                    grid[i][j].setAdjacentBombs(findAdjacentMines(i, j));
                }
            }
        }
    }

    //EFFECTS: returns the number of adjacent mines relative to a cell in the grid
    public int findAdjacentMines(int i, int j) {
        int answer = 0;
        int minI = i - 1;
        int maxI = i + 1;
        int minJ = j - 1;
        int maxJ = j + 1;

        if (i == 0) {
            minI = i;
        } else if (i == LONG_SIDE - 1) {
            maxI = i;
        }

        if (j == 0) {
            minJ = j;
        } else if (j == SHORT_SIDE - 1) {
            maxJ = j;
        }

        for (int p = minI; p <= maxI; p++) {
            for (int q = minJ; q <= maxJ; q++) {
                if (grid[p][q].getIsMine()) {
                    answer++;
                }
            }
        }

        return answer;
    }


    //REQUIRES: grid must have cells in it
    //EFFECTS: print a representation of the grid with a coordinate system on its edges
    public void drawGrid() {
        String line;
        System.out.println("    a b c d e f g h i j k l m n o p\n");

        for (int i = 0; i < 9; i++) {
            line = "";
            line += (i + 1) + "-  ";
            for (int j = 0; j < SHORT_SIDE; j++) {
                line += grid[i][j].draw() + " ";
            }
            System.out.println(line + "\n");
        }

        for (int i = 9; i < LONG_SIDE; i++) {
            line = "";
            line += (i + 1) + "- ";
            for (int j = 0; j < SHORT_SIDE; j++) {
                line += grid[i][j].draw() + " ";
            }
            System.out.println(line + "\n");
        }
    }

    //MODIFIES: this
    //EFFECTS: open the referenced cell and executes openAdjacent if referenced cell is not a mine and has no adjacent
    //         ones
    //         returns true if cell is a mine and false if not
    public boolean openCell(int coordinateI, int coordinateJ) {
        if (grid[coordinateJ][coordinateI].isFlagged()) {
            System.out.println("cell is flagged");
            return true;
        }

        boolean returnValue;
        returnValue = grid[coordinateJ][coordinateI].openCell();

        openAdjacent(coordinateI, coordinateJ);

        return returnValue;
    }

    //MODIFIES: this
    //EFFECTS: open all adjacent cells if the inputted one has 0 adjacent bombs, else do nothing
    //         repeats process for all adjacent bombs
    public void openAdjacent(int coordinateI, int coordinateJ) {

        if (grid[coordinateJ][coordinateI].getAdjacentBombs() == 0) {
            int minI = coordinateJ - 1;
            int maxI = coordinateJ + 1;
            int minJ = coordinateI - 1;
            int maxJ = coordinateI + 1;

            if (coordinateJ == 0) {
                minI = coordinateJ;
            } else if (coordinateJ == LONG_SIDE - 1) {
                maxI = coordinateJ;
            }

            if (coordinateI == 0) {
                minJ = coordinateI;
            } else if (coordinateI == SHORT_SIDE - 1) {
                maxJ = coordinateI;
            }

            openAdjacentHelper(coordinateI, coordinateJ, minI,maxI,minJ,maxJ);
        }
    }

    //MODIFIES: this
    //EFFECTS: opens all adjacent cells and calls this function for all adjacent ones that are 0
    public void openAdjacentHelper(int coordinateI, int coordinateJ, int minI, int maxI, int minJ, int maxJ) {
        boolean stubVar;

        for (int i = minI; i <= maxI; i++) {
            for (int j = minJ; j <= maxJ; j++) {
                if ((!(i == coordinateJ && j == coordinateI)) && !grid[i][j].getIsOpen()) {
                    stubVar = grid[i][j].openCell();
                    openAdjacent(j, i);
                }
            }
        }
    }

    //MODIFIES: referenced cell
    //EFFECTS: change the state of isFlagged of the referenced cell
    public void flagCell(int coordinateI, int coordinateJ) {
        grid[coordinateI][coordinateJ].changeMarking();
    }


    //EFFECTS: returns true if cell is flagged
    public boolean isFlagged(int coordinateI, int coordinateJ) throws RuntimeException {
        checkCoordinatesRange(coordinateI, coordinateJ);

        return grid[coordinateI][coordinateJ].isFlagged();
    }

    private void checkCoordinatesRange(int coordinateI, int coordinateJ) {
        if (coordinateI >= LONG_SIDE || coordinateJ >= SHORT_SIDE || coordinateI < 0 || coordinateJ < 0) {
            throw new RuntimeException();
        }
    }

    //EFFECTS: return true if cell is a mine
    public boolean isMine(int coordinateI, int coordinateJ) throws RuntimeException {
        checkCoordinatesRange(coordinateI, coordinateJ);

        return grid[coordinateI][coordinateJ].getIsMine();
    }

    //EFFECTS: returns the number that is in the cell being passed on
    public int getNumber(int coordinateI, int coordinateJ) {
        return grid[coordinateI][coordinateJ].getAdjacentBombs();
    }

    //EFFECTS: sets the cell to be open
    public void setOpen(int coordinateI, int coordinateJ) {
        grid[coordinateI][coordinateJ].openCell();
    }

    //EFFECTS: returns true if cell is open
    public boolean getIsOpen(int coordinateI, int coordinateJ) {
        return grid[coordinateI][coordinateJ].getIsOpen();
    }

//    public void setAllClosed() {
//        for (int i = 0; i < LONG_SIDE; i++) {
//            for (int j = 0; j < SHORT_SIDE; j++) {
//                grid[i][j].setClosed();
//            }
//        }
//    }

    public int[][] parseGridIntoSolverMatrix() {
        int[][] returnArray = new int[LONG_SIDE][];

        for (int i = 0; i < LONG_SIDE; i++) {
            int[] aux = new int[SHORT_SIDE];
            for (int j = 0; j < SHORT_SIDE; j++) {
                Cell currentCell = grid[i][j];
                if (currentCell.getIsOpen()) aux[j] = currentCell.getAdjacentBombs();
                else if (currentCell.isFlagged()) aux[j] = -2;
                else aux[j] = -1;
            }
            returnArray[i] = aux;
        }

        return returnArray;
    }
}