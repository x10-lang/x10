package polyglot.ext.x10.visit;

import org.w3c.dom.Element;

import polyglot.ast.AbstractBlock_c;
import polyglot.ast.AmbAssign_c;
import polyglot.ast.AmbExpr_c;
import polyglot.ast.AmbPrefix_c;
import polyglot.ast.AmbQualifierNode_c;
import polyglot.ast.AmbReceiver_c;
import polyglot.ast.AmbTypeNode_c;
import polyglot.ast.ArrayAccessAssign_c;
import polyglot.ast.ArrayAccess_c;
import polyglot.ast.ArrayInit_c;
import polyglot.ast.ArrayTypeNode_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.Block_c;
import polyglot.ast.BooleanLit_c;
import polyglot.ast.Branch_c;
import polyglot.ast.Call_c;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Case_c;
import polyglot.ast.Cast_c;
import polyglot.ast.Catch_c;
import polyglot.ast.CharLit_c;
import polyglot.ast.ClassBody_c;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassLit_c;
import polyglot.ast.Conditional_c;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Do_c;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr_c;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.FloatLit_c;
import polyglot.ast.For_c;
import polyglot.ast.Formal_c;
import polyglot.ast.If_c;
import polyglot.ast.Import;
import polyglot.ast.Import_c;
import polyglot.ast.Initializer_c;
import polyglot.ast.Instanceof_c;
import polyglot.ast.IntLit_c;
import polyglot.ast.JL;
import polyglot.ast.Labeled_c;
import polyglot.ast.Lit_c;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.LocalClassDecl_c;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Local_c;
import polyglot.ast.Loop_c;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.NewArray_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.NullLit_c;
import polyglot.ast.NumLit_c;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Return_c;
import polyglot.ast.SourceCollection_c;
import polyglot.ast.SourceFile_c;
import polyglot.ast.Special_c;
import polyglot.ast.Stmt_c;
import polyglot.ast.StringLit_c;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Synchronized_c;
import polyglot.ast.Term_c;
import polyglot.ast.Throw_c;
import polyglot.ast.Try_c;
import polyglot.ast.TypeNode_c;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ast.While_c;
import polyglot.ext.x10.ast.ArrayConstructor_c;
import polyglot.ext.x10.ast.AssignPropertyCall_c;
import polyglot.ext.x10.ast.Async_c;
import polyglot.ext.x10.ast.AtEach_c;
import polyglot.ext.x10.ast.AtomicMethodDecl_c;
import polyglot.ext.x10.ast.Atomic_c;
import polyglot.ext.x10.ast.Await_c;
import polyglot.ext.x10.ast.ConstantDistMaker_c;
import polyglot.ext.x10.ast.DepParameterExpr_c;
import polyglot.ext.x10.ast.Finish_c;
import polyglot.ext.x10.ast.ForEach_c;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.FutureNode_c;
import polyglot.ext.x10.ast.Future_c;
import polyglot.ext.x10.ast.GenParameterExpr_c;
import polyglot.ext.x10.ast.Here_c;
import polyglot.ext.x10.ast.Next_c;
import polyglot.ext.x10.ast.Now_c;
import polyglot.ext.x10.ast.NullableNode_c;
import polyglot.ext.x10.ast.ParExpr_c;
import polyglot.ext.x10.ast.PlaceCast_c;
import polyglot.ext.x10.ast.Point_c;
import polyglot.ext.x10.ast.PropertyDecl_c;
import polyglot.ext.x10.ast.Range_c;
import polyglot.ext.x10.ast.RectRegionMaker_c;
import polyglot.ext.x10.ast.RegionMaker_c;
import polyglot.ext.x10.ast.Region_c;
import polyglot.ext.x10.ast.RemoteCall_c;
import polyglot.ext.x10.ast.StmtSeq_c;
import polyglot.ext.x10.ast.Tuple_c;
import polyglot.ext.x10.ast.ValueClassDecl_c;
import polyglot.ext.x10.ast.When_c;
import polyglot.ext.x10.ast.X10AmbTypeNode_c;
import polyglot.ext.x10.ast.X10ArrayAccess1Assign_c;
import polyglot.ext.x10.ast.X10ArrayAccess1Unary_c;
import polyglot.ext.x10.ast.X10ArrayAccess1_c;
import polyglot.ext.x10.ast.X10ArrayAccessAssign_c;
import polyglot.ext.x10.ast.X10ArrayAccessUnary_c;
import polyglot.ext.x10.ast.X10ArrayAccess_c;
import polyglot.ext.x10.ast.X10ArrayTypeNode_c;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10BooleanLit_c;
import polyglot.ext.x10.ast.X10Call_c;
import polyglot.ext.x10.ast.X10CanonicalTypeNode_c;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10CharLit_c;
import polyglot.ext.x10.ast.X10ClassBody_c;
import polyglot.ext.x10.ast.X10ClassDecl_c;
import polyglot.ext.x10.ast.X10ClockedLoop_c;
import polyglot.ext.x10.ast.X10Conditional_c;
import polyglot.ext.x10.ast.X10ConstructorDecl_c;
import polyglot.ext.x10.ast.X10FieldDecl_c;
import polyglot.ext.x10.ast.X10Field_c;
import polyglot.ext.x10.ast.X10FloatLit_c;
import polyglot.ext.x10.ast.X10Formal_c;
import polyglot.ext.x10.ast.X10If_c;
import polyglot.ext.x10.ast.X10Instanceof_c;
import polyglot.ext.x10.ast.X10IntLit_c;
import polyglot.ext.x10.ast.X10LocalDecl_c;
import polyglot.ext.x10.ast.X10Local_c;
import polyglot.ext.x10.ast.X10Loop_c;
import polyglot.ext.x10.ast.X10MethodDecl_c;
import polyglot.ext.x10.ast.X10New_c;
import polyglot.ext.x10.ast.X10Special_c;
import polyglot.ext.x10.ast.X10StringLit_c;
import polyglot.ext.x10.ast.X10TypeNode_c;
import polyglot.ext.x10.ast.X10Unary_c;
import polyglot.ext.x10.ast.X10While_c;
import polyglot.util.InternalCompilerError;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import polyglot.ast.Node;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Package;
import polyglot.types.PrimitiveType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.util.Copy;
import polyglot.util.Enum;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import polyglot.visit.NodeVisitor;

public class DomGenerator implements Copy {
	public DomGenerator() {
		super();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation domImpl = builder.getDOMImplementation();
			
			TransformerFactory transFactory = TransformerFactory.newInstance();
			format = transFactory.newTransformer();
			scratchPad = domImpl.createDocument(null, "ast", null);
		}
		catch (FactoryConfigurationError e) {
			throw new InternalCompilerError("Could not configure factory.", e);
		}
		catch (ParserConfigurationException e) {
			throw new InternalCompilerError("Could not configure parser.", e);
		}
		catch (Exception e) {
			throw new InternalCompilerError(e);
		}
		
		typeMap = new HashMap<TypeObject,Element>();
		parent = createElement(null, "AST");
	}
	
	Element parent;
	Map<TypeObject,Element> typeMap;
	Document scratchPad;
	Transformer format;
	
	public Element get() {
		return parent;
	}
	
	public void serialize() {
		try {
			if (parent != null)
				serialize(parent, System.out, true);
		}
		catch (IOException e) {
			throw new InternalCompilerError(e);
		}
	}
	
	/**
	 * Serialize a given node to an output stream (possibly as document).
	 * @param n the node to serialize
	 * @param stream the target stream
	 * @param asDoc if true, serialize as a complete document
	 */
	public void serialize(org.w3c.dom.Node n, PrintStream stream, boolean asDoc) throws IOException {
		if (n instanceof Element) {
			DOMSource source = new DOMSource(n);
			try {
				format.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, asDoc?"no":"yes");
				//format.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				format.setOutputProperty(OutputKeys.INDENT, "yes");
				format.transform(source, new StreamResult(stream));
			}
			catch (TransformerException e) {
				throw new InternalCompilerError("Error in serializing data to stream");
			}
		}
	}
	
	public Object copy() {
		try {
			return (DomGenerator) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new InternalCompilerError("Java clone() weirdness");
		}
	}
	
	public Element createElement(Element parent, String tag) {
		Element e = scratchPad.createElement(tag);
		if (parent != null)
			parent.appendChild(e);
		return e;
	}
	
	public void createAttribute(Element parent, String tag, String value) {
		assert(parent != null);
		parent.setAttribute(tag, value);
	}
	
	public void createText(Element parent, String content) {
		assert(parent != null);
		Document doc = parent.getOwnerDocument();
		Text t = doc.createTextNode(content);
		parent.appendChild(t);
	}
	
	public void createPI(Element parent, String target, String content) {
		assert(parent != null);
		Document doc = parent.getOwnerDocument();
		ProcessingInstruction t = doc.createProcessingInstruction(target,content);
		parent.appendChild(t);
	}
	
	public void createComment(Element parent, String content) {
		assert(parent != null);
		Document doc = parent.getOwnerDocument();
		Comment t = doc.createComment(content);
		parent.appendChild(t);
	}
	
	public Element setAttribute(Element elem, String attrName, Object value) {
		elem.setAttribute(attrName, value.toString());
		return elem;
	}
	
	/**
	 *
	 * @param elem
	 * @param newChild
	 * @return elem
	 */
	public void appendChild(Element elem, org.w3c.dom.Node newChild) {
		elem.appendChild(elem.getOwnerDocument().importNode(newChild, true));
	}
	
	/**
	 *
	 * @param elem
	 * @param newChild
	 * @return elem
	 */
	public void appendChildren(Element elem, Object newTextChild) {
		elem.appendChild(scratchPad.createTextNode(newTextChild.toString()));
	}
	
	public void appendChildren(Element elem, List list) {
		Document d = elem.getOwnerDocument();
		for (Iterator i = list.iterator(); i.hasNext();) {
			org.w3c.dom.Node n = (org.w3c.dom.Node) i.next();
			elem.appendChild(d.importNode(n, true));
		}
	}
	
	/**
	 *
	 */
	public void insertBefore(Element parent, Element ref, Element newChild) {
		parent.insertBefore(newChild, ref);
	}
	
	/**
	 *
	 */
	public void insertAfter(Element parent, Element ref, Element newChild) {
		org.w3c.dom.Node next = ref.getNextSibling();
		if (next == null) {
			parent.appendChild(newChild);
		}
		else {
			parent.insertBefore(newChild, next);
		}
	}
	
	/**
	 *
	 * @param elem
	 * @param newChildren an array of elements
	 * @return elem
	 */
	public void appendChildren(Element elem, Element[] newChildren) {
		for (int i = 0; i < newChildren.length; i++) {
			elem.appendChild(newChildren[i]);
		}
	}
	
	public DomGenerator tag(String tag) {
		DomGenerator v = (DomGenerator) copy();
		v.parent = createElement(parent, tag);
		return v;
	}
	
	public <T> DomGenerator gen(String tag, T n, X10Dom.AbsLens<T> lens) {
		DomGenerator v = tag(tag);
		lens.toXML(v, n);
		return this;
	}
	
}
