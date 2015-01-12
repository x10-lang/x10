/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.comm.mpi;

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;

/**
 * This class provides a request handle for nonblocking MPI communication.
 */
@NativeCPPInclude("mpi_api.h")
@NativeCPPCompilationUnit("mpi_api.cc")
public class RequestHandleMPI {

    @Native("c++","mpi_get_request_memsize((#1)->raw)")
		public static native def get_request_memsize(sl:Rail[Int]):void;

	// Request waiting
	@Native("c++","mpi_wait_request((#1)->raw)")
		public static native def wait_request(hreq:Rail[Int]):void;

	@Native("c++","mpi_test_request((#1)->raw, (#2)->raw)")
		public static native def test_request(hreq:Rail[Int], 
											  flag:Rail[Int]):void;

	public val valid:Boolean;
	public val handle:Rail[Int];

	public def this() {
		valid = true;
		val size_int = new Rail[Int](1);

		get_request_memsize(size_int);
		
		handle = new Rail[Int](size_int(0));
	}

	public def this(vld:Boolean) {
		valid = vld;
		val size_int = new Rail[Int](1);
		get_request_memsize(size_int);
		
		handle = new Rail[Int](size_int(0));
	}

	// stop until the requested communication is complete
	public def mywait():void {
		if (valid)
			wait_request(this.handle);
	}

	public def test():Boolean {
		val flag = new Rail[Int](1);
		if (valid) {
			this.test_request(this.handle, flag);
			if (flag(0) >0) 
				return true;
			else
				return false;	
		}
		return true;
	}
}
