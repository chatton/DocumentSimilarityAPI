package ie.gmit.sw.documents;

import afu.org.checkerframework.checker.oigj.qual.O;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type URLDocument. A URLDocument is a {@link Document} implementation
 * that is built using a URL and HTML tags.
 */
public class URLDocument implements Document {

    private final StringDocument stringDoc;

    /**
     * Instantiates a new Url document.
     *
     * @param url  the base url which will be used to construct the document.
     * @param tags the html tags which are to be included in the document.
     * @throws IOException if there is an error connecting to the provided url
     */
    public URLDocument(final String url, final List<String> tags) throws IOException {
        final org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
        final String text = tags.stream()
                .map(doc::getElementsByTag)
                .map(Elements::text)
                .collect(Collectors.joining(System.lineSeparator()));

        stringDoc = new StringDocument(text);
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

    /**
     * A Builder class to construct URLDocuments step by step.
     */
    public static class Builder {

        private final static String HTTPS_PROTOCOL = "https";
        private final List<String> tags;
        private String url;

        /**
         * Instantiates a new UrlDocument Builder.
         */
        public Builder() {
            tags = new ArrayList<>();
        }


        /**
         * Url builder.
         *
         * @param url the which will be used for the URLDocument
         * @return the Builder instance for method chaining.
         */
        public Builder url(final String url) {
            if (!url.startsWith(HTTPS_PROTOCOL)) {
                this.url = HTTPS_PROTOCOL + "://" + url;
            } else {
                this.url = url;
            }
            return this;
        }

        /**
         * Tags builder.
         *
         * @param tags a variable number of Strings each should be a HTML tag.
         * @return the Builder instance for method chaining.
         */
        public Builder tags(final String... tags) {
            Arrays.stream(tags).forEach(this::addTag);
            return this;
        }


        /**
         * @param tag the HTML tag as a String.
         * @return the Builder instance for method chaining.
         */
        public Builder addTag(final String tag) {
            tags.add(tag);
            return this;
        }

        /**
         * Build url document.
         *
         * @return the url document created using the values set in the Builder.
         * @throws IOException if there is an error creating the UrlDocument
         */
        public URLDocument build() throws IOException {
            if (url == null || tags.isEmpty()) {
                throw new IllegalStateException(
                        "Cannot build a URLDocument without providing a valid URL and at least 1 tag.");
            }
            return new URLDocument(url, tags);
        }
    }
}
