import java.util.List;

public class QuickSort
{
    public static void sort(List<QueryResult> results)
    { quickSort(results, 0, results.size() - 1); }

    private static void quickSort(List<QueryResult> results, int left, int right)
    {
        if (left < right)
        {
            int pivotPosition = partition(results, left, right);

            quickSort(results, left, pivotPosition - 1);
            quickSort(results, pivotPosition + 1, right);
        }
    }

    //Finds a pivot value to place in its proper place in the array
    private static int partition(List<QueryResult> results, int left, int right) {
        QueryResult pivot = results.get(right);

        //it will track the index of the last item less than the pivot to leave the next position for pivot
        int lastLess = left - 1;
        int compareOutput;

        for (int j = left; j < right; j++) {
            if((compareOutput = results.get(j).compareTo(pivot)) <= 0) {
                lastLess++;
                swap(results, lastLess, j);
            }
        }

        swap(results, lastLess + 1, right);
        // Return the pivot's final resting position
        return lastLess + 1;
    }

    private static void swap(List<QueryResult> results, int first, int second) {
        QueryResult temp = results.get(first);
        results.set(first, results.get(second));
        results.set(second, temp);
    }
}
