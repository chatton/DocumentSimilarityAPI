package ie.gmit.sw.documents;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Url document.
 */
public class URLDocument implements Document {

    private final String text;

    /**
     * Instantiates a new Url document.
     *
     * @param url  the base url which will be used to construct the document.
     * @param tags the html tags which are to be included in the document.
     * @throws IOException if there is an error connecting to the provided url
     */
    public URLDocument(final String url, final List<String> tags) throws IOException {
        final org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
        text = tags.stream()
                .map(doc::getElementsByTag)
                .map(Elements::text)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public String text() {
        return text;
    }

    /**
     * The type Builder.
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
         * @param url the url
         * @return the builder
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
         * @param tags the tags
         * @return the builder
         */
        public Builder tags(final String... tags) {
            Arrays.stream(tags).forEach(this::addTag);
            return this;
        }

        /**
         * Add tag builder.
         *
         * @param tag the tag
         * @return the builder
         */
        public Builder addTag(final String tag) {
            tags.add(tag);
            return this;
        }

        /**
         * Build url document.
         *
         * @return the url document
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
