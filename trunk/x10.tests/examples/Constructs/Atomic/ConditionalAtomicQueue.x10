/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

import x10.util.Box;

/**
 * When test using producer-consumer paradigm.
 * This is a circular buffer implementation of a FIFO queue.
 *
 * @author kemal, 12/2004
 */
public class ConditionalAtomicQueue extends x10Test {

	private val siz: int;
	private val Q: Rail[T]!; // The circular buffer
	private var nelems: int; // number of items in buffer Q
	private var tail: int; // next free slot to insert incoming items
	// at tail of queue
	private var head: int; // pointer to item to remove from the front

	public def this(): ConditionalAtomicQueue = {
                val sz = 3;
		Q = Rail.make[T](sz);
                siz = sz;
		nelems = 0;
		tail = 0;
		head = 0;
	}

	/**
	 * insert i at the tail end of fifo queue.
	 */
	def insert(var i: T): void = {
		Q(tail) = i;
		tail = inc(tail, siz);
		nelems++;
	}

	/**
	 * remove an item from the queue
	 */
	def remove(): T = {
		var t: T = Q(head);
		head = inc(head, siz);
		nelems--;
		return t;
	}
	/**
	 * increment x modulo n
	 */
	static def inc(var x: int, var n: int): int = {
		var y: int = x+1;
		return y == n ? 0 : y;
	}

	/**
	 * true iff queue is empty
	 */
	def empty(): boolean = {
		chk(nelems> -1);
		return nelems <= 0;
	}

	/**
	 * true iff queue is full
	 */
	def full(): boolean = {
		chk(nelems < siz+1);
		return nelems >= siz;
	}

	public def run(): boolean = {
		val N = T.N;
		val NP = Place.MAX_PLACES;
		val D2  = MyDist.val(N*NP);
		val received = Array.make[int](D2);

		finish {
			// spawn producer activities on each place
			async( this )
				ateach (val (i): Point in MyDist.unique()) {
					for (val (j): Point in [0..N-1]) {
						val t = new T(i, j); // produce a T
						async(this) {
							when (!full()) { insert(t); }
						}
					}
				}
			// spawn a single consumer activity in place P0
			async( this ) {
				for (val p in D2.region) {
					var t: Box[T];
					when (!empty()) { t = remove(); }
					val t1 = t.value;
					async(t1) { t1.consume(); } // consume the T
					val m = at (t1) t1.getval();
					received(m) += 1;
					// remember how many times
					// we received this item
				}
			}
		}

		// Ensure all messages were received exactly once
		for (val p in D2.region) chk(received(p) == 1);

		// Ensure the FIFO queue is empty now
		chk(empty());

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ConditionalAtomicQueue().execute();
	}

	/**
	 * T is the type of the item that is being produced and consumed
	 */
	static class T {

		public const N: int = 2;

		var val: int; // the id of the item

		def this(var i: int, var j: int) = { // produce a T
			val = N*i+j;
		}

		public def consume(): void = { // consume a T
		}

		public def getval(): int = { return val; }
	}

	/**
	 * Utility routines to create simple common dists
	 */
	static class MyDist {
		/**
		 * create a simple 1D blocked dist
		 */
		static def block(var arraySize: int) = {
			return Dist.makeBlock(0..(arraySize-1), 0);
		}
		/**
		 * create a unique dist (mapping each i to place i)
		 */
		static def unique() =  {
			return Dist.makeUnique(Place.places);
		}

		/**
		 * create a constant-Here dist
		 */
		static def val(var arraySize: int) = {
			return [0..(arraySize-1)]->here;
		}
	}
}
