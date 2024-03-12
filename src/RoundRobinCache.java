public class RoundRobinCache extends Cache {
    private final int[] lastLine;

    public RoundRobinCache(Memory memory) {
        super(memory);
        lastLine = new int[Cache.CACHE_SETS_COUNT];
        for (int i = 0; i < Cache.CACHE_SETS_COUNT; ++i) {
            lastLine[i] = i * Cache.CACHE_WAY;
        }
    }

    @Override
    protected void update(int line) {
        int idx = line / Cache.CACHE_WAY;
        lastLine[idx] = line + 1;
        if (line + 1 == (idx + 1) * Cache.CACHE_WAY) {
            lastLine[idx] = idx * Cache.CACHE_WAY;
        }
    }

    @Override
    protected int findOptimal(int idx) {
        return lastLine[idx];
    }
}
