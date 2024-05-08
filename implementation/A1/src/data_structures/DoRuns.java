package data_structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

import data_structures.implementation.CoarseGrainedList;
import data_structures.implementation.CoarseGrainedTree;
import data_structures.implementation.FineGrainedList;
import data_structures.implementation.FineGrainedTree;
/*
import data_structures.implementation.LazyList;
import data_structures.implementation.LazyTree;
*/

/**
 * Adds and then removes items from a {@link Sorted} data structure, by
 * splitting the items to add and items to remove into chunks, to be added and
 * removed multi-threaded.
 *
 * @param <T>
 *            item type to be used.
 */
public class DoRuns<T extends Comparable<T>> {

    /** Specifies the number of threads to use. */
    private final int nrThreads;
    /** Specifies the items to be added. */
    private final T[] itemsToAdd;
    /** Specifies the items to be removed. */
    private final T[] itemsToRemove;
    /**
     * If {@literal >} 0, the threads spend some CPU time in between add or
     * remove operations.
     */
    private final int workTime;
    /** When set, the data structure is printed after adding the items. */
    private final boolean debug;

    /** The actual data structure. */
    private final Sorted<T> sorted;

    /**
     * Initializes this object, and instantiates the actual data structure to be
     * used.
     *
     * @param dataStructure
     *            specifies which kind of data structure to use
     * @param nrThreads
     *            specifies the number of threads to use
     * @param itemsToAdd
     *            specifies the items to add
     * @param itemsToRemove
     *            specifies the items to remove
     * @param workTime
     *            if {@literal >} 0, the threads spend some CPU time in between
     *            add or remove operations.
     * @param debug
     *            when set, the data structure is printed after adding the items
     */
    public DoRuns(String dataStructure, int nrThreads, T[] itemsToAdd,
            T[] itemsToRemove, int workTime, boolean debug) {
        this.nrThreads = nrThreads;
        this.itemsToAdd = itemsToAdd;
        this.itemsToRemove = itemsToRemove;
        this.workTime = workTime;
        this.debug = debug;

        // Determine and allocate the data structure to be used.

        if (dataStructure.equalsIgnoreCase(Main.CGL)) {
            sorted = new CoarseGrainedList<T>();
        } else if (dataStructure.equalsIgnoreCase(Main.CGT)) {
            sorted = new CoarseGrainedTree<T>();
        } else if (dataStructure.equalsIgnoreCase(Main.FGL)) {
            sorted = new FineGrainedList<T>();
        } else if (dataStructure.equalsIgnoreCase(Main.FGT)) {
            sorted = new FineGrainedTree<T>();
/*
        } else if (dataStructure.equalsIgnoreCase(Main.LL)) {
            sorted = new LazyList<T>();
        } else if (dataStructure.equalsIgnoreCase(Main.LT)) {
            sorted = new LazyTree<T>();
*/
        } else {
            sorted = null;
            Main.exitWithError();
        }
    }

    /**
     * Runs the test, by first creating the worker threads, then starting them,
     * and then waiting for them to finish.
     */
    public void runDataStructure() {
        int runCount = 0;
        long totalTime = 0;
        for (int run = 0; run < 10; run++) {
            ArrayList<WorkerThread<T>> workerThreads = new ArrayList<WorkerThread<T>>();
            CyclicBarrier barrier = new CyclicBarrier(nrThreads);

            int sz = itemsToAdd.length / nrThreads;
            for (int i = 0; i < nrThreads; i++) {
                T[] toAdd = Arrays.copyOfRange(itemsToAdd, i * sz, (i + 1) * sz);
                T[] toRemove = Arrays.copyOfRange(itemsToRemove, i * sz,
                        (i + 1) * sz);
                workerThreads.add(new WorkerThread<T>(i, sorted, toAdd, toRemove,
                        workTime, barrier, debug));
            }

            // Start worker threads
            long start = System.currentTimeMillis();

            for (WorkerThread<T> t : workerThreads) {
                t.start();
            }

            // Wait until worker threads are finished
            for (WorkerThread<T> t : workerThreads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new Error(
                            "Unexpected InterruptedException. Should not happen.",
                            e);
                }
            }
            long end = System.currentTimeMillis();

            // Report result.
            ArrayList<T> result = sorted.toArrayList();
            if (result.size() > 0) {
                System.out.println("ERROR: " + result.toString());
                break;
            } else {
                System.out.printf("time: %d ms\n", end - start);
                totalTime += end - start;
                runCount++;
            }
        }
        if (runCount > 0) {
            System.out.printf("Average time: %f ms\n", (float) totalTime / runCount);
        }
    }
}
