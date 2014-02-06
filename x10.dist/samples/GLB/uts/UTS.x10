

import x10.compiler.*;

public class UTS {
    protected var hash:Rail[SHA1Rand];
    protected var lower:Rail[Int];
    protected var upper:Rail[Int];
    protected var size:Long;

    private val den:Double;

    public var count:Long;

    public def this(factor:Int) {
        hash = new Rail[SHA1Rand](4096);
        lower = new Rail[Int](4096);
        upper = new Rail[Int](4096);
        den = Math.log(factor / (1.0 + factor));
    }

    @Inline public final def init(seed:Int, height:Int) {
        push(SHA1Rand(seed, height));
        ++count;
    }

    @Inline public final def push(h:SHA1Rand):void {
        val u = Math.floor(Math.log(1.0 - h() / 2147483648.0) / den) as Int;
        if(u > 0n) {
            if(size >= hash.size) grow();
            hash(size) = h;
            lower(size) = 0n;
            upper(size++) = u;
        }
    }

    @Inline public final def expand() {
        val top = size - 1;
        val h = hash(top);
        val d = h.depth;
        val l = lower(top);
        val u = upper(top) - 1n;
        
        if(d > 1n) {
            if(u == l) --size; else upper(top) = u;
            push(SHA1Rand(h, u, d - 1n));
        } else {
            --size;
            count += u - l;
        }
    }

    public final def grow():void {
        val capacity = size * 2;
        val h = new Rail[SHA1Rand](capacity);
        Rail.copy(hash, 0, h, 0, size);
        hash = h;
        val l = new Rail[Int](capacity);
        Rail.copy(lower, 0, l, 0, size);
        lower = l;
        val u = new Rail[Int](capacity);
        Rail.copy(upper, 0, u, 0, size);
        upper = u;
    }

    static def sub(str:String, start:Int, end:Int) = str.substring(start, Math.min(end, str.length()));

    public static def print(time:Long, count:Long) {
        Console.OUT.println("Performance: " + count + "/" +
                sub("" + time/1e9, 0n, 6n) + " = " +
                sub("" + (count/(time/1e3)), 0n, 6n) + "M nodes/s");
    }

    public static def main(Rail[String]) {
        val queue = new UTS(4n); // branching factor
        Console.OUT.println("Starting...");
        var time:Long = System.nanoTime();
        queue.init(19n, 13n); // seed, depth
        while (queue.size > 0) {
            queue.expand();
            ++queue.count;
        }
        time = System.nanoTime() - time;
        Console.OUT.println("Finished.");
        print(time, queue.count);
    }
}
