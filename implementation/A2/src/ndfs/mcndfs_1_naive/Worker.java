package ndfs.mcndfs_1_naive;

import graph.Graph;
import graph.GraphFactory;
import graph.State;
import ndfs.NDFS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import static ndfs.mcndfs_1_naive.NNDFS.zero;

/**
 * This is a straightforward implementation of Figure 1 of
 * <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
 * paper"</a>.
 */
public class Worker implements Callable<Worker> {

    private final Graph graph;
    private final Colors colors = new Colors();
    private boolean result = false;
    private final Map<State,Boolean> pink = new HashMap<>();

    private boolean isPink(State s){
        return pink.get(s) != null;
    }

    // Throwing an exception is a convenient way to cut off the search in case a
    // cycle is found.
    private static class CycleFoundException extends Exception {
        private static final long serialVersionUID = 1L;
        public CycleFoundException(){super();}

        public CycleFoundException(String m) {super(m);}
    }

    /**
     * Constructs a Worker object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public Worker(File promelaFile) throws FileNotFoundException {

        this.graph = GraphFactory.createGraph(promelaFile);
    }

    private void dfsRed(State s) throws CycleFoundException, InterruptedException {
        /*if(Thread.currentThread().isInterrupted()){
            if(NNDFS.done)
                throw new InterruptedException();
        }*/
        if(NNDFS.done)
            throw new InterruptedException(Thread.currentThread().getName());

        pink.put(s,true);
        for(State t: randPost(s)){
            if(colors.hasColor(t,Color.CYAN)){
                throw new CycleFoundException(Thread.currentThread().getName());
            }

            else if(!isPink(t) & !NNDFS.isRed(t))
                dfsRed(t);
        }
        if(s.isAccepting()){
            NNDFS.decCount(s);
            NNDFS.countLock.writeLock().lock();
            try {
                while(NNDFS.count.get(s) != null) {
                    zero.await();
                }

                zero.signalAll();
            }
            finally {
                NNDFS.countLock.writeLock().unlock();
            }
        }
        NNDFS.setRed(s);
        pink.remove(s);
    }

    private void dfsBlue(State s) throws CycleFoundException, InterruptedException {
        if(NNDFS.done)
            throw new InterruptedException(Thread.currentThread().getName());

        colors.color(s,Color.CYAN);
        for (State t : randPost(s)) {
            if(colors.hasColor(t, Color.WHITE) && !NNDFS.isRed(t)){
                dfsBlue(t);
            }
        }
        if(s.isAccepting()){
            NNDFS.incCount(s);
            dfsRed(s);
        }
        colors.color(s,Color.BLUE);
    }

    private void nndfs(State s) throws CycleFoundException, InterruptedException {
        dfsBlue(s);
    }

    public List<State> randPost(State s){
        List<State> post = graph.post(s);
        //Collections.shuffle(post,new Random(System.nanoTime()));
        //Collections.shuffle(post,new Random(Thread.currentThread().getId()*s.hashCode()));
        Collections.shuffle(post, new Random(ThreadLocalRandom.current().nextLong()));

        return post;
    }


    @Override
    public Worker call() throws InterruptedException {
        try {
            nndfs(graph.getInitialState());
        } catch(CycleFoundException e) {
            System.out.println(e.getMessage()  + " found a cycle!");
            result = true;
        }
        //catch(InterruptedException e){
        //    System.out.println(e.getMessage()  + " interrupted.");
        //    throw e;
        //}
        return this;
    }

    public boolean getResult() {
        return result;
    }
}