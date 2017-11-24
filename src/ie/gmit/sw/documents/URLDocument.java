package ie.gmit.sw.documents;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;

public class URLDocument implements Document {

    private final String text;

    public URLDocument(final String url) throws IOException {
        org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
        Elements paragraphs = doc.getElementsByTag("p");
        StringBuilder sb = new StringBuilder();
        paragraphs.forEach(p -> sb.append(p.text()));
        text = sb.toString();
    }

    @Override
    public String text() {
        return text;
    }

}
