/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;
import x10.util.*;

/**
 * Test operator redefinition.
 * @author mandel
 */

class All extends x10Test {

    /** async */
    static class Async {
	public static operator async[T] (body: ()=>void) {}
	public static operator async[T] (x:T, body: ()=>void) {}
	public static operator async[T1,T2] (x:T1, y:T2, body: ()=>void) {}
    }
    class SubAsync extends Async {
	public def test() {
	    Async.async[Long] {}
	    (new Async()).async[Long] {}
	    super.async[Long] {}
	    SubAsync.super.async[Long] {}

	    // Async.async[Long] clocked () {}
	    // (new Async()).async[Long] clocked () {}
	    // super.async[Long] clocked () {}
	    // SubAsync.super.async[Long] clocked () {}

	    Async.async[Long] clocked (1) {}
	    (new Async()).async[Long] clocked (1) {}
	    super.async[Long] clocked (1) {}
	    SubAsync.super.async[Long] clocked (1) {}

	    Async.async clocked (1) {}
	    (new Async()).async clocked (1) {}
	    super.async clocked (1) {}
	    SubAsync.super.async clocked (1) {}

	    Async.async[Long, Boolean] clocked (1, true) {}
	    (new Async()).async[Long, Boolean] clocked (1, true) {}
	    super.async[Long, Boolean] clocked (1, true) {}
	    SubAsync.super.async[Long, Boolean] clocked (1, true) {}

	    Async.async clocked (1, true) {}
	    (new Async()).async clocked (1, true) {}
	    super.async clocked (1, true) {}
	    SubAsync.super.async clocked (1, true) {}

	    operator async[Long, Boolean] (1, true, ()=>{});
	    Async.operator async[Long, Boolean] (1, true, ()=>{});
	    (new Async()).operator async[Long, Boolean] (1, true, ()=>{});
	    super.operator async[Long, Boolean] (1, true, ()=>{});
	    SubAsync.super.operator async[Long, Boolean] (1, true, ()=>{});
	}
    }

    /** at */
    static class At {
	public static operator at[T] (body: ()=>void) {}
	public static operator at[T] (x:T, body: ()=>void) {}
	public static operator at[T1,T2] (x:T1, y:T2, body: ()=>void) {}
    }
    class SubAt extends At {
	public def test() {
	    At.at[Long]() {}
	    (new At()).at[Long]() {}
	    super.at[Long]() {}
	    SubAt.super.at[Long]() {}

	    At.at[Long](1) {}
	    (new At()).at[Long](1) {}
	    super.at[Long](1) {}
	    SubAt.super.at[Long](1) {}

	    At.at(1) {}
	    (new At()).at(1) {}
	    super.at(1) {}
	    SubAt.super.at(1) {}

	    At.at[Long, Boolean](1, true) {}
	    (new At()).at[Long, Boolean](1, true) {}
	    super.at[Long, Boolean](1, true) {}
	    SubAt.super.at[Long, Boolean](1, true) {}

	    At.at(1, true) {}
	    (new At()).at(1, true) {}
	    super.at(1, true) {}
	    SubAt.super.at(1, true) {}

	    operator at[Long, Boolean] (1, true, ()=>{});
	    At.operator at[Long, Boolean] (1, true, ()=>{});
	    (new At()).operator at[Long, Boolean] (1, true, ()=>{});
	    super.operator at[Long, Boolean] (1, true, ()=>{});
	    SubAt.super.operator at[Long, Boolean] (1, true, ()=>{});
	}
    }

    /** atomic */
    static class Atomic {
	public static operator atomic[T] (body: ()=>void) {}
    }
    class SubAtomic extends Atomic {
	public def test() {
	    Atomic.atomic[Long] {}
	    (new Atomic()).atomic[Long] {}
	    super.atomic[Long] {}
	    SubAtomic.super.atomic[Long] {}

	    operator atomic[Long] (()=>{});
	    Atomic.operator atomic[Long] (()=>{});
	    (new Atomic()).operator atomic[Long] (()=>{});
	    super.operator atomic[Long] (()=>{});
	    SubAtomic.super.operator atomic[Long] (()=>{});
	}
    }

    /** when */
    static class When {
	public static operator when[T] (body: ()=>void) {}
	public static operator when[T] (x:T, body: ()=>void) {}
	public static operator when[T1,T2] (x:T1, y:T2, body: ()=>void) {}
    }
    class SubWhen extends When {
	public def test() {
	    When.when[Long]() {}
	    (new When()).when[Long]() {}
	    super.when[Long]() {}
	    SubWhen.super.when[Long]() {}

	    When.when[Long](1) {}
	    (new When()).when[Long](1) {}
	    super.when[Long](1) {}
	    SubWhen.super.when[Long](1) {}

	    When.when(1) {}
	    (new When()).when(1) {}
	    super.when(1) {}
	    SubWhen.super.when(1) {}

	    When.when[Long, Boolean](1, true) {}
	    (new When()).when[Long, Boolean](1, true) {}
	    super.when[Long, Boolean](1, true) {}
	    SubWhen.super.when[Long, Boolean](1, true) {}

	    When.when(1, true) {}
	    (new When()).when(1, true) {}
	    super.when(1, true) {}
	    SubWhen.super.when(1, true) {}

	    operator when[Long, Boolean] (1, true, ()=>{});
	    When.operator when[Long, Boolean] (1, true, ()=>{});
	    (new When()).operator when[Long, Boolean] (1, true, ()=>{});
	    super.operator when[Long, Boolean] (1, true, ()=>{});
	    SubWhen.super.operator when[Long, Boolean] (1, true, ()=>{});
	}
    }

    /** finish */
    static class Finish {
	public static operator finish[T] (body: ()=>void) {}
    }
    class SubFinish extends Finish {
	public def test() {
	    Finish.finish[Long] {}
	    (new Finish()).finish[Long] {}
	    super.finish[Long] {}
	    SubFinish.super.finish[Long] {}

	    operator finish[Long] (()=>{});
	    Finish.operator finish[Long] (()=>{});
	    (new Finish()).operator finish[Long] (()=>{});
	    super.operator finish[Long] (()=>{});
	    SubFinish.super.operator finish[Long] (()=>{});
	}
    }


    /** throw */
    static class Throw {
	public static operator throw[T] (x: T) {}
    }
    class SubThrow extends Throw {
	public def test() {
	    Throw.throw[Long] 1 + 1;
	    (new Throw()).throw[Long] 1 + 1;
	    super.throw[Long] 1 + 1;
	    SubThrow.super.throw[Long] 1 + 1;

	    Throw.throw 1 + 1;
	    (new Throw()).throw 1 + 1;
	    super.throw 1 + 1;
	    SubThrow.super.throw 1 + 1;

	    operator throw[Long] (1 + 1);
	    Throw.operator throw[Long] (1 + 1);
	    (new Throw()).operator throw[Long] (1 + 1);
	    super.operator throw[Long] (1 + 1);
	    SubThrow.super.operator throw[Long] (1 + 1);
	}
    }


    /** try */
    static class Try {
	public static operator try[T] (body: ()=>void, hdl: ()=>void) {}
	public static operator try[T] (body: ()=>void, hdl: (T)=>void) {}
	public static operator try[T1,T2] (body: ()=>void, hdl1: (T1)=>void, hdl2: (T2)=>void, ()=>void) {}
    }
    class SubTry extends Try {
	public def test() {
	    Try.try[Long] {} catch () {}
	    (new Try()).try[Long] {} catch () {}
	    super.try[Long]{} catch () {}
	    SubTry.super.try[Long]{} catch () {}

	    Try.try[Long] {} finally {}
	    (new Try()).try[Long] {} finally {}
	    super.try[Long]{} finally {}
	    SubTry.super.try[Long]{} finally {}

	    Try.try[Long] {} catch (x:Long) {}
	    (new Try()).try[Long] {} catch (x:Long) {}
	    super.try[Long] {} catch (x:Long) {}
	    SubTry.super.try[Long] {} catch (x:Long) {}

	    Try.try {} catch (x:Long) {}
	    (new Try()).try {} catch (x:Long) {}
	    super.try {} catch (x:Long) {}
	    SubTry.super.try {} catch (x:Long) {}

	    Try.try[Long, Boolean]{} catch (x: Long) {} catch (x: Boolean) {} catch () {}
	    (new Try()).try[Long, Boolean]{} catch (x: Long) {} catch (x: Boolean) {} catch () {}
	    super.try[Long, Boolean]{} catch (x: Long) {} catch (x: Boolean) {} catch () {}
	    SubTry.super.try[Long, Boolean]{} catch (x: Long) {} catch (x: Boolean) {} catch () {}

	    Try.try{} catch (x: Long) {} catch (x: Boolean) {} catch () {}
	    (new Try()).try{} catch (x: Long) {} catch (x: Boolean) {} catch () {}
	    super.try{} catch (x: Long) {} catch (x: Boolean) {} catch () {}
	    SubTry.super.try{} catch (x: Long) {} catch (x: Boolean) {} catch () {}

	    Try.try{} catch (x: Long) {} catch (x: Boolean) {} finally {}
	    (new Try()).try{} catch (x: Long) {} catch (x: Boolean) {} finally {}
	    super.try{} catch (x: Long) {} catch (x: Boolean) {} finally {}
	    SubTry.super.try{} catch (x: Long) {} catch (x: Boolean) {} finally {}

	    operator try[Long, Boolean] (()=>{}, (x: Long)=>{}, (x: Boolean)=>{}, ()=>{});
	    Try.operator try[Long, Boolean] (()=>{}, (x: Long)=>{}, (x: Boolean)=>{}, ()=>{});
	    (new Try()).operator try[Long, Boolean] (()=>{}, (x: Long)=>{}, (x: Boolean)=>{}, ()=>{});
	    super.operator try[Long, Boolean] (()=>{}, (x: Long)=>{}, (x: Boolean)=>{}, ()=>{});
	    SubTry.super.operator try[Long, Boolean] (()=>{}, (x: Long)=>{}, (x: Boolean)=>{}, ()=>{});
	}
    }


    /** Launch tests */
    public def run() : boolean {
	(new SubAsync()).test();
	(new SubAt()).test();
	(new SubAtomic()).test();
	(new SubWhen()).test();
	(new SubFinish()).test();
	(new SubThrow()).test();
	(new SubTry()).test();
	return true;
    }

    public static def main(Rail[String]) {
        new All().execute();
    }
}
