package x10dt.ui.contentProposer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

public class X10TemplateProposal extends TemplateProposal implements
		ICompletionProposalExtension5 {

	private Object fAdditionalInfo;
	
	public X10TemplateProposal(Template template, TemplateContext context, IRegion region, Image image, Object additionalInfo) {
		super(template, context, region, image);
		fAdditionalInfo= additionalInfo;
	}
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		((X10BrowserInformationControlInput)fAdditionalInfo).addToHtml(super.getAdditionalProposalInfo());
		return fAdditionalInfo;
	}
	

	
 public IInformationControlCreator getInformationControlCreator() {
			BrowserControl.PresenterControlCreator presenterControlCreator= new BrowserControl.PresenterControlCreator();
		
		
		return  new BrowserControl.HoverControlCreator(presenterControlCreator, false);
		
	}
	

}