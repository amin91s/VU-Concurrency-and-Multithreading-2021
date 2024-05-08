package data_structures.tests;

import java.util.Vector;

import data_structures.Sorted;
import data_structures.implementation.CoarseGrainedList;
import data_structures.implementation.CoarseGrainedTree;
import data_structures.implementation.FineGrainedList;
import data_structures.implementation.FineGrainedTree;

abstract class TestCase<T extends Comparable<T>> {
    private static final String CGL = "cgl";
    private static final String CGT = "cgt";
    private static final String FGL = "fgl";
    private static final String FGT = "fgt";

    protected Sorted<T> sorted;
    protected String ds;

    public TestCase(String ds) {
        this.ds = ds;
        buildSorted(ds);
    }

    abstract public String description();

    abstract public void run();

    protected void buildSorted(String dataStructure) {
        if (dataStructure.equals(CGL)) {
            this.sorted = new CoarseGrainedList<T>();
        } else if (dataStructure.equals(CGT)) {
            this.sorted = new CoarseGrainedTree<T>();
        } else if (dataStructure.equals(FGL)) {
            this.sorted = new FineGrainedList<T>();
        } else if (dataStructure.equals(FGT)) {
            this.sorted = new FineGrainedTree<T>();
        } else {
            System.out.printf("Unkown algoritm %s\n", dataStructure);
            System.exit(1);
        }
    }

    protected void strEqual(String expect) {
        String got = this.sorted.toArrayList().toString();
        if (!got.equals(expect)) {
            throw new TestFailedException(
                    String.format("Expected '%s', but got '%s'", expect, got));
        }
    }

    protected void expectElements(Vector<T> expectedElements) {
        Vector<T> sortedElements = new Vector<T>(this.sorted.toArrayList());

        // Compare lengths.
        if (expectedElements.size() != sortedElements.size()) {
            Vector<T> missing = difference(expectedElements, sortedElements);
            Vector<T> extra = difference(sortedElements, expectedElements);

            String msg = String.format(
                    "Non equal length. Expected %s, got %s. ",
                    expectedElements.size(), sortedElements.size());
            if (missing.size() != 0) {
                msg += "Missing: ";
                for (T x : missing) {
                    msg += x;
                    msg += ", ";
                }
            }

            if (extra.size() != 0) {
                msg += "Extra: ";
                for (T x : extra) {
                    msg += x;
                    msg += ", ";
                }
            }
            throw new TestFailedException(msg);
        }

        // Compare elements
        int wrong = 0;
        for (int i = 0; i < expectedElements.size(); i++) {
            T expected = expectedElements.get(i);
            T got = sortedElements.get(i);

            if (!expected.equals(got)) {
                wrong += 1;
            }
        }

        if (wrong != 0) {
            throw new TestFailedException(
                    String.format("%s elements have the wrong value", wrong));
        }
    }

    Vector<T> difference(Vector<T> a, Vector<T> b) {
        Vector<T> output = new Vector<T>();
        for (T x : a) {
            if (!b.contains(x)) {
                output.add(x);
            }
        }

        return output;
    }
}
