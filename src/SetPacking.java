import java.util.ArrayList;
import java.util.Random;
public class SetPacking {
    public static void main(String[] args) {
        int n = 100; // number of sets
        int m = 50; // number of elements
        int[][] sets = new int[n][m]; // sets represented as a 2D array
        Random rand = new Random();
        // Initialize sets with random elements
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                sets[i][j] = rand.nextInt(2);
            }
        }
        ArrayList<Integer> solution = new ArrayList<>(); // list to store the chosen sets
        boolean[] elements = new boolean[m]; // array to keep track of chosen elements
        // Iterate through all sets
        for (int i = 0; i < n; i++) {
            boolean added = false;
            for (int j = 0; j < m; j++) {
                if (sets[i][j] == 1 && !elements[j]) {
                    added = true;
                    elements[j] = true;
                }
            }
            if (added) {
                solution.add(i);
            }
        }
        // Print the chosen sets
        for (int i : solution) {
            System.out.print(i + " ");
        }
    }
}
