package loops;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import java.util.*;

public final class ALoops {
    static final ExecutorService pool = Executors.newCachedThreadPool();
    static final LoopHelpers.SimpleRandom rng = new LoopHelpers.SimpleRandom();
    static boolean print = false;
    static int iters = 2000000;
    static int mask = 0;
    static long loopTime = Long.MAX_VALUE;
    static final long NCPU = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {
        int maxThreads = 100;
        if (args.length > 0)
            maxThreads = Integer.parseInt(args[0]);

        if (args.length > 1)
            mask = Integer.parseInt(args[1]);

		System.out.println("Running ALoops.main with these values:");
		System.out.println("Mask: " + mask + " CPUs: " + NCPU + " Iters: " + iters + " MaxThreads: " + maxThreads);
		System.out.println("\n");

        warmup();
        print = true;

        for (int m = 1; m <= 256; m <<= 1) {
            mask = m - 1;
            System.out.println("Mask: " + mask + " CPUs: " + NCPU + " Iters: " + iters + " MaxThreads: " + maxThreads);


            int k = 1;
            for (int i = 1; i <= maxThreads;) {
                System.out.println("Threads: " + i);
                new Loop(1).test();
                if (i <= 4)
                    new CASLoop(i).test();
                new ReentrantLockLoop(i).test();
                new LockLoop(i).test();
                new MutexLoop(i).test();
                if (i == k) {
                    k = i << 1;
                    i = i + (i >>> 1);
                }
                else
                    i = k;
            }
        }

        pool.shutdown();
    }

    static void warmup() throws Exception {
        for (int i = 0; i < 30; ++i)
            new Loop(1).test();

        for (int i = 0; i < 30; ++i)
            new CASLoop(1).test();

        for (int i = 0; i < 30; ++i)
            new MutexLoop(1).test();

        for (int i = 0; i < 30; ++i)
            new ReentrantLockLoop(1).test();

        for (int i = 0; i < 30; ++i)
            new LockLoop(1).test();

        for (int i = 0; i < 30; ++i)
            new Loop(1).test();
    }

    private static int nextRandom(int x) {
        int t = (x % 127773) * 16807 - (x / 127773) * 2836;
        return (t > 0)? t : t + 0x7fffffff;
    }



    private static double ratio(int n, long t) {
        double s = 1.0 / (1.0 + (double) mask);
        double ns = 1.0 - s;
        double seq = loopTime * s * n;
        double ideal;
        if (n <= NCPU)
            ideal = seq + loopTime * ns;
        else
            ideal = seq + loopTime * ns * n / NCPU;
        return (double)t / ideal;
    }

    private static void printTimes(String label, long tpi, double ratio) {
        if (print) {
            System.out.println(label + "\t" +
                               LoopHelpers.rightJustify(tpi) +
                               " \t" + ratio);
        }
    }

    private static void useResult(int r) {
        if (r == 0) // avoid overoptimization
            System.out.println("useless result: " + r);
        try {
            Thread.sleep(100);
        } catch(InterruptedException ex) {}

    }


    static final class Loop implements Runnable {
        private int v = rng.next();
        private volatile int result = 17;
        private final LoopHelpers.BarrierTimer timer = new LoopHelpers.BarrierTimer();
        private final CyclicBarrier barrier;
        private final int nthreads;
        private volatile int readBarrier;
        Loop(int nthreads) {
            this.nthreads = nthreads;
            barrier = new CyclicBarrier(nthreads+1, timer);
        }

        final void test() throws Exception {
            for (int i = 0; i < nthreads; ++i)
                pool.execute(this);
            barrier.await();
            barrier.await();
            long time = timer.getTime();
            if (nthreads == 1 && time < loopTime) loopTime = time;
            long tpi = time / ((long)iters * nthreads);
            printTimes("Loop", tpi, 0.0);
            useResult(result);
        }

        public final void run() {
            try {
                barrier.await();
                int x = v;
                int n = iters;
                while (n-- > 0) {
                    if ((x & mask) == 0) {
                        v = x = nextRandom(v);
                    }
                    else
                        x = nextRandom(x);
                }
                barrier.await();
                result += x + v;
            }
            catch (Exception ie) {
                return;
            }
        }
    }

    static final class ReentrantLockLoop implements Runnable {
        private int v = rng.next();
        private volatile int result = 17;
        private final ReentrantLock lock = new ReentrantLock();
        private final LoopHelpers.BarrierTimer timer = new LoopHelpers.BarrierTimer();
        private final CyclicBarrier barrier;
        private final int nthreads;
        private volatile int readBarrier;
        ReentrantLockLoop(int nthreads) {
            this.nthreads = nthreads;
            barrier = new CyclicBarrier(nthreads+1, timer);
        }

        final void test() throws Exception {
            for (int i = 0; i < nthreads; ++i)
                pool.execute(this);
            barrier.await();
            barrier.await();
            long time = timer.getTime();
            long tpi = (time - loopTime) / ((long)iters * nthreads);
            double ratio = ratio(nthreads, time);
            printTimes("RL", tpi, ratio);
            useResult(result);
        }

        public final void run() {
            try {
                barrier.await();
                int x = v;
                final ReentrantLock lock = this.lock;
                int n = iters;
                int m = mask;
                while (n-- > 0) {
                    if ((x & m) == 0) {
                        lock.lock();
                        v = x = nextRandom(v);
                        lock.unlock();
                    }
                    else
                        x = nextRandom(x);
                }
                barrier.await();
                result += x + v;
            }
            catch (Exception ie) {
                return;
            }
        }
    }

    static final class LockLoop implements Runnable {
        private int v = rng.next();
        private volatile int result = 17;
        private final LoopHelpers.BarrierTimer timer = new LoopHelpers.BarrierTimer();
        private final CyclicBarrier barrier;
        private final int nthreads;
        private volatile int readBarrier;
        LockLoop(int nthreads) {
            this.nthreads = nthreads;
            barrier = new CyclicBarrier(nthreads+1, timer);
        }

        final void test() throws Exception {
            for (int i = 0; i < nthreads; ++i)
                pool.execute(this);
            barrier.await();
            barrier.await();
            long time = timer.getTime();
            long tpi = (time - loopTime) / (((long)iters) * nthreads);
            double ratio = ratio(nthreads, time);
            printTimes("Sync", tpi, ratio);
            useResult(result);
        }

        public final void run() {
            try {
                barrier.await();
                int x = v;
                int n = iters;
                int m = mask;
                while (n-- > 0) {
                    if ((x & m) == 0) {
                        synchronized(this) {
                            v = x = nextRandom(v);
                        }
                    }
                    else
                        x = nextRandom(x);
                }
                barrier.await();
                result += x + v;
            }
            catch (Exception ie) {
                return;
            }
        }
    }

    static final class MutexLoop implements Runnable {
        private int v = rng.next();
        private volatile int result = 17;
        private final Mutex lock = new Mutex();
        private final LoopHelpers.BarrierTimer timer = new LoopHelpers.BarrierTimer();
        private final CyclicBarrier barrier;
        private final int nthreads;
        private volatile int readBarrier;
        MutexLoop(int nthreads) {
            this.nthreads = nthreads;
            barrier = new CyclicBarrier(nthreads+1, timer);
        }

        final void test() throws Exception {
            for (int i = 0; i < nthreads; ++i)
                pool.execute(this);
            barrier.await();
            barrier.await();
            long time = timer.getTime();
            long tpi = (time - loopTime) / ((long)iters * nthreads);
            double ratio = ratio(nthreads, time);
            printTimes("Mutex", tpi, ratio);
            useResult(result);
        }

        public final void run() {
            try {
                barrier.await();
                int x = v;
                final Mutex lock = this.lock;
                int n = iters;
                int m = mask;
                while (n-- > 0) {
                    if ((x & m) == 0) {
                        lock.lock();
                        v = x = nextRandom(v);
                        lock.unlock();
                    }
                    else
                        x = nextRandom(x);
                }
                barrier.await();
                result += x + v;
            }
            catch (Exception ie) {
                return;
            }
        }
    }

    static final class FairReentrantLockLoop implements Runnable {
        private int v = rng.next();
        private volatile int result = 17;
        private final ReentrantLock lock = new ReentrantLock(true);
        private final LoopHelpers.BarrierTimer timer = new LoopHelpers.BarrierTimer();
        private final CyclicBarrier barrier;
        private final int nthreads;
        private volatile int readBarrier;
        FairReentrantLockLoop(int nthreads) {
            this.nthreads = nthreads;
            barrier = new CyclicBarrier(nthreads+1, timer);
        }

        final void test() throws Exception {
            for (int i = 0; i < nthreads; ++i)
                pool.execute(this);
            barrier.await();
            barrier.await();
            long time = timer.getTime();
            long tpi = (time - loopTime) / (((long)iters) * nthreads);
            double ratio = ratio(nthreads, time);
            printTimes("FairRL", tpi, ratio);
            useResult(result);
        }

        public final void run() {
            try {
                barrier.await();
                int x = v;
                final ReentrantLock lock = this.lock;
                int n = iters;
                int m = mask;
                while (n-- > 0) {
                    if ((x & m) == 0) {
                        lock.lock();
                        v = x = nextRandom(v);
                        lock.unlock();
                    }
                    else
                        x = nextRandom(x);
                }
                barrier.await();
                result += x + v;
            }
            catch (Exception ie) {
                return;
            }
        }
    }

    static final class CASLoop implements Runnable {
        private final AtomicInteger v = new AtomicInteger(rng.next());
        private volatile int result = 17;
        private final LoopHelpers.BarrierTimer timer = new LoopHelpers.BarrierTimer();
        private final CyclicBarrier barrier;
        private final int nthreads;
        private volatile int readBarrier;
        CASLoop(int nthreads) {
            this.nthreads = nthreads;
            barrier = new CyclicBarrier(nthreads+1, timer);
        }

        final void test() throws Exception {
            for (int i = 0; i < nthreads; ++i)
                pool.execute(this);
            barrier.await();
            barrier.await();
            long time = timer.getTime();
            long tpi = (time - loopTime) / ((long)iters * nthreads);
            double ratio = ratio(nthreads, time);
            printTimes("CAS", tpi, ratio);
            useResult(result);
        }

        public final void run() {
            try {
                barrier.await();
                int x = v.get();
                int n = iters;
                int m = mask;
                while (n > 0) {
                    if ((x & m) == 0) {
                        int oldx = v.get();
                        int newx = nextRandom(oldx);
                        if (v.compareAndSet(oldx, newx)) {
                            x = newx;
                            --n;
                        }
                    }
                    else {
                        x = nextRandom(x);
                        --n;
                    }
                }
                barrier.await();
                result += x + v.get();
            }
            catch (Exception ie) {
                return;
            }
        }
    }

}
