package hexlet.code;

import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;


public class App {

    public static final int PORT = 4896;
    public static final String DEVELOPMENT_MODE = "development";
    public static final String PRODUCTION_MODE = "production";

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", DEVELOPMENT_MODE);
    }



    private static void addRoutes(Javalin app) {

        app.get("/", RootController.greet);

        app.routes(() -> {
            path("urls", () -> {
                get(UrlController.listUrl);
                post(UrlController.createUrl);
                get("{id}", UrlController.showUrl);
                post("{id}/checks", UrlController.checkUrl);
            });
        });
    }

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());
        ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setPrefix("/templates/");
        templateEngine.addTemplateResolver(classLoaderTemplateResolver);

        return templateEngine;
    }
    public static Javalin getApp() {

        Javalin app = Javalin.create(config -> {
            if (!getMode().equals(PRODUCTION_MODE)) {
                config.plugins.enableDevLogging();
            }

            JavalinThymeleaf.init(getTemplateEngine());
        });

        addRoutes(app);

        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });

        return app;
    }


    public static void main(String[] args) {

        Javalin app = getApp();
        app.start(PORT);
    }
}
