/**
 * 
 */
package org.eclipse.imp.x10dt.core.builder;

import java.util.ArrayList;
import java.util.List;

import lpg.runtime.IToken;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;

import polyglot.ast.Node;
import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;
import x10.parser.X10Parser;

public class CollectBookmarksGoal extends SourceGoal_c {
    private final static String[] sTaskPrefixes= new String[] { "// TODO ", "// BUG ", "// FIXME "  };

    private X10Builder fBuilder;

    public CollectBookmarksGoal(Job job, X10Builder builder) {
        super(job);
        fBuilder= builder;
        addPrereq(job.extensionInfo().scheduler().intern(new CheckPackageDeclGoal(job, fBuilder)));
    }

    @Override
	public boolean runTask() {
        Job job= this.job();
        Node ast= job.ast();
        String path= job.source().path();
        // PORT1.7 if in a zip or jar then we don't need to...
        if(path.contains("*.zip")||path.contains("*.jar")) {
        	return true;
        }
        X10Parser.JPGPosition pos= (X10Parser.JPGPosition) ast.position();
        
        List<IToken> adjuncts=null;
        try {
        	//PORT1.7 -- uses getLeftToken()... can we get to parse stream in another way??
        	//adjuncts= pos.getLeftIToken().getPrsStream().getAdjuncts();
        	throw new UnsupportedOperationException("X10Builder collecting bookmarks, need adjuncts");
        }
        catch(Exception e) {
        	//PORT1.7 -- Hack to postpone JPGPosition.getLeftIToken() problem (always null now, need general method to recompute)
        	//X10DTCorePlugin.getInstance().log("Error while collecting bookmarks during build: JPGPosition / adjuncts", e);
        	// filed Jira bug to note this. http://jira.codehaus.org/browse/XTENLANG-417
        	adjuncts=new ArrayList<IToken>();
        }
        IFile file= fBuilder.getProject().getFile(path.substring(fBuilder.getProject().getLocation().toOSString().length()));

        try {
            file.deleteMarkers(IMarker.TASK, true, 1);
        } catch (CoreException e) {
            X10DTCorePlugin.getInstance().logException("Error while creating task", e);
        }

        for(IToken adjunct: adjuncts) {
            String adjunctStr= adjunct.toString();
            for(int i=0; i < sTaskPrefixes.length; i++) {
                if (adjunctStr.startsWith(sTaskPrefixes[i])) {
                    String msg= adjunctStr.substring(3);
                    int lineNum= adjunct.getLine();
                    int startOffset= adjunct.getStartOffset();
                    int endOffset= adjunct.getEndOffset();

                    fBuilder.addTaskTo(file, msg, IMarker.SEVERITY_INFO, IMarker.PRIORITY_NORMAL, lineNum, startOffset, endOffset);
                }
            }
        }
        return true;
	}
}