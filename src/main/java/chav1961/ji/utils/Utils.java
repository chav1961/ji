package chav1961.ji.utils;

import java.net.URI;
import java.util.Iterator;

import chav1961.ji.ResourceRepository;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class Utils {
	public static ContentNodeMetadata buildContentReferenceByClass(final Class<?> clazz) {
		return new ContentNodeMetadata(){
			@Override
			public Iterator<ContentNodeMetadata> iterator() {
				return new Iterator<ContentNodeMetadata>() {
					@Override public boolean hasNext() {return false;}
					@Override public ContentNodeMetadata next() {return null;}
				};
			}

			@Override public String getName() {return clazz.getCanonicalName();}
			@Override public boolean mounted() {return false;}
			@Override public Class<?> getType() {return clazz;}
			@Override public String getLabelId() {return clazz.getSimpleName();}
			@Override public String getTooltipId() {return null;}
			@Override public String getHelpId() {return null;}
			@Override public FieldFormat getFormatAssociated() {return null;}
			@Override public URI getApplicationPath() {return URI.create(ContentMetadataInterface.APPLICATION_SCHEME+":/"+clazz.getCanonicalName());}
			@Override public URI getUIPath() {return null;}
			@Override public URI getRelativeUIPath() {return null;}
			@Override public URI getLocalizerAssociated() {return ResourceRepository.APP_LOCALIZER.getLocalizerId();}
			@Override public URI getIcon() {return URI.create("root://"+Utils.class.getCanonicalName()+"/images/Java.png");}
			@Override public ContentNodeMetadata getParent() {return null;}
			@Override public int getChildrenCount() {return 0;}
			@Override public ContentNodeMetadata getChild(int index) {return null;}
			@Override public ContentMetadataInterface getOwner() {return null;}
		};
	}
}
