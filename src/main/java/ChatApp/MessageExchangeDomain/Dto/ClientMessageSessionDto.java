package ChatApp.MessageExchangeDomain.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientMessageSessionDto {
    private Instant creationTime;
    private WebSocketSession session;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (Objects.isNull(o) || !Objects.equals(this.getClass(), o.getClass()))
            return false;
        WebSocketSession webSocketSession = (WebSocketSession) o;
        return StringUtils.equals(this.session.getId(), webSocketSession.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.session, this.creationTime);
    }
}