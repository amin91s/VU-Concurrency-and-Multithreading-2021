package data_structures.tests;

import java.util.Arrays;

public class UnitTestRunner {

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1 || args.length > 1) {
            System.out.printf("We need exactly 1 argument: choose between cgl, cgt, fgl, fgt\n");
            System.exit(1);
        }

        String dataStructure = args[0];
        UnitTestRunner utr = new UnitTestRunner();
        utr.runAll(dataStructure);
    }

    public void runAll(String ds) {
        TestCase<?>[] sequential = SequentialTestsInteger.testCases(ds);
        TestCase<?>[] threaded = ThreadedTestsInteger.testCases(ds);
        TestCase<?>[] sequentialStrings = SequentialTestsString.testCases(ds);
        TestCase<?>[] all = concat(concat(sequential, sequentialStrings),
                threaded);

        runTests(all);
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public void runTests(TestCase<?>[] testCases) {
        int passed = 0, failed = 0, exceptions = 0;

        for (int i = 0; i < testCases.length; i++) {
            TestCase<?> tc = testCases[i];
            System.out.printf(" - %s ", tc.description());
            try {
                tc.run();
                passed += 1;
                System.out.printf("[ok]\n");
            } catch (TestFailedException e) {
                failed += 1;
                System.out.printf("[FAILED]\n    %s\n", e.reason);
            } catch (Exception e) {
                exceptions += 1;
                System.out.printf("[EXCEPTION]! %s\n", e.toString());
            }
        }

        System.out.printf("Statistics: %s passed, %s failed, %s exceptions\n",
                passed, failed, exceptions);
    }
}
