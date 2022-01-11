package Solver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;


public class AdjacentCellIteratorTests {
    Iterator<Coordinates> iterator;

    @Test
    void eightAdjacentTest() {
        Coordinates coordinates = new Coordinates(5,13);
        iterator = new NaiveSolver.CoordinateIterable(coordinates, 20, 15).iterator();
        iteratorResultCheckerHelper(coordinates, iterator, -1, 1, -1 ,1);
    }

    @Test
    void cellTouchingSingleWallTest() {
        Coordinates coordinates = new Coordinates(0,2);
        iterator = new NaiveSolver.CoordinateIterable(coordinates,5, 4).iterator();
        iteratorResultCheckerHelper(coordinates, iterator, 0, 1, -1, 1);

        coordinates = new Coordinates(3,0);
        iterator = new NaiveSolver.CoordinateIterable(coordinates,6, 4).iterator();
        iteratorResultCheckerHelper(coordinates, iterator, -1, 1, 0, 1);

        coordinates = new Coordinates(4,2);
        iterator = new NaiveSolver.CoordinateIterable(coordinates,5, 4).iterator();
        iteratorResultCheckerHelper(coordinates, iterator, -1, 0, -1, 1);

        coordinates = new Coordinates(3,3);
        iterator = new NaiveSolver.CoordinateIterable(coordinates,6, 4).iterator();
        iteratorResultCheckerHelper(coordinates, iterator, -1, 1, -1, 0);
    }

    @Test
    void cellInCornerTest() {
        Coordinates coordinates = new Coordinates(0,0);
        iterator = new NaiveSolver.CoordinateIterable(coordinates,5, 3).iterator();
        iteratorResultCheckerHelper(coordinates, iterator, 0, 1, 0, 1);

        coordinates = new Coordinates(5,0);
        iterator = new NaiveSolver.CoordinateIterable(coordinates,6, 4).iterator();
        iteratorResultCheckerHelper(coordinates, iterator, -1, 0, 0, 1);

        coordinates = new Coordinates(0,2);
        iterator = new NaiveSolver.CoordinateIterable(coordinates,5, 3).iterator();
        iteratorResultCheckerHelper(coordinates, iterator, 0, 1, -1, 0);

        coordinates = new Coordinates(3,3);
        iterator = new NaiveSolver.CoordinateIterable(coordinates,4, 4).iterator();
        iteratorResultCheckerHelper(coordinates, iterator, -1, 0, -1, 0);
    }

    @Test
    void singleCellGridTest() {
        Coordinates coordinates = new Coordinates(0,0);
        iterator = new NaiveSolver.CoordinateIterable(coordinates,1, 1).iterator();
        Assertions.assertFalse(iterator.hasNext());
    }

    private void iteratorResultCheckerHelper(Coordinates coordinates, Iterator<Coordinates> iterator, int startI,
                                             int endI, int startJ, int endJ) {
        for (int i = startI; i <= endI; i++)
            for (int j = startJ; j <= endJ; j++)
                if (!(i == 0 && j == 0)) {
                    Assertions.assertTrue(iterator.hasNext());
                    Coordinates generatedCoordinates = iterator.next();
                    Assertions.assertEquals(generatedCoordinates.x(), coordinates.x() + i);
                    Assertions.assertEquals(generatedCoordinates.y(), coordinates.y() + j);
                }
        Assertions.assertFalse(iterator.hasNext());
    }
}
