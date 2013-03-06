/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

package x10.core;

final public class Rail<$T> extends x10.core.Ref implements x10.lang.Iterable, x10.core.fun.Fun_0_1,
        x10.serialization.X10JavaSerializable {
    private static final long serialVersionUID = 1L;
    public static final x10.rtt.RuntimeType<Rail> $RTT = x10.rtt.NamedType
            .<Rail> make("x10.lang.Rail", /* base class */
                         Rail.class,
                         /* variances */x10.rtt.RuntimeType.INVARIANTS(1), /* parents */
                         new x10.rtt.Type[] {
                                 x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.UnresolvedType.PARAM(0)),
                                 x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT,
                                                                x10.rtt.UnresolvedType.PARAM(0)),
                                 x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.LONG,
                                                                x10.rtt.UnresolvedType.PARAM(0)) });

    public x10.rtt.RuntimeType<?> $getRTT() {
        return $RTT;
    }

    public x10.rtt.Type<?> $getParam(int i) {
        if (i == 0) return $T;
        return null;
    }

    public static <$T> x10.serialization.X10JavaSerializable $_deserialize_body(x10.core.Rail<$T> $_obj,
                                                                                x10.serialization.X10JavaDeserializer $deserializer)
            throws java.io.IOException {

        $_obj.$T = (x10.rtt.Type) $deserializer.readRef();
        $_obj.raw = $deserializer.readRef();
        $_obj.size = $deserializer.readLong();
        return $_obj;

    }

    public static x10.serialization.X10JavaSerializable $_deserializer(x10.serialization.X10JavaDeserializer $deserializer)
            throws java.io.IOException {

        Rail $_obj = new Rail((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);

    }

    public void $_serialize(x10.serialization.X10JavaSerializer $serializer) throws java.io.IOException {

        $serializer.write((x10.serialization.X10JavaSerializable) this.$T);
        if (raw instanceof x10.serialization.X10JavaSerializable) {
            $serializer.write((x10.serialization.X10JavaSerializable) this.raw);
        } else {
            $serializer.write(this.raw);
        }
        $serializer.write(this.size);

    }

    // constructor just for allocation
    public Rail(final java.lang.System[] $dummy, final x10.rtt.Type $T) {
        x10.core.Rail.$initParams(this, $T);
    }

    // dispatcher for method abstract public (Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
        if (t1.equals(x10.rtt.Types.LONG)) {
            return $apply$G(x10.core.Long.$unbox(a1));
        }
        if (t1.equals(x10.rtt.Types.INT)) {
            return $apply$G(x10.core.Int.$unbox(a1));
        }
        throw new java.lang.Error("dispatch mechanism not completely implemented for contra-variant types.");
    }

    private x10.rtt.Type $T;

    // initializer of type parameters
    public static void $initParams(final Rail $this, final x10.rtt.Type $T) {
        $this.$T = $T;
    }

    public long size;

     final public x10.lang.LongRange range() {
        final x10.lang.LongRange alloc46509 = ((x10.lang.LongRange) (new x10.lang.LongRange((java.lang.System[]) null)));
        final long min69312 = ((long) (((int) (0))));
        final long t69329 = size;
        final long t69330 = ((long) (((int) (1))));
        final long max69313 = ((t69329) - (((long) (t69330))));
        final boolean x69428 = ((long) min69312) == ((long) 0L);
        alloc46509.min = min69312;
        alloc46509.max = max69313;
        alloc46509.zeroBased = x69428;
        return alloc46509;
    }

    public x10.lang.Iterator iterator() {
        final x10.lang.RailIterator alloc46510 = ((x10.lang.RailIterator) (new x10.lang.RailIterator<$T>(
                                                                                                         (java.lang.System[]) null,
                                                                                                         $T)));
        final x10.core.Rail x69316 = ((x10.core.Rail) (this));
        alloc46510.cur = 0L;
        alloc46510.rail = ((x10.core.Rail) (x69316));
        alloc46510.cur = 0L;
        return alloc46510;
    }

    public java.lang.String toString() {
        final x10.util.StringBuilder sb = ((x10.util.StringBuilder) (new x10.util.StringBuilder(
                                                                                                (java.lang.System[]) null)));

        final x10.util.ArrayList alloc69437 = ((x10.util.ArrayList) (new x10.util.ArrayList<x10.core.Char>(
                                                                                                           (java.lang.System[]) null,
                                                                                                           x10.rtt.Types.CHAR)));

        alloc69437.x10$util$ArrayList$$init$S();
        sb.buf = ((x10.util.ArrayList) (alloc69437));
        sb.add(((java.lang.String) ("[")));
        final long a69322 = size;
        final boolean t69331 = ((a69322) < (((long) (10L))));
        long t69332 = 0;
        if (t69331) {

            // #line 352 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Math.x10"
            t69332 = a69322;
        } else {

            // #line 352 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Math.x10"
            t69332 = 10L;
        }

        // #line 27
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final long sz = t69332;

        // #line 28
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        long i69438 = 0L;

        // #line 28
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        for (; true;) {

            // #line 28
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69439 = i69438;

            // #line 28
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final boolean t69440 = ((t69439) < (((long) (sz))));

            // #line 28
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            if (!(t69440)) {

                // #line 28
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                break;
            }

            // #line 29
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69429 = i69438;

            // #line 29
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final boolean t69430 = ((t69429) > (((long) (0L))));

            // #line 29
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            if (t69430) {

                // #line 29
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                sb.add(((java.lang.String) (",")));
            }

            // #line 30
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69431 = ((x10.core.IndexedMemoryChunk) (raw));

            // #line 30
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69432 = i69438;

            // #line 30
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final $T t69433 = (($T) ((((x10.core.IndexedMemoryChunk<$T>) (t69431))).$apply$G((int) (((long) (t69432))))));

            // #line 30
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final java.lang.String t69434 = (("") + (t69433));

            // #line 30
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            sb.add(((java.lang.String) (t69434)));

            // #line 28
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69435 = i69438;

            // #line 28
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69436 = ((t69435) + (((long) (1L))));

            // #line 28
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            i69438 = t69436;
        }

        // #line 32
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final long t69344 = size;

        // #line 32
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final boolean t69349 = ((sz) < (((long) (t69344))));

        // #line 32
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        if (t69349) {

            // #line 32
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69345 = size;

            // #line 32
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69346 = ((t69345) - (((long) (sz))));

            // #line 32
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final java.lang.String t69347 = (("...(omitted ") + ((x10.core.Long.$box(t69346))));

            // #line 32
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final java.lang.String t69348 = ((t69347) + (" elements)"));

            // #line 32
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            sb.add(((java.lang.String) (t69348)));
        }

        // #line 33
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        sb.add(((java.lang.String) ("]")));

        // #line 34
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final java.lang.String t69350 = sb.toString();

        // #line 34
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        return t69350;
    }

    // #line 39 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public x10.core.IndexedMemoryChunk<$T> raw;

    // #line 41 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public x10.core.IndexedMemoryChunk raw() {

        // #line 41
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69351 = ((x10.core.IndexedMemoryChunk) (raw));

        // #line 41
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        return t69351;
    }

    // #line 43 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T, final x10.core.IndexedMemoryChunk<$T> backingStore,
            __0$1x10$lang$Rail$$T$2 $dummy) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S(backingStore, (x10.core.Rail.__0$1x10$lang$Rail$$T$2) null);
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S(final x10.core.IndexedMemoryChunk<$T> backingStore,
                                                         __0$1x10$lang$Rail$$T$2 $dummy) {
        {

            // #line 44
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final int t69441 = ((((x10.core.IndexedMemoryChunk<$T>) (backingStore))).length);

            // #line 44
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69442 = ((long) (((int) (t69441))));

            // #line 44
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = t69442;

            // #line 45
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (backingStore));
        }
        return this;
    }

    // #line 49 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S();
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S() {
        {

            // #line 50
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = 0L;

            // #line 51
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69354 = x10.core.IndexedMemoryChunk
                    .<$T> allocate($T, ((int) (0)), false);

            // #line 51
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (t69354));
        }
        return this;
    }

    // #line 55 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T, final x10.lang.Unsafe.Token id$123, final long size) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S(id$123, size);
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S(final x10.lang.Unsafe.Token id$123, final long size) {
        {

            // #line 56
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = size;

            // #line 57
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69355 = x10.core.IndexedMemoryChunk.<$T> allocate($T, ((long) (size)),
                                                                                                 false);

            // #line 57
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (t69355));
        }
        return this;
    }

    // #line 61 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T, final x10.core.Rail<$T> src, __0$1x10$lang$Rail$$T$2 $dummy) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S(src, (x10.core.Rail.__0$1x10$lang$Rail$$T$2) null);
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S(final x10.core.Rail<$T> src, __0$1x10$lang$Rail$$T$2 $dummy) {
        {

            // #line 62
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69443 = ((x10.core.Rail<$T>) src).size;

            // #line 62
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = t69443;

            // #line 63
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69357 = ((x10.core.Rail<$T>) src).size;

            // #line 63
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final int size = ((int) (long) (((long) (t69357))));

            // #line 64
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk dst = x10.core.IndexedMemoryChunk
                    .<$T> allocate($T, ((int) (size)), false);

            // #line 66
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69358 = ((x10.core.IndexedMemoryChunk) (((x10.core.Rail<$T>) src).raw));

            // #line 66
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            x10.core.IndexedMemoryChunk.<$T> copy(t69358, ((int) (0)), dst, ((int) (0)), ((int) (size)));

            // #line 67
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (dst));
        }
        return this;
    }

    // #line 72 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T, final long size) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S(size);
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S(final long size) {
        {

            // #line 73
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = size;

            // #line 74
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69359 = ((x10.core.IndexedMemoryChunk) (x10.core.IndexedMemoryChunk
                    .<$T> allocate($T, ((long) (size)), true)));

            // #line 74
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (t69359));
        }
        return this;
    }

    // #line 77 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T, final long size, final $T init, __1x10$lang$Rail$$T $dummy) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S(size, init, (x10.core.Rail.__1x10$lang$Rail$$T) null);
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S(final long size, final $T init, __1x10$lang$Rail$$T $dummy) {
        {

            // #line 78
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = size;

            // #line 79
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69360 = x10.core.IndexedMemoryChunk.<$T> allocate($T, ((long) (size)),
                                                                                                 false);

            // #line 79
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (t69360));

            // #line 80
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            long i69448 = 0L;

            // #line 80
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            for (; true;) {

                // #line 80
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final long t69449 = i69448;

                // #line 80
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final boolean t69450 = ((t69449) < (((long) (size))));

                // #line 80
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                if (!(t69450)) {

                    // #line 80
                    // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                    break;
                }

                // #line 80
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final x10.core.IndexedMemoryChunk t69444 = ((x10.core.IndexedMemoryChunk) (raw));

                // #line 80
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final long t69445 = i69448;

                // #line 80
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                (((x10.core.IndexedMemoryChunk<$T>) (t69444))).$set((int) (((long) (t69445))), init);

                // #line 80
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final long t69446 = i69448;

                // #line 80
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final long t69447 = ((t69446) + (((long) (1L))));

                // #line 80
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                i69448 = t69447;
            }
        }
        return this;
    }

    // #line 83 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T, final long size, final x10.core.fun.Fun_0_1<x10.core.Long, $T> init,
            __1$1x10$lang$Long$3x10$lang$Rail$$T$2 $dummy) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S(size, init, (x10.core.Rail.__1$1x10$lang$Long$3x10$lang$Rail$$T$2) null);
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S(final long size,
                                                         final x10.core.fun.Fun_0_1<x10.core.Long, $T> init,
                                                         __1$1x10$lang$Long$3x10$lang$Rail$$T$2 $dummy) {
        {

            // #line 84
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = size;

            // #line 85
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69368 = x10.core.IndexedMemoryChunk.<$T> allocate($T, ((long) (size)),
                                                                                                 false);

            // #line 85
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (t69368));

            // #line 86
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            long i69457 = ((long) (((int) (0))));

            // #line 86
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            for (; true;) {

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final long t69458 = i69457;

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final boolean t69459 = ((t69458) < (((long) (size))));

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                if (!(t69459)) {

                    // #line 86
                    // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                    break;
                }

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final x10.core.IndexedMemoryChunk t69451 = ((x10.core.IndexedMemoryChunk) (raw));

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final long t69452 = i69457;

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final long t69453 = i69457;

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final $T t69454 = (($T) ((($T) ((x10.core.fun.Fun_0_1<x10.core.Long, $T>) init).$apply(x10.core.Long
                        .$box(t69453), x10.rtt.Types.LONG))));

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                (((x10.core.IndexedMemoryChunk<$T>) (t69451))).$set((int) (((long) (t69452))), t69454);

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final long t69455 = i69457;

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final long t69456 = ((t69455) + (((long) (1L))));

                // #line 86
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                i69457 = t69456;
            }
        }
        return this;
    }

    // #line 89 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public $T $apply$G(final long index) {

        // #line 89
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69378 = ((x10.core.IndexedMemoryChunk) (raw));

        // #line 89
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final $T t69379 = (($T) ((((x10.core.IndexedMemoryChunk<$T>) (t69378))).$apply$G((int) (((long) (index))))));

        // #line 89
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        return t69379;
    }

    // #line 91 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public $T $set__1x10$lang$Rail$$T$G(final long index, final $T v) {

        // #line 92
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69380 = ((x10.core.IndexedMemoryChunk) (raw));

        // #line 92
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        (((x10.core.IndexedMemoryChunk<$T>) (t69380))).$set((int) (((long) (index))), v);

        // #line 93
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        return v;
    }

    // #line 96 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public static <$T> void copy__0$1x10$lang$Rail$$T$2__1$1x10$lang$Rail$$T$2(final x10.rtt.Type $T,
                                                                               final x10.core.Rail<$T> src,
                                                                               final x10.core.Rail<$T> dst) {

        // #line 97
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final long t69381 = ((x10.core.Rail<$T>) src).size;

        // #line 97
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final long t69382 = ((x10.core.Rail<$T>) dst).size;

        // #line 97
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final boolean t69384 = ((long) t69381) != ((long) t69382);

        // #line 97
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        if (t69384) {

            // #line 97
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final java.lang.IllegalArgumentException t69383 = ((java.lang.IllegalArgumentException) (new java.lang.IllegalArgumentException(
                                                                                                                                            "source and destination do not have equal size")));

            // #line 97
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            throw t69383;
        }

        // #line 98
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69386 = ((x10.core.IndexedMemoryChunk) (((x10.core.Rail<$T>) src).raw));

        // #line 98
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69387 = ((x10.core.IndexedMemoryChunk) (((x10.core.Rail<$T>) dst).raw));

        // #line 98
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69385 = ((x10.core.IndexedMemoryChunk) (((x10.core.Rail<$T>) src).raw));

        // #line 98
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final int t69388 = ((((x10.core.IndexedMemoryChunk<$T>) (t69385))).length);

        // #line 98
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        x10.core.IndexedMemoryChunk.<$T> copy(t69386, ((int) (0)), t69387, ((int) (0)), ((int) (t69388)));
    }

    // #line 101 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public static <$T> void copy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(final x10.rtt.Type $T,
                                                                               final x10.core.Rail<$T> src,
                                                                               final long srcIndex,
                                                                               final x10.core.Rail<$T> dst,
                                                                               final long dstIndex, final long numElems) {

        // #line 103
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        long i = 0L;

        // #line 103
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        for (; true;) {

            // #line 103
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69390 = i;

            // #line 103
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final boolean t69397 = ((t69390) < (((long) (numElems))));

            // #line 103
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            if (!(t69397)) {

                // #line 103
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                break;
            }

            // #line 104
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69461 = i;

            // #line 91 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long index69462 = ((dstIndex) + (((long) (t69461))));

            // #line 104
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69463 = i;

            // #line 89 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long index69464 = ((srcIndex) + (((long) (t69463))));

            // #line 89 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69465 = ((x10.core.IndexedMemoryChunk) (((x10.core.Rail<$T>) src).raw));

            // #line 91 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final $T v69466 = (($T) ((((x10.core.IndexedMemoryChunk<$T>) (t69465)))
                    .$apply$G((int) (((long) (index69464))))));

            // #line 91 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            $T ret69467 = null;

            // #line 92 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69460 = ((x10.core.IndexedMemoryChunk) (((x10.core.Rail<$T>) dst).raw));

            // #line 92 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            (((x10.core.IndexedMemoryChunk<$T>) (t69460))).$set((int) (((long) (index69462))), v69466);

            // #line 93 .
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            ret69467 = (($T) (v69466));

            // #line 103
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69468 = i;

            // #line 103
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69469 = ((t69468) + (((long) (1L))));

            // #line 103
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            i = t69469;
        }
    }

    // #line 108 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public void clear() {

        // #line 109
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69398 = ((x10.core.IndexedMemoryChunk) (raw));

        // #line 109
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final long t69399 = ((long) (((int) (0))));

        // #line 109
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final long t69400 = size;

        // #line 109
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        (((x10.core.IndexedMemoryChunk<$T>) (t69398))).clear((int) (((long) (t69399))), (int) (((long) (t69400))));
    }

    // #line 114 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T, final int size) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S(size);
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S(final int size) {
        {

            // #line 115
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69470 = ((long) (((int) (size))));

            // #line 115
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = t69470;

            // #line 116
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69402 = ((x10.core.IndexedMemoryChunk) (x10.core.IndexedMemoryChunk
                    .<$T> allocate($T, ((int) (size)), true)));

            // #line 116
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (t69402));
        }
        return this;
    }

    // #line 119 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T, final int size, final $T init, __1x10$lang$Rail$$T $dummy) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S(size, init, (x10.core.Rail.__1x10$lang$Rail$$T) null);
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S(final int size, final $T init, __1x10$lang$Rail$$T $dummy) {
        {

            // #line 120
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69471 = ((long) (((int) (size))));

            // #line 120
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = t69471;

            // #line 121
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69404 = x10.core.IndexedMemoryChunk.<$T> allocate($T, ((int) (size)),
                                                                                                 false);

            // #line 121
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (t69404));

            // #line 122
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            int i69476 = 0;

            // #line 122
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            for (; true;) {

                // #line 122
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final int t69477 = i69476;

                // #line 122
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final boolean t69478 = ((t69477) < (((int) (size))));

                // #line 122
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                if (!(t69478)) {

                    // #line 122
                    // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                    break;
                }

                // #line 122
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final x10.core.IndexedMemoryChunk t69472 = ((x10.core.IndexedMemoryChunk) (raw));

                // #line 122
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final int t69473 = i69476;

                // #line 122
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                (((x10.core.IndexedMemoryChunk<$T>) (t69472))).$set(((int) (t69473)), init);

                // #line 122
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final int t69474 = i69476;

                // #line 122
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final int t69475 = ((t69474) + (((int) (1))));

                // #line 122
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                i69476 = t69475;
            }
        }
        return this;
    }

    // #line 125 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type $T, final int size, final x10.core.fun.Fun_0_1<x10.core.Int, $T> init,
            __1$1x10$lang$Int$3x10$lang$Rail$$T$2 $dummy) {
        this((java.lang.System[]) null, $T);
        x10$lang$Rail$$init$S(size, init, (x10.core.Rail.__1$1x10$lang$Int$3x10$lang$Rail$$T$2) null);
    }

    // constructor for non-virtual call
    final public x10.core.Rail<$T> x10$lang$Rail$$init$S(final int size,
                                                         final x10.core.fun.Fun_0_1<x10.core.Int, $T> init,
                                                         __1$1x10$lang$Int$3x10$lang$Rail$$T$2 $dummy) {
        {

            // #line 126
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final long t69479 = ((long) (((int) (size))));

            // #line 126
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.size = t69479;

            // #line 127
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            final x10.core.IndexedMemoryChunk t69413 = x10.core.IndexedMemoryChunk.<$T> allocate($T, ((int) (size)),
                                                                                                 false);

            // #line 127
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            this.raw = ((x10.core.IndexedMemoryChunk) (t69413));

            // #line 128
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            int i69486 = 0;

            // #line 128
            // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
            for (; true;) {

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final int t69487 = i69486;

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final boolean t69488 = ((t69487) < (((int) (size))));

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                if (!(t69488)) {

                    // #line 128
                    // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                    break;
                }

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final x10.core.IndexedMemoryChunk t69480 = ((x10.core.IndexedMemoryChunk) (raw));

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final int t69481 = i69486;

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final int t69482 = i69486;

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final $T t69483 = (($T) ((($T) ((x10.core.fun.Fun_0_1<x10.core.Int, $T>) init).$apply(x10.core.Int
                        .$box(t69482), x10.rtt.Types.INT))));

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                (((x10.core.IndexedMemoryChunk<$T>) (t69480))).$set(((int) (t69481)), t69483);

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final int t69484 = i69486;

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                final int t69485 = ((t69484) + (((int) (1))));

                // #line 128
                // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
                i69486 = t69485;
            }
        }
        return this;
    }

    // #line 131 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public $T $apply$G(final int index) {

        // #line 131
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69423 = ((x10.core.IndexedMemoryChunk) (raw));

        // #line 131
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final $T t69424 = (($T) ((((x10.core.IndexedMemoryChunk<$T>) (t69423))).$apply$G(((int) (index)))));

        // #line 131
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        return t69424;
    }

    // #line 133 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public $T $set__1x10$lang$Rail$$T$G(final int index, final $T v) {

        // #line 134
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69425 = ((x10.core.IndexedMemoryChunk) (raw));

        // #line 134
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        (((x10.core.IndexedMemoryChunk<$T>) (t69425))).$set(((int) (index)), v);

        // #line 135
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        return v;
    }

    // #line 138 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    public static <$T> void copy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(final x10.rtt.Type $T,
                                                                               final x10.core.Rail<$T> src,
                                                                               final int srcIndex,
                                                                               final x10.core.Rail<$T> dst,
                                                                               final int dstIndex, final int numElems) {

        // #line 141
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69426 = ((x10.core.IndexedMemoryChunk) (((x10.core.Rail<$T>) src).raw));

        // #line 141
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        final x10.core.IndexedMemoryChunk t69427 = ((x10.core.IndexedMemoryChunk) (((x10.core.Rail<$T>) dst).raw));

        // #line 141
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        x10.core.IndexedMemoryChunk.<$T> copy(t69426, ((int) (srcIndex)), t69427, ((int) (dstIndex)),
                                              ((int) (numElems)));
    }

    // #line 17 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    final public x10.core.Rail x10$lang$Rail$$this$x10$lang$Rail() {

        // #line 17
        // "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
        return x10.core.Rail.this;
    }

    // synthetic type for parameter mangling
    public static final class __0$1x10$lang$Rail$$T$2 {
    }

    // synthetic type for parameter mangling
    public static final class __1x10$lang$Rail$$T {
    }

    // synthetic type for parameter mangling
    public static final class __1$1x10$lang$Long$3x10$lang$Rail$$T$2 {
    }

    // synthetic type for parameter mangling
    public static final class __1$1x10$lang$Int$3x10$lang$Rail$$T$2 {
    }

}
