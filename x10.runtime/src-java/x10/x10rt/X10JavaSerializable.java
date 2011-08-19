/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.x10rt;


import x10.runtime.impl.java.Runtime;

import java.io.IOException;

public interface X10JavaSerializable {

    /**
     * Force use of custom java serialization. Default is to use default java serialization
    */
    public static final boolean CUSTOM_JAVA_SERIALIZATION = Runtime.isCustomSerialization();

    public static final boolean CUSTOM_JAVA_SERIALIZATION_USING_REFLECTION = Runtime.isCustomSerializationUsingReflection();

	public void $_serialize(X10JavaSerializer serializer) throws IOException;
	public short $_get_serialization_id();
}
