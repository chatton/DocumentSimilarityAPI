package ie.gmit.sw.similarity.shingles;

import ie.gmit.sw.documents.Document;

public interface Shinglizer  {
    ShinglizeResult shinglize(Document document);
}
