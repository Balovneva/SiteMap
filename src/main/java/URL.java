import java.util.concurrent.CopyOnWriteArraySet;

public class URL {
    private String url;
    private URL rootUrl;
    private volatile int depth;
    private volatile CopyOnWriteArraySet<URL> sublinks;

    public URL(String url) {
        this.url = url;
        sublinks = new CopyOnWriteArraySet<>();
        depth = 0;
        rootUrl = null;
    }

    public void addSublink(URL sublink) {
        if (!sublinks.contains(sublink) && sublink.getUrl().startsWith(url)) {
            this.sublinks.add(sublink);
            sublink.setRootUrl(this);
        }
    }

    private void setRootUrl(URL rootUrl) {
        synchronized (this) {
            this.rootUrl = rootUrl;
            this.depth = setDepth();
        }
    }

    public int getDepth() {
        return depth;
    }

    private int setDepth() {
        if (rootUrl == null) {
            return  0;
        }
        return 1 + rootUrl.getDepth();
    }

    public CopyOnWriteArraySet<URL> getSublinks() {
        return sublinks;
    }

    public String getUrl() {
        return url;
    }
}
