/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by igor on Sep 14, 2006
 */
package x10.lang;

/**
 * An interface implemented by all x10 arrays.
 * @author igor Sep 14, 2006
 */
public interface Array extends Indexable {
	public static interface binaryOp { }
	public static interface unaryOp { }
	public static interface pointwiseOp { } 
        public int[] getDescriptor();
}
