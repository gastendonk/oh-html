package ohhtml.toc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

public class LocalAnchorsTest {

    @Test
    public void test() {
        String html = "<html><body><h1 id=\"t1\">Hello</h1><p>Bla <a href=\"#target 1\">Anchor link</a> ...</p><h2 id=\"t2\">target 1</h2><p>blub ...</p></body></html>";
        
        String html2 = new LocalAnchors().transform(html);
        
        Document doc = Jsoup.parse(html2);
        check("t2", 2, "Anchor link", doc);
    }

    @Test
    public void missingIdAttr() {
        String html = "<html><body><h1>Hello</h1><p>Bla <a href=\"#target 1\">Anchor link</a> ...<a href=\"#Hello\">hello</a></p><h2>target 1</h2><p>blub ...</p></body></html>";
        
        String html2 = new LocalAnchors().transform(html);
        
        Document doc = Jsoup.parse(html2);
        check("j2", 1, "hello", doc);
        check("j1", 2, "Anchor link", doc);
    }

    @Test
    public void targetUsedTwoTimes() {
        String html = "<html><body><h1>Hello</h1><p>Bla <a href=\"#target 1\">Anchor link</a> ...<a href=\"#target 1\">hello</a></p><h2>target 1</h2><p>blub ...</p></body></html>";
        
        String html2 = new LocalAnchors().transform(html);
        
        Assert.assertTrue(html2, html2.contains("<p>Bla <a href=\"#j1\">Anchor link</a> ...<a href=\"#j1\">hello</a></p>"));
        Assert.assertTrue(html2, html2.contains("<h2 id=\"j1\">target 1</h2>"));
    }

    @Test
    public void withoutHref() {
        String html = "<html><body><h1>Hello</h1><p>Bla <a>without h ref</a>...</p></body></html>";
        Assert.assertEquals(html, new LocalAnchors().transform(html));
    }

    @Test
    public void emptyBody() {
        String html = "<html><body></body></html>";
        Assert.assertEquals(html, new LocalAnchors().transform(html));
    }

    @Test
    public void empty() {
        Assert.assertEquals("", new LocalAnchors().transform(""));
    }

    @Test
    public void isNull() {
        Assert.assertNull(new LocalAnchors().transform(null));
    }

    private void check(String id, int headingLevel, String text, Document doc) {
        Elements e = doc.selectXpath("//a[@href='#" + id + "']");
        Assert.assertEquals("Test 1 failed", 1, e.size());
        Assert.assertEquals("Test 2 failed", text, e.get(0).text());

        Elements ee = doc.selectXpath("//h" + headingLevel + "[@id='" + id + "']");
        Assert.assertEquals("Test 3 failed", 1, ee.size());
    }
}
