package ie.gmit.sw.documents;

public interface DocumentProvider {
    Document get() throws DocumentRetrievalException;
}
