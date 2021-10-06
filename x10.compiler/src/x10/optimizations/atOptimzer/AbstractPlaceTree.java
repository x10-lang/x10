package x10.optimizations.atOptimzer;

import x10.optimizations.atOptimzer.PlaceNode;

public class AbstractPlaceTree
{
	public PlaceNode absPlTree;

	public AbstractPlaceTree(PlaceNode plNode) {
		this.absPlTree = plNode;
	}

	public AbstractPlaceTree() {
		this.absPlTree = null;
	}

	public void addPlaceNode(PlaceNode plNode) {
		this.absPlTree = plNode;
	}
}
