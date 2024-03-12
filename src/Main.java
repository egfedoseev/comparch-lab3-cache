public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();
        LRUCache lruCache = new LRUCache(new Memory());

        cpu.run(lruCache);

        double hitPercLRU = (double) CPU.hit / ((double) CPU.hit + (double) CPU.miss) * 100f;
        int timeLRU = CPU.clock;

        System.out.printf("LRU:\thit perc. %3.4f%%\ttime: %d\n", hitPercLRU, timeLRU);

        BitPLRUCache plruCache = new BitPLRUCache(new Memory());

        CPU.clock = CPU.hit = CPU.miss = 0;
        cpu.run(plruCache);

        double hitPercPLRU = (double) CPU.hit / ((double) CPU.hit + (double) CPU.miss) * 100f;
        int timePLRU = CPU.clock;

        System.out.printf("pLRU:\thit perc. %3.4f%%\ttime: %d\n", hitPercPLRU, timePLRU);

        RoundRobinCache roundRobinCache = new RoundRobinCache(new Memory());

        CPU.clock = CPU.hit = CPU.miss = 0;
        cpu.run(roundRobinCache);

        double hitPercRR = (double) CPU.hit / ((double) CPU.hit + (double) CPU.miss) * 100f;
        int timeRR = CPU.clock;

        System.out.printf("RR:\thit perc. %3.4f%%\ttime: %d\n", hitPercRR, timeRR);
    }
}
