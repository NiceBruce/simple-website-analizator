package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }
    private static Javalin app;
    private static String baseUrl;
    private static Database test;

    private static MockWebServer server;

    public static Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

                switch (request.getPath()) {
                    case "/":
                        return new MockResponse().setResponseCode(200).setBody("Анализатор страниц");
                    case "/urls":
                        return new MockResponse().setResponseCode(200).setBody("https://leetcode.com");
                    case "/urls/1/checks":
                        return new MockResponse().setResponseCode(200).setBody("Страница успешно проверена");
                    case "/urls/2/checks":
                        return new MockResponse().setResponseCode(200).setBody("Некорректный URL-адрес");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
    };

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        test = DB.getDefault();

        server = new MockWebServer();
        server.setDispatcher(dispatcher);
    }

    @AfterAll
    public static void afterAll() throws IOException {
        server.shutdown();
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        test.script().run("/cleanTables.sql");
        test.script().run("/seed.sql");
    }
    @Nested
    class RootTest {
        @Test
        void testGreet() {
            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).contains("Анализатор страниц");
        }
    }

    @Nested
    class UrlTest {
        @Test
        void testListUrls() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();

            String content = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(content).contains("https://leetcode.com");
            assertThat(content).contains("www.thymeleaf.org:8090");
        }

        @Test
        void testShowUrl() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/1")
                    .asString();

            String content = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(content).contains("https://github.com");
        }

        @Test
        void testCreateUrl() {
            String urlName = "https://docs.gradle.org";

            HttpResponse<String> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", urlName)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();

            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("url created");
            assertThat(body).contains(urlName);

            Url actualUrl = new QUrl()
                    .name.equalTo(urlName)
                    .findOne();

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(urlName);
        }

        @Test
        void testAlreadyCreatedUrl() {
            String urlName = "https://leetcode.com";

            HttpResponse<String> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", urlName)
                    .asString();

            String body = responsePost.getBody();
            assertThat(responsePost.getStatus()).isEqualTo(200);
            assertThat(responsePost.getHeaders().containsKey("Location")).isFalse();
            assertThat(body).contains("url is already created");
        }

        @Test
        void testWrongUrl() {
            String urlName = "abraCada.bra";

            HttpResponse<String> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", urlName)
                    .asString();

            String body = responsePost.getBody();
            assertThat(responsePost.getStatus()).isEqualTo(200);
            assertThat(responsePost.getHeaders().containsKey("Location")).isFalse();
            assertThat(body).contains("url is no valid");
        }

        @Test
        void testCheckUrl() throws InterruptedException {
            String url = server.url("/urls/1/checks").toString();

            HttpResponse<String> response = Unirest
                    .post(url)
                    .asString();

            String content = response.getBody();

            RecordedRequest request = server.takeRequest();
            assertThat("/urls/1/checks").isEqualTo(request.getPath());
            assertThat("POST").isEqualTo(request.getMethod());
            assertThat(content).contains("Страница успешно проверена");
        }

        @Test
        void testCheckWrongUrl() throws InterruptedException {
            String url = server.url("/urls/2/checks").toString();

            HttpResponse<String> response = Unirest
                    .post(url)
                    .asString();

            String content = response.getBody();

            RecordedRequest request = server.takeRequest();
            assertThat("/urls/2/checks").isEqualTo(request.getPath());
            assertThat("POST").isEqualTo(request.getMethod());
            assertThat(content).contains("Некорректный URL-адрес");
        }
    }
}

