package data_structures.tests;

class SequentialTestsInteger {
    public static TestCase<?>[] testCases(String ds) {
        return new SequentialTestsInteger().allTestCases(ds);
    }

    private TestCase<?>[] allTestCases(String ds) {
        TestCase<?>[] testCases = { new Test1(ds), new Test1a(ds),
                new Test2(ds), new Test3(ds), new Test4(ds), new Test4a(ds),
                new Test5(ds), new Test5a(ds), new Test6(ds), new Test6a(ds), };

        return testCases;
    }

    class Test1 extends TestCase<Integer> {
        public Test1(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Empty data structure";
        }

        @Override
        public void run() {
            this.strEqual("[]");
        }
    }

    class Test1a extends TestCase<Integer> {
        public Test1a(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Empty data structure, twice";
        }

        @Override
        public void run() {
            this.strEqual("[]");
            this.strEqual("[]");
        }
    }

    class Test2 extends TestCase<Integer> {
        public Test2(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add one element";
        }

        @Override
        public void run() {
            this.sorted.add(1);
            this.strEqual("[1]");
        }
    }

    class Test3 extends TestCase<Integer> {
        public Test3(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add one, remove one";
        }

        @Override
        public void run() {
            this.sorted.add(1);
            this.sorted.remove(1);
            this.strEqual("[]");
        }
    }

    class Test4 extends TestCase<Integer> {
        public Test4(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two";
        }

        @Override
        public void run() {
            this.sorted.add(1);
            this.sorted.add(2);
            this.strEqual("[1, 2]");
        }
    }

    class Test4a extends TestCase<Integer> {
        public Test4a(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two, reverse order";
        }

        @Override
        public void run() {
            this.sorted.add(2);
            this.sorted.add(1);
            this.strEqual("[1, 2]");
        }
    }

    class Test5 extends TestCase<Integer> {
        public Test5(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two, remove first";
        }

        @Override
        public void run() {
            this.sorted.add(1);
            this.sorted.add(2);
            this.sorted.remove(1);
            this.strEqual("[2]");
        }
    }

    class Test5a extends TestCase<Integer> {
        public Test5a(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two, remove last";
        }

        @Override
        public void run() {
            this.sorted.add(1);
            this.sorted.add(2);
            this.sorted.remove(2);
            this.strEqual("[1]");
        }
    }

    class Test6 extends TestCase<Integer> {
        public Test6(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two, remove both, start with first";
        }

        @Override
        public void run() {
            this.sorted.add(1);
            this.sorted.add(2);
            this.sorted.remove(1);
            this.sorted.remove(2);
            this.strEqual("[]");
        }
    }

    class Test6a extends TestCase<Integer> {
        public Test6a(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two, remove both, start with last";
        }

        @Override
        public void run() {
            this.sorted.add(1);
            this.sorted.add(2);
            this.sorted.remove(2);
            this.sorted.remove(1);
            this.strEqual("[]");
        }
    }
}
