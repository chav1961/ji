package chav1961.ji.interfaces;

import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

@FunctionalInterface
public interface ClickMetadataListener {
	void process(final ContentNodeMetadata meta, final String action) throws ContentException;
}
