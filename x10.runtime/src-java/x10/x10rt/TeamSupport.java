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
package x10.x10rt;

import x10.core.Rail;
import x10.lang.Complex;
import x10.xrx.FinishState;
import x10.rtt.Type;
import x10.util.Team.DoubleIdx;

public class TeamSupport {
    
    /*
     * Must be manually kept in synch with x10rt_red_type in x10rt_types.h
     */
    private static final int RED_TYPE_BYTE = 1;
    private static final int RED_TYPE_SHORT = 2;
    private static final int RED_TYPE_INT = 4;
    private static final int RED_TYPE_LONG = 6;
    private static final int RED_TYPE_DOUBLE = 8;
    private static final int RED_TYPE_FLOAT = 9;
    private static final int RED_TYPE_COMPLEX = 11;
    
    private static int getTypeCode(Rail<?> chunk) {
        Object chunkRaw = chunk.getBackingArray();
        if (chunkRaw instanceof byte[]) {
            return RED_TYPE_BYTE;
        } else if (chunkRaw instanceof short[]) {
            return RED_TYPE_SHORT;
        } else if (chunkRaw instanceof int[]) {
            return RED_TYPE_INT;
        } else if (chunkRaw instanceof long[]) {
            return RED_TYPE_LONG;
        } else if (chunkRaw instanceof double[]) {
            return RED_TYPE_DOUBLE;
        } else if (chunkRaw instanceof float[]) {
            return RED_TYPE_FLOAT;
        } else if (chunkRaw instanceof Object[]) {
            Type<?> elemType = chunk.$getParam(0);
            if (elemType.equals(x10.lang.Complex.$RTT)) {
                return RED_TYPE_COMPLEX;
            }
        }
        throw new java.lang.UnsupportedOperationException("Unsupported type of src array "+chunk.$getParam(0).typeName()+" in nativeAllReduce");
    }
    
    // Called from native code in jni_team.cc
    private static void copyDoubleToComplex(double[] src, Object dstObj, int offset, int count) {
        Object[] dst = (Object[])dstObj;
        for (int i=0; i<count; i++) {
            dst[i+offset] = new Complex(src[2*i], src[2*i+1]);
        }
    }
    
    private static double[] copyComplexToNewDouble(Rail<?> src, int src_off, int count) {
        Object[] boxedSrc = src.getObjectArray();
        double[] tmp = new double[count*2];
        for (int i=0; i<count; i++) {
            x10.lang.Complex c = (Complex) boxedSrc[i+src_off];
            tmp[2*i] = c.re;
            tmp[2*i + 1] = c.im;
        }
        return tmp;
    }
    
    private static void aboutToDie(String methodName) {
        System.err.println("About to die in " + methodName);
        throw new java.lang.UnsupportedOperationException("About to die in " + methodName);
    }

    public static void nativeMake(Rail<x10.core.Int> places, int count, Rail<x10.core.Int> result) {
        if (!X10RT.forceSinglePlace) {
        int[] int_places = places.getIntArray();
        int[] nr = result.getIntArray();

        FinishState fs = ActivityManagement.activityCreationBookkeeping();

        try {
            nativeMakeImpl(int_places, count, nr, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeMake");
        }
        } else {
        int[] nr = result.getIntArray();
        nr[0] = 0;
        }
    }
        
    public static int nativeSize(int id) {
        if (!X10RT.forceSinglePlace) {
            int size = 0;
            try {
                size = nativeSizeImpl(id);
            } catch (UnsatisfiedLinkError e) {
                aboutToDie("nativeSize");
            }
            return size;
        }
        return 1;
    }

    public static void nativeBarrier(int id, int role) {
        if (!X10RT.forceSinglePlace) {
            FinishState fs = ActivityManagement.activityCreationBookkeeping();
            try {
                nativeBarrierImpl(id, role, fs);
            } catch (UnsatisfiedLinkError e) {
                aboutToDie("nativeBarrier");
            }
        }
    }
        
    public static void nativeScatter(int id, int role, int root, Rail<?> src, int src_off, 
                                     Rail<?> dst, int dst_off, int count) {
        if (!X10RT.forceSinglePlace) {
        Object srcRaw = src.getBackingArray();
        Object dstRaw = dst.getBackingArray();

        int typeCode = getTypeCode(src);
        assert getTypeCode(dst) == typeCode : "Incompatible src and dst arrays";

        FinishState fs = ActivityManagement.activityCreationBookkeeping();

        try {
            nativeScatterImpl(id, role, root, srcRaw, src_off, dstRaw, dst_off, count, typeCode, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeScatter");
        }
        }
    }
        
    public static void nativeBcast(int id, int role, int root, Rail<?> src, int src_off, 
                                   Rail<?> dst, int dst_off, int count) {
        if (!X10RT.forceSinglePlace) {
        int typeCode = getTypeCode(src);
        assert getTypeCode(dst) == typeCode : "Incompatible src and dst arrays";
        
        Object srcRaw = typeCode == RED_TYPE_COMPLEX ? copyComplexToNewDouble(src, src_off, count) : src.getBackingArray();
        Object dstRaw = dst.getBackingArray();

        FinishState fs = ActivityManagement.activityCreationBookkeeping();

        try {
            nativeBcastImpl(id, role, root, srcRaw, src_off, dstRaw, dst_off, count, typeCode, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeBcast");
        }
        }
    }

    public static void nativeAllToAll(int id, int role, Rail<?> src, int src_off, 
                                      Rail<?> dst, int dst_off, int count) {
        if (!X10RT.forceSinglePlace) {
        int typeCode = getTypeCode(src);
        Object srcRaw = typeCode == RED_TYPE_COMPLEX ? copyComplexToNewDouble(src, src_off, count) : src.getBackingArray();
        Object dstRaw = dst.getBackingArray();

        assert getTypeCode(dst) == typeCode : "Incompatible src and dst arrays";

        FinishState fs = ActivityManagement.activityCreationBookkeeping();

        try {
            nativeAllToAllImpl(id, role, srcRaw, src_off, dstRaw, dst_off, count, typeCode, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeAllToAll");
        }
        }
    }

    public static void nativeReduce(int id, int role, int root, Rail<?> src, int src_off, 
                                       Rail<?> dst, int dst_off, int count, int op) {
        if (!X10RT.forceSinglePlace) {
        int typeCode = getTypeCode(src);
        Object srcRaw = typeCode == RED_TYPE_COMPLEX ? copyComplexToNewDouble(src, src_off, count) : src.getBackingArray();
        Object dstRaw = dst.getBackingArray();
        
        assert getTypeCode(dst) == typeCode : "Incompatible src and dst arrays";
        
        FinishState fs = ActivityManagement.activityCreationBookkeeping();

        try {
            nativeReduceImpl(id, role, root, srcRaw, src_off, dstRaw, dst_off, count, op, typeCode, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeReduce");
        }
        }
    }
    
    public static void nativeAllReduce(int id, int role, Rail<?> src, int src_off, 
                                       Rail<?> dst, int dst_off, int count, int op) {
        if (!X10RT.forceSinglePlace) {
        int typeCode = getTypeCode(src);
        Object srcRaw = typeCode == RED_TYPE_COMPLEX ? copyComplexToNewDouble(src, src_off, count) : src.getBackingArray();
        Object dstRaw = dst.getBackingArray();
        
        assert getTypeCode(dst) == typeCode : "Incompatible src and dst arrays";
        
        FinishState fs = ActivityManagement.activityCreationBookkeeping();

        try {
            nativeAllReduceImpl(id, role, srcRaw, src_off, dstRaw, dst_off, count, op, typeCode, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeAllReduce");
        }
        }
    }

    public static void nativeIndexOfMax(int id, int role, Rail<?> src,
                                        Rail<?> dst) {
        if (!X10RT.forceSinglePlace) {
        DoubleIdx sObj = ((x10.util.Team.DoubleIdx[])src.getBackingArray())[0];
        DoubleIdx dObj = ((x10.util.Team.DoubleIdx[])dst.getBackingArray())[0];
        FinishState fs = ActivityManagement.activityCreationBookkeeping();

        try {
            nativeIndexOfMaxImpl(id, role, sObj, dObj, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeIndexOfMax");
        }
        } else {
            x10.util.Team.DoubleIdx dstTuple = ((x10.util.Team.DoubleIdx[])dst.getBackingArray())[0];
            x10.util.Team.DoubleIdx srcTuple = ((x10.util.Team.DoubleIdx[])src.getBackingArray())[0];
            dstTuple.value = srcTuple.value;
            dstTuple.idx = srcTuple.idx;
        }
    }

    public static void nativeIndexOfMin(int id, int role, Rail<?> src,
                                        Rail<?> dst) {
        if (!X10RT.forceSinglePlace) {
        DoubleIdx sObj = ((x10.util.Team.DoubleIdx[])src.getBackingArray())[0];
        DoubleIdx dObj = ((x10.util.Team.DoubleIdx[])dst.getBackingArray())[0];
        FinishState fs = ActivityManagement.activityCreationBookkeeping();

        try {
            nativeIndexOfMinImpl(id, role, sObj, dObj, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeIndexOfMin");
        }
        } else {
            x10.util.Team.DoubleIdx dstTuple = ((x10.util.Team.DoubleIdx[])dst.getBackingArray())[0];
            x10.util.Team.DoubleIdx srcTuple = ((x10.util.Team.DoubleIdx[])src.getBackingArray())[0];
            dstTuple.value = srcTuple.value;
            dstTuple.idx = srcTuple.idx;
        }
    }

    public static void nativeSplit(int id, int role, int color, int new_role, Rail<x10.core.Int> result) {
        if (!X10RT.forceSinglePlace) {
        int[] nr = result.getIntArray();

        FinishState fs = ActivityManagement.activityCreationBookkeeping();

        try {
            nativeSplitImpl(id, role, color, new_role, nr, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeSplit");
        }
        }
    }
    
    public static void nativeDel(int id, int role) {
        if (!X10RT.forceSinglePlace) {
        FinishState fs = ActivityManagement.activityCreationBookkeeping();
        
        try {
            nativeDelImpl(id, role, fs);
        } catch (UnsatisfiedLinkError e) {
            aboutToDie("nativeDel");
        }
        }
    }
    
    private static native void nativeMakeImpl(int[] places, int count, int[] result, FinishState fs);
    
    private static native int nativeSizeImpl(int id);
    
    private static native void nativeBarrierImpl(int id, int role, FinishState fs);
    
    private static native void nativeScatterImpl(int id, int role, int root, Object srcRaw, int src_off, 
                                                 Object dstRaw, int dst_off,
                                                 int count, int typecode, FinishState fs);
    
    private static native void nativeBcastImpl(int id, int role, int root, Object srcRaw, int src_off, 
                                               Object dstRaw, int dst_off,
                                               int count, int typecode, FinishState fs);
    
    private static native void nativeAllToAllImpl(int id, int role, Object srcRaw, int src_off, 
                                                  Object dstRaw, int dst_off,
                                                  int count, int typecode, FinishState fs);

    private static native void nativeReduceImpl(int id, int role, int root, Object srcRaw, int src_off, 
                                                   Object dstRaw, int dst_off,
                                                   int count, int op, int typecode, FinishState fs);

    private static native void nativeAllReduceImpl(int id, int role, Object srcRaw, int src_off, 
                                                   Object dstRaw, int dst_off,
                                                   int count, int op, int typecode, FinishState fs);
    
    private static native void nativeIndexOfMaxImpl(int id, int role, DoubleIdx src, DoubleIdx dst, FinishState fs);
    
    private static native void nativeIndexOfMinImpl(int id, int role, DoubleIdx src, DoubleIdx dst, FinishState fs);
    
    private static native void nativeSplitImpl(int id, int role, int color, int new_role, int[] nr, FinishState fs);
    
    private static native void nativeDelImpl(int id, int role, FinishState fs);
    
    static native void initialize();
}
