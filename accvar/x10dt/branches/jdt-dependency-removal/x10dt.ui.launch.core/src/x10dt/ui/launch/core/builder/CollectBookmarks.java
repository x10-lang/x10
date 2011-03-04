package x10dt.ui.launch.core.builder;

import java.util.ArrayList;
import java.util.List;

import lpg.runtime.IToken;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

import polyglot.frontend.Job;
import x10dt.ui.launch.core.utils.CoreResourceUtils;

public class CollectBookmarks {
	private final static String[] sTaskPrefixes= new String[] { "// TODO ", "// BUG ", "// FIXME "  };

	private Job fJob;
	private AbstractX10Builder fBuilder;
	
	public CollectBookmarks(Job job, AbstractX10Builder builder){
		fJob = job;
		fBuilder = builder;
	}
	
	public void perform(){
	     String path= fJob.source().path();
	     if(path.contains("*.zip")||path.contains("*.jar")) {
	     	return;
	     }
	     
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
	     CoreResourceUtils.deleteTasks(file, IResource.DEPTH_ONE);

	     for(IToken adjunct: adjuncts) {
	         String adjunctStr= adjunct.toString();
	         for(int i=0; i < sTaskPrefixes.length; i++) {
	             if (adjunctStr.startsWith(sTaskPrefixes[i])) {
	                 String msg= adjunctStr.substring(3);
	                 int lineNum= adjunct.getLine();
	                 int startOffset= adjunct.getStartOffset();
	                 int endOffset= adjunct.getEndOffset();
	                 CoreResourceUtils.addTask(file, msg, IMarker.SEVERITY_INFO, file.getLocation().toString(), 
	                                           IMarker.PRIORITY_NORMAL, lineNum, startOffset, endOffset);
	             }
	         }
	     }
	}
}
