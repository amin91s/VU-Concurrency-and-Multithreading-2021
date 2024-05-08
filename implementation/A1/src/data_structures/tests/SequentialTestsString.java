package data_structures.tests;

class SequentialTestsString {
    public static TestCase<?>[] testCases(String ds) {
        return new SequentialTestsString().allTestCases(ds);
    }

    private TestCase<?>[] allTestCases(String ds) {
        TestCase<?>[] testCases = { new Test2(ds), new Test3(ds), new Test4(ds),
                new Test4a(ds), new Test5(ds), new Test5a(ds), new Test6(ds),
                new Test6a(ds), };

        return testCases;
    }

    class Test2 extends TestCase<String> {
        public Test2(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add one string element";
        }

        @Override
        public void run() {
            this.sorted.add("1");
            this.strEqual("[1]");
        }
    }

    class Test3 extends TestCase<String> {
        public Test3(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add one string, remove one string";
        }

        @Override
        public void run() {
            this.sorted.add("1");
            this.sorted.remove("1");
            this.strEqual("[]");
        }
    }

    class Test4 extends TestCase<String> {
        public Test4(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two strings";
        }

        @Override
        public void run() {
            this.sorted.add("1");
            this.sorted.add("2");
            this.strEqual("[1, 2]");
        }
    }

    class Test4a extends TestCase<String> {
        public Test4a(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two strings, reverse order";
        }

        @Override
        public void run() {
            this.sorted.add("2");
            this.sorted.add("1");
            this.strEqual("[1, 2]");
        }
    }

    class Test5 extends TestCase<String> {
        public Test5(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two strings, remove first string";
        }

        @Override
        public void run() {
            this.sorted.add("1");
            this.sorted.add("2");
            this.sorted.remove("1");
            this.strEqual("[2]");
        }
    }

    class Test5a extends TestCase<String> {
        public Test5a(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two strings, remove last string";
        }

        @Override
        public void run() {
            this.sorted.add("1");
            this.sorted.add("2");
            this.sorted.remove("2");
            this.strEqual("[1]");
        }
    }

    class Test6 extends TestCase<String> {
        public Test6(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two strings, remove both, start with first";
        }

        @Override
        public void run() {
            this.sorted.add("1");
            this.sorted.add("2");
            this.sorted.remove("1");
            this.sorted.remove("2");
            this.strEqual("[]");
        }
    }

    class Test6a extends TestCase<String> {
        public Test6a(String ds) {
            super(ds);
        };

        @Override
        public String description() {
            return "Add two strings, remove both, start with last";
        }

        @Override
        public void run() {
            this.sorted.add("1");
            this.sorted.add("2");
            this.sorted.remove("2");
            this.sorted.remove("1");
            this.strEqual("[]");
        }
    }
}
