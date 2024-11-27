package ChatApp.MessageExchangeDomain.Handler;

import ChatApp.AutowireHelper;
import ChatApp.ConversationDomain.Entity.ChatMessage;
import ChatApp.ConversationDomain.Service.ChatMessageService;
import ChatApp.MessageExchangeDomain.Dto.ClientMessageSessionDto;
import ChatApp.MessageExchangeDomain.Dto.SocketMessageDto;
import ChatApp.UserDomain.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SocketTextHandler extends TextWebSocketHandler {

    public static MultiValuedMap<Long, ClientMessageSessionDto> sessionMap = new ArrayListValuedHashMap<>();

    public static final Logger logger = LoggerFactory.getLogger(SocketTextHandler.class);

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        AutowireHelper.autowire(this, this.chatMessageService);
        AutowireHelper.autowire(this, this.userService);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        AutowireHelper.autowire(this, this.chatMessageService);
        AutowireHelper.autowire(this, this.userService);
        String payload = message.getPayload();
        SocketMessageDto socketMessage = (new ObjectMapper()).readValue(payload, SocketMessageDto.class);
        logger.info("socketMessage :"+ socketMessage);
        logger.info("websocket session {} recieved message: {}", session.getId(), payload);

        switch (socketMessage.getContentType()) {
            case INIT_SESSION:
                ClientMessageSessionDto sessionDto = new ClientMessageSessionDto();
                sessionDto.setCreationTime(Instant.now());
                sessionDto.setSession(session);
                if (!sessionMap.containsValue(sessionDto))
                    sessionMap.put(socketMessage.getSenderId(),sessionDto);
                break;
            case TEXT:
            case IMAGE:
            case VIDEO:
            case ADD_PARTICIPANT:
            case REMOVE_PARTICIPANT:
                logger.info("saved message:"+this.chatMessageService);
                ChatMessage savedMessage = this.chatMessageService.createMessage(socketMessage);
                socketMessage.setCreationTime(savedMessage.getCreationTime());
                List<Long> recipientUserIds = this.userService.getUserIdsInConversation(socketMessage.getConversationId())
                        .stream().map(id -> id.longValue()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(recipientUserIds)) {
                    recipientUserIds.stream().filter(id -> !id.equals(savedMessage.getSender())).forEach(id -> {
                        Collection<ClientMessageSessionDto> sessionDtos = sessionMap.get(id);
                        if (CollectionUtils.isNotEmpty(sessionDtos)) {
                            sessionDtos.stream().filter(dto -> Objects.nonNull(dto.getSession()) && dto.getSession().isOpen()).forEach(dto -> {
                                try {
                                    String stringMessage = (new ObjectMapper()).writeValueAsString(socketMessage);
                                    dto.getSession().sendMessage(new TextMessage(stringMessage));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                }
                break;
        }
    }

}
