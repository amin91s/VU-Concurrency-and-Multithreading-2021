package data_structures.tests;

import java.util.Vector;
import java.util.concurrent.CyclicBarrier;

class ThreadedTestsInteger {
    public static TestCase<?>[] testCases(String ds) {
        return new ThreadedTestsInteger().allTestCases(ds);
    }

    private TestCase<?>[] allTestCases(String ds) {
        TestCase<?>[] testCases = { new Test1(ds), new Test2(ds),
                new Test2b(ds), };
        return testCases;
    }

    abstract class ThreadedTestCase<T extends Comparable<T>>
            extends TestCase<T> {
        static final int REPEAT = 10000;

        public ThreadedTestCase(String ds) {
            super(ds);
        };

        abstract void runThreaded() throws Exception;

        @Override
        public void run() {
            try {
                for (int i = 0; i < REPEAT; i++) {
                    buildSorted(this.ds);
                    this.runThreaded();
                }
            } catch (TestFailedException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    abstract class TestRunnable implements Runnable {
        abstract void runThreaded() throws Exception;

        public void run() {
            try {
                this.runThreaded();
            } catch (TestFailedException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Test1 extends ThreadedTestCase<Integer> {
        public Test1(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Each thread adds 100 numbers";
        }

        @Override
        public void runThreaded() throws Exception {
            final CyclicBarrier barrier = new CyclicBarrier(2);
            Thread thr_a = new Thread(new TestRunnable() {
                @Override
                public void runThreaded() throws Exception {
                    barrier.await();
                    for (int i = 0; i < 100; i++) {
                        sorted.add(i);
                    }
                }
            });

            Thread thr_b = new Thread(new TestRunnable() {
                @Override
                public void runThreaded() throws Exception {
                    barrier.await();
                    for (int i = 100; i < 200; i++) {
                        sorted.add(i);
                    }
                }
            });

            thr_a.start();
            thr_b.start();
            thr_a.join();
            thr_b.join();

            Vector<Integer> elements = new Vector<Integer>();
            for (int i = 0; i < 200; i++) {
                elements.addElement(i);
            }

            this.expectElements(elements);
        }
    }

    class Test2 extends ThreadedTestCase<Integer> {
        public Test2(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Each thread adds and removes 100 numbers";
        }

        @Override
        public void runThreaded() throws Exception {
            final CyclicBarrier barrier = new CyclicBarrier(2);
            Thread thr_a = new Thread(new TestRunnable() {
                @Override
                public void runThreaded() throws Exception {
                    barrier.await();
                    for (int i = 0; i < 100; i++) {
                        sorted.add(i);
                    }
                    for (int i = 0; i < 100; i++) {
                        sorted.remove(i);
                    }
                }
            });

            Thread thr_b = new Thread(new TestRunnable() {
                @Override
                public void runThreaded() throws Exception {
                    barrier.await();
                    for (int i = 100; i < 200; i++) {
                        sorted.add(i);
                    }
                    for (int i = 100; i < 200; i++) {
                        sorted.remove(i);
                    }
                }
            });

            thr_a.start();
            thr_b.start();
            thr_a.join();
            thr_b.join();

            this.strEqual("[]");
        }
    }

    class Test2b extends ThreadedTestCase<Integer> {
        public Test2b(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Two threads add 100, each removes 50";
        }

        @Override
        public void runThreaded() throws Exception {
            final CyclicBarrier barrier = new CyclicBarrier(2);
            Thread thr_a = new Thread(new TestRunnable() {
                @Override
                public void runThreaded() throws Exception {
                    barrier.await();
                    for (int i = 0; i < 100; i++) {
                        sorted.add(i);
                    }
                    for (int i = 0; i < 50; i++) {
                        sorted.remove(i);
                    }
                }
            });

            Thread thr_b = new Thread(new TestRunnable() {
                @Override
                public void runThreaded() throws Exception {
                    barrier.await();
                    for (int i = 100; i < 200; i++) {
                        sorted.add(i);
                    }
                    for (int i = 150; i < 200; i++) {
                        sorted.remove(i);
                    }
                }
            });

            thr_a.start();
            thr_b.start();
            thr_a.join();
            thr_b.join();

            Vector<Integer> elements = new Vector<Integer>();

            for (int i = 0; i < 100; i++) {
                elements.addElement(i + 50);
            }

            this.expectElements(elements);
        }
    }
}
