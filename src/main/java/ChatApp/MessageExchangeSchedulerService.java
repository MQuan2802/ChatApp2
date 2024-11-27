package ChatApp;

import ChatApp.MessageExchangeDomain.Dto.ClientMessageSessionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static ChatApp.MessageExchangeDomain.Handler.SocketTextHandler.sessionMap;

@Service
public class MessageExchangeSchedulerService {

    public static final Logger logger = LoggerFactory.getLogger(MessageExchangeSchedulerService.class);

    @Scheduled(fixedRate = 30000)
    public void removeExpiredMessageSession() {
        Iterator<Map.Entry<Long, ClientMessageSessionDto>> iterator = sessionMap.entries().iterator();
        while(iterator.hasNext()) {
            Map.Entry<Long, ClientMessageSessionDto> pair = iterator.next();
            if (Duration.between(pair.getValue().getCreationTime(), Instant.now()).toMinutes() > 120) {
                iterator.remove();
            }
            if (Objects.isNull(pair.getValue().getSession()) || !pair.getValue().getSession().isOpen())
                iterator.remove();
        }
        logger.info("Clear expired message socket session is completed.");
    }
}