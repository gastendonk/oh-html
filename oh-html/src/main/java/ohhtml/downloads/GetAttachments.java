package ohhtml.downloads;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ohhtml.base.FileService;
import ohhtml.downloads.DownloadsHTML.GetDownloads;

public class GetAttachments implements GetDownloads {
    private final String id;

    public GetAttachments(String page) {
        int o = page.indexOf("#");
        if (o >= 0) {
            id = page.substring(0, o);
        } else {
            id = page;
        }
    }

    @Override
    public List<Download> getDownloads(String customer) {
        Set<String> filenames = getFilenames();
        String dir = "attachments/" + id;
        List<Attachment> attachments = list(dir, filenames);
        return attachments.stream().map(att -> {
            Download d = new Download();
            d.setFileInWorkspace(dir + "/" + att.getFilename());
            d.setName(att.getFilename());
            d.getKeys().addAll(att.getCategories());
            d.getCustomers().add(customer);
            return d;
        }).collect(Collectors.toList());
    }
    
    private Set<String> getFilenames() {
        Set<String> filenames = new HashSet<>();
        File dir = new File("attachments/" + id);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    filenames.add(file.getName());
                }
            }
        }
        return filenames;
    }

    public static List<Attachment> list(String dir, Set<String> filenames) {
        Map<String, Attachment> map = new HashMap<>();
        if (filenames != null) {
            // first collect attachment files
            for (String dn : filenames) {
                if (!dn.endsWith(".cat")) { // attachment file
                    map.put(dn, new Attachment(dn));
                }
            }
            // then add categories to the attachment files
            for (String dn : filenames) {
                if (dn.endsWith(".cat")) { // categories file
                    String content = FileService.loadTextFile(new File(dir, dn));
                    dn = dn.substring(0, dn.length() - ".cat".length());
                    Attachment att = map.get(dn);
                    if (att != null) {
                        att.fromString(content);
                    } // else: categories without attachment -> ignore it
                }
            }
        }
        List<Attachment> ret = new ArrayList<>(map.values());
        ret.sort((a, b) -> a.getFilename().compareToIgnoreCase(b.getFilename()));
        return ret;
    }
}
