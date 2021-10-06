package x10.optimizations.atOptimzer;

public class PlaceNode 
{
	public String toName;
	public PlaceNode child;
	public PlaceNode sibling;

	public PlaceNode(String placeName) {
		this.toName = placeName;
		this.child = null;
		this.sibling = null;
	}

	public PlaceNode() {
		this.toName = null;
		this.child = null;
		this.sibling = null;
	}

	public void addChild(PlaceNode plNode) {
		PlaceNode temp = this;
		
		if(temp.child == null) {
			temp.child = plNode;
		}
		else {
			PlaceNode temp1 = temp.child;

			while(temp1.sibling != null) {
				temp1 = temp1.sibling;
			}
			temp1.sibling = plNode;
			
		}
	}

	public void addSibling() {
	}
}
