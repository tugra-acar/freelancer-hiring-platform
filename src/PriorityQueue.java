import java.util.ArrayList;

public class PriorityQueue {

    private ArrayList<Freelancer> heap;

    public PriorityQueue() {
        heap = new ArrayList<>();
        heap.add(null); //first element will be null
    }

    private boolean better(Freelancer a, Freelancer b) {
        if (a.getCompositeScore() != b.getCompositeScore())
            return a.getCompositeScore() > b.getCompositeScore();

        return a.getFreelancerID().compareTo(b.getFreelancerID()) < 0; // tie-break
    }

    public ArrayList<Freelancer> getInternalHeap() {
        return heap;
    }

    public int getSize() {
        return heap.size() - 1;
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public Freelancer peek() {
        if (isEmpty()) return null;
        return heap.get(1);
    }

    public void push(Freelancer f) {
        heap.add(f);
        percolateUp(getSize());
    }

    public Freelancer pop() {
        if (isEmpty()) return null;

        Freelancer best = heap.get(1);
        int last = getSize();

        swap(1, last);
        heap.remove(last);

        if (!isEmpty()) {
            percolateDown(1);
        }


        return best;
    }

    private void percolateUp(int i) {
        while (i > 1) {
            int parent = i / 2;

            if (!better(heap.get(i), heap.get(parent)))
                break;

            swap(i, parent);
            i = parent;
        }
    }

    private void percolateDown(int i) {
        int n = getSize();

        while (2 * i <= n) {
            int left = 2 * i;
            int right = left + 1;
            int best = left;

            if (right <= n && better(heap.get(right), heap.get(left))) {
                best = right;
            }

            if (!better(heap.get(best), heap.get(i)))
                break;

            swap(i, best);
            i = best;
        }
    }

    private void swap(int i, int j) {
        Freelancer tmp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, tmp);
    }

    // O(n) for removing
    public void remove(Freelancer f) {
        int n = getSize();

        for (int i = 1; i <= n; i++) {
            if (heap.get(i) == f) {
                if (i == n) { // removing last element
                    heap.remove(n);
                    return;
                }

                swap(i, n);
                heap.remove(n);

                percolateDown(i);
                percolateUp(i);
                return;
            }
        }
    }
}
