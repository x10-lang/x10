public class TestMethodCall {
}

class Callee {
	
	//the testing driver
	public def testDriver() {
		var c1:Callee = new Callee();
		var c2:linked Callee = new linked Callee();
		
		c2 = this.returnLinked(); //ok
		c1 = this.returnLinked(); //does not type check, need to cast off linked
		
		c1 = this.returnNoLinked(); //ok
		c2 = this.returnNoLinked(); //does not type check
		
		this.needNoLinkedArg(c1);  //ok
		this.needNoLinkedArg(c2);  //does not type check
		
		this.needLinkedArg(c2);    //ok
		this.needLinkedArg(c1);    //does not type check
		
		c2 = this.needNoLinkButReturnLink(c1); //ok
		c1 = this.needNoLinkButReturnNoLink(c1); //ok
		
		c2 = this.needLinkedButReturnLinked(c2); //ok
		c1 = this.needLinkedButReturnNoLink(c2); //ok
		
		//now use this as the parameter
		c2 = this.needNoLinkButReturnLink(this); //ok
		c1 = this.needNoLinkButReturnNoLink(this); //ok
		
		c2 = this.needLinkedButReturnLinked(this); //ok
		c1 = this.needLinkedButReturnNoLink(this); //ok
		
		this.needNoLinkedArg(this);  //ok
		this.needNoLinkedArg(this);  //ok
		
		this.needLinkedArg(this);    //ok
		this.needLinkedArg(this);    //ok
		
		//test use c1 as caller
		c1.needNoLinkedArg(this);  //ok
		c2.needNoLinkedArg(this);  //ok
		
		c1.needLinkedArg(this);    //error, c1 should be linked
		c2.needLinkedArg(this);    //ok
		
		//use c2 as caller
		c2 = c2.needNoLinkButReturnLink(this); //ok
		c2 = c1.needNoLinkButReturnLink(this); //does not type check
		
		c1 = c2.needNoLinkButReturnNoLink(this); //ok
		c1 = c1.needNoLinkButReturnNoLink(this); //ok
		
		c2 = c2.needLinkedButReturnLinked(this); //ok
		c1 = c2.needLinkedButReturnNoLink(this); //ok
	}
	
	/** all the method definition for test */
	public def returnLinked() : linked Callee {
		return new linked Callee();
	}
	
	public def returnNoLinked() : Callee {
		return new Callee();
	}
	
	public def needNoLinkedArg(c:Callee) {
		//do nothing
	}
	
	public def needLinkedArg(c:linked Callee) {
		//do nothing
	}
	
	public def needNoLinkButReturnLink(c:Callee) : linked Callee {
		return new linked Callee();
	}
	
	public def needNoLinkButReturnNoLink(c:Callee) : Callee {
		return new Callee();
	}
	
	public def needLinkedButReturnLinked(c:linked Callee) : linked Callee {
		return new linked Callee();
	}
	
	public def needLinkedButReturnNoLink(c:linked Callee) : Callee {
		return new Callee();
	}
}