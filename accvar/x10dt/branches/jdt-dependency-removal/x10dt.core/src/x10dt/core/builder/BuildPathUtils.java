package x10dt.core.builder;

import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.IPathEntry.PathEntryType;

public class BuildPathUtils {
	
	public static boolean isExcluded(IPath filePath, ISourceProject project, Language language){
		  // --- Determine the classpath source entry that corresponds to file. 
		  IPathEntry srcEntry = null;
		  if (project == null){
			  return true; // --- If there is no java project associated with this file, then the file must be excluded.
		  }
		  // --- If filePath is not relative to the workspace, make it so.
		  IPath workspace = project.getRawProject().getLocation().removeLastSegments(1);
		  if (workspace.isPrefixOf(filePath)){
			  filePath = filePath.makeRelativeTo(workspace);
		  }
		  for (final IPathEntry cpEntry : project.getBuildPath(language)) {
			  if (cpEntry.getEntryType() == PathEntryType.SOURCE_FOLDER && cpEntry.getRawPath().isPrefixOf(filePath)){
				  srcEntry = cpEntry;
				  break;
			  }
		  } 
		  if (srcEntry == null){ // --- If there is no source entry on the build path corresponding to filePath, then the file must be excluded.
			  return true;
		  }
		  
		  return false;
	}
	
	  
	  /**
	   * This method takes a file and a pattern and determines if there is a match.
	   * The patterns have the same format as ANT patterns:
	   * - '*' matches zero or more characters.
	   * - '?' matches one character.
	   * - '/' is used to separate folders: This means the first segment in the pattern is matched against the most outer folder name in the path to match, the second segment with the second, and so on.
	   * - '**' matches any number of folders.
	   * 
	   * @param file The file we are matching.
	   * @param pattern The pattern to match.
	   * @return true if there is a match.
	   */
	  private static boolean matches(IPath path, IPath pattern){
		if (path.equals(pattern)){
			return true;
		}
		if (pattern.segmentCount() == 1 && pattern.segment(0).equals("**")){
			return true;
		}
		if (path.isEmpty() && !pattern.isEmpty()){ // --- In this case, there is a match only if pattern is of the form **/**/**...
			if (!pattern.segment(0).equals("**"))
				return false;
			return matches(path, pattern.removeFirstSegments(1));
		}
		if (!path.isEmpty() && pattern.isEmpty()){ 
			return false;
		}
		String pathFirst = path.segment(0);
		String patternFirst = pattern.segment(0);
		if (patternFirst.equals("**")){
			IPath patternNext = pattern.removeFirstSegments(1);
			for (int i = 0; i < path.segmentCount(); i++){
				if (matches(path.removeFirstSegments(i), patternNext)){
					return true;
				}
			}
			return false;
		} else {
			if (!segMatches(pathFirst, patternFirst)){
				return false;
			}
			return matches(path.removeFirstSegments(1), pattern.removeFirstSegments(1));
		}	
	  }
	  
	  /**
	   * This method determines if 2 string segments match. We assume that the pattern does not contain '**'.
	   * 
	   * @param seg The segment to match
	   * @param pattern The pattern to match
	   * @return true if the segment and pattern match
	   */
	  private static boolean segMatches(String seg, String pattern){
		  if (seg.equals(pattern) || pattern.equals("*")){
			  return true;
		  }
		  if (seg.equals("") && !pattern.equals("")){ // --- In this case, there is a match only if pattern is of the form ***...
			  if (pattern.charAt(0) != '*'){
				  return false;
			  }
			  return segMatches(seg, pattern.substring(1));
		  }
		  if (!seg.equals("") && pattern.equals("")){ 
			  return false;
		  }
		  char patternFirst = pattern.charAt(0);
		  char segFirst = seg.charAt(0);
		  if (patternFirst == '*'){
			  String patternNext = pattern.substring(1);
			  for(int i = 0; i < seg.length(); i++){
				  if (segMatches(seg.substring(i), patternNext)){
					  return true;
				  }
			  }
			  return false;
		  }
		  if (patternFirst == '?'){
			  return segMatches(seg.substring(1), pattern.substring(1));
		  } else {
			  if (patternFirst != segFirst){
				  return false;
			  }
			  return segMatches(seg.substring(1), pattern.substring(1));
		  }
		  
	  }
}
