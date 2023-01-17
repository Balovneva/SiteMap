import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static String siteUrl = "https://lenta.ru/";;
    private static String doc = "src/main/resources/sitemap.txt";

    public static void main(String[] args) {
        URL rootUrl = new URL(siteUrl);
        new ForkJoinPool().invoke(new UrlConnections(rootUrl, rootUrl));
        writeUrl(rootUrl, doc);
    }

    public static void writeUrl(URL url, String doc) {
        int depth = url.getDepth();
        String tabs = String.join("", Collections.nCopies(depth, "\t"));
        StringBuilder result = new StringBuilder(tabs + url.getUrl() + "\n");
        appendStringInFile(doc, result.toString());
        url.getSublinks().forEach(child -> writeUrl(child, doc));
    }

    private static void appendStringInFile(String fileName, String line) {
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(new File(fileName), true);
            outputStream.write(line.getBytes(), 0, line.length());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
