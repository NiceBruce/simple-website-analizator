package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class UrlController {

    public static boolean isValidUrl(String url) throws MalformedURLException {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static String getCorrectUrlName(String urlsNameFromForm) throws MalformedURLException {
        URL urls = new URL(urlsNameFromForm);
        String urlWithoutPort = urls.getProtocol() + "://" + urls.getHost();
        String urlWithPort = urlWithoutPort + ":" + urls.getPort();
        return urls.getPort() == -1 ? urlWithoutPort : urlWithPort;
    }

    public static Handler listUrl = ctx -> {
        List<Url> urls = new QUrl()
                .orderBy()
                    .id.asc()
                .findList();

        ctx.attribute("urls", urls);
        ctx.render("urls/index.html");
    };

    public static Handler showUrl = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        ctx.attribute("url", url);
        ctx.render("urls/show.html");
    };

    public static Handler createUrl = ctx -> {
        String urlsNameFromForm = ctx.formParam("url");

        if (isValidUrl(urlsNameFromForm)) {
            String urlName = getCorrectUrlName(urlsNameFromForm);

            Url url = new QUrl()
                    .name.equalTo(urlName)
                    .findOne();

            if (url != null) {
                ctx.sessionAttribute("flash", "url is already created");
                ctx.render("index.html");
            } else {
                url = new Url(urlName);
                url.save();
                ctx.sessionAttribute("flash", "url created");
                ctx.redirect("/urls");
            }

        } else {
            ctx.sessionAttribute("flash", "url is no valid");
            ctx.render("index.html");
        }
    };
}
