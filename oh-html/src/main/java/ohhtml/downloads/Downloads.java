package ohhtml.downloads;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import ohhtml.base.FileService;

public class Downloads {

    public List<Download> parseDownloads(File downloadsFile) {
        return parseDownloads(FileService.loadTextFile(downloadsFile));
    }

    public List<Download> parseDownloads(String content) {
        List<Download> ret = new ArrayList<>();
        Set<String> customers = new TreeSet<>();
        for (String line : content.replace("\r\n", "\n").split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("#")) { // ignore
            } else if (line.startsWith("customers=")) {
                makeCustomersSet(line, customers);
            } else if (line.startsWith("/") && !customers.isEmpty()) { // file or file pattern
                ret.add(createDownload(line, customers));
            } else {
                System.err.println("Unsupported line is ignored: " + line);
            }
        }
        return ret;
    }

    private void makeCustomersSet(String line, Set<String> customers) {
        String[] c = line.substring("customers=".length()).trim().split(",");
        customers.clear();
        for (String cu : c) {
            if (!cu.isBlank()) {
                customers.add(cu.trim());
            }
        }
    }

    private Download createDownload(String line, Set<String> customers) {
        Download d = new Download();
        d.getCustomers().addAll(customers);
        String[] w = line.split("\\|");
        d.setFileInWorkspace(w[0].trim().substring(1));
        for (int i = 1; i < w.length; i++) {
            if (!w[i].isBlank()) {
                d.getKeys().add(w[i].trim());
            }
        }
        return d;
    }
    
    public void resolve(List<Download> downloads, File root) {
        for (Download d : downloads) {
            String dn = d.getFilename();
            if (dn.contains("*") || dn.contains("?")) {
                FileFilter filter = WildcardFileFilter.builder().setWildcards(dn).setIoCase(IOCase.INSENSITIVE).get();
                File dir = new File(root, d.getPath());
                File[] files = dir.listFiles(filter);
                if (files == null) {
                    throw new RuntimeException("Error accessing download folder: " + dir.getAbsolutePath());
                } else {
                    for (File f : files) {
                        d.getFiles().add(f);
                    }
                }
            } else {
                File file = new File(root, d.getFileInWorkspace());;
                if (!file.isFile()) {
                    throw new RuntimeException("Download file does not exist: " + file.getAbsolutePath());
                }
                d.getFiles().add(file);
            }
        }
    }

    public void copyDownloadFiles(List<Download> downloads, File targetDir) {
        FileService.deleteFolder(targetDir);
        for (Download d : downloads) {
            for (File src : d.getFiles()) {
                try {
                    File trg = new File(targetDir, d.getPath() + "/" + src.getName());
                    if (!trg.isFile()) {
                        trg.getParentFile().mkdirs();
                        Files.copy(src.toPath(), trg.toPath());
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error copying file " + src.getAbsolutePath() + " to " + targetDir.getAbsolutePath(), e);
                }
            }
        }
    }
}
