package x10dt.ui.typeHierarchy;
/*******************************************************************************
* Copyright (c) 2008,2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.services.ILabelProvider;
import org.eclipse.imp.utils.MarkerUtils;
import org.eclipse.jdt.internal.ui.viewsupport.StorageLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import x10dt.ui.X10DTUIPlugin;

/**
 * Text and icon labels for X10 Outline View (and possibly others).<br>
 * To add an image, e.g. "foo", add a String field ending in "_IMAGE_NAME", a similarly named Image field ending in
 * "_IMAGE", and an icon in the icons directory of this (x10dt.ui) project that matches the FOO_IMAGE_NAME+".gif"
 * <br>
 * Static initializer block finds all thusly named fields and creates the images and does the registry
 * bookkeeping.
 * @author Beth Tibbitts
 */
public class X10LabelProvider implements ILabelProvider, ILanguageService, IStyledLabelProvider, IColorProvider {
    private Set<ILabelProviderListener> fListeners= new HashSet<ILabelProviderListener>();
    
    private ArrayList fLabelDecorators;
    protected X10ElementImageProvider fImageLabelProvider;
    protected StorageLabelProvider fStorageLabelProvider;
    
    private int fImageFlags;
	private long fTextFlags;
	
    public X10LabelProvider()
    {
    	this(X10ElementLabels.ALL_DEFAULT, X10ElementImageProvider.OVERLAY_ICONS);
    }
    
    public X10LabelProvider(long textFlags, int imageFlags)
    {
    	fImageLabelProvider= new X10ElementImageProvider();
    	fStorageLabelProvider= new StorageLabelProvider();
    	fImageFlags= imageFlags;
		fTextFlags= textFlags;
    }

    private static ImageRegistry sImageRegistry= X10DTUIPlugin.getInstance().getImageRegistry();


    //================== PORT1.7 Images moved here from Outliner
    
    public static Image _DESC_ELCL_VIEW_MENU = X10PluginImages.DESC_ELCL_VIEW_MENU.createImage();

    /** Images for the default, private, protected, and public versions of fields */
    public static Image _DESC_FIELD_DEFAULT = X10PluginImages.DESC_FIELD_DEFAULT.createImage();
    public static Image _DESC_FIELD_PRIVATE = X10PluginImages.DESC_FIELD_PRIVATE.createImage();
    public static Image _DESC_FIELD_PROTECTED = X10PluginImages.DESC_FIELD_PROTECTED.createImage();
    public static Image _DESC_FIELD_PUBLIC = X10PluginImages.DESC_FIELD_PUBLIC.createImage();

    /** Images for the default, private, protected, and public versions of miscellaneous objects */
    public static Image _DESC_MISC_DEFAULT = X10PluginImages.DESC_MISC_DEFAULT.createImage();
    public static Image _DESC_MISC_PRIVATE = X10PluginImages.DESC_MISC_PRIVATE.createImage();
    public static Image _DESC_MISC_PROTECTED = X10PluginImages.DESC_MISC_PROTECTED.createImage();
    public static Image _DESC_MISC_PUBLIC = X10PluginImages.DESC_MISC_PUBLIC.createImage();

    public static Image _DESC_OBJS_CFILECLASS = X10PluginImages.DESC_OBJS_CFILECLASS.createImage();
    public static Image _DESC_OBJS_CFILEINT = X10PluginImages.DESC_OBJS_CFILEINT.createImage();

    /** Images for the default, private, protected, and public versions of objects */
    public static Image _DESC_OBJS_INNER_CLASS_DEFAULT = X10PluginImages.DESC_OBJS_INNER_CLASS_DEFAULT.createImage();
    public static Image _DESC_OBJS_INNER_CLASS_PRIVATE = X10PluginImages.DESC_OBJS_INNER_CLASS_PRIVATE.createImage();
    public static Image _DESC_OBJS_INNER_CLASS_PROTECTED = X10PluginImages.DESC_OBJS_INNER_CLASS_PROTECTED.createImage();
    public static Image _DESC_OBJS_INNER_CLASS_PUBLIC = X10PluginImages.DESC_OBJS_INNER_CLASS_PUBLIC.createImage();


    /** Images for the default, private, protected, and public versions of Interfaces */
    public static Image _DESC_OBJS_INNER_INTERFACE_DEFAULT = X10PluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT.createImage();
    public static Image _DESC_OBJS_INNER_INTERFACE_PRIVATE = X10PluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE.createImage();
    public static Image _DESC_OBJS_INNER_INTERFACE_PROTECTED = X10PluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED.createImage();
    public static Image _DESC_OBJS_INNER_INTERFACE_PUBLIC = X10PluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC.createImage();



    public static Image _DESC_OBJS_PACKDECL = X10PluginImages.DESC_OBJS_PACKDECL.createImage();
    
    
    public static Image[] FIELD_DESCS= {
    	X10LabelProvider._DESC_FIELD_DEFAULT, X10LabelProvider._DESC_FIELD_PRIVATE, X10LabelProvider._DESC_FIELD_PROTECTED, X10LabelProvider._DESC_FIELD_PUBLIC
        };  
    public static Image[] MISC_DESCS= {
    	X10LabelProvider._DESC_MISC_DEFAULT, X10LabelProvider._DESC_MISC_PRIVATE, X10LabelProvider._DESC_MISC_PROTECTED, X10LabelProvider._DESC_MISC_PUBLIC
        };
    public static Image[] INNER_CLASS_DESCS= {
    	X10LabelProvider._DESC_OBJS_INNER_CLASS_DEFAULT, X10LabelProvider._DESC_OBJS_INNER_CLASS_PRIVATE, X10LabelProvider._DESC_OBJS_INNER_CLASS_PROTECTED, X10LabelProvider._DESC_OBJS_INNER_CLASS_PUBLIC
        };
    public static Image[] INNER_INTF_DESCS= {
    	X10LabelProvider._DESC_OBJS_INNER_INTERFACE_DEFAULT, X10LabelProvider._DESC_OBJS_INNER_INTERFACE_PRIVATE, X10LabelProvider._DESC_OBJS_INNER_INTERFACE_PROTECTED, X10LabelProvider._DESC_OBJS_INNER_INTERFACE_PUBLIC
        };
    
    public static final ImageDescriptor DESC_OVR_FOCUS= X10PluginImages.DESC_OVR_FOCUS;
	
    
    //===================

    static {
        // Retrieve all images and put them in the appropriately-named fields
        Class<?> myClass= X10LabelProvider.class;
        Field[] fields= myClass.getDeclaredFields();

        for(int i= 0; i < fields.length; i++) {
            Field imageNameField= fields[i];
            String imageNameFieldName= imageNameField.getName();

            if (!imageNameFieldName.endsWith("_IMAGE_NAME"))
                continue;

            try {
                String imageName= (String) imageNameField.get(null);
                ImageDescriptor desc= X10DTUIPlugin.create(imageName + ".gif");

                sImageRegistry.put(imageName, desc);

                try {
                    String imageFieldName= imageNameFieldName.substring(0, imageNameFieldName.lastIndexOf("_NAME"));
                    Field imageField= myClass.getDeclaredField(imageFieldName);

                    imageField.set(null, sImageRegistry.get(imageName));
                } catch(NoSuchFieldException e) {
                    X10DTUIPlugin.log(e);
                }
            } catch (IllegalArgumentException e) {
                X10DTUIPlugin.log(e);
            } catch (IllegalAccessException e) {
                X10DTUIPlugin.log(e);
            }
        }
    }
    
    /**
	 * Adds a decorator to the label provider
	 * @param decorator the decorator to add
	 */
	public void addLabelDecorator(ILabelDecorator decorator) {
		if (fLabelDecorators == null) {
			fLabelDecorators= new ArrayList(2);
		}
		fLabelDecorators.add(decorator);
	}

    private Language fX10Language= LanguageRegistry.findLanguage("x10");

    public Image getImage(Object o) {
        if (o instanceof IResource || o instanceof ISourceEntity) {
            IResource res= (o instanceof ISourceEntity) ? ((ISourceEntity) o).getResource() : (IResource) o;
            return getErrorTicksFromMarkers(res);
        }

        Image result= fImageLabelProvider.getImageLabel(o, fImageFlags);
        return decorateImage(result, o);
    }

    /**
     * Get image based on the public/protected/private qualifier
     * @param flags
     * @param images
     * @return
     */
//    private Image getImageFromQualifiers(i flags, Image[] images) {
//        if (flags.isPrivate())
//            return images[1];
//        else if (flags.isProtected())
//            return images[2];
//        else if (flags.isPublic())
//            return images[3];
//        else
//            return images[0];
//    }

    public Image getErrorTicksFromMarkers(IResource res) {
        if (res instanceof IFile) {
            IFile file= (IFile) res;
            boolean found= false;

            for (String ext : fX10Language.getFilenameExtensions()) {
                if (ext.equals(file.getFileExtension())) {
                    found= true;
                }
            }
            if (found) {
//                return selectDecoratedImage(res, CU_IMAGES);
            }
        }
        if (res instanceof IProject) {
//            return selectDecoratedImage(res, PROJECT_IMAGES);
        }
        return null;
    }

    private Image selectDecoratedImage(IResource res, Image[] images) {
        int severity= MarkerUtils.getMaxProblemMarkerSeverity(res, IResource.DEPTH_ONE);

        switch (severity) {
        case IMarker.SEVERITY_ERROR: return images[0];
        case IMarker.SEVERITY_WARNING: return images[1];
        case IMarker.SEVERITY_INFO: return images[2];
        default: return images[3];
        }
    }

    private String filter(String name) {
        return name.replaceAll("\n", "").replaceAll("\\{amb\\}", "");
    }

    public void addListener(ILabelProviderListener listener) {
        fListeners.add(listener);
    }

    /* (non-Javadoc)
	 * @see IBaseLabelProvider#dispose
	 */
	public void dispose() {
		if (fLabelDecorators != null) {
			for (int i= 0; i < fLabelDecorators.size(); i++) {
				ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
				decorator.dispose();
			}
			fLabelDecorators= null;
		}
//		fStorageLabelProvider.dispose();
		fImageLabelProvider.dispose();
	}

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
        fListeners.remove(listener);
    }

	
	public String getText(Object element) {
		String result= X10ElementLabels.getTextLabel(element, fTextFlags);
		if (result.length() == 0 && (element instanceof IStorage)) {
			result= fStorageLabelProvider.getText(element);
		}
		return decorateText(result, element);
	}

	public StyledString getStyledText(Object element) {
		StyledString string= X10ElementLabels.getStyledTextLabel(element, (fTextFlags | X10ElementLabels.COLORIZE));
		if (string.length() == 0 && (element instanceof IStorage)) {
			string= new StyledString(fStorageLabelProvider.getText(element));
		}
		String decorated= decorateText(string.getString(), element);
		if (decorated != null) {
			return StyledCellLabelProvider.styleDecoratedString(decorated, StyledString.DECORATIONS_STYLER, string);
		}
		return string;
	}
	
	protected Image decorateImage(Image image, Object element) {
		if (fLabelDecorators != null && image != null) {
			for (int i= 0; i < fLabelDecorators.size(); i++) {
				ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
				image= decorator.decorateImage(image, element);
			}
		}
		return image;
	}
	
	protected String decorateText(String text, Object element) {
		if (fLabelDecorators != null && text.length() > 0) {
			for (int i= 0; i < fLabelDecorators.size(); i++) {
				ILabelDecorator decorator= (ILabelDecorator) fLabelDecorators.get(i);
				String decorated= decorator.decorateText(text, element);
				if (decorated != null) {
					text= decorated;
				}
			}
		}
		return text;
	}
	
	/**
	 * Sets the textFlags.
	 * @param textFlags The textFlags to set
	 */
	public final void setTextFlags(long textFlags) {
		fTextFlags= textFlags;
	}

	/**
	 * Sets the imageFlags
	 * @param imageFlags The imageFlags to set
	 */
	public final void setImageFlags(int imageFlags) {
		fImageFlags= imageFlags;
	}
	
	/**
	 * Gets the image flags.
	 * Can be overwritten by super classes.
	 * @return Returns a int
	 */
	public final int getImageFlags() {
		return fImageFlags;
	}

	/**
	 * Gets the text flags.
	 * @return Returns a int
	 */
	public final long getTextFlags() {
		return fTextFlags;
	}
	
	/**
     * Fires a label provider changed event to all registered listeners
     * Only listeners registered at the time this method is called are notified.
     *
     * @param event a label provider changed event
     *
     * @see ILabelProviderListener#labelProviderChanged
     */
    protected void fireLabelProviderChanged(final LabelProviderChangedEvent event) {
        for (final ILabelProviderListener l : fListeners) {
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.labelProviderChanged(event);
                }
            });
        }
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
	 */
	public Color getBackground(Object element) {
		return null;
	}
}
