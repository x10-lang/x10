public class Deque[T] {
    public static class EmptyDequeError extends Error {}
    var r:Rail[T]!;
    var rSize:UInt=0U; // the rail representing the underlying memory
    var head:UInt=0U; // points to the element to be popped from the stack.
    var tail:UInt=0U; // points to the next element to be written into
    var size:UInt;  // the number of elements in the deque.
    def this() {
        rSize = 10u;
        r = Rail.make[T](rSize as Int);
    }
   
    def push(t:T) {
        if (rSize==size)
            resize();
        r(tail) = t;
        tail++;
        if (tail==rSize)
        	tail=0;
        size++;
    }
    def resize() {
    	val r1 = Rail.make[T]((rSize*2) as Int);
    	r.copyTo(head, r1, 0, rSize-head);
    	 if (head > 0u) {
    		 r.copyTo(0, r1,rSize-head, head);
    	 }
    	 Runtime.deallocObject(r);
		 r=r1;
		 head=0;
		 tail=rSize;
		 rSize *=2;
		 return;
    }
    def size()=size;
    def pop():T {
        if (size==0u)
            throw new EmptyDequeError();
        size--;
        tail = tail==0u ? rSize-1 : tail-1;
        return r(tail);
    }
    def steal(k:UInt) {
		val result = ValRail.make[T](k, (i:Int)=> r(head+i < rSize ? head+i : head+i-rSize));
		head += k;
		if (head >= rSize)
			head -= rSize;
		size -= k;
		return result;
    }
}
