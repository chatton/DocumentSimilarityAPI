package ie.gmit.sw.similarity.shingles;

import ie.gmit.sw.documents.Document;

/**
 * The interface Shinglizer.
 */
public interface Shinglizer  {
    /**
     * Shinglize shinglize result.
     *
     * @param document the document
     * @return the shinglize result
     */
    ShinglizeResult shinglize(final Document document);
}
