/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.visit;

import java.io.IOException;
import java.util.*;

import polyglot.ast.*;
import polyglot.main.Reporter;
import polyglot.main.Version;
import polyglot.types.*;
import polyglot.util.*;
import x10.ast.X10ClassDecl;
import x10.types.X10ClassDef;
import x10.types.X10ParsedClassType_c;
import x10.types.constants.ConstantValue;

/**
 * Visitor which serializes class objects and adds a field to the class
 * containing the serialization.
 */
public class ClassSerializer extends NodeVisitor
{
    /**
     * The maximum number of characters that will be assigned to an encoded type string field.
     * More characters than this will be broken up over several fields.
     */
    private static final int MAX_ENCODED_TYPE_INFO_STRING_LENGTH = 8192;
    
    protected TypeEncoder te;
    protected ErrorQueue eq;
    protected Date date;
    protected TypeSystem ts;
    protected Reporter reporter;
    protected NodeFactory nf;
    protected Version ver;

    public ClassSerializer(TypeSystem ts, NodeFactory nf, Date date, ErrorQueue eq, Version ver) {
        this.ts = ts;
        this.reporter = ts.extensionInfo().getOptions().reporter;
        this.nf = nf;
        this.te = new TypeEncoder(ts);
        this.eq = eq;
        this.date = date;
        this.ver = ver;
    }
    
    @Override
    public Node override(Node n) {
        // Stop at class members.  We only want to encode top-level classes.
	if (n instanceof ClassMember && ! (n instanceof ClassDecl)) {
	    return n;
	}

	return null;
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
	if (! (n instanceof ClassDecl)) {
	    return n;
	}

        X10ClassDecl cd = (X10ClassDecl) n;
        ClassBody body = cd.body();

        List<ClassMember> l = createSerializationMembers(cd);

        for (ClassMember m : l) {
            body = body.addMember(m);
        }

        return cd.body(body);
    }

    public List<ClassMember> createSerializationMembers(X10ClassDecl cd) {
        return createSerializationMembers(cd.classDef());
    }
    
    public List<ClassMember> createSerializationMembers(X10ClassDef cd) {
	try {
	    byte[] b;
            List<ClassMember> newMembers = new ArrayList<ClassMember>(3);
            
            ClassType ct = cd.asType();

            // HACK: force class members to get created from lazy class
            // initializer.
            ct.memberClasses();
            ct.constructors();
            ct.methods();
            ct.fields();
            ct.interfaces();
            ct.superClass();

            // Only serialize top-level and member classes.
            if (! ct.isTopLevel() && ! ct.isMember()) {
                return Collections.<ClassMember>emptyList();
            }

            /* Add the compiler version number. */
            String suffix = ver.name();

	    // Check if we've already serialized.
	    if (ct.fieldNamed(Name.make("jlc$CompilerVersion$" + suffix)) != null ||
		ct.fieldNamed(Name.make("jlc$SourceLastModified$" + suffix)) != null ||
		ct.fieldNamed(Name.make("jlc$ClassType$" + suffix)) != null) {

		eq.enqueue(ErrorInfo.SEMANTIC_ERROR,
			   "Cannot serialize class information " +
			   "more than once.");

		return Collections.<ClassMember>emptyList();
	    }

	    Flags flags = Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL);

	    FieldDecl f;
            FieldDef fi;
            InitializerDef ii;

	    /* Add the compiler version number. */
	    String version = ver.major() + "." +
			     ver.minor() + "." +
			     ver.patch_level();

            Position pos = Position.COMPILER_GENERATED;

	    fi = ts.fieldDef(pos, Types.ref(new X10ParsedClassType_c(cd)),
                                  flags, Types.ref(ts.String()),
                                  Name.make("jlc$CompilerVersion$" + suffix));
            fi.setConstantValue(ConstantValue.makeString(version));
            ii = ts.initializerDef(pos, Types.ref(new X10ParsedClassType_c(cd)), Flags.STATIC);
	    f = nf.FieldDecl(fi.position(), nf.FlagsNode(fi.position(), fi.flags()),
		             nf.CanonicalTypeNode(fi.position(), fi.type()),
                             nf.Id(fi.position(), fi.name()),
			     nf.StringLit(pos, version).type(ts.String()));

	    f = f.fieldDef(fi);
            f = f.initializerDef(ii);
            newMembers.add(f);

	    /* Add the date of the last source file modification. */
	    long time = date.getTime();

	    fi = ts.fieldDef(pos, Types.ref(new X10ParsedClassType_c(cd)),
                                  flags, Types.ref(ts.Long()),
                                  Name.make("jlc$SourceLastModified$" + suffix));
            fi.setConstantValue(ConstantValue.makeLong(time));
            ii = ts.initializerDef(pos, Types.ref(new X10ParsedClassType_c(cd)), Flags.STATIC);
	    f = nf.FieldDecl(fi.position(), nf.FlagsNode(fi.position(), fi.flags()),
		             nf.CanonicalTypeNode(fi.position(), fi.type()),
                             nf.Id(fi.position(), fi.name()),
			     nf.IntLit(pos, IntLit.LONG, time).type(ts.Long()));

	    f = f.fieldDef(fi);
            f = f.initializerDef(ii);
            newMembers.add(f);

            // output the encoded type info, over several fields if needed.
            String encodedTypeInfo = te.encode(ct);
            int etiStart = 0;
            int etiEnd = 0;
            int numberETIFields = 0;
            do {
                etiEnd = encodedTypeInfo.length();
                if (etiEnd - etiStart > MAX_ENCODED_TYPE_INFO_STRING_LENGTH) {
                    etiEnd = etiStart + MAX_ENCODED_TYPE_INFO_STRING_LENGTH;
                }
                // add an additional suffix to distinguish fields.
                String additionalFieldSuffix = numberETIFields==0?"":("$" + numberETIFields);
                String encoded = encodedTypeInfo.substring(etiStart, etiEnd);
                fi = ts.fieldDef(pos, Types.ref(new X10ParsedClassType_c(cd)),
                                      flags, Types.ref(ts.String()),
                                      Name.make("jlc$ClassType$" + suffix + additionalFieldSuffix));
                fi.setConstantValue(ConstantValue.makeString(encoded));
                ii = ts.initializerDef(pos, Types.ref(new X10ParsedClassType_c(cd)), Flags.STATIC);

                f = nf.FieldDecl(fi.position(), nf.FlagsNode(fi.position(), fi.flags()),
                                 nf.CanonicalTypeNode(fi.position(), fi.type()),
                                 nf.Id(fi.position(), fi.name()),
                                 nf.StringLit(pos, encoded).type(ts.String()));

                f = f.fieldDef(fi);
                f = f.initializerDef(ii);
                newMembers.add(f);
                
                numberETIFields++;
                etiStart = etiEnd;
            }
            while (etiEnd != encodedTypeInfo.length());
            
            return newMembers;
	}
	catch (IOException e) {
            if (reporter.should_report(Reporter.serialize, 1))
                e.printStackTrace();
	    eq.enqueue(ErrorInfo.IO_ERROR,
		       "Unable to serialize class information.");
            return Collections.<ClassMember>emptyList();
	}
    }
}
