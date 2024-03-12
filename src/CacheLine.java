public class CacheLine {
    public CacheLineState flag;

    public int tag;

    public byte[] data;

    public CacheLine() {
        flag = CacheLineState.INVALID;
        tag = 0;
        data = new byte[Cache.CACHE_LINE_SIZE];
    }

    public void reset() {
        flag = CacheLineState.INVALID;
    }

    public boolean isEqualTag(int tag) {
        return (this.tag >> (Cache.CACHE_IDX_LEN + Cache.CACHE_OFFSET_LEN)) == tag;
    }
}
