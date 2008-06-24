package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.TypePropertyNode;
import polyglot.ext.x10.ast.X10ClassDecl;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.visit.ContextVisitor;

public class PolymorphicMethodRemover extends ContextVisitor {

	public PolymorphicMethodRemover(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	@Override
	protected Node leaveCall(Node n) throws SemanticException {
		final X10NodeFactory nf = (X10NodeFactory) this.nf;
		X10TypeSystem ts = (X10TypeSystem) this.ts;
		X10Context c = (X10Context) this.context;

		if (n instanceof ClassBody) {
			ClassBody body = (ClassBody) n;

			List<ClassMember> members = new ArrayList<ClassMember>();

			for (ClassMember m : body.members()) {
				if (m instanceof X10MethodDecl) {
					X10MethodDecl md = (X10MethodDecl) m;
					if (md.typeParameters().size() > 0) {
						X10Flags flags = (X10Flags) md.flags().flags();
						flags = (X10Flags) flags.clearAbstract();
						flags = (X10Flags) flags.clearNative();
						flags = flags.clearLocal();
						flags = flags.clearAtomic();
						flags = flags.clearNonBlocking();
						flags = flags.clearPure();
						flags = flags.clearSafe();
						flags = flags.clearSequential();

						String newName = md.id().id();

						ClassBody newBody = nf.ClassBody(md.position(), Collections.<ClassMember>singletonList(md));
						List<TypePropertyNode> typeProps = new TransformingList<TypeParamNode, TypePropertyNode>(md.typeParameters(), new Transformation<TypeParamNode, TypePropertyNode>() {
							public TypePropertyNode transform(TypeParamNode o) {
								return nf.TypePropertyNode(o.position(), o.id(), TypeProperty.Variance.INVARIANT);
							}
						});
						X10ClassDecl cd = nf.X10ClassDecl(md.position(), md.flags().flags(flags), md.id(), typeProps, Collections.EMPTY_LIST, null, nf.CanonicalTypeNode(md.position(), Types.ref(ts.Object())), Collections.EMPTY_LIST, newBody);
						members.add(cd);
					}
					else {
						members.add(md);
					}
				}
				else {
					members.add(m);
				}
			}
			
			return body.members(members);
		}
		
		return super.leaveCall(n);
	}
}
