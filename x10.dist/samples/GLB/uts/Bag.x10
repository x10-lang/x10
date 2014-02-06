

import x10.compiler.*;

public final class Bag implements x10.glb.TaskBag {
    public val hash:Rail[SHA1Rand];
    public val lower:Rail[Int];
    public val upper:Rail[Int];
    
    public def this(size:Long) {
        hash = new Rail[SHA1Rand](size);
        lower = new Rail[Int](size);
        upper = new Rail[Int](size);
    }
    
    @Inline public def size() = hash.size;
}
