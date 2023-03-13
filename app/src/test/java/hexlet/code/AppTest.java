package hexlet.code;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import io.javalin.Javalin;
import io.ebean.DB;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.Database;

public final class AppTest {

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }
    private static Javalin app;
    private static String baseUrl;

    private static Database testDB;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        testDB = DB.getDefault();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        testDB.script().run("/truncate.sql");
        testDB.script().run("/seed.sql");
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

//        @Test
//        void testAlreadyCreatedUrl() {
//            String urlName = "https://leetcode.com";
//
//            HttpResponse<String> responsePost = Unirest
//                    .post(baseUrl + "/urls")
//                    .field("url", urlName)
//                    .asString();
//
//            String body = responsePost.getBody();
//            assertThat(responsePost.getStatus()).isEqualTo(200);
//            assertThat(responsePost.getHeaders().containsKey("Location")).isFalse();
//            assertThat(body).contains("url is already created");
//        }
//
//        @Test
//        void testWrongUrl() {
//            String urlName = "abraCada.bra";
//
//            HttpResponse<String> responsePost = Unirest
//                    .post(baseUrl + "/urls")
//                    .field("url", urlName)
//                    .asString();
//
//            String body = responsePost.getBody();
//            assertThat(responsePost.getStatus()).isEqualTo(200);
//            assertThat(responsePost.getHeaders().containsKey("Location")).isFalse();
//            assertThat(body).contains("url is no valid");
//        }
    }

}
