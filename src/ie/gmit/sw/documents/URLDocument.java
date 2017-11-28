package ie.gmit.sw.documents;

import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class URLDocument implements Document {

    private final String text;

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

    public static class Builder {

        private final List<String> tags;
        private String url;

        public Builder() {
            this(null);
        }

        public Builder(final String url) {
            this.url = url;
            this.tags = new ArrayList<>();
        }

        public Builder url(final String url) {
            this.url = url;
            return this;
        }

        public Builder addTags(final String... tags) {
            for (String tag : tags) {
                addTag(tag);
            }
            return this;
        }

        public Builder addTag(final String tag) {
            tags.add(tag);
            return this;
        }

        public URLDocument build() throws IOException {
            if (url == null || tags.isEmpty()) {
                throw new IllegalStateException(
                        "Cannot build a URLDocument without providing a valid URL and at least 1 tag.");
            }
            return new URLDocument(url, tags);
        }
    }
}
