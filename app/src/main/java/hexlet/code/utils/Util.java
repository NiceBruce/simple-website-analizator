package hexlet.code.utils;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import io.ebean.DB;
import io.ebean.Database;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.ListIterator;


public class Util {

    public static final Database DATABASE = DB.getDefault();

    public static Url getUrl(long urlId) {
        return DATABASE.find(Url.class, urlId);
    }

    public static List<Url> getUrls() {
        return DATABASE.find(Url.class).findList();
    }

    public static List<UrlCheck> getUrlChecks(Url url) {

        List<UrlCheck> urlChecks = url.getUrlChecks();
        List<UrlCheck> reverseUrlChecks = new ArrayList<>();
        ListIterator<UrlCheck> listIterator = urlChecks.listIterator(urlChecks.size());

        while (listIterator.hasPrevious()) {
            UrlCheck urlCheckUnit = listIterator.previous();
            reverseUrlChecks.add(urlCheckUnit);
        }

        return reverseUrlChecks;
    }

    public static boolean isExistUrl(String urlName) {
        return DATABASE.find(Url.class)
                .where().
                eq("name", urlName)
                .findOne() != null;
    }

    public static Map<Long, List<Object>> getUrlsWithCheck(List<Url> urlsFromBD) {
        Map<Long, List<Object>> urls = new HashMap<>();

        for (Url url : urlsFromBD) {
            List<UrlCheck> urlChecks = url.getUrlChecks();

            urls.put(url.getId(), List.of(url.getName(),
                    (urlChecks.isEmpty()) ? ""
                            : urlChecks.get(urlChecks.size() - 1).getCreatedAt(),
                    (urlChecks.isEmpty()) ? ""
                            : urlChecks.get(urlChecks.size() - 1).getStatusCode()));
        }
        return urls;
    }

    public static String getCorrectUrlName(String urlsNameFromForm) throws MalformedURLException {

        try {
            URL urls = new URL(urlsNameFromForm);
            String urlWithoutPort = urls.getProtocol() + "://" + urls.getHost();
            String urlWithPort = urlWithoutPort + ":" + urls.getPort();
            return urls.getPort() == -1 ? urlWithoutPort : urlWithPort;

        } catch (MalformedURLException exceptionMessage) {
            Logger logger = LoggerFactory.getLogger(Url.class);
            logger.error("ВНИМАНИЕ! ВВЕДЕН НЕПРАВИЛЬНЫЙ URL", new Exception(exceptionMessage));
            return "wrong url";
        }
    }

    public static HttpResponse<String> getResponse(Url url) throws UnirestException {
        HttpResponse<String> response = Unirest
                .get(url.getName())
                .asString();

        return response;
    }

    public static Document getHtmlDocument(HttpResponse<String> response) {
        String body = response.getBody();

        return Jsoup.parse(body);
    }

    public static UrlCheck parseHTML(Url url) {

        HttpResponse<String> response = getResponse(url);
        int statusCode = response.getStatus();

        Document doc = getHtmlDocument(response);

        String title = (doc.title() == null) ? "" : doc.title();

        String description = (doc.select("meta[name=description]").isEmpty()) ? ""
                : doc.select("meta[name=description]").get(0)
                        .attr("content");

        String h1 = (doc.select("h1").first() == null) ? ""
                : doc.select("h1").first().text();

        return new UrlCheck(statusCode, title, h1, description, url);
    }
}
