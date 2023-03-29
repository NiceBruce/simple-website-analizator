package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class UrlCheck extends Model {
    @Id @GeneratedValue
    private long id;
    @NotNull
    private int statusCode;
    private String title;
    private String h1;
    @Lob
    private String description;
    @ManyToOne
    @NotNull
    private Url url;
    @WhenCreated
    private Instant createdAt;

    public UrlCheck(int statusCode, String title, String h1, String description, Url url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
    }
}
