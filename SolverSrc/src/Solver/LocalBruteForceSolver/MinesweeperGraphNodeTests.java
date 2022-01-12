package Solver.LocalBruteForceSolver;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class MinesweeperGraphNodeTests {

    @Test
    public void parseIntoValue() {
        MinesweeperGraphNode node = new MinesweeperGraphNode(2,3);
        assertEquals(node.parseIntoNumber(), -1);

        node.setKnown(true);
        node.setValue(2);
        assertEquals(node.parseIntoNumber(), 2);

        node.setBomb(true);
        assertEquals(node.parseIntoNumber(), -2);

        node.setKnown(false);
        assertEquals(node.parseIntoNumber(), -1);
    }
}
