/**
 * 
 */
package org.eclipse.imp.x10dt.core.builder;

import java.io.File;

import org.eclipse.core.resources.IProject;

import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class CheckPackageDeclVisitor extends NodeVisitor {
    private final Job fJob;
    private final IProject fProject;
    private boolean fSeenPkg= false;

    public CheckPackageDeclVisitor(Job job, IProject project) {
        fJob= job;
        fProject= project;
    }
    @Override
    public NodeVisitor begin() {
        String path= fJob.source().path();
        // BRT don't bother looking for dependencies if we're in jar/zip
        //PORT1.7
        if(path.endsWith(".jar")|| path.endsWith(".zip")) {
        	System.out.println("looking for resource in zip/jar???");
        	return null;
        }
        return super.begin();
    }
    private void checkPackage(String declaredPkg, String actualPkg, Position pos) {
        if (!actualPkg.equals(declaredPkg)) {
            fJob.extensionInfo().compiler().errorQueue().enqueue(new ErrorInfo(ErrorInfo.SEMANTIC_ERROR, "Declared package doesn't match source file location.", pos));
        }
    }

    @Override
    public NodeVisitor enter(Node n) {
        if (n instanceof PackageNode) {
            PackageNode pkg= (PackageNode) n;
            Source src= fJob.source();
            String declaredPkg= pkg.package_().get().fullName().name().toString();
            String actualPkg= determineActualPackage(src);

            checkPackage(declaredPkg, actualPkg, pkg.position());
            fSeenPkg= true;
        }
        return super.enter(n);
    }

    private String determineActualPackage(Source src) {
        String srcPath= src.path();
        String projPath= fProject.getLocation().toOSString();
        String pkgPath;

        if (srcPath.startsWith(projPath)) {
            pkgPath= srcPath.substring(projPath.length()+1);
        } else {
            pkgPath= srcPath;
        }
        if (pkgPath.startsWith("src" + File.separator)) {
            pkgPath= pkgPath.substring(4);
        }
        if (pkgPath.length() == src.name().length()) {
            return ""; // It's in the default pkg
        } else {
            return pkgPath.substring(0, pkgPath.length() - src.name().length() - 1).replace(File.separatorChar, '.');
        }
    }

    @Override
    public Node override(Node n) {
        if (fSeenPkg) {
            return n;
        }
        return null;
    }
    @Override
    public void finish() {
        if (!fSeenPkg) { // No package decl -> implicitly in the default package
            Source src= fJob.source();
            checkPackage("", determineActualPackage(src), new Position(src.path(), src.name(), 1, 1));
        }
    }
}