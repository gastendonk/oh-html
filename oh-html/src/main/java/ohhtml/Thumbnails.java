package ohhtml;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

public class Thumbnails {
    
    private Thumbnails() {
    }
    
    /**
     * Displays large images as thumbnails and add a link for enlarging them to it.
     * 
     * @param html -
     * @param folder book folder, without trailing "/"
     * @param id page ID
     * @param bookLink "/s/{branch}/{book-folder}/"
     * @return HTML
     */
    public static String thumbnails(String html, String folder, String id, String bookLink) {
        final int max = 1400;
        final String t = "<a href=\"{link}\" target=\"minimg\"><img src=\"{dn}\" {attr}=\"270\" border=\"0\"></a>";

        Set<String> img = findHtmlTags(html, "img", "src", i -> true, false);
        for (String dn : img) {
            File file = new File(folder + "/" + dn);
            try {
                BufferedImage picture = ImageIO.read(file);
                int width = picture.getWidth();
                int height = picture.getHeight();
                if (width > max || height > max) {
                    int o = html.indexOf("<img src=\"" + dn + "\"");
                    if (o >= 0) {
                        int oo = html.indexOf(">", o);
                        if (oo > o) {
                            String insert = t
                                    .replace("{link}", bookLink + dn)
                                    .replace("{dn}", dn)
                                    .replace("{attr}", height > width ? "height" : "width");
                            html = html.substring(0, o) + insert + html.substring(oo + 1);
                        }
                    }
                }
            } catch (Exception e) {
				System.err.println("[oh-html] Page ID: " + id + " | thumbnail: " + dn + " => " + file.getAbsolutePath()
						+ " => " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        return html;
    }
    
    /**
     * @param html the whole HTML string
     * @param tag tag name
     * @param attr attribute name
     * @return set of attribute values
     */
    public static Set<String> findHtmlTags(String html, String tag, String attr) {
        return findHtmlTags(html, tag, attr, all -> true, false);
    }
 
    /**
     * @param html the whole HTML string
     * @param tag tag name
     * @param attr attribute name
     * @param filter attribute value filter
     * @param returnOne return first occurence
     * @return set of attribute values
     */
    public static Set<String> findHtmlTags(String html, String tag, String attr, Predicate<String> filter, boolean returnOne) {
        Set<String> ret = new TreeSet<>();
        String x1 = "<" + tag;
        String x2 = attr + "=\"";
        int o = html.indexOf(x1);
        while (o >= 0) {
            o += x1.length();
            int oo = html.indexOf(">", o); // end of tag
            if (oo >= 0) {
                int z = html.indexOf(x2, o);
                if (z >= o && z < oo) {
                    z += x2.length();
                    int zz = html.indexOf("\"", z); // end of attr val
                    if (zz >= z && zz < oo) {
                        String val = html.substring(z, zz);
                        if (filter.test(val)) {
                            ret.add(val);
                            if (returnOne) {
                                return ret;
                            }
                        }
                    }
                }
            }
            o = html.indexOf(x1, o);
        }
        return ret;
    }
}
