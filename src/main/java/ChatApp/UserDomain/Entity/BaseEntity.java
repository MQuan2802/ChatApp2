package ChatApp.UserDomain.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.persistence.*;
import java.util.Calendar;

@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    @Getter
    protected Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time")
    @Getter
    @JsonProperty("creationTime")
    protected Calendar creationTime;

    @PrePersist
    protected void onPrePersist() {
        this.creationTime = Calendar.getInstance();
    }
}
