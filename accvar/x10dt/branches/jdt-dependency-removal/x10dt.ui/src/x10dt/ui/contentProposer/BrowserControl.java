/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Genady Beryozkin <eclipse@genady.org> - [hovering] tooltip for constant string does not show constant value - https://bugs.eclipse.org/bugs/show_bug.cgi?id=85382
 *******************************************************************************/
package x10dt.ui.contentProposer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.imp.actions.SimpleSelectionProvider;
import org.eclipse.imp.editor.hover.AbstractTextHover;
import org.eclipse.imp.utils.OpenBrowserUtil;
import org.eclipse.imp.utils.UIUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;
import org.eclipse.jface.internal.text.html.BrowserInput;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension4;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;


/**
 * Provides Javadoc as hover info for Java elements.
 *
 * @since 2.1
 */
public class BrowserControl extends AbstractTextHover {

	/**
	 * Action to go back to the previous input in the hover control.
	 *
	 * @since 3.4
	 */
	private static final class BackAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public BackAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			//setText(JavaHoverMessages.JavadocHover_back);
			setText("back");
			ISharedImages images= PlatformUI.getWorkbench().getSharedImages();
			setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
			setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));

			update();
		}

		public void run() {
			X10BrowserInformationControlInput previous= (X10BrowserInformationControlInput) fInfoControl.getInput().getPrevious();
			if (previous != null) {
				fInfoControl.setInput(previous);
			}
		}

		public void update() {
			X10BrowserInformationControlInput current= (X10BrowserInformationControlInput) fInfoControl.getInput();

			if (current != null && current.getPrevious() != null) {
				BrowserInput previous= current.getPrevious();
				//setToolTipText(Messages.format(JavaHoverMessages.JavadocHover_back_toElement_toolTip, BasicElementLabels.getJavaElementName(previous.getInputName())));
				setEnabled(true);
			} else {
				//setToolTipText(JavaHoverMessages.JavadocHover_back);
				setEnabled(false);
			}
		}
	}

	/**
	 * Action to go forward to the next input in the hover control.
	 *
	 * @since 3.4
	 */
	private static final class ForwardAction extends Action {
		private final BrowserInformationControl fInfoControl;

		public ForwardAction(BrowserInformationControl infoControl) {
			fInfoControl= infoControl;
			//setText(JavaHoverMessages.JavadocHover_forward);
			setText("forward");
			ISharedImages images= PlatformUI.getWorkbench().getSharedImages();
			setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
			setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));

			update();
		}

		public void run() {
			X10BrowserInformationControlInput next= (X10BrowserInformationControlInput) fInfoControl.getInput().getNext();
			if (next != null) {
				fInfoControl.setInput(next);
			}
		}

		public void update() {
			X10BrowserInformationControlInput current= (X10BrowserInformationControlInput)fInfoControl.getInput();

			if (current != null && current.getNext() != null) {
				//setToolTipText(Messages.format(JavaHoverMessages.JavadocHover_forward_toElement_toolTip, BasicElementLabels.getJavaElementName(current.getNext().getInputName())));
				setEnabled(true);
			} else {
				//setToolTipText(JavaHoverMessages.JavadocHover_forward_toolTip);
				setEnabled(false);
			}
		}
	}

	public final static String APPEARANCE_JAVADOC_FONT= "org.eclipse.jdt.ui.javadocfont"; //$NON-NLS-1$


	/**
	 * Presenter control creator.
	 *
	 * @since 3.3
	 */
	public static final class PresenterControlCreator extends AbstractReusableInformationControlCreator {

		/*
		 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
		 */
		public IInformationControl doCreateInformationControl(Shell parent) {
			if (BrowserInformationControl.isAvailable(parent)) {
				ToolBarManager tbm= new ToolBarManager(SWT.FLAT);
				String font= APPEARANCE_JAVADOC_FONT;
				final BrowserInformationControl iControl= new BrowserInformationControl(parent, font, tbm);

				final BackAction backAction= new BackAction(iControl);
				
				backAction.setEnabled(false);
				tbm.add(backAction);
				final ForwardAction forwardAction= new ForwardAction(iControl);
				tbm.add(forwardAction);
				forwardAction.setEnabled(false);
				
				final SimpleSelectionProvider selectionProvider= new SimpleSelectionProvider();
				//OpenExternalBrowserAction openExternalJavadocAction= new OpenExternalBrowserAction(parent.getDisplay(), selectionProvider);
				//selectionProvider.addSelectionChangedListener(openExternalJavadocAction);
				selectionProvider.setSelection(new StructuredSelection());
				//tbm.add(openExternalJavadocAction);

				IInputChangedListener inputChangeListener= new IInputChangedListener() {
					public void inputChanged(Object newInput) {
						backAction.update();
						forwardAction.update();
						if (newInput == null) {
							selectionProvider.setSelection(new StructuredSelection());
						} else if (newInput instanceof BrowserInformationControlInput) {
							BrowserInformationControlInput input= (BrowserInformationControlInput) newInput;
							Object inputElement= input.getInputElement();
							selectionProvider.setSelection(new StructuredSelection(inputElement));
//							boolean isJavaElementInput= inputElement instanceof ISourceEntity;
//							showInJavadocViewAction.setEnabled(isJavaElementInput);
//							openDeclarationAction.setEnabled(isJavaElementInput);
						}
					}
				};
				iControl.addInputChangeListener(inputChangeListener);

				tbm.update(true);

				addLinkListener(iControl);
				return iControl;

			} else {
				return new DefaultInformationControl(parent, true);
			}
		}
	}


	/**
	 * Hover control creator.
	 *
	 * @since 3.3
	 */
	public static final class HoverControlCreator extends AbstractReusableInformationControlCreator {
		/**
		 * The information presenter control creator.
		 * @since 3.4
		 */
		private final IInformationControlCreator fInformationPresenterControlCreator;
		/**
		 * <code>true</code> to use the additional info affordance, <code>false</code> to use the hover affordance.
		 */
		private final boolean fAdditionalInfoAffordance;

		/**
		 * @param informationPresenterControlCreator control creator for enriched hover
		 * @since 3.4
		 */
		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator) {
			this(informationPresenterControlCreator, false);
		}

		/**
		 * @param informationPresenterControlCreator control creator for enriched hover
		 * @param additionalInfoAffordance <code>true</code> to use the additional info affordance, <code>false</code> to use the hover affordance
		 * @since 3.4
		 */
		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator, boolean additionalInfoAffordance) {
			fInformationPresenterControlCreator= informationPresenterControlCreator;
			fAdditionalInfoAffordance= additionalInfoAffordance;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
		 */
		public IInformationControl doCreateInformationControl(Shell parent) {
			String tooltipAffordanceString= "";//fAdditionalInfoAffordance? JavaPlugin.getAdditionalInfoAffordanceString() : EditorsUI.getTooltipAffordanceString();
			if (BrowserInformationControl.isAvailable(parent)) {
				//String font= PreferenceConstants.APPEARANCE_JAVADOC_FONT;
				BrowserInformationControl iControl= new BrowserInformationControl(parent, null /*font*/, tooltipAffordanceString) {
					/*
					 * @see org.eclipse.jface.text.IInformationControlExtension5#getInformationPresenterControlCreator()
					 */
					public IInformationControlCreator getInformationPresenterControlCreator() {
						return fInformationPresenterControlCreator;
					}
				};
				addLinkListener(iControl);
				return iControl;
			} else {
				return new DefaultInformationControl(parent, tooltipAffordanceString);
			}
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#canReuse(org.eclipse.jface.text.IInformationControl)
		 */
		public boolean canReuse(IInformationControl control) {
			if (!super.canReuse(control))
				return false;

			if (control instanceof IInformationControlExtension4) {
				String tooltipAffordanceString= fAdditionalInfoAffordance ? UIUtils.getAdditionalInfoAffordanceString() : EditorsUI.getTooltipAffordanceString();
				((IInformationControlExtension4)control).setStatusText(tooltipAffordanceString);
			}

			return true;
		}
	}


	/**
	 * The style sheet (css).
	 * @since 3.4
	 */
	private static String fgStyleSheet;

	/**
	 * The hover control creator.
	 *
	 * @since 3.2
	 */
	private IInformationControlCreator fHoverControlCreator;
	/**
	 * The presentation control creator.
	 *
	 * @since 3.2
	 */
	private IInformationControlCreator fPresenterControlCreator;

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getInformationPresenterControlCreator()
	 * @since 3.1
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null)
			fPresenterControlCreator= new PresenterControlCreator();
		return fPresenterControlCreator;
	}

	/*
	 * @see ITextHoverExtension#getHoverControlCreator()
	 * @since 3.2
	 */
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null)
			fHoverControlCreator= new HoverControlCreator(getInformationPresenterControlCreator());
		return fHoverControlCreator;
	}

	
 @SuppressWarnings("restriction")
private static void addLinkListener(final BrowserInformationControl control) {
	 control.addLocationListener(new LocationAdapter(){
		 public void changing(LocationEvent event){
			 String loc= event.location;
			 
			 if (loc.startsWith("about:")) { //$NON-NLS-1$
					// Relative links should be handled via head > base tag.
					// If no base is available, links just won't work.
					return;
			 }
			 try {
				 URL url = new URL(loc);
				 control.notifyDelayedInputChange(null);
				 control.dispose(); //FIXME: should have protocol to hide, rather than dispose
				 // open external links in real browser:
				 OpenBrowserUtil.open(url, event.display, ""); //$NON-NLS-1$
				 
				 
				 //The following is old code to display html inside control.
				 /*BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
				 try {
					 StringBuilder html = new StringBuilder();
					 String line;
					 while((line = in.readLine())!= null){
						 html.append(line).append("\n");
					 }
					 control.setInput(new X10BrowserInformationControlInput((X10BrowserInformationControlInput)control.getInput(), html.toString(), ""));
				 } finally {
					 in.close();
				 }*/
			 } catch(MalformedURLException e){
			 		System.err.println(e);
			 } catch(IOException e){
			 		System.err.println(e);
			 }
		 }
	 });
 }
//		control.addLocationListener(JavaElementLinks.createLocationListener(new JavaElementLinks.ILinkHandler() {
//			/* (non-Javadoc)
//			 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleJavadocViewLink(org.eclipse.jdt.core.ISourceEntity)
//			 */
//			public void handleJavadocViewLink(ISourceEntity linkTarget) {
//				control.notifyDelayedInputChange(null);
//				control.setVisible(false);
//				control.dispose(); //FIXME: should have protocol to hide, rather than dispose
//				try {
//					JavadocView view= (JavadocView) JavaPlugin.getActivePage().showView(JavaUI.ID_JAVADOC_VIEW);
//					view.setInput(linkTarget);
//				} catch (PartInitException e) {
//					JavaPlugin.log(e);
//				}
//			}
//
//			/* (non-Javadoc)
//			 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleInlineJavadocLink(org.eclipse.jdt.core.ISourceEntity)
//			 */
//			public void handleInlineJavadocLink(ISourceEntity linkTarget) {
//				JavadocBrowserInformationControlInput hoverInfo= getHoverInfo(new ISourceEntity[] { linkTarget }, null, (JavadocBrowserInformationControlInput) control.getInput());
//				if (control.hasDelayedInputChangeListener())
//					control.notifyDelayedInputChange(hoverInfo);
//				else
//					control.setInput(hoverInfo);
//			}
//
//			/* (non-Javadoc)
//			 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleDeclarationLink(org.eclipse.jdt.core.ISourceEntity)
//			 */
//			public void handleDeclarationLink(ISourceEntity linkTarget) {
//				control.notifyDelayedInputChange(null);
//				control.dispose(); //FIXME: should have protocol to hide, rather than dispose
//				try {
//					//FIXME: add hover location to editor navigation history?
//					JavaUI.openInEditor(linkTarget);
//				} catch (PartInitException e) {
//					JavaPlugin.log(e);
//				} catch (ModelException e) {
//					JavaPlugin.log(e);
//				}
//			}
//
//			/* (non-Javadoc)
//			 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaElementLinks.ILinkHandler#handleExternalLink(java.net.URL, org.eclipse.swt.widgets.Display)
//			 */
//			public boolean handleExternalLink(URL url, Display display) {
//				control.notifyDelayedInputChange(null);
//				control.dispose(); //FIXME: should have protocol to hide, rather than dispose
//
//				// open external links in real browser:
//				OpenBrowserUtil.open(url, display, ""); //$NON-NLS-1$
//
//				return true;
//			}
//
//			public void handleTextSet() {
//			}
//		}));
//	}

	
	/**
	 * @deprecated see {@link org.eclipse.jface.text.ITextHover#getHoverInfo(ITextViewer, IRegion)}
	 */
	@SuppressWarnings("restriction")
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		//JavadocBrowserInformationControlInput info= (JavadocBrowserInformationControlInput) getHoverInfo2(textViewer, hoverRegion);
		X10BrowserInformationControlInput info= (X10BrowserInformationControlInput) getHoverInfo2(textViewer, hoverRegion);
		return info != null ? info.getHtml() : null;
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
//	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
//		return internalGetHoverInfo(textViewer, hoverRegion);
//	}
//
//	private JavadocBrowserInformationControlInput internalGetHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
//		ISourceEntity[] elements= getJavaElementsAt(textViewer, hoverRegion);
//		if (elements == null || elements.length == 0)
//			return null;
//
//		String constantValue;
//		if (elements.length == 1 && elements[0].getElementType() == ISourceEntity.FIELD) {
//			constantValue= getConstantValue((IField) elements[0], hoverRegion);
//			if (constantValue != null)
//				constantValue= HTMLPrinter.convertToHTMLContent(constantValue);
//		} else {
//			constantValue= null;
//		}
//
//		return getHoverInfo(elements, constantValue, null);
//	}

//	/**
//	 * Computes the hover info.
//	 *
//	 * @param elements the resolved elements
//	 * @param constantValue a constant value iff result contains exactly 1 constant field, or <code>null</code>
//	 * @param previousInput the previous input, or <code>null</code>
//	 * @return the HTML hover info for the given element(s) or <code>null</code> if no information is available
//	 * @since 3.4
//	 */
//	private static JavadocBrowserInformationControlInput getHoverInfo(ISourceEntity[] elements, String constantValue, JavadocBrowserInformationControlInput previousInput) {
//		int nResults= elements.length;
//		StringBuffer buffer= new StringBuffer();
//		boolean hasContents= false;
//		String base= null;
//		ISourceEntity element= null;
//
//		int leadingImageWidth= 0;
//
//		if (nResults > 1) {
//
//			for (int i= 0; i < elements.length; i++) {
//				HTMLPrinter.startBulletList(buffer);
//				ISourceEntity curr= elements[i];
//				if (curr instanceof IMember || curr.getElementType() == ISourceEntity.LOCAL_VARIABLE) {
//					//FIXME: provide links
//					HTMLPrinter.addBullet(buffer, getInfoText(curr, constantValue, false));
//					hasContents= true;
//				}
//				HTMLPrinter.endBulletList(buffer);
//			}
//
//		} else {
//
//			element= elements[0];
//			if (element instanceof IMember) {
//				IMember member= (IMember) element;
//				HTMLPrinter.addSmallHeader(buffer, getInfoText(member, constantValue, true));
//				Reader reader;
//				try {
////					reader= JavadocContentAccess.getHTMLContentReader(member, true, true);
//					String content= JavadocContentAccess2.getHTMLContent(member, true);
//					reader= content == null ? null : new StringReader(content);
//
//					// Provide hint why there's no Javadoc
//					if (reader == null && member.isBinary()) {
//						boolean hasAttachedJavadoc= JavaDocLocations.getJavadocBaseLocation(member) != null;
//						ISourceFolderRoot root= (ISourceFolderRoot)member.getAncestor(ISourceEntity.PACKAGE_FRAGMENT_ROOT);
//						boolean hasAttachedSource= root != null && root.getSourceAttachmentPath() != null;
//						IOpenable openable= member.getOpenable();
//						boolean hasSource= openable.getBuffer() != null;
//
//						if (!hasAttachedSource && !hasAttachedJavadoc)
//							reader= new StringReader(JavaHoverMessages.JavadocHover_noAttachments);
//						else if (!hasAttachedJavadoc && !hasSource)
//							reader= new StringReader(JavaHoverMessages.JavadocHover_noAttachedJavadoc);
//						else if (!hasAttachedSource)
//							reader= new StringReader(JavaHoverMessages.JavadocHover_noAttachedSource);
//						else if (!hasSource)
//							reader= new StringReader(JavaHoverMessages.JavadocHover_noInformation);
//
//					} else {
//						base= JavaDocLocations.getBaseURL(member);
//					}
//
//				} catch (ModelException ex) {
//					reader= new StringReader(JavaHoverMessages.JavadocHover_error_gettingJavadoc);
//					JavaPlugin.log(ex);
//				}
//
//				if (reader != null) {
//					HTMLPrinter.addParagraph(buffer, reader);
//				}
//				hasContents= true;
//
//			} else if (element.getElementType() == ISourceEntity.LOCAL_VARIABLE || element.getElementType() == ISourceEntity.TYPE_PARAMETER) {
//				HTMLPrinter.addSmallHeader(buffer, getInfoText(element, constantValue, true));
//				hasContents= true;
//			}
//			leadingImageWidth= 20;
//		}
//
//		if (!hasContents)
//			return null;
//
//		if (buffer.length() > 0) {
//			HTMLPrinter.insertPageProlog(buffer, 0, JavadocHover.getStyleSheet());
//			if (base != null) {
//				int endHeadIdx= buffer.indexOf("</head>"); //$NON-NLS-1$
//				buffer.insert(endHeadIdx, "\n<base href='" + base + "'>\n"); //$NON-NLS-1$ //$NON-NLS-2$
//			}
//			HTMLPrinter.addPageEpilog(buffer);
//			return new JavadocBrowserInformationControlInput(previousInput, element, buffer.toString(), leadingImageWidth);
//		}
//
//		return null;
//	}
//
//	private static String getInfoText(ISourceEntity element, String constantValue, boolean allowImage) {
//		long flags;
//		switch (element.getElementType()) {
//			case ISourceEntity.LOCAL_VARIABLE:
//				flags= LOCAL_VARIABLE_FLAGS;
//				break;
//			case ISourceEntity.TYPE_PARAMETER:
//				flags= TYPE_PARAMETER_FLAGS;
//				break;
//			default:
//				flags= LABEL_FLAGS;
//				break;
//		}
//		StringBuffer label= new StringBuffer(JavaElementLinks.getElementLabel(element, flags));
//		if (element.getElementType() == ISourceEntity.FIELD) {
//			if (constantValue != null) {
//				ISourceProject javaProject= element.getJavaProject();
//				if (JavaCore.INSERT.equals(javaProject.getOption(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, true)))
//					label.append(' ');
//				label.append('=');
//				if (JavaCore.INSERT.equals(javaProject.getOption(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR, true)))
//					label.append(' ');
//				label.append(constantValue);
//			}
//		}
//
//		String imageName= null;
//		if (allowImage) {
//			URL imageUrl= JavaPlugin.getDefault().getImagesOnFSRegistry().getImageURL(element);
//			if (imageUrl != null) {
//				imageName= imageUrl.toExternalForm();
//			}
//		}
//
//		StringBuffer buf= new StringBuffer();
//		addImageAndLabel(buf, imageName, 16, 16, 2, 2, label.toString(), 20, 2);
//		return buf.toString();
//	}
//
//	/*
//	 * @since 3.4
//	 */
//	private static boolean isStaticFinal(IField field) {
//		try {
//			return JdtFlags.isFinal(field) && JdtFlags.isStatic(field);
//		} catch (ModelException e) {
//			JavaPlugin.log(e);
//			return false;
//		}
//	}
//
//	/**
//	 * Returns the constant value for the given field.
//	 *
//	 * @param field the field
//	 * @param hoverRegion the hover region
//	 * @return the constant value for the given field or <code>null</code> if none
//	 * @since 3.4
//	 */
//	private String getConstantValue(IField field, IRegion hoverRegion) {
//		if (!isStaticFinal(field))
//			return null;
//
//		ITypeRoot typeRoot= getEditorInputJavaElement();
//		if (typeRoot == null)
//			return null;
//
//		Object constantValue= null;
//
//		CompilationUnit unit= SharedASTProvider.getAST(typeRoot, SharedASTProvider.WAIT_ACTIVE_ONLY, null);
//		if (unit == null)
//			return null;
//
//		ASTNode node= NodeFinder.perform(unit, hoverRegion.getOffset(),	hoverRegion.getLength());
//		if (node != null && node.getNodeType() == ASTNode.SIMPLE_NAME) {
//			IBinding binding= ((SimpleName)node).resolveBinding();
//			if (binding != null && binding.getKind() == IBinding.VARIABLE) {
//				IVariableBinding variableBinding= (IVariableBinding)binding;
//				if (field.equals(variableBinding.getJavaElement())) {
//					constantValue= variableBinding.getConstantValue();
//				}
//			}
//		}
//		if (constantValue == null)
//			return null;
//
//		if (constantValue instanceof String) {
//			StringBuffer result= new StringBuffer();
//			result.append('"');
//			String stringConstant= (String)constantValue;
//			if (stringConstant.length() > 80) {
//				result.append(stringConstant.substring(0, 80));
//				result.append(JavaElementLabels.ELLIPSIS_STRING);
//			} else {
//				result.append(stringConstant);
//			}
//			result.append('"');
//			return result.toString();
//
//		} else if (constantValue instanceof Character) {
//			String constantResult= '\'' + constantValue.toString() + '\'';
//
//			char charValue= ((Character) constantValue).charValue();
//			String hexString= Integer.toHexString(charValue);
//			StringBuffer hexResult= new StringBuffer("\\u"); //$NON-NLS-1$
//			for (int i= hexString.length(); i < 4; i++) {
//				hexResult.append('0');
//			}
//			hexResult.append(hexString);
//			return formatWithHexValue(constantResult, hexResult.toString());
//
//		} else if (constantValue instanceof Byte) {
//			int byteValue= ((Byte) constantValue).intValue() & 0xFF;
//			return formatWithHexValue(constantValue, "0x" + Integer.toHexString(byteValue)); //$NON-NLS-1$
//
//		} else if (constantValue instanceof Short) {
//			int shortValue= ((Short) constantValue).shortValue() & 0xFFFF;
//			return formatWithHexValue(constantValue, "0x" + Integer.toHexString(shortValue)); //$NON-NLS-1$
//
//		} else if (constantValue instanceof Integer) {
//			int intValue= ((Integer) constantValue).intValue();
//			return formatWithHexValue(constantValue, "0x" + Integer.toHexString(intValue)); //$NON-NLS-1$
//
//		} else if (constantValue instanceof Long) {
//			long longValue= ((Long) constantValue).longValue();
//			return formatWithHexValue(constantValue, "0x" + Long.toHexString(longValue)); //$NON-NLS-1$
//
//		} else {
//			return constantValue.toString();
//		}
//	}
//
//	/**
//	 * Creates and returns a formatted message for the given
//	 * constant with its hex value.
//	 *
//	 * @param constantValue the constant value
//	 * @param hexValue the hex value
//	 * @return a formatted string with constant and hex values
//	 * @since 3.4
//	 */
//	private static String formatWithHexValue(Object constantValue, String hexValue) {
//		return Messages.format(JavaHoverMessages.JavadocHover_constantValue_hexValue, new String[] { constantValue.toString(), hexValue });
//	}

//	/**
//	 * Returns the Javadoc hover style sheet with the current Javadoc font from the preferences.
//	 * @return the updated style sheet
//	 * @since 3.4
//	 */
//	private static String getStyleSheet() {
//		if (fgStyleSheet == null)
//			fgStyleSheet= loadStyleSheet();
//		String css= fgStyleSheet;
//		if (css != null) {
//			FontData fontData= JFaceResources.getFontRegistry().getFontData(PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
//			css= HTMLPrinter.convertTopLevelFont(css, fontData);
//		}
//
//		return css;
//	}
//
//	/**
//	 * Loads and returns the Javadoc hover style sheet.
//	 * @return the style sheet, or <code>null</code> if unable to load
//	 * @since 3.4
//	 */
//	private static String loadStyleSheet() {
//		Bundle bundle= Platform.getBundle(JavaPlugin.getPluginId());
//		URL styleSheetURL= bundle.getEntry("/JavadocHoverStyleSheet.css"); //$NON-NLS-1$
//		if (styleSheetURL != null) {
//			BufferedReader reader= null;
//			try {
//				reader= new BufferedReader(new InputStreamReader(styleSheetURL.openStream()));
//				StringBuffer buffer= new StringBuffer(1500);
//				String line= reader.readLine();
//				while (line != null) {
//					buffer.append(line);
//					buffer.append('\n');
//					line= reader.readLine();
//				}
//				return buffer.toString();
//			} catch (IOException ex) {
//				JavaPlugin.log(ex);
//				return ""; //$NON-NLS-1$
//			} finally {
//				try {
//					if (reader != null)
//						reader.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return null;
//	}

	public static void addImageAndLabel(StringBuffer buf, String imageName, int imageWidth, int imageHeight, int imageLeft, int imageTop, String label, int labelLeft, int labelTop) {

		if (imageName != null) {
			StringBuffer imageStyle= new StringBuffer("position: absolute; "); //$NON-NLS-1$
			imageStyle.append("width: ").append(imageWidth).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("height: ").append(imageHeight).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("top: ").append(imageTop).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			imageStyle.append("left: ").append(imageLeft).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$

			buf.append("<!--[if lte IE 6]><![if gte IE 5.5]>\n"); //$NON-NLS-1$
			buf.append("<span style=\"").append(imageStyle).append("filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='").append(imageName).append("')\"></span>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buf.append("<![endif]><![endif]-->\n"); //$NON-NLS-1$

			buf.append("<!--[if !IE]>-->\n"); //$NON-NLS-1$
			buf.append("<img style='").append(imageStyle).append("' src='").append(imageName).append("'/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buf.append("<!--<![endif]-->\n"); //$NON-NLS-1$
			buf.append("<!--[if gte IE 7]>\n"); //$NON-NLS-1$
			buf.append("<img style='").append(imageStyle).append("' src='").append(imageName).append("'/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buf.append("<![endif]-->\n"); //$NON-NLS-1$
		}

		buf.append("<div style='word-wrap:break-word;"); //$NON-NLS-1$
		if (imageName != null) {
			buf.append("margin-left: ").append(labelLeft).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("margin-top: ").append(labelTop).append("px; "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		buf.append("'>"); //$NON-NLS-1$
		buf.append(label);
		buf.append("</div>"); //$NON-NLS-1$
	}

	 public static void open(final URL url, Display display, final String dialogTitle) {
			display.syncExec(new Runnable() {
				public void run() {
					internalOpen(url, dialogTitle);
				}
			});
		}

		private static void internalOpen(final URL url, String title) {
			BusyIndicator.showWhile(null, new Runnable() {
				public void run() {
					PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(url.toExternalForm() + "?noframes=true"); //$NON-NLS-1$
				}
			});
		}
}
