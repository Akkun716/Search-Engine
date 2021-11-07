import java.util.List;

/**
 * Quick sorting algorithm implementation, currently used for:
 * 		- InvertedIndex search() functions 
 */
public class QuickSort
{
	/**
	 * Over-arching sort function, calls recursive quickSort() function
	 * 
	 * @param results list to be sorted
	 */
    public static void sort(List<QueryResult> results)
    { quickSort(results, 0, results.size() - 1); }

	/**
	 * Recursive sort function; moves all elements "less than" element at 
	 * pivotPosition to left of pivotPosition and then quick sorts the list left
	 * and right of pivotPosition individually.
	 * 
	 * @param results list to be sorted
	 * @param left left edge index
	 * @param right right edge index
	 */
    private static void quickSort(List<QueryResult> results, int left, int right)
    {
        if (left < right)
        {
            int pivotPosition = partition(results, left, right);

            quickSort(results, left, pivotPosition - 1);
            quickSort(results, pivotPosition + 1, right);
        }
    }

	/**
	 * Uses right element as pivot value to place less and greater than elements in
	 * their "proper" regions in the array.
	 * 
	 * @param results list to be sorted
	 * @param left left edge index
	 * @param right right edge index
	 */
    private static int partition(List<QueryResult> results, int left, int right) {
        QueryResult pivot = results.get(right);

        //it will track the index of the last item less than the pivot to leave the next position for pivot
        int lastLess = left - 1;

        for(int j = left; j < right; j++) {
            if(results.get(j).compareTo(pivot) <= 0) {
                lastLess++;
                swap(results, lastLess, j);
            }
        }

        swap(results, lastLess + 1, right);
        // Return the pivot's final resting position
        return lastLess + 1;
    }

	/**
	 * Swaps the values at passed indices.
	 * 
	 * @param results list to be sorted
	 * @param first first element's index
	 * @param second second element's index
	 */
    private static void swap(List<QueryResult> results, int first, int second) {
        QueryResult temp = results.get(first);
        results.set(first, results.get(second));
        results.set(second, temp);
    }
}
