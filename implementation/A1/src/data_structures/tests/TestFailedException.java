package data_structures.tests;

@SuppressWarnings("serial")
class TestFailedException extends RuntimeException {
    public String reason;

    public TestFailedException(String reason) {
        this.reason = reason;
    }
}
