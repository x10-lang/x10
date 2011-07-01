/*
 * Created on Sep 8, 2005
 */
package x10.wala.translator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import polyglot.ast.ClassMember;
import polyglot.ast.Node;
import polyglot.types.CodeInstance;
import polyglot.types.Type;

import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstNode;
import com.ibm.wala.cast.tree.CAstTypeDictionary;
import com.ibm.wala.cast.tree.impl.CAstControlFlowRecorder;
import com.ibm.wala.cast.tree.impl.CAstNodeTypeMapRecorder;
import com.ibm.wala.cast.tree.impl.CAstSourcePositionRecorder;
import com.ibm.wala.util.collections.Pair;

public interface WalkContext {
    void addScopedEntity(CAstNode node, CAstEntity e);

    CAstControlFlowRecorder cfg();

    CAstSourcePositionRecorder pos();

    CAstNodeTypeMapRecorder getNodeTypeMap();

    Collection<Pair<Type, Object>> getCatchTargets(Type label);

    Node getContinueFor(String label);

    Node getBreakFor(String label);

    Node getFinally();

    CodeInstance getEnclosingMethod();

    Type getEnclosingType();

    CAstTypeDictionary getTypeDictionary();

    List<ClassMember> getStaticInitializers();

    List<ClassMember> getInitializers();

    Map<Node, String> getLabelMap();

    boolean needLVal();
}
