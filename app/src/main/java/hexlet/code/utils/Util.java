package hexlet.code.utils;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
    public static boolean isValidUrl(String url) throws MalformedURLException {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static Map<Long, List<Object>> getUrlsWithCheck(List<Url> urlsFromBD) {
        Map<Long, List<Object>> urls = new HashMap<>();

        for (Url url : urlsFromBD) {
            urls.put(url.getId(), List.of(url.getName(),
                    (url.getUrlCheck().isEmpty()) ? ""
                            : url.getUrlCheck().get(url.getUrlCheck().size() - 1).getCreatedAt(),
                    (url.getUrlCheck().isEmpty()) ? ""
                            : url.getUrlCheck().get(url.getUrlCheck().size() - 1).getStatusCode()));
        }

        return urls;
    }

    public static String getCorrectUrlName(String urlsNameFromForm) throws MalformedURLException {
        URL urls = new URL(urlsNameFromForm);

        String urlWithoutPort = urls.getProtocol() + "://" + urls.getHost();
        String urlWithPort = urlWithoutPort + ":" + urls.getPort();

        return urls.getPort() == -1 ? urlWithoutPort : urlWithPort;
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
