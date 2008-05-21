// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 

package polyglot.ext.x10;

/**
 * Version information for x10 extension
 */
public class Version extends polyglot.main.Version {
    java.util.Date date;
    public Version() {
    	Class c = Version.class;
//    	java.net.URL r = c.getClassLoader().getResource("META-INF/MANIFEST.MF");
    	java.net.URL r = c.getClassLoader().getResource(c.getName().replace('.','/')+".class");
    	long lastModified = 0;
    	try {
			lastModified = r.openConnection().getLastModified();
		} catch (java.io.IOException e) { /* ignore */ }
    	this.date = lastModified == 0 ? null : new java.util.Date(lastModified);
    }
    public String name() { return "x10"; }

    public int major() { return 1; }
    public int minor() { return 0; }
    public int patch_level() { return 0; }
    public String toString() {
        String version = super.toString();
        if (date != null)
        	version += " (" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + ")";
		return version;
    }
}
