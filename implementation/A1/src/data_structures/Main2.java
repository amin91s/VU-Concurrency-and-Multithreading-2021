package data_structures;

import java.util.Random;

/**
 * This is another main program of the Concurrency and Multithreading
 * programming assignment.This version is using String instead of Integer.
 */
public class Main2 {

    private static final int STRING_LENGTH = 16;

    /**
     * Generates a random string of the specified length, using the random
     * number generator specified and the specified characters.
     *
     * @param rnd
     *            the random number generator
     * @param characters
     *            the characters to be used
     * @param length
     *            length of the resulting string
     * @return the generated string
     */
    private static String generateString(Random rnd, String characters,
            int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rnd.nextInt(characters.length()));
        }
        return new String(text);
    }

    /**
     * Permutes an array in a predictable manner, based on a specific seed.
     *
     * @param array
     *            the array to permute.
     * @param seed
     *            the seed to use for the random number generator.
     */
    private static void permute(String[] array, long seed) {
        Random random = new Random(seed);

        for (int i = 0; i < array.length; i++) {
            int r = random.nextInt(array.length);
            String swapped = array[i];
            array[i] = array[r];
            array[r] = swapped;
        }
    }

    /**
     * Fills the specified <code>itemsToAdd</code> and
     * <code>itemsToRemove</code> arrays with pseudo-random strings, based on
     * the specified seed.
     *
     * @param itemsToAdd
     *            array to be initialized with items to add
     * @param itemsToRemove
     *            array to be initialized with items to remove (the same items
     *            as <code>ItemsToAdd</code>, but in a different order)
     * @param seed
     *            the seed
     */
    private static void createWorkData(String[] itemsToAdd,
            String[] itemsToRemove, long seed) {
        Random random = new Random(seed);
        for (int i = 0; i < itemsToAdd.length; i++) {
            String s = generateString(random, "abcdefghijklmnopqrstuvwxyz",
                    STRING_LENGTH);
            itemsToAdd[i] = s;
            itemsToRemove[i] = s;
        }

        permute(itemsToRemove, seed + 1);
    }

    public static void main(String[] args) {
        Main.parseArgs(args);

        // Create the items to be added and deleted.
        String[] itemsToAdd = new String[Main.nrItems];
        String[] itemsToRemove = new String[Main.nrItems];

        createWorkData(itemsToAdd, itemsToRemove, Main.seed);

        DoRuns<String> run = new DoRuns<String>(Main.dataStructure,
                Main.nrThreads, itemsToAdd, itemsToRemove, Main.workTime,
                Main.debug);

        run.runDataStructure();
    }
}
