package data_structures;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * A <code>WorkerThread</code> adds items to a data structure, and then removes
 * some other items.
 *
 * @param <T>
 *            the type of the items.
 */
public class WorkerThread<T extends Comparable<T>> extends Thread {

    /** Thread number. */
    private final int id;
    /** The data structure to be used. */
    private final Sorted<T> sorted;
    /** The items to be added by this thread. */
    private final T[] itemsToAdd;
    /** The items to be removed by this thread. */
    private final T[] itemsToRemove;
    /**
     * If > 0, CPU time to be spent in between add or remove operations, in
     * microseconds.
     */
    private final int workTime;
    /**
     * When set, have thread 0 print the data structure when addition is done.
     */
    private final boolean doDebug;
    /** Barrier to be used after addition. */
    private final CyclicBarrier barrier;

    ThreadMXBean bean;

    /**
     * Creates a worker thread with the specified parameters.
     *
     * @param id
     *            the thread number.
     * @param list
     *            the data structure.
     * @param itemsToAdd
     *            the items to add to the data structure.
     * @param itemsToRemove
     *            the items to remove from the data structure.
     * @param workTime
     *            if {@literal >} 0, specifies the amount of CPU time to be
     *            spent in between add or remove operations, in microseconds.
     * @param barrier
     *            used to synchronize the threads after adding items.
     * @param debug
     *            when set, the data structure is printed when all items have
     *            been added.
     */
    public WorkerThread(int id, Sorted<T> list, T[] itemsToAdd,
            T[] itemsToRemove, int workTime, CyclicBarrier barrier,
            boolean debug) {
        this.sorted = list;
        this.id = id;
        this.itemsToAdd = itemsToAdd;
        this.itemsToRemove = itemsToRemove;
        this.workTime = workTime;
        this.barrier = barrier;
        this.doDebug = debug;
        if (workTime > 0) {
            bean = ManagementFactory.getThreadMXBean();
        }
    }

    @Override
    public void run() {
        // First: add my items.
        for (T t : itemsToAdd) {
            doWork();
            sorted.add(t);
        }

        // Barrier, and possibly print result.
        try {
            barrier.await();
            if (this.doDebug) {
                if (this.id == 0) {
                    ArrayList<T> result = sorted.toArrayList();
                    for (int i = 0; i < result.size() - 1; i++) {
                        if (result.get(i).compareTo(result.get(i + 1)) > 0) {
                            System.err.println(
                                    "The result is not correctly sorted");
                            break;
                        }
                    }
                    System.out.printf(
                            "Output after adding, before removing:\n%s\n",
                            sorted.toArrayList().toString());
                }
                barrier.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        // Remove my items.
        for (T t : itemsToRemove) {
            doWork();
            sorted.remove(t);
        }
    }

    /**
     * Spends some CPU time, but only if {@link #workTime} is {@literal >} 0, in
     * which case it tries to spend the specified (in microseconds) amount of
     * CPU time.
     */
    private void doWork() {
        if (bean != null) {
            // Do some work in between operations. workTime indicates the cpu
            // time to be consumed, in microseconds.
            long start = bean.getCurrentThreadCpuTime();

            // getThreadCpuTime() returns cpu time in nano seconds.
            long end = start + workTime * 1000;
            while (bean.getCurrentThreadCpuTime() < end)
                ; // busy until we used enough cpu time.
        }
    }
}
