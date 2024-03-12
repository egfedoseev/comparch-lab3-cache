import java.util.Arrays;

public class BitPLRUCache extends Cache {
    private final boolean[] mruBits;

    public BitPLRUCache(Memory memory) {
        super(memory);
        mruBits = new boolean[Cache.CACHE_LINE_COUNT];
        Arrays.fill(mruBits, false);
    }

    @Override
    protected void update(int i) {
        mruBits[i] = true;
        int cntTrue = 0;
        int idx = i / Cache.CACHE_WAY;
        for (int j = idx * Cache.CACHE_WAY; j < (idx + 1) * Cache.CACHE_WAY; ++j) {
            if (mruBits[j]) {
                ++cntTrue;
            }
        }
        if (cntTrue == Cache.CACHE_WAY) {
            for (int j = idx * Cache.CACHE_WAY; j < (idx + 1) * Cache.CACHE_WAY; ++j) {
                if (j != i) {
                    mruBits[j] = false;
                }
            }
        }
    }

    @Override
    protected int findOptimal(int idx) {
        for (int i = idx * Cache.CACHE_WAY; i < (idx + 1) * Cache.CACHE_WAY; ++i) {
            if (!mruBits[i]) {
                return i;
            }
        }
        return 0;
    }
}
