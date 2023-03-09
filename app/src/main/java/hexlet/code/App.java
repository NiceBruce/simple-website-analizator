package hexlet.code;

import hexlet.code.controllers.RootController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


public class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "4896");
        return Integer.valueOf(port);
    }

    private static void addRoutes(Javalin app) {

        app.get("/", RootController.helloWorld);
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
            config.plugins.enableDevLogging();
            JavalinThymeleaf.init(getTemplateEngine());
        });

        // Добавляем маршруты в приложение
        addRoutes(app);

        // Обработчик before запускается перед каждым запросом
        // Устанавливаем атрибут ctx для запросов
        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}