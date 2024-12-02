package ChatApp.UserDomain.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Setter
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
