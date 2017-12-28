package ie.gmit.sw.similarity.shingles;

import ie.gmit.sw.documents.Document;

/**
 * The interface Shinglizer.
 *
 * Implementing subclasses must be able to break a {@link Document} up
 * into {@link Shingle}s.
 */
public interface Shinglizer  {
    /**
     * @param document the document to be broken up into shingles.
     * @return the shinglize result
     */
    ShinglizeResult shinglize(Document document);
}
