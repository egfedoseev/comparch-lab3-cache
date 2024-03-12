import java.util.Arrays;

public class LRUCache extends Cache {
    private final int[] age;

    public LRUCache(Memory memory) {
        super(memory);
        age = new int[Cache.CACHE_LINE_COUNT];
        Arrays.fill(age, 0);
    }

    @Override
    protected void update(int line) {
        age[line] = CPU.clock;
    }

    @Override
    protected int findOptimal(int idx) {
        int minAge = Integer.MAX_VALUE, idxMinAge = 0;
        for (int i = idx * Cache.CACHE_WAY; i < (idx + 1) * Cache.CACHE_WAY; ++i) {
            if (age[i] < minAge) {
                minAge = age[i];
                idxMinAge = i;
            }
        }
        return idxMinAge;
    }
}
