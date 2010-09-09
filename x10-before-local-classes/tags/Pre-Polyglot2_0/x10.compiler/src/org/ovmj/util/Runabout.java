/**
 * (C) 2002, 2003, 2004 Christian Grothoff
 *
 * The Runabout is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2, or (at
 * your option) any later version.
 *
 * The Runabout is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Runabout; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * @file org/ovmj/util/Runabout.java
 */
package org.ovmj.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Runabout is a fast implementation of the Walkabout which is a
 * variant of the Visitor Pattern that does not require an accept
 * method and uses reflection instead.<p>
 *
 * An instance of Runabout is able to walk over an arbitrary object
 * graph using visit methods which take arguments of the specific type
 * of the object to visit. For each node in the object graph the
 * Runabout invokes the most appropriate visit method.<p>
 *
 * Using the Runabout typically involves subclassimg Runabout and
 * adding a couple of visit methods. The Runabout provides a
 * 'visitAppropriate' method which will invoke the most appropriate
 * visit method of the current Runabout instance. If no visit method
 * is applicable, visitAppropriate calls visitDefault() which, if
 * not overriden, throws an exception.<p>
 *
 * The elements of the object graph typically extend the Element
 * class, which provides a generic way to quickly invoke the Runabout
 * on all the fields of the Element.<p>
 * 
 * Note that the Runabout uses dynamic code generation and dynamic
 * loading in order to be quickly able to invoke the appropriate visit
 * methods. To make the dynamic code generation fast, the code inlines
 * parts of Java class-files in binary form (ugly!).<br>
 * 
 * A per-thread Cache is used to speed-up the creation of the Runabout
 * by caching reflection, code creation and dynamic loading
 * operations.<p>
 *
 * <bf>Restrictions:</bf>
 * Java semantics require:
 * <ul>
 *   <li>all subclasses must be public, sadly this also means that
 *       <strong>you can not use an anonymous inner class</strong> (!)</li>
 *   <li>the types to all arguments of visit methods must be public</li>
 *   <li>all visit methods must be public (!)</li>
 * </ul>
 * Otherwise the visitor will die with an IllegalAccessError during
 * execution.<p>
 *
 * @author Christian Grothoff
 */
public class Runabout {

    /**
     * Name of the visit methods.  Do not change.
     */
    public static final String VISIT = "visit";

    /**
     * ThreadLocal for the Runabout.Cache. We cache reflective
     * information per thread; this avoids the need for synchronization
     * while allowing an application to GC Runabout state by terminating
     * the Thread that used the Runabout.
     */
    private static final ThreadLocal tl_cache_;
    static {
	tl_cache_ = new ThreadLocal() {
		protected Object initialValue() {
		    return new Runabout.Cache();
		}
	    };
    }

    /**
     * map_ contains a mapping from a class to the appropriate visit
     * method. Note that at the beginning, map_ only contains the
     * explicit mappings as given by the visit methods. Over time,
     * map_ will be amended to also contain direct mappings for
     * subclasses to the appropriate visit methods if they are used.
     */
    private HTClass2Runabout_Code map_;

    /**
     * Is this a private map (only for this instance) or is it shared
     * via a cache (and must thus be cloned on write)?  Shared maps
     * are used to increase efficiency unless maps are extended using
     * 'addExternalVisit'.
     */
    private boolean mapIsPrivate_;

    /**
     * Code to invoke if no visitor is found (used to avoid scanning
     * the hierarchy again and again).
     */
    protected final Code noCode;

    /**
     * Create a Runabout.
     */
    public Runabout() {	
	this.noCode = new NoCode();
	Runabout.Cache cache 
	    = (Runabout.Cache) tl_cache_.get();
	HTClass2Runabout_Code map 
	    = cache.get(this.getClass());
	if (map == null) 
	    map = makeMap(cache);
	this.map_ = map;	
	this.mapIsPrivate_ = false;
    }

    /**
     * Add a Code of a visit method to visit for a certain type which
     * is not defined in the visitor itself. Note that this extention
     * is not added to the cache for all instances but kept private to
     * this class.<p>
     *
     * This method should only be invoked before the Runabout is used
     * (for example in the constructor) since afterwards subtypes of
     * cl may already be mapped to more general handlers. This method
     * will not check this!
     *
     * @param cl the type for which to invoke this visit method
     * @param co the code to invoke
     */
    public final void addExternalVisit(Class cl,
				       Code co) {
	if (false == this.mapIsPrivate_) {
	    map_ = map_.cloneHT();
	    this.mapIsPrivate_ = true;
	}
	map_.put(cl, co);
    }

    /**
     * Call the appropriate visit method. Use this method if you are
     * visiting a graph of objects (no primitives).
     * @param o the object to visit
     */ 
    public final void visitAppropriate(Object o) {
	visitAppropriate(o, o.getClass());
    }

    /**
     * Call the appropriate visit method. Use this methd if the
     * traversal may hit primitives.  This is typcially the case if
     * you walk over some graph using reflection.
     *
     * @param o the object to visit
     * @param c the type of the object, should be equal to o.getClass() unless o
     * is primitive (then it should describe the primitive type)
     */ 
    public void visitAppropriate(Object o,
				 Class c) {
	getAppropriateCodeInternal(c).visit(this,o);
    }

    /**
     * Visit method that is called from visitAppropriate(Integer i,
     * Integer.TYPE) with i.intValue() as the argument. Override if
     * you need to visit primitive integers.
     */
    public void visit(int i) {
	visitDefault(new Integer(i));
    }

    /**
     * Visit method that is called from visitAppropriate(Float f,
     * Float.TYPE) with f.floatValue() as the argument. Override if
     * you need to visit primitive floats.
     */
    public void visit(float f) {
	visitDefault(new Float(f));
    }

    /**
     * Visit method that is called from visitAppropriate(Double d,
     * Double.TYPE) with d.doubleValue() as the argument. Override if
     * you need to visit primitive doubles.
     */
    public void visit(double d) {
	visitDefault(new Double(d));
    }

    /**
     * Visit method that is called from visitAppropriate(Long l,
     * Long.TYPE) with l.longValue() as the argument. Override if
     * you need to visit primitive longs.
     */
    public void visit(long l) {
	visitDefault(new Long(l));
    }

    /**
     * Visit method that is called from visitAppropriate(Byte b,
     * Byte.TYPE) with b.byteValue() as the argument. Override if
     * you need to visit primitive bytes.
     */
    public void visit(byte b) {
	visitDefault(new Byte(b));
    }

    /**
     * Visit method that is called from visitAppropriate(Character c,
     * Character.TYPE) with c.charValue() as the argument. Override if
     * you need to visit primitive characters.
     */
    public void visit(char c) {
	visitDefault(new Character(c));
    }

    /**
     * Visit method that is called from visitAppropriate(Boolean b,
     * Boolean.TYPE) with b.booleanValue() as the argument. Override if
     * you need to visit primitive booleans.
     */
    public void visit(boolean b) {
	visitDefault(new Boolean(b));
    }

    /**
     * Visit method that is called from visitAppropriate(Short s,
     * Short.TYPE) with s.shortValue() as the argument. Override if
     * you need to visit primitive shorts.
     */
    public void visit(short s) {
	visitDefault(new Short(s));
    }

    /**
     * Obtain the appropriate code to call for class c.  The method
     * either obtains the code quickly from the code map (fast path)
     * or by calling the lookup method getAppropriateCode.
     *
     * @return the code, never returns null
     */
    private Code getAppropriateCodeInternal(Class c) {
	Code co = map_.get(c);
	if (co != null)
	    return co;
	co = getAppropriateCode(c);
	if (co == null)
	    co = noCode;
	map_.put(c, co);
	return co;
    }

    /**
     * Find the appropriate Code to call in the map. If no code is
     * found, return null.  This lookup strategy first attempts to
     * find a visit method defined for the parent classes of c.  If no
     * such method exists, it attempts to find an unambiguous visit
     * method matching any interface transitively implemented by c.
     * If that does not exist either, null is returned.  If only an
     * ambiguous visit method exists, an exception is raised.
     *
     * @param c the class for which to find the code
     * @return the code to run, or null if no code was found
     * @throws RunaboutException if the lookup would be ambiguous
     */
    protected Code getAppropriateCode(Class c) {
	if (c.isArray()) {
	    // uh uh, array subtyping in action...
	    int dims = 1;
	    Class component = c.getComponentType();
	    while (component.isArray()) {
		dims++;
		component = component.getComponentType();
	    }
	    Class superComp = component.getSuperclass();
	    while (superComp != null) {
		Code co = getCodeForClass
		    (Array.newInstance(superComp, 
				       new int[dims]).getClass());
		if (co != null)
		    return co;
		superComp = superComp.getSuperclass();
	    }
	    // now try subtyping with multi-dimensional Object[]
	    // (see crazy runabout test).
	    Class objectClass = c.getSuperclass();
	    while (dims > 1) {
		Code co = getCodeForClass
		    (Array.newInstance(objectClass, 
				       new int[dims]).getClass());
		if (co != null)
		    return co;		
		dims--;
	    }
	}
	Class cl = c.getSuperclass();	    
	while (cl != null) {
	    Code co = getCodeForClass(cl);
	    if (co != null) 
		return co;	    
	    cl = cl.getSuperclass();	    
	}
	return getAppropriateCode_ifc(c, c);
    }

    /**
     * Helper method to allow subclasses to override 
     * getAppropriateCode with their own lookup mechanims.
     *
     * getAppropriateCode must return the appropriate Code
     * object for the resolved method.  This helper method
     * can be used to obtain the generated Code objects for the visit
     * methods of the Runabout.  If the class does not have a
     * visit method for the specified class, the result of
     * a previous run to getAppropriateCode for that class will
     * be returned <strong>if</strong> getAppropriateCode was
     * called for this class before.  If there is no visit method
     * for c and getAppropriateCode was never called for c, null
     * is returned.
     *
     * @param c a class
     * @return the Code to invoke visit(c) of the given class     
     */
    protected final Code getCodeForClass(Class c) {
	return map_.get(c);
    }					     
    
    /**
     * Find the appropriate Code to call in the map.  If no code is
     * found, return null.
     *
     * @param c the class for which to find the code
     * @param cl the class where to start looking from
     * @return the code to run, or null if no code was found
     */
    private Code getAppropriateCode_ifc(Class c,
					Class cl) {
	Code co = null;
	while (cl != null) {
	    Class[] ifc = cl.getInterfaces();
	    for (int i=0;i<ifc.length;i++) {
		Code r = getCodeForClass(ifc[i]);
		if (r != null) {
		    if ( (co != null) &&
			 (r != co) )
			throw new RunaboutException("Ambiguous resolution for visit call to " + c + 
						    " in " + this.getClass().getName());
		    co = r;
		}
	    }
	    for (int i=0;i<ifc.length;i++) {
		Code r = getAppropriateCode_ifc(c, ifc[i]);
		if (r != null) {
		    if ( (co != null) &&
			 (r != co) )
			throw new RunaboutException("Ambiguous resolution for visit call to " + c + 
						    " in " + this.getClass().getName());
		    co = r;
		}
	    }
	    cl = cl.getSuperclass();
	}
	return co;
    }

    /**
     * Generate the initial version of the map that maps
     * classes to Code to call the appropriate visit method.
     */
    private HTClass2Runabout_Code makeMap(Cache cache) {
	// get number of methods
	int size = 0;
	Class me = this.getClass();
	while (me != null) {
	    size += me.getDeclaredMethods().length;
	    me = me.getSuperclass();
	}
	// create map with slight over-estimate
	HTClass2Runabout_Code result 
	    = new HTClass2Runabout_Code(size*2);
	// handle primitives...
	result.put(Integer.TYPE,
		   new IntCode());
	result.put(Float.TYPE,
		   new FloatCode());
	result.put(Double.TYPE,
		   new DoubleCode());
	result.put(Long.TYPE,
		   new LongCode());
	result.put(Byte.TYPE,
		   new ByteCode());
	result.put(Character.TYPE,
		   new CharCode());
	result.put(Boolean.TYPE,
		   new BooleanCode());
	result.put(Short.TYPE,
		   new ShortCode());
	// for all methods - create call code, put
	me = this.getClass();
	while (me != null) {
	    Method[] methods = me.getDeclaredMethods();
	    for (int i=0;i<methods.length;i++) {
		Method m = methods[i];
		if ( (m.getName().equals(VISIT)) &&
		     (!Modifier.isStatic(m.getModifiers())) ) {
		    Class[] args = m.getParameterTypes();
		    if (args.length != 1)
			throw new RunaboutException
			    ("Invalid number of arguments for Runabout in method " + m);
		    Class viC = args[0];
		    if (result.get(viC) != null)
			continue;
		    Code co = null;
		    co = makeCode(cache,
				  viC);
		    if (co == null)
			throw new RunaboutException
			    ("Could not create/load dynamic code!");
		    result.put(viC, co);
		}
	    }
	    me = me.getSuperclass();
	}
	cache.put(this.getClass(), result);
	return result;
    }

    /**
     * Create the code to invoke a visit method.
     * @param cache the cache to use (for dynamic loading)
     * @param c the type of the argument to the visit method
     */
    private Code makeCode(Cache cache,
			  Class c) {
	byte[] myName // Lovm/util/RunaboutExample; substitute 
	    = canonicalName(getClass(), false); 
	final int myNameLen = myName.length;
	final int myNameLenM2 = myNameLen-2;
	byte[] cName // Ljava/lang/String; substitute
	    = canonicalName(c, false);
	byte[] cNameCast
	    = canonicalName(c, true);
	final int cNameLen = cName.length;
	final int cNameLenCast = cNameCast.length-2;
	byte[] code
	    = new byte[genCodeTemplate.length - 63 +
		       myNameLenM2 + cNameLenCast + cNameLen];

	// Build code by substituting a few strings in genCodeTemplate.	
	// 118-146: ovm.util/RunaboutExample => myName
	// 150-165: java/lang/String => cName
	// 195-202: XXXXXXXX => number
	// 254-274: (Ljava/lang/String;)V => "("+cName+")V"	
	// 283-299: ovm.util/Runabout => myName
	
	System.arraycopy(genCodeTemplate, 0,
			 code, 0,
			 116);
	code[116] = (byte) ( (myNameLenM2) >> 8);
	code[117] = (byte) ( (myNameLenM2) & 255);	
	System.arraycopy(myName, 1,
			 code, 118,
			 myNameLenM2);
	code[118 + myNameLenM2] = 1; // tag for string
	code[119 + myNameLenM2] = (byte) ( (cNameLenCast) >> 8);
	code[120 + myNameLenM2] = (byte) ( (cNameLenCast) & 255);
	System.arraycopy(cNameCast, 1,
			 code, 121 + myNameLenM2,
			 cNameLenCast);
	System.arraycopy(genCodeTemplate, 166,
			 code, 121 + myNameLenM2 + cNameLenCast,
			 252 - 166);
	code[121 + myNameLenM2 + cNameLenCast + 252 - 166] = (byte) ( (cNameLen+3) >> 8);
	code[122 + myNameLenM2 + cNameLenCast + 252 - 166] = (byte) ( (cNameLen+3) & 255); 
	code[123 + myNameLenM2 + cNameLenCast + 252 - 166] = (byte) '(';
	System.arraycopy(cName, 0,
			 code, 124 + myNameLenM2 + cNameLenCast + 252 - 166,
			 cNameLen);
	code[124 + myNameLenM2 + cNameLenCast + 252 - 166 + cNameLen] = (byte) ')'; 
	code[125 + myNameLenM2 + cNameLenCast + 252 - 166 + cNameLen] = (byte) 'V'; 
	System.arraycopy(genCodeTemplate, 275,
			 code, 126 + myNameLenM2 + cNameLenCast + 252 - 166 + cNameLen,
			 genCodeTemplate.length - 275);	
	return cache.loadCode(code, 
			      195 - 16 - 29 + myNameLenM2 + cNameLenCast);
    }

    /**
     * Get the class name in canonical form.
     *
     * @param cls the class, may not be primitive
     * @return the ovm name, following the convention of
     * <code>java.util.Class.forName</code> according to the JavaDoc
     * specification (JDK 1.2.2/1.3/1.4) which differs from the actual
     * implementation in both SUN and IBM VMs.
     */
    private static byte[] canonicalName(Class cls,
					boolean forCast) {
        String cname = cls.getName();
	try {
	    byte[] utf = cname.getBytes("UTF-8");
	    int len = utf.length; // may be > cname.length()!
	    if ( (cname.charAt(0) != '[') || (forCast) ) {
		byte[] ret = new byte[len+2];
		ret[0] = (byte)'L';
		System.arraycopy(utf, 0, ret, 1, len);
		ret[len+1] = (byte)';';
		for (int i=len;i>0;i--)
		    if (ret[i] == (byte) '.')
			ret[i] = (byte) '/';
		return ret;
	    } else {
		byte[] ret = utf;
		for (int i=len-1;i>=0;i--)
		    if (ret[i] == (byte) '.')
			ret[i] = (byte) '/';
		return ret;
	    }
	} catch (UnsupportedEncodingException uee) {
	    throw new RunaboutException("UTF8 encoding not supported!?: " + uee);
	}
    }

    /**
     * The Runabout.Cache is essentially a per-class cache of the
     * internal constant state of a Runabout instance. It contains the
     * generated code to quicly invoke the appropriate visit methods.
     *
     * @author Christian Grothoff
     */
    static final class Cache {

	/**
	 * ClassLoader to use to load the code.
	 */
	private final ClassLoader loader_;

	/**
	 * Last name used by the class loader.
	 */
	private final byte[] lastName_;

	/**
	 * Mapping of classes to Maps.
	 */ 
	private final HTClass2HTClass2Runabout_Code cache_;

	/**
	 * Code that the loader should use.
	 */
	byte[] code;

	/**
	 * Create the Cache.
	 */
	Cache() {
	    this.loader_ = new ClassLoader() {
		    public Class loadClass(String name) 
			throws ClassNotFoundException {
			if (name == "Code")
			    return defineClass(null, code, 0, code.length);
			else
			    return super.loadClass(name);
		    }
		};
	    this.cache_ = new HTClass2HTClass2Runabout_Code();
	    this.lastName_ = new byte[8];
	    for (int i=0;i<8;i++) 
		lastName_[i] = (byte) '0';	    
	}

	/**
	 * Create a class from the given bytecode.  Since classes
	 * loaded by the same Loader must have a unique name, this
	 * method patches the bytecode at the given offset, changing
	 * the next 8 characters to a unique Java classname.
	 *
	 * @param byteCode the bytecode of the class which must
	 *        describe a class of type 'Code'.  The class must contain a
	 *        sequence XXXXXXXX at offset xIdx where the classname is to be
	 *        patched
	 * @param xIdx the index of the XXXXXXXX sequence
	 * @return an instance of the loaded class, null on error
	 * @throws ArrayIndexOutOfBoundsException if more than
	 *         62<sup>8</sup> classes are loaded :-)
	 * @throws RunaboutException if there are problems with dynamic loading
	 *         of the byteCode
	 */
	final Code loadCode(byte[] byteCode,
			    int xIdx) {
	    boolean overflow = true;
	    int index = 7;
	    while (overflow) {
		overflow = false;
		lastName_[index]++;
		if (lastName_[index] == (byte) ('9'+1))
		    lastName_[index] = (byte) 'A';
		if (lastName_[index] == (byte) ('Z'+1))
		    lastName_[index] = (byte) 'a';
		if (lastName_[index] == (byte) ('z'+1)) {
		    lastName_[index] = (byte) '0';
		    overflow = true;
		    index--;
		}		
	    }
	    System.arraycopy(lastName_, 0,
			     byteCode, xIdx,
			     8);
	    this.code = byteCode;

	    Code co = null;
	    try {
		co = (Code)loader_.loadClass("Code").newInstance();
	    } catch (InstantiationException ie) {
		throw new RunaboutException(ie.toString());
	    } catch (ClassNotFoundException cnfe) {
		throw new RunaboutException(cnfe.toString());
	    } catch (IllegalArgumentException iae) {
		throw new RunaboutException(iae.toString());
	    } catch (ClassFormatError cfe) {
		throw new RunaboutException(cfe.toString());
	    } catch (IllegalAccessException iae) {
	    }
	    this.code = null; // help GC	
	    return co;
	}

	/**
	 * Associate a CodeMap with a Class.
	 */ 
	final void put(Class cl,
		       HTClass2Runabout_Code co) {
	    this.cache_.put(cl, co);
	}

	/**
	 * Obtain a map from the cache.
	 */
	final HTClass2Runabout_Code get(Class cl) {
	    return this.cache_.get(cl);
	}
	
    } // end of Runabout.Cache

    /**
     * Code is the generic interface that all generated classes
     * implement. It is used to quickly map a given class to the
     * appropriate visit method.
     *
     * @author Christian Grothoff
     */
    public static abstract class Code {
	public abstract void visit(Runabout r,
				   Object o);
    } // end of Runabout.Code

    /**
     * Implementation of Code that is called if no visit method
     * matches (calls visitDefault).
     *
     * @author Christian Grothoff
     */
    static final class NoCode extends Code {
	public final void visit(Runabout r, 
				Object o) {
	    r.visitDefault(o);
	}
    } // end of Runabout.NoCode
    
    /**
     * Override this method to provide a default behavior when no other visit 
     * matches.  The Runabout semantics are to search for a visit(X)
     * and if there is no match,  call visitDefault().  As usual with the 
     * Runabout, visit(X) looks at classes before interfaces.  By default,
     * visitDefault throws an exception.
     */
    protected void visitDefault(Object o) {
	throw new RunaboutException("No visit method defined in " + this.getClass() + 
				    " for " + o.getClass()); 
    }

    /* ******** implementations of Code for Primitives ********** */

    final class IntCode extends Code {
	public final void visit(Runabout r,
				Object o) {
	    r.visit(((Integer)o).intValue());
	}
    }
    final class FloatCode extends Code {
	public final void visit(Runabout r,
				Object o) {
	     r.visit(((Float)o).floatValue());
	}
    }
    final class DoubleCode extends Code {
	public final void visit(Runabout r,
				Object o) {
	     r.visit(((Double)o).doubleValue());
	}
    }
    final class LongCode extends Code {
	public final void visit(Runabout r,
				Object o) {
	     r.visit(((Long)o).longValue());
	}
    }
    final class ShortCode extends Code {
	public final void visit(Runabout r,
				Object o) {
	     r.visit(((Short)o).shortValue());
	}
    }
    final class CharCode extends Code {
	public final void visit(Runabout r,
				Object o) {
	     r.visit(((Character)o).charValue());
	}
    }  
    final class ByteCode extends Code {
	public final void visit(Runabout r,
				Object o) {
	     r.visit(((Byte)o).byteValue());
	}
    }   
    final class BooleanCode extends Code {
	public final void visit(Runabout r,
				Object o) {
	     r.visit(((Boolean)o).booleanValue());
	}
    }
    
    /**
     * Generic Exception for problems in the Runabout.
     * @author Christian Grothoff
     */
    public static final class RunaboutException 
	extends RuntimeException {
	RunaboutException(String s) {
	    super(s);
	}
    }

    /* **************** data *************** */

    /**
     * Compile 'GenCodeXXXXXXXX.java' with the option '-g:none' to 
     * tell javac not to include any debugging information. This
     * is the generated class file. 
     */
    private final static byte genCodeTemplate[] = {
	-54,-2,-70,-66,0,0,0,46,0,22,10,0,6,0,12,7,0,13,7,0,
	14,10,0,2,0,15,7,0,16,7,0,18,1,0,6,60,105,110,105,116,
	62,1,0,3,40,41,86,1,0,4,67,111,100,101,1,0,5,118,105,115,
	105,116,1,0,45,40,76,111,114,103,47,111,118,109,106,47,117,116,105,108,
	47,82,117,110,97,98,111,117,116,59,76,106,97,118,97,47,108,97,110,103,
	47,79,98,106,101,99,116,59,41,86,12,0,7,0,8,1,0,29,111,114,
	103,47,111,118,109,106,47,117,116,105,108,47,82,117,110,97,98,111,117,116,
	69,120,97,109,112,108,101,1,0,16,106,97,118,97,47,108,97,110,103,47,
	83,116,114,105,110,103,12,0,10,0,20,1,0,29,111,114,103,47,111,118,
	109,106,47,117,116,105,108,47,71,101,110,67,111,100,101,88,88,88,88,88,
	88,88,88,7,0,21,1,0,27,111,114,103,47,111,118,109,106,47,117,116,
	105,108,47,82,117,110,97,98,111,117,116,36,67,111,100,101,1,0,12,73,
	110,110,101,114,67,108,97,115,115,101,115,1,0,21,40,76,106,97,118,97,
	47,108,97,110,103,47,83,116,114,105,110,103,59,41,86,1,0,22,111,114,
	103,47,111,118,109,106,47,117,116,105,108,47,82,117,110,97,98,111,117,116,
	0,33,0,5,0,6,0,0,0,0,0,2,0,1,0,7,0,8,0,1,
	0,9,0,0,0,17,0,1,0,1,0,0,0,5,42,-73,0,1,-79,0,
	0,0,0,0,1,0,10,0,11,0,1,0,9,0,0,0,24,0,2,0,
	3,0,0,0,12,43,-64,0,2,44,-64,0,3,-74,0,4,-79,0,0,0,
	0,0,1,0,19,0,0,0,10,0,1,0,6,0,17,0,9,4,9}; // GenCodeXXXXXXXX.class

    /* ******************* Hashtables ************************* */

    /**
     * Hashtable.
     * @author Christian Grothoff
     */
    static final class HTClass2HTClass2Runabout_Code {

	static final int MININT = -0x7fffffff;
	static final HTClass2Runabout_Code NOTFOUND = null; 
	private static final int DEFAULT_SIZE = 256;
	
	final private int mask_;                 // mask for main arrays
	final private int maskCollisions_;       // mask for collision table
	private Class[] keys_;                   // array of keys
	private HTClass2Runabout_Code[] values_; // array of values
	
	/**
	 * If there is a collision, we keep the 'collided' elements in here (linked list).
	 */
	private Binding[] collisions_;
		
	/**
	 * Internal binding class. Stores a key, value, and a link
	 * to the next binding in the chain.
	 */
	static private final class Binding {
	    Binding link;
	    Class key;
	    HTClass2Runabout_Code value;
	    Binding(Binding link, Class key, HTClass2Runabout_Code value) {
		this.link = link;
		this.key = key;
		this.value = value;
	    } 
	    Binding cloneBinding() {
		if (link == null)
		    return new Binding(null, key, value);
		else
		    return new Binding(link.cloneBinding(), key, value);
	    }
	}
	
	/**
	 * Default constructor.
	 */
	HTClass2HTClass2Runabout_Code() {
	    this(DEFAULT_SIZE);
	}
	
	protected HTClass2HTClass2Runabout_Code(HTClass2HTClass2Runabout_Code other) {
	    this.mask_ = other.mask_;
	    this.maskCollisions_ = other.maskCollisions_;
	    this.collisions_ = other.collisions_;
	    this.keys_ = other.keys_;
	    this.values_ = other.values_;
	}
	
	/**
	 * Constructor to build a new hashtable. The size hint passed is used
	 * to choose the least power of 2 greater than the hint for the hashtable.
	 */
	HTClass2HTClass2Runabout_Code(int hint) {	    
	    int size = 8;
	    int sizeCollisions;
	    while ( size < hint ) size *= 2;
	    mask_ = size-1;
	    sizeCollisions = size>>3;
	    maskCollisions_ = sizeCollisions-1; /* make collision table 1/8th of the size of the main table */
	    this.collisions_ = new Binding[sizeCollisions];
	    this.keys_ = new Class[size];
	    this.values_ = new HTClass2Runabout_Code[size];	    
	}
	
	/**
	 * Get a value from hashtable given a key. Returns the value NOTFOUND
	 * if the key specified is not found in the hashtable.
	 */
	final HTClass2Runabout_Code get(Class key) {
	    int hash = mask_&key.hashCode();
	    Class keys_hash = keys_[hash];
	    if (keys_hash == null) {		
		return NOTFOUND;
	    }
	    if (key.equals(keys_hash)) {		
		return values_[hash];
	    }
	    return findInCollisions(maskCollisions_&hash, key);
	}
	
	/**
	 * Put a key and value into the hashtable. Checks to see if
	 * the key is already in the hashtable, and if so, updates
	 * the value associated with the key. If the {@link #keys_ keys_},
	 * {@link #collisions_ collisions_} or {@link #values_ values_} 
	 * arrays (or any other internal state) ever change, notify the readonly 
	 * view.  
	 */
	final void put(Class key, HTClass2Runabout_Code value) {
	    int hash = mask_&key.hashCode();
	    Class keys_hash = keys_[hash];
	    if (keys_hash == null) { // simple insert
		keys_[hash] = key;
		values_[hash] = value;
		
		return;
	    }     
	    if (key.equals(keys_hash)) { // replace
		values_[hash] = value;
		
		return;
	    } 
	    // collision
	    int hashCollision = hash&maskCollisions_;
	    insertInCollisions(hashCollision, key, value);
	    
	}
	
	/**
	 * Private function to search the collision table for a key value.
	 * Searches the collisions_ table starting at the given offset until
	 * either the correct Binding is found or the end of the list is reached.
	 */
	private final HTClass2Runabout_Code findInCollisions(int offset, Class key) {
	    Binding p;
	    
	    for (p = collisions_[offset]; p != null; p = p.link) {
		if (key.equals(p.key)) { 
		    return p.value;
		}		
	    } 
	    return NOTFOUND;
	}
	
	/**
	 * Private function to insert a key and value into the collision table.
	 * Searches the collisions_ table starting at the given offset and
	 * continues until either the correct Binding is found, or the end of
	 * the list is reached. If no Binding is found with the correct key, it
	 * will create a new Binding to hold the key and value.
	 */
	private final  void insertInCollisions(int offset, 
					       Class key,
					       HTClass2Runabout_Code value) {
	    Binding h = collisions_[offset], p;
	    	    
	    for (p = h; p != null; p = p.link) {
		if (key.equals(p.key)) { 
		    p.value = value;		    
		    return;
		}		
	    }	    
	    collisions_[offset] = new Binding(h, key, value); 
	}
	
    } // End HTClass2HTClass2Runabout_Code
    
    /**
     * Hashtable.
     * @author Christian Grothoff
     */
    static final class HTClass2Runabout_Code {

	static final int MININT = -0x7fffffff;
	static final Runabout.Code NOTFOUND = null; 
	
	final private int mask_;                     // mask for main arrays
	final private int maskCollisions_;           // mask for collision table
	private Class[] keys_;     // array of keys
	private Runabout.Code[] values_; // array of values

	/**
	 * If there is a collision, we keep the 'collided' elements in here (linked list).
	 */
	private Binding[] collisions_;

	/**
	 * Internal binding class. Stores a key, value, and a link
	 * to the next binding in the chain.
	 */
	static private final class Binding {
	    Binding link;
	    Class key;
	    Runabout.Code value;
	    Binding(Binding link, Class key, Runabout.Code value) {
		this.link = link;
		this.key = key;
		this.value = value;
	    } 
	    Binding cloneBinding() {
		if (link == null)
		    return new Binding(null, key, value);
		else
		    return new Binding(link.cloneBinding(), key, value);
	    }
	}

	protected HTClass2Runabout_Code(HTClass2Runabout_Code other) {
	    this.mask_ = other.mask_;
	    this.maskCollisions_ = other.maskCollisions_;
	     this.collisions_ = other.collisions_;
	     this.keys_ = other.keys_;
	     this.values_ = other.values_;
	}
	
	/**
	 * Constructor to build a new hashtable. The size hint passed is used
	 * to choose the least power of 2 greater than the hint for the hashtable.
	 */
	HTClass2Runabout_Code(int hint) {	    
	    int size = 8;
	    int sizeCollisions;
	    while ( size < hint ) size *= 2;
	    mask_ = size-1;
	    sizeCollisions = size>>3;
	    maskCollisions_ = sizeCollisions-1; /* make collision table 1/8th of the size of the main table */
	    this.collisions_ = new Binding[sizeCollisions];
	    this.keys_ = new Class[size];
	    this.values_ = new Runabout.Code[size];
	}
	
	/**
	 * Get a value from hashtable given a key. Returns the value NOTFOUND
	 * if the key specified is not found in the hashtable.
	 */
	final Runabout.Code get(Class key) {
	    int hash = mask_&key.hashCode();
	    Class keys_hash = keys_[hash];
	    if (keys_hash == null) {
           
		return NOTFOUND;
	    }
	    if (key.equals(keys_hash)) {
           
		return values_[hash];
	    }
	    return findInCollisions(maskCollisions_&hash, key);
	}

	/**
	 * Put a key and value into the hashtable. Checks to see if
	 * the key is already in the hashtable, and if so, updates
	 * the value associated with the key. If the {@link #keys_ keys_},
	 * {@link #collisions_ collisions_} or {@link #values_ values_} 
	 * arrays (or any other internal state) ever change, notify the readonly 
	 * view.  
	 */
	final void put(Class key, Runabout.Code value) {
	    int hash = mask_&key.hashCode();
	    Class keys_hash = keys_[hash];
	    if (keys_hash == null) { // simple insert
		keys_[hash] = key;
		values_[hash] = value;
           
		return;
	    }     
	    if (key.equals(keys_hash)) { // replace
		values_[hash] = value;
	   
		return;
	    } 
	    // collision
	    int hashCollision = hash&maskCollisions_;
	    insertInCollisions(hashCollision, key, value);
	
	}

	/**
	 * Private function to search the collision table for a key value.
	 * Searches the collisions_ table starting at the given offset until
	 * either the correct Binding is found or the end of the list is reached.
	 */
	private final Runabout.Code findInCollisions(int offset, Class key) {
	    Binding p;

	    for (p = collisions_[offset]; p != null; p = p.link) {
		if (key.equals(p.key)) { 
		    return p.value;
		}	    
	    } 
	    return NOTFOUND;
	}

	final HTClass2Runabout_Code cloneHT() {
	    HTClass2Runabout_Code clone = new HTClass2Runabout_Code(this);
	    keys_ = (Class[]) keys_.clone();
	    values_ = (Runabout.Code[]) values_.clone();
	    Binding[] col = new Binding[collisions_.length];
	    for (int i=0;i<collisions_.length;i++)
		if (collisions_[i] != null)
		    col[i] = collisions_[i].cloneBinding();
	    collisions_ = col;
	    return clone;
	}
	
	/**
	 * Private function to insert a key and value into the collision table.
	 * Searches the collisions_ table starting at the given offset and
	 * continues until either the correct Binding is found, or the end of
	 * the list is reached. If no Binding is found with the correct key, it
	 * will create a new Binding to hold the key and value.
	 */
	private final void insertInCollisions(int offset, 
					      Class key,
					      Runabout.Code value) {
	    Binding h = collisions_[offset], p;       
	    for (p = h; p != null; p = p.link) {
		if (key.equals(p.key)) { 
		    p.value = value;                
		    return;
		}	    
	    }
	    collisions_[offset] = new Binding(h, key, value); 
	}
    
    } // End HTClass2Runabout_Code

} // end of Runabout
