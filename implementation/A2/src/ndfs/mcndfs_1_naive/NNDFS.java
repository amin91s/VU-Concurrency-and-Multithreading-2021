package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import ndfs.NDFS;
import graph.State;
/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {
    static boolean done = false;
    //shared data structures
    private static final Map<State, Boolean> red = new HashMap<>();
    public static final Map<State, AtomicInteger> count = new HashMap<>();

    public static boolean isRed(State s){
        redLock.readLock().lock();
        try {
            //red boolean should never become false??
            return red.get(s) != null;
        }finally {
            redLock.readLock().unlock();
        }
    }

    public  static void setRed(State s){
        redLock.writeLock().lock();
        try {
            red.put(s,true);
        }finally {
            redLock.writeLock().unlock();
        }
    }

    //increment counter
    public static void incCount(State s){
        countLock.writeLock().lock();
        try {
            if(count.get(s) == null){
                count.put(s,new AtomicInteger(1));
            } else {
                count.get(s).incrementAndGet();
            }
        } finally {
            countLock.writeLock().unlock();
        }
    }
    public static void decCount(State s){
        countLock.writeLock().lock();
        try {
            if(count.get(s).get() == 1){
                count.remove(s);
            } else
                count.get(s).decrementAndGet();
        }finally {
            countLock.writeLock().unlock();
        }
    }


    //locks
    public static ReentrantReadWriteLock redLock = new ReentrantReadWriteLock();
    public static ReentrantReadWriteLock countLock = new ReentrantReadWriteLock();
    final static Condition zero  = countLock.writeLock().newCondition();

    private final Worker[] workers;
    CompletionService<Worker> ecs;
    ExecutorService pool;
    /**
     * Constructs an NDFS object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public NNDFS(File promelaFile, int nThreads) throws FileNotFoundException {
        workers = new Worker[nThreads];
        pool = Executors.newFixedThreadPool(nThreads);
        ecs = new ExecutorCompletionService<>(pool);

        for(int i = 0; i < nThreads; i++){
            workers[i] = new Worker(promelaFile);
            ecs.submit(workers[i]);
        }

    }

    @Override
    public boolean ndfs() {
        for(int i = 0; i < workers.length; ++i){
            try {
                boolean res = ecs.take().get().getResult();
                if(res){
                    //shutdown() Initiates an orderly shutdown in which
                    // previously submitted tasks are executed, but no new tasks will be accepted.
                    // use shutdownNow() to force shutdown.
                    done = true;
                    pool.shutdownNow();
                    return true;

                }

            }  catch (Exception ignore) {}
        }
        //no cycle found. all submitted tasks are executed.
        pool.shutdown();
        return false;
    }
}
