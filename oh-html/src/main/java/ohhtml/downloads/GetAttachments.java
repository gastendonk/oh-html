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
    public static boolean debug = true;
    public static String attachmentsFolder = "attachments/";

    public GetAttachments(String page) {
        int o = page.indexOf("#");
        String a;
        if (o >= 0) {
            a = page.substring(0, o);
        } else {
            a = page;
        }
        if (a.endsWith(".html")) {
            a = a.substring(0, a.length() - ".html".length());
        }
        id = a;
        if (debug) {
            System.out.println("GetAttachments(" + page + ") id: " + id);
        }
    }

    @Override
    public List<Download> getDownloads(String customer) {
        if (debug) {
            System.out.println("GetAttachments.getDownloads(" + customer + "), id=" + id);
        }
        Set<String> filenames = getFilenames();
        String dir = attachmentsFolder + id;
        List<Attachment> attachments = list(dir, filenames);
        return attachments.stream().map(att -> {
            Download d = new Download();
            d.setFileInWorkspace(dir + "/" + att.getFilename());
            d.setName(att.getFilename());
            d.getKeys().addAll(att.getCategories());
            d.getCustomers().add(customer);
            d.getFiles().add(new File(dir, att.getFilename()));
            return d;
        }).collect(Collectors.toList());
    }
    
    private Set<String> getFilenames() {
        Set<String> filenames = new HashSet<>();
        File dir = new File(attachmentsFolder + id);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    filenames.add(file.getName());
                }
            }
        }
        if (debug) {
            System.out.println("GetAttachments.getFilenames: dir = " + dir.getAbsolutePath());
            System.out.println("GetAttachments.getFilenames:       " + filenames);
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
        if (debug) {
            for (Attachment att : ret) {
                System.out.println("Attachment: " + att.getFilename() + " | " + att.getCategories());
            }
        }
        return ret;
    }
}
