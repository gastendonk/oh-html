package ohhtml.downloads;

import java.util.Objects;

public class DownloadOccurrence {
    private String text;
    private boolean one;
    private String key = "";

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isOne() {
        return one;
    }

    public void setOne(boolean one) {
        this.one = one;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, one);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DownloadOccurrence other = (DownloadOccurrence) obj;
        return one == other.one && Objects.equals(key, other.key);
    }
}
