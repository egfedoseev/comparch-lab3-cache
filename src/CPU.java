import java.nio.ByteBuffer;

public class CPU {
    public static int clock = 0;

    public static int miss = 0;

    public static int hit = 0;

    void run(Cache cache) {
        cache.reset();
        clock = 0;
        hit = 0;
        miss = 0;

        final int M = 64;
        final int N = 60;
        final int K = 32;

        final int pointerA = 0x1000;
        final int pointerB = pointerA + M * K;
        final int pointerC = pointerB + K * N * 2;

        int pa = pointerA;
        ++clock;
        int pc = pointerC;
        ++clock;

        ++clock;
        for (int y = 0; y < M; y++) {

            ++clock;
            for (int x = 0; x < N; x++) {

                int pb = pointerB;
                ++clock;
                int s = 0;
                ++clock;

                ++clock;
                for (int k = 0; k < K; k++) {
                    int paKAddr = pa + k;
                    int pbXAddr = pb + x * 2;

                    byte[] paK = new byte[1];
                    ++clock;

                    cache.recieveCommand(paKAddr, paK, CacheCommand.C1_READ8);

                    byte[] pbX = new byte[2];
                    ++clock;

                    cache.recieveCommand(pbXAddr, pbX, CacheCommand.C1_READ16);

                    s = ((((char) Byte.toUnsignedInt(pbX[1])) << 8) + (char) Byte.toUnsignedInt(pbX[0])) * paK[0];
                    clock += 6;

                    pb += N * 2;
                    ++clock;

                    ++clock;
                }

                int pcXAddr = pc + x * 4;
                byte[] bytes = ByteBuffer.allocate(4).putInt(s).array();

                cache.recieveCommand(pcXAddr, bytes, CacheCommand.C1_WRITE32);

                ++clock;
            }
            pa += K;
            ++clock;

            pc += N * 4;
            ++clock;

            ++clock;
        }
    }
}
