import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;

import static java.lang.Thread.sleep;

public class UrlConnections extends RecursiveAction {
    private URL url;
    private final URL rootUrl;
    private static CopyOnWriteArraySet<String> allLinks = new CopyOnWriteArraySet<>();

    public UrlConnections(URL url, URL rootUrl) {
        this.url = url;
        this.rootUrl = rootUrl;
    }

    @Override
    protected void compute() {
        Set<UrlConnections> taskList = new HashSet<>();
        try {
            long time = Math.round((Math.random() * (151 - 100) + 100));
            sleep(time);
            Document doc = Jsoup.connect(url.getUrl()).get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String absUrl = link.attr("abs:href");
                if (isCorrected(absUrl)) {
                    url.addSublink(new URL(absUrl));
                    allLinks.add(absUrl);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (URL sublink : url.getSublinks()) {
            UrlConnections task = new UrlConnections(sublink, rootUrl);
            task.fork();
            taskList.add(task);
        }

        for (UrlConnections task : taskList) {
            task.join();
        }
    }

    private boolean isCorrected(String url) {
        return (url.startsWith(rootUrl.getUrl())
                && !allLinks.contains(url)
                && url.matches(".+/"));
    }
}
