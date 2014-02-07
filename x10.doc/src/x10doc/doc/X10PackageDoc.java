/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10doc.doc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

public class X10PackageDoc extends X10Doc implements PackageDoc {
    String name;
    String path;

    ArrayList<X10ClassDoc> classes;
    X10RootDoc rootDoc;
    boolean included;

    X10ClassDoc[] includedClasses;

    public X10PackageDoc(String name, String path) {
        // super("");

        this.name = name;
        this.path = path;
        this.rootDoc = X10RootDoc.getRootDoc();
        this.classes = new ArrayList<X10ClassDoc>();
        this.included = false; // defn of included: set of entities
                               // (classes/packages) that are specified on the
                               // command-line and that pass through the access
                               // modifier filter; at present, x10doc
                               // does not handle command-line specified
                               // packages, so X10PackageDoc.included is
                               // false for all packages

        super.processComment(getComment());
    }

    public void addClass(X10ClassDoc cd) {
        classes.add(cd);

        /*
         * if (!included && cd.isIncluded()) { included = true; //
         * rootDoc.makePackageIncluded(name); }
         */
    }

    @Override
    public String name() {
        return name;
    }

    public boolean isIncluded() {
        return included;
    }

    // returns all *included* classes and interfaces, as per definition
    public ClassDoc[] allClasses() {
        // System.out.println("PackageDoc.allClasses() called.");
        // return classes.toArray(new X10ClassDoc[0]);
        if (includedClasses != null) {
            return includedClasses;
        }
        int size = 0;
        for (X10ClassDoc cd : classes) {
            if (cd.isIncluded()) {
                size++;
            }
        }
        includedClasses = new X10ClassDoc[size];
        int i = 0;
        for (X10ClassDoc cd : classes) {
            if (cd.isIncluded()) {
                includedClasses[i++] = cd;
            }
        }
        Comparator<X10ClassDoc> cmp = new Comparator<X10ClassDoc>() {
            public int compare(X10ClassDoc first, X10ClassDoc second) {
                return first.name().compareTo(second.name());
            }

            public boolean equals(Object other) {
                return false;
            }
        };
        Arrays.sort(includedClasses, cmp);
        return includedClasses;
    }

    public ClassDoc[] allClasses(boolean arg0) {
        System.out.println("PackageDoc.allClasses(boolean) called.");
        return allClasses();
        /*
         * if (arg0) { return allClasses(); } return classes.toArray(new
         * X10ClassDoc[0]);
         */
    }

    public AnnotationTypeDoc[] annotationTypes() {
        // TODO Auto-generated method stub
        System.out.println("PackageDoc.annotationTypes() called.");
        return new AnnotationTypeDoc[0];
    }

    public AnnotationDesc[] annotations() {
        // TODO Auto-generated method stub
        // System.out.println("PackageDoc.annotations() called.");
        return new AnnotationDesc[0];
    }

    public ClassDoc[] enums() {
        // TODO Auto-generated method stub
        System.out.println("PackageDoc.enums() called.");
        return new ClassDoc[0];
    }

    public ClassDoc[] errors() {
        // TODO Auto-generated method stub
        System.out.println("PackageDoc.errors() called.");
        return new ClassDoc[0];
    }

    public ClassDoc[] exceptions() {
        // TODO Auto-generated method stub
        System.out.println("PackageDoc.exceptions() called.");
        return new ClassDoc[0];
    }

    public ClassDoc findClass(String arg0) {
        // TODO Auto-generated method stub
        System.out.println("PackageDoc.findClass() called.");
        return null;
    }

    public ClassDoc[] interfaces() {
        // TODO Auto-generated method stub
        System.out.println("PackageDoc.interfaces() called.");
        return new ClassDoc[0];
    }

    public ClassDoc[] ordinaryClasses() {
        // TODO Auto-generated method stub
        System.out.println("PackageDoc.ordinaryClasses() called.");
        return allClasses();
    }

    private String getComment() {
        FileChannel fc;
        BufferedReader br;
        try {
            File file = new File(path + "package.html");
            if (file.exists()) {
                StringBuilder builder = new StringBuilder();
                fc = new FileInputStream(file).getChannel();
                br = new BufferedReader(Channels.newReader(fc, "UTF-8"));

                String delim = System.getProperty("line.separator");
                String line = "";
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                    builder.append(delim);
                }

                String html = builder.toString();
                int start = html.toLowerCase().indexOf("<body");
                start = html.indexOf(">", start) + 1;
                int end = html.toLowerCase().indexOf("</body>", start);
                String body = html.substring(start, end);

                br.close();
                fc.close();

                return "/**" + body + "*/";
            }
        } catch (Exception e) {
            // fall through
        }
        return "";
    }

    public X10ClassDoc classDocForName(String name) {
        for (X10ClassDoc cd : classes) {
            if (cd.simpleTypeName().equals(name)) {
                return cd;
            }
        }
        return null;
    }
}
