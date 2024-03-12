public class DumbCache extends Cache {
    public DumbCache(Memory memory) {
        super(memory);
    }

    @Override
    protected void update(int line) {

    }

    @Override
    protected int findOptimal(int idx) {
        return 0;
    }
}
