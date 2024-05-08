package ndfs.mcndfs_2_naive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;



import ndfs.NDFS;
import graph.State;
/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {
    static volatile boolean done = false;

    //shared data structures
    private static final Map<State, Boolean> red = new ConcurrentHashMap<>();
    public static final Map<State, AtomicInteger> count = new ConcurrentHashMap<>();

    public static boolean isRed(State s){return red.get(s) != null;}

    public  static void setRed(State s){red.put(s,true);}

    //increment counter
    public static void incCount(State s){
        //if there is no mapping
        count.putIfAbsent(s,new AtomicInteger(0));
        count.get(s).incrementAndGet();
    }
    public static void decCount(State s){
        count.compute(s,(k,v)-> {
            if (v != null){
                if(v.get() == 1){
                    count.remove(s);
                }else v.getAndDecrement();
            }

            return v;
        });
    }


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
                    //System.out.println("count size: "+ count.size());
                    return true;

                }

            }  catch (Exception ignore) {}
        }
        //no cycle found. all submitted tasks are executed. can also use shutdown here
        pool.shutdown();
        //System.out.println("count size: "+ count.size());
        return false;
    }
}
