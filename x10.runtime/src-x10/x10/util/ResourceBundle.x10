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

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.io.EOFException;
import x10.io.File;
import x10.io.IOException;
import x10.io.Reader;


/**
 * Resource bundles contain locale-specific objects.  When your program needs a
 * locale-specific resource, your program can load it from the resource bundle
 * that is appropriate for the current user's locale.
 * @see MissingResourceException
 */
@NativeRep("java", "x10.core.util.ResourceBundle", null, "x10.core.util.ResourceBundle.$RTT")
public final class ResourceBundle {

    private static val cache = new HashMap[String,ResourceBundle]();
    private val props = new HashMap[String,String]();
    private val baseName:String;

    private def this(baseName:String) {
        this.baseName = baseName;
        var reader:Reader = null;
        try {
            reader = new File(baseName + ".properties").openRead();
        } catch (e:IOException) {
            throw new MissingResourceException("Can't find bundle for base name " + baseName, baseName, null);
        }
        try {
            while (true) {
                val s = reader.readLine();
                val delim = s.indexOf('=');
                val key = s.substring(0n,delim);
                val value = s.substring(delim+1n);
                props.put(key,value);
            }
        } catch (e:EOFException) {
        } catch (e:CheckedThrowable) {
            throw new MissingResourceException("Error in loading bundle " + baseName, baseName, null, e);
        } finally {
            reader.close();
        }
    }

    /**
     * Gets a resource bundle using the specified base name and the default locale.
     * @param baseName the base name of the resource bundle, a fully qualified class name
     * @return a resource bundle for the given base name and the default locale
     */
    @Native("java", "x10.core.util.ResourceBundle.getBundle(#baseName)")
    public static def getBundle(baseName:String):ResourceBundle {
        var bundle:ResourceBundle = null;
        atomic {
            bundle = cache.getOrElse(baseName, null);
            if (bundle == null) {
                bundle = new ResourceBundle(baseName);
                cache.put(baseName,bundle);
            }
        }
        return bundle;
    }

    /**
     * Gets a resource bundle using the specified base name, the default locale,
     * and class loader. This method has a meaning only for managed X10. For other backends,
     * this is equivalent to {@link #getBundle(String) getBundle}.
     * @param baseName the base name of the resource bundle, a fully qualified class name
     * @param loaderOrLoaded the class loader from which to load the resource bundle.
     *        It is either the class loader, a class loaded by the class loader,
     *        or an instance of the loaded class.
     * @return a resource bundle for the given base name and the default locale
     */
    @Native("java", "x10.core.util.ResourceBundle.getBundle(#baseName, #loaderOrLoaded)")
    public static def getBundle(baseName:String, loaderOrLoaded:Any):ResourceBundle = getBundle(baseName);

    /**
     * Determines whether the given key is contained in this resource bundle.
     * @param key the resource key
     * @return true if the given key is contained in this resource bundle; false otherwise.
     */
    @Native("java", "#this.containsKey$O(#key)")
    public final def containsKey(key:String):Boolean = props.containsKey(key);

    /**
     * Gets a string for the given key from this resource bundle.
     * @param key the key for the desired string
     * @return the string for the given key
     */
    @Native("java", "#this.getString(#key)")
    public final def getString(key:String):String {
        val value = props.getOrElse(key, null);
        if (value != null) return value;
        throw new MissingResourceException("Can't find resource for bundle " + baseName + ", key " + key, baseName, key); 
    }

    /**
     * Returns a set of all keys contained in this resource bundle.
     * @return a set of all keys contained in this resource bundle.
     */
    @Native("java", "#this.keySet()")
    public final def keySet():Set[String] = props.keySet();

}
