package x10dt.ui.quickfix;

import java.util.Collection;

import org.eclipse.imp.editor.hover.ProblemLocation;
import org.eclipse.imp.editor.quickfix.IAnnotation;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.imp.services.base.DefaultQuickFixAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class QuickFixAssistant extends DefaultQuickFixAssistant {

	public QuickFixAssistant() {

	}

	public boolean canAssist(IQuickFixInvocationContext invocationContext) {
		return false;
	}

	public boolean canFix(Annotation annotation) {
		int errorCode = -1;
		if (annotation instanceof IAnnotation) {
			errorCode = ((IAnnotation) annotation).getId();
		}

		if (annotation instanceof MarkerAnnotation) {
			errorCode = (((MarkerAnnotation) annotation).getMarker()
					.getAttribute(IMessageHandler.ERROR_CODE_KEY, -1));
		}

		switch (errorCode) {
		case 1001:
		case 1002:
		case 1003:
		case 1004:
			return true;
		}

		return false;
	}

	public String[] getSupportedMarkerTypes() {
		return new String[]{"x10dt.core.problemMarker"};
	}
	
	public void addProposals(IQuickFixInvocationContext context,
			ProblemLocation problem, Collection<ICompletionProposal> proposals) {
		int id = problem.getProblemId();

		switch (id) {
		case 1001:
			proposals.add(new SurroundThrowProposal(context));
			break;
		case 1002:
			proposals.add(new CreateMethodProposal(context, problem.getAttribute("METHOD", ""), problem.getAttribute("ARGUMENTS", "")));
			break;
		case 1003:
			proposals.add(new ConstructorFromSuperclassProposal(context,
					problem.getAttribute("CONSTRUCTOR", ""), problem.getAttribute("ARGUMENTS", "")));
			break;
		case 1004:
			proposals.add(new UnimplementedMethodProposal(context));
			
			proposals.add(new MakeAbstractProposal(context, problem.getAttribute("CLASS", "")));
			break;
		default:
		}
	}
}
