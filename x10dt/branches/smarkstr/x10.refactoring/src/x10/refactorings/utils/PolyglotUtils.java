package x10.refactorings.utils;

import java.util.List;

import polyglot.ast.Node;

public abstract class PolyglotUtils {

    public static Node findNodeOfType(List<Node> path, Class clazz) {
        for(Node node : path) {
    	if (clazz.isInstance(node))
    	    return node;
        }
        return null;
    }

    public static Node findInnermostNodeOfType(List<Node> path, Class clazz) {
        Node result= null;
        for(Node node : path) {
    	if (clazz.isInstance(node))
    	    result= node;
        }
        return result;
    }

    public static Node findInnermostNodeOfTypes(List<Node> path, Class[] classes) {
        Node result= null;
        for(Node node : path) {
    	for(Class clazz: classes) {
    	    if (clazz.isInstance(node))
    		result= node;
    	}
        }
        return result;
    }

}
