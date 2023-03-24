package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
@RequiredArgsConstructor
@Getter
public final class UrlCheck extends Model {
    @Id @GeneratedValue
    private long id;
    @NotNull
    private final int statusCode;
    private final String title;
    private final String h1;
    @Lob
    private final String description;
    @ManyToOne
    @NotNull
    private final Url url;
    @WhenCreated
    private Instant createdAt;
}
