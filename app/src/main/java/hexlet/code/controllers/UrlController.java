package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import io.javalin.http.Handler;
import kong.unirest.UnirestException;

import java.util.List;

import static hexlet.code.utils.Util.getUrl;
import static hexlet.code.utils.Util.getUrls;
import static hexlet.code.utils.Util.getUrlsWithCheck;
import static hexlet.code.utils.Util.getUrlChecks;
import static hexlet.code.utils.Util.getCorrectUrlName;
import static hexlet.code.utils.Util.ifExistsUrl;
import static hexlet.code.utils.Util.parseHTML;

public final class UrlController {
    public static Handler listUrl = ctx -> {
        ctx.attribute("urls", getUrlsWithCheck(getUrls()));
        ctx.render("urls/index.html");
    };

    public static Handler showUrl = ctx -> {

        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = getUrl(id);

        ctx.attribute("url", url);
        ctx.attribute("urlChecks", getUrlChecks(url));
        ctx.render("urls/show.html");
    };

    public static Handler createUrl = ctx -> {

        String urlName = getCorrectUrlName(ctx.formParam("url"));

        if (urlName == null) {
            ctx.sessionAttribute("flash", "Введен неправильный URL-адрес");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("index.html");
            return;
        }

        if (ifExistsUrl(urlName)) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("index.html");
            return;
        }

        Url url = new Url(urlName);
        url.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");
    };

    public static Handler checkUrl = ctx -> {

        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Url url = getUrl(id);

        try {
            UrlCheck check = parseHTML(url);
            check.save();

            List<UrlCheck> urlChecks = getUrlChecks(url);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.attribute("url", url);
            ctx.attribute("urlChecks", urlChecks);
            ctx.redirect("/urls/" + id);

        } catch (UnirestException e) {
            ctx.attribute("url", url);
            ctx.sessionAttribute("flash", "Некорректный URL-адрес");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("urls/show.html");
        }
    };
}
