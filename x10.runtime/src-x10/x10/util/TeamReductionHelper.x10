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

package x10.util;

/**
 * We want to get highly efficient code for reductions on both Managed 
 * and Native X10 without dropping into @Native code.
 *
 * Sadly, that means we need to typecase on the type of the Rail and then
 * dispatch to hand specialized routines for Rail's of each primitive type
 * on which we support reductions. 
 * This gives the compiler enough static information to generate good
 * code for the loops, which is critical for reduction performance on
 * large Rails.
 *
 * Pulled into a separate class to avoid exploding the size of Team
 * and to make it easier to add new typecases.
 */
class TeamReductionHelper {

    // TODO: the casts to Any are to workaround XTENLANG-3407.
    //       Remove them when it is fixed.
    static def performReduction[T](src:Rail[T], src_off:Long, dst:Rail[T], dst_off:Long, count:Long, operation:Int) {
        if ((src as Any) instanceof Rail[Double]) {
            reduce(src as Any as Rail[Double], src_off, dst as Any as Rail[Double], dst_off, count, operation);
        } else if ((src as Any) instanceof Rail[Float]) {
            reduce(src as Any as Rail[Float], src_off, dst as Any as Rail[Float], dst_off, count, operation);
        } else if ((src as Any) instanceof Rail[Complex]) {
            reduce(src as Any as Rail[Complex], src_off, dst as Any as Rail[Complex], dst_off, count, operation);
        } else if ((src as Any) instanceof Rail[Long]) {
            reduce(src as Any as Rail[Long], src_off, dst as Any as Rail[Long], dst_off, count, operation);
        } else if ((src as Any) instanceof Rail[Int]) {
            reduce(src as Any as Rail[Int], src_off, dst as Any as Rail[Int], dst_off, count, operation);
        } else if ((src as Any) instanceof Rail[Short]) {
            reduce(src as Any as Rail[Short], src_off, dst as Any as Rail[Short], dst_off, count, operation);
        } else if ((src as Any) instanceof Rail[Byte]) {
            reduce(src as Any as Rail[Byte], src_off, dst as Any as Rail[Byte], dst_off, count, operation);
        } else {
            reduce(src, src_off, dst, dst_off, count, operation);
        }
    }

    private static def reduce(src:Rail[Double], src_off:Long, dst:Rail[Double], dst_off:Long, count:Long, operation:Int) {
        switch (operation) {
            case Team.ADD:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) + src(i+src_off);
                }       
            break;
            case Team.MUL:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) * src(i+src_off);
                }       
            break;
            case Team.AND:
                Runtime.println("ERROR: Bitwise AND not supported on Double");
            break;
            case Team.OR:
                Runtime.println("ERROR: Bitwise OR not supported on Double");
            break;
            case Team.XOR:
                Runtime.println("ERROR: Bitwise XOR not supported on Double");
            break;
            case Team.MAX:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = Math.max(dst(i+dst_off), src(i+src_off));
                }       
            break;
            case Team.MIN:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = Math.min(dst(i+dst_off), src(i+src_off));
                }       
            break;
            default:
                Runtime.println("ERROR: Unknown reduction operation: "+operation);
        }
    }

    private static def reduce(src:Rail[Float], src_off:Long, dst:Rail[Float], dst_off:Long, count:Long, operation:Int) {
        switch (operation) {
            case Team.ADD:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) + src(i+src_off);
                }       
            break;
            case Team.MUL:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) * src(i+src_off);
                }       
            break;
            case Team.AND:
                Runtime.println("ERROR: Bitwise AND not supported on Float");
            break;
            case Team.OR:
                Runtime.println("ERROR: Bitwise OR not supported on Float");
            break;
            case Team.XOR:
                Runtime.println("ERROR: Bitwise XOR not supported on Float");
            break;
            case Team.MAX:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = Math.max(dst(i+dst_off), src(i+src_off));
                }       
            break;
            case Team.MIN:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = Math.min(dst(i+dst_off), src(i+src_off));
                }       
            break;
            default:
                Runtime.println("ERROR: Unknown reduction operation: "+operation);
        }
    }

    private static def reduce(src:Rail[Complex], src_off:Long, dst:Rail[Complex], dst_off:Long, count:Long, operation:Int) {
        switch (operation) {
            case Team.ADD:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) + src(i+src_off);
                }       
            break;
            case Team.MUL:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) * src(i+src_off);
                }       
            break;
            case Team.AND:
                Runtime.println("ERROR: Bitwise AND not supported on Complex");
            break;
            case Team.OR:
                Runtime.println("ERROR: Bitwise OR not supported on Complex");
            break;
            case Team.XOR:
                Runtime.println("ERROR: Bitwise XOR not supported on Complex");
            break;
            case Team.MAX:
                Runtime.println("ERROR: Max not supported on Complex");
            break;
            case Team.MIN:
                Runtime.println("ERROR: Min not supported on Complex");
            break;
            default:
                Runtime.println("ERROR: Unknown reduction operation: "+operation);
        }
    }

    private static def reduce(src:Rail[Long], src_off:Long, dst:Rail[Long], dst_off:Long, count:Long, operation:Int) {
        switch (operation) {
            case Team.ADD:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) + src(i+src_off);
                }       
            break;
            case Team.MUL:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) * src(i+src_off);
                }       
            break;
            case Team.AND:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) & src(i+src_off);
                }       
            break;
            case Team.OR:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) | src(i+src_off);
                }       
            break;
            case Team.XOR:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) ^ src(i+src_off);
                }       
            break;
            case Team.MAX:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = Math.max(dst(i+dst_off), src(i+src_off));
                }       
            break;
            case Team.MIN:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = Math.min(dst(i+dst_off), src(i+src_off));
                }       
            break;
            default:
                Runtime.println("ERROR: Unknown reduction operation: "+operation);
        }
    }

    private static def reduce(src:Rail[Int], src_off:Long, dst:Rail[Int], dst_off:Long, count:Long, operation:Int) {
        switch (operation) {
            case Team.ADD:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) + src(i+src_off);
                }       
            break;
            case Team.MUL:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) * src(i+src_off);
                }       
            break;
            case Team.AND:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) & src(i+src_off);
                }       
            break;
            case Team.OR:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) | src(i+src_off);
                }       
            break;
            case Team.XOR:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) ^ src(i+src_off);
                }       
            break;
            case Team.MAX:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = Math.max(dst(i+dst_off), src(i+src_off));
                }       
            break;
            case Team.MIN:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = Math.min(dst(i+dst_off), src(i+src_off));
                }       
            break;
            default:
                Runtime.println("ERROR: Unknown reduction operation: "+operation);
        }
    }

    private static def reduce(src:Rail[Short], src_off:Long, dst:Rail[Short], dst_off:Long, count:Long, operation:Int) {
        switch (operation) {
            case Team.ADD:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) + src(i+src_off);
                }       
            break;
            case Team.MUL:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) * src(i+src_off);
                }       
            break;
            case Team.AND:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) & src(i+src_off);
                }       
            break;
            case Team.OR:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) | src(i+src_off);
                }       
            break;
            case Team.XOR:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) ^ src(i+src_off);
                }       
            break;
            case Team.MAX:
                for (i in 0..(count-1)) {
                    val a = dst(i+dst_off);
                    val b = src(i+src_off);
                    dst(i+dst_off) = a < b ? b : a;
                }       
            break;
            case Team.MIN:
                for (i in 0..(count-1)) {
                    val a = dst(i+dst_off);
                    val b = src(i+src_off);
                    dst(i+dst_off) = a < b ? a : b;
                }       
            break;
            default:
                Runtime.println("ERROR: Unknown reduction operation: "+operation);
        }
    }

    private static def reduce(src:Rail[Byte], src_off:Long, dst:Rail[Byte], dst_off:Long, count:Long, operation:Int) {
        switch (operation) {
            case Team.ADD:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) + src(i+src_off);
                }       
            break;
            case Team.MUL:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) * src(i+src_off);
                }       
            break;
            case Team.AND:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) & src(i+src_off);
                }       
            break;
            case Team.OR:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) | src(i+src_off);
                }       
            break;
            case Team.XOR:
                for (i in 0..(count-1)) {
                    dst(i+dst_off) = dst(i+dst_off) ^ src(i+src_off);
                }       
            break;
            case Team.MAX:
                for (i in 0..(count-1)) {
                    val a = dst(i+dst_off);
                    val b = src(i+src_off);
                    dst(i+dst_off) = a < b ? b : a;
                }       
            break;
            case Team.MIN:
                for (i in 0..(count-1)) {
                    val a = dst(i+dst_off);
                    val b = src(i+src_off);
                    dst(i+dst_off) = a < b ? a : b;
                }       
            break;
            default:
                Runtime.println("ERROR: Unknown reduction operation: "+operation);
        }
    }

    private static def reduce[T](src:Rail[T], src_off:Long, dst:Rail[T], dst_off:Long, count:Long, operation:Int) {
        val thunk = (op:(a:T,b:T)=>T) => {
            for (i in 0..(count-1)) {
                dst(i+dst_off) = op( dst(i+dst_off), src(i+src_off) );
            }       
        };

        switch (operation) {
            case Team.ADD:
                thunk((a:T, b:T) => (a as Arithmetic[T] + b));
            break;
            case Team.MUL:
                thunk((a:T, b:T) => (a as Arithmetic[T] * b));
            break;
            case Team.AND:
                thunk((a:T, b:T) => (a as Bitwise[T] & b));
            break;
            case Team.OR:
                thunk((a:T, b:T) => (a as Bitwise[T] | b));
            break;
            case Team.XOR:
                thunk((a:T, b:T) => (a as Bitwise[T] ^ b));
            break;
            case Team.MAX:
                thunk((a:T, b:T) => (a as Ordered[T] < b ? b : a));
            break;
            case Team.MIN:
                thunk((a:T, b:T) => (a as Ordered[T] < b ? a : b));
            break;
            default:
                Runtime.println("ERROR: Unknown reduction operation: "+operation);
        }
    }
}
