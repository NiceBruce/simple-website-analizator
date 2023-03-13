package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import java.time.Instant;
import java.util.List;


@Entity
public final class Url extends Model {

    @Id @GeneratedValue
    private long id;

    @NotNull
    private String name;

    @WhenCreated
    private Instant createdAt;
    @OneToMany(cascade = CascadeType.ALL)
    private List<UrlCheck> urlCheck;

    public Url(String name) {
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<UrlCheck> getUrlCheck() {
        return urlCheck;
    }

    public void setUrlCheck(List<UrlCheck> urlCheck) {
        this.urlCheck = urlCheck;
    }
}
