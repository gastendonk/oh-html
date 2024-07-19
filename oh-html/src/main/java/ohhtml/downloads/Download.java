package ohhtml.downloads;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a file that is downloadable in the online help
 */
public class Download {
    private String fileInWorkspace; // last path segment can contain wildcards
    private final List<File> files = new ArrayList<>();
    private String name;
    private final Set<String> keys = new TreeSet<>();
    private Set<String> customers = new TreeSet<>();

    public String getFileInWorkspace() {
        return fileInWorkspace;
    }

    public void setFileInWorkspace(String fileInWorkspace) {
        this.fileInWorkspace = fileInWorkspace;
    }
    
    public String getPath() {
        String a = fileInWorkspace.replace("\\", "/");
        int o = a.lastIndexOf("/");
        return o >= 0 ? a.substring(0, o) : "";
    }

    public String getFilename() {
        String a = fileInWorkspace.replace("\\", "/");
        return a.substring(a.lastIndexOf("/") + 1);
    }

    public List<File> getFiles() {
        return files;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getKeys() {
        return keys;
    }

    public Set<String> getCustomers() {
        return customers;
    }
}
