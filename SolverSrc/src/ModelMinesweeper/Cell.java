// Code from https://github.com/VictorAKang/Brain-Flexer-Time-Waster

package ModelMinesweeper;

// this class represents a single cell of the grid
// an object of this class stores whether the cell has been opened, if it is a mine, if it is flagged,
// and how many mines it has adjacent to it (if the cell at question is not a mine)
public class Cell {
    private boolean isOpen; //true if  cell is open, false if closed
    private boolean isMine; //true if cell is mine, false if not
    private boolean isFlagged; //true if cell is flagged, false if not or is open
    private int adjacentBombs; //number of adjacent bombs, counts diagonal as adjacent

    public Cell() {
        isOpen = false;
        isMine = false;
        isFlagged = false;
        adjacentBombs = 0;
    }

    //EFFECTS: returns adjacentBombs in case it is open
    //                 F if cell is closed and isFlagged
    //                 # if cell is closed
    public String draw() {
        if (isOpen) {
            return Integer.toString(adjacentBombs);
        }
        if (isFlagged) {
            return "F";
        }
        return "#";
    }

    //EFFECTS: returns true if mine is open, false if it's not
    public boolean getIsOpen() {
        return isOpen;
    }

    //REQUIRES: must not be open
    //MODIFIES: this
    //EFFECTS: returns true if the cell is a mine,
    //         else, return false and change cell status to open and remove flag if necessary
    public boolean openCell() {
        if (isFlagged) {
            return false;
        }

        if (isMine) {
            System.out.println("Game Over");
            return true;
        }
        this.isOpen = true;
        return false;
    }

    //MODIFIES: this
    //EFFECTS: makes this cell a mine
    public void makeMine() {
        isMine = true;
        adjacentBombs = -1;
    }

    //EFFECTS: return true if cell is a mine, else return false
    public boolean getIsMine() {
        return isMine;
    }

    //REQUIRES: cell must be closed
    //MODIFIES: this
    //EFFECTS: change the status of whether cell is flagged or not
    public void changeMarking() {
        this.isFlagged = !this.isFlagged;
    }

    //EFFECTS: returns true if cell is flagged, false if not
    public boolean isFlagged() {
        return isFlagged;
    }

    //EFFECTS: return the amount of bombs adjacent to cell
    public int getAdjacentBombs() {
        return adjacentBombs;
    }

    //MODIFIES: this
    //EFFECTS: changes the value of the adjacentBombs
    public void setAdjacentBombs(int adjacent) {
        this.adjacentBombs = adjacent;
    }

//    public void setClosed() {
//        this.isOpen = false;
//    }
}
