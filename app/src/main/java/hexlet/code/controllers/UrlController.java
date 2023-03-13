package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.javalin.http.Handler;
import kong.unirest.UnirestException;

import java.util.List;

import static hexlet.code.utils.Util.getUrlsWithCheck;
import static hexlet.code.utils.Util.isValidUrl;
import static hexlet.code.utils.Util.getCorrectUrlName;
import static hexlet.code.utils.Util.parseHTML;


public final class UrlController {


    public static Handler listUrl = ctx -> {

        List<Url> urlsFromBD = new QUrl()
                .orderBy()
                    .id.asc()
                .findList();


        ctx.attribute("urls", getUrlsWithCheck(urlsFromBD));
        ctx.render("urls/index.html");
    };

    public static Handler showUrl = ctx -> {

        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        List<UrlCheck> urlChecks = new QUrlCheck()
                .url.id.equalTo(id)
                .orderBy()
                .id.desc()
                .findList();

        ctx.attribute("url", url);
        ctx.attribute("urlChecks", urlChecks);
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
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.render("index.html");
            } else {
                url = new Url(urlName);
                url.save();
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flash-type", "success");
                ctx.redirect("/urls");
            }

        } else {
            ctx.sessionAttribute("flash", "url is no valid");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("index.html");
        }
    };

    public static Handler checkUrl = ctx -> {

        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        try {
            UrlCheck check = parseHTML(url);
            check.save();

            List<UrlCheck> urlChecks = new QUrlCheck()
                    .url.id.equalTo(id)
                    .orderBy()
                    .id.desc()
                    .findList();

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.attribute("url", url);
            ctx.attribute("urlChecks", urlChecks);
            ctx.redirect("/urls/" + id);
//            ctx.render("urls/show.html");

        } catch (UnirestException e) {
            ctx.attribute("url", url);
            ctx.sessionAttribute("flash", "Некорректный URL-адрес");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("urls/show.html");
        }
    };
}
