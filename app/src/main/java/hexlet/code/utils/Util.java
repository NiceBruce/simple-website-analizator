package hexlet.code.utils;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
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
    public static final Logger LOGGER = LoggerFactory.getLogger(Url.class);
    public static Url getUrl(long urlId) {
        return new QUrl()
                .id.equalTo(urlId)
                .findOne();
    }

    public static List<Url> getUrls() {
        return new QUrl()
                .orderBy()
                .id.asc()
                .findList();
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
    public static boolean ifExistsUrl(String urlName) {
        return new QUrl()
                .name.equalTo(urlName)
                .findOne() != null;
    }
    public static Map<Long, List<Object>> getUrlsWithCheck(List<Url> urlsFromBD) {
        Map<Long, List<Object>> urls = new HashMap<>();

        for (Url url : urlsFromBD) {
            List<UrlCheck> urlChecks = url.getUrlChecks();

            String urlCreatedDate = urlChecks.isEmpty()
                    ? "" : urlChecks.get(urlChecks.size() - 1).getCreatedAt().toString();
            String urlStatusCode = urlChecks.isEmpty()
                    ? "" : Integer.toString(urlChecks.get(urlChecks.size() - 1).getStatusCode());

            urls.put(url.getId(), List.of(url.getName(), urlCreatedDate, urlStatusCode));
        }

        return urls;
    }
    public static String getCorrectUrlName(String urlsNameFromForm) throws MalformedURLException {

        try {
            URL urls = new URL(urlsNameFromForm);
            return urls.getProtocol() + "://" + urls.getAuthority();

        } catch (MalformedURLException exceptionMessage) {
            LOGGER.error("ВНИМАНИЕ! ВВЕДЕН НЕПРАВИЛЬНЫЙ URL", new Exception(exceptionMessage));
            return null;
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
    public static String getHtmlH1Tag(Document doc) {
        return (doc.select("h1").first() == null) ? ""
                : doc.select("h1").first().text();
    }
    public static String getHtmlTitleTag(Document doc) {
        return (doc.title() == null) ? "" : doc.title();
    }
    public static String getHtmlMetaDescription(Document doc) {
        return (doc.select("meta[name=description]").isEmpty()) ? ""
                : doc.select("meta[name=description]").get(0)
                .attr("content");
    }
    public static UrlCheck parseHTML(Url url) {

        HttpResponse<String> response = getResponse(url);
        int statusCode = response.getStatus();

        Document doc = getHtmlDocument(response);

        String title = getHtmlTitleTag(doc);
        String h1 = getHtmlH1Tag(doc);
        String description = getHtmlMetaDescription(doc);

        return new UrlCheck(statusCode, title, h1, description, url);
    }
}
