public abstract class Cache {
    public static final int CACHE_WAY = 4;

    public static final int CACHE_LINE_SIZE = 32;

    public static final int CACHE_SETS_COUNT = 32; // 16

    public static final int CACHE_IDX_LEN = 5; // log_2(CACHE_SETS_COUNT)

    public static final int CACHE_SIZE = CACHE_WAY * CACHE_SETS_COUNT * CACHE_LINE_SIZE; // 4096

    public static final int CACHE_LINE_COUNT = CACHE_SIZE / CACHE_LINE_SIZE; // 128

    public static final int CACHE_OFFSET_LEN = 5; // log_2(CACHE_LINE_SIZE)

    public static final int CACHE_TAG_LEN = Memory.ADDR_LEN - CACHE_IDX_LEN - CACHE_OFFSET_LEN; // 6

    protected CacheLine[] lines;

    protected Memory memory;

    public Cache(Memory memory) {
        this.memory = memory;
        lines = new CacheLine[Cache.CACHE_LINE_COUNT];
        for (int i = 0; i < Cache.CACHE_LINE_COUNT; ++i) {
            lines[i] = new CacheLine();
        }
    }

    public void reset() {
        for (CacheLine line : lines) {
            line.reset();
        }
    }

    public CacheCommand recieveCommand(int address, byte[] data, CacheCommand command) {
        ++CPU.clock;
        if (command == CacheCommand.C1_WRITE32) {
            ++CPU.clock;
        }

        switch (command) {
            case C1_READ8:
                read8(address, data);
                break;
            case C1_READ16:
                read16(address, data);
                break;
            case C1_READ32:
                read32(address, data);
                break;
            case C1_WRITE8:
                write8(address, data);
                break;
            case C1_WRITE16:
                write16(address, data);
                break;
            case C1_WRITE32:
                write32(address, data);
                break;
            default:
                break;
        }

        ++CPU.clock;
        if (command == CacheCommand.C1_READ32) {
            ++CPU.clock;
        }
        return CacheCommand.C1_RESPONSE;
    }

    protected int findLine(int address) {
        int idx = (address / Cache.CACHE_LINE_SIZE) % Cache.CACHE_SETS_COUNT;
        int tag = address >> (Cache.CACHE_IDX_LEN + Cache.CACHE_OFFSET_LEN);

        for (int i = idx * Cache.CACHE_WAY; i < (idx + 1) * Cache.CACHE_WAY; ++i) {
            if (!lines[i].isEqualTag(tag)) {
                continue;
            }

            if (lines[i].flag == CacheLineState.INVALID) {
                ++CPU.miss;

                CPU.clock += 4;
                memory.recieveCommand(lines[i].tag, lines[i].data, MemoryCommand.C2_READ_LINE);
                lines[i].flag = CacheLineState.SHARED;
            } else {
                CPU.clock += 6;
                ++CPU.hit;
            }

            update(i);
            return i;
        }

        ++CPU.miss;

        int idxOptimal = findOptimal(idx);

        if (lines[idxOptimal].flag == CacheLineState.MODIFIED) {
            CPU.clock += 4;
            memory.recieveCommand(lines[idxOptimal].tag, lines[idxOptimal].data, MemoryCommand.C2_WRITE_LINE);
            lines[idxOptimal].flag = CacheLineState.SHARED;
        }

        lines[idxOptimal].tag = address - (address % Cache.CACHE_LINE_SIZE);
        CPU.clock += 4;
        memory.recieveCommand(lines[idxOptimal].tag, lines[idxOptimal].data, MemoryCommand.C2_READ_LINE);

        update(idxOptimal);

        return idxOptimal;
    }

    protected void readLine(int address, byte[] data, int bytesCount) {
        int line = findLine(address);
        int offset = address % Cache.CACHE_LINE_SIZE;

        for (int i = offset; i < offset + bytesCount; ++i) {
            data[i - offset] = lines[line].data[i];
        }
    }

    protected void writeLine(int address, byte[] data, int bytesCount) {
        int line = findLine(address);
        int offset = address % Cache.CACHE_LINE_SIZE;

        for (int i = offset; i < offset + bytesCount; ++i) {
            lines[line].data[i] = data[i - offset];
        }
        lines[line].flag = CacheLineState.MODIFIED;
    }

    protected void read8(int address, byte[] data) {
        readLine(address, data, 1);
    }

    protected void read16(int address, byte[] data) {
        readLine(address, data, 2);
    }

    protected void read32(int address, byte[] data) {
        readLine(address, data, 4);
    }

    protected void write8(int address, byte[] data) {
        writeLine(address, data, 1);
    }

    protected void write16(int address, byte[] data) {
        writeLine(address, data, 2);
    }

    protected void write32(int address, byte[] data) {
        writeLine(address, data, 4);
    }

    protected abstract void update(int line);

    protected abstract int findOptimal(int idx);
}
