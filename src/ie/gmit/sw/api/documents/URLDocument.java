package ie.gmit.sw.api.documents;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type URLDocument. A URLDocument is a {@link Document} implementation
 * that is built using a URL and HTML tags.
 *
 * @author Cian Hatton
 */
public class URLDocument implements Document {

    private final static String HTTPS_PROTOCOL = "https";
    private final StringDocument stringDoc;

    /**
     * Instantiates a new Url document.
     *
     * @param url  the base url which will be used to construct the document.
     * @param tags the html tags which are to be included in the document.
     * @throws IOException              if there is an error connecting to the provided url
     * @throws IllegalArgumentException if no tags are passed in or url is null
     */
    public URLDocument(final String url, final List<String> tags) throws IOException {
        validateArguments(url, tags);
        final String urlToConnectTo = ensureProtocol(url);
        final org.jsoup.nodes.Document doc = Jsoup.connect(urlToConnectTo).get();
        final String text = tags.stream()
                .map(doc::getElementsByTag)
                .map(Elements::text)
                .collect(Collectors.joining(System.lineSeparator()));

        stringDoc = new StringDocument(text);
    }

    private void validateArguments(final String url, final List<String> tags) {
        if (url == null || tags.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot build a URLDocument without providing a valid URL and at least 1 tag.");
        }
    }

    /**
     * @param url the desired input url
     * @return the url with the https protocol prepended if it wasn't present.
     */
    private String ensureProtocol(String url) {
        String urlToConnectTo;
        if (!url.startsWith(HTTPS_PROTOCOL)) {
            urlToConnectTo = HTTPS_PROTOCOL + "://" + url;
        } else {
            urlToConnectTo = url;
        }
        return urlToConnectTo;
    }

    @Override
    public String text() {
        return stringDoc.text();
    }

    @Override
    public int id() {
        return hashCode();
    }

    @Override
    public int hashCode() {
        return stringDoc.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof URLDocument)) {
            return false;
        }
        return stringDoc.equals(((URLDocument) other).stringDoc);
    }
}
