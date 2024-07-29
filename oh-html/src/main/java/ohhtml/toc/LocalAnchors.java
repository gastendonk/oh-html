package ohhtml.toc;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Add "#" + title as a link to a local heading anchor. If the heading has no ID
 * an ID starting with "j" will be added.
 */
public class LocalAnchors {
    
    public String transform(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        Document doc = Jsoup.parse(html);
        Map<String, Heading> headings = getHeadings(doc);
        boolean dirty = false;
        int nr = 0;
        for (Element e : doc.select("a")) {
            String href = e.attr("href");
            if (href.startsWith("#")) {
                String heading = href.substring(1).trim();
                Heading h = headings.get(heading);
                if (h != null) {
                    if (h.getId().isBlank()) {
                        h.setId("j" + ++nr);
                    }
                    e.attr("href", "#" + h.getId());
                    dirty = true;
                }
            }
        }
        return dirty ? doc.html() : html;
    }

    private Map<String, Heading> getHeadings(Document doc) {
        Map<String, Heading> headings = new HashMap<>();
        for (Element e : doc.select("h2,h3,h4,h5,h6")) {
            headings.put(e.wholeOwnText().trim(), new Heading(e));
        }
        return headings;
    }

    private static class Heading {
        private final Element element;
        private String id;
        
        public Heading(Element element) {
            this.element = element;
            id = element.attr("id");
        }

        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
            element.attr("id", id);
        }
    }
}
