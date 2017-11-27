package ie.gmit.sw.similarity.shingles;

import ie.gmit.sw.documents.Document;

public interface Shinglizer  {
    ShinglizeResult shinglize(final Document document);
}
