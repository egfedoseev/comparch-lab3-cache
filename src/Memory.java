public class Memory {
    public static final int ADDR_LEN = 16;

    public static final int MEM_SIZE = (1 << ADDR_LEN); // 65536

    private final byte[] bytes;

    public Memory() {
        bytes = new byte[MEM_SIZE];
    }

    public MemoryCommand recieveCommand(int address, byte[] data, MemoryCommand command) {
        switch (command) {
            case C2_READ_LINE:
                ++CPU.clock;
                for (int i = address; i < address + Cache.CACHE_LINE_SIZE; ++i) {
                    data[i - address] = bytes[i];
                }
                CPU.clock += Cache.CACHE_LINE_SIZE / 4;
                break;
            case C2_WRITE_LINE:
                CPU.clock += Cache.CACHE_LINE_SIZE / 4;
                for (int i = address; i < address + Cache.CACHE_LINE_SIZE; ++i) {
                    bytes[i] = data[i - address];
                }
                ++CPU.clock;
                break;
            default:
                break;
        }
        CPU.clock += 100;
        return MemoryCommand.C2_RESPONSE;
    }
}
