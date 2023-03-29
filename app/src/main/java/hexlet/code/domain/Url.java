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
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import java.time.Instant;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class Url extends Model {
    @Id @GeneratedValue
    private long id;

    @NotNull
    private String name;

    @WhenCreated
    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    private List<UrlCheck> urlChecks;


    public Url(String urlName) {
        this.name = urlName;
    }
}
