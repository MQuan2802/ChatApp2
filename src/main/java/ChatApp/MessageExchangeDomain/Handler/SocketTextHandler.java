package ChatApp.MessageExchangeDomain.Handler;

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

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class SocketTextHandler extends TextWebSocketHandler {

    public static MultiValuedMap<Long, ClientMessageSessionDto> sessionMap = new ArrayListValuedHashMap<>();

    public static final Logger logger = LoggerFactory.getLogger(SocketImageHandler.class);

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        String payload = message.getPayload();
        SocketMessageDto socketMessage = (new ObjectMapper()).readValue(payload, SocketMessageDto.class);
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
            case ADD_PARTICIPANT:
            case REMOVE_PARTICIPANT:
                socketMessage.setCreationTime(this.chatMessageService.createMessage(socketMessage).getCreationTime());
                List<Long> recipientUserIds = this.userService.getUserIdsInConversation(socketMessage.getConversationId());
                if (CollectionUtils.isNotEmpty(recipientUserIds)) {
                    recipientUserIds.forEach(id -> {
                        Collection<ClientMessageSessionDto> sessionDtos = sessionMap.get(id);
                        if (CollectionUtils.isNotEmpty(sessionDtos)) {
                            sessionDtos.forEach(dto -> {
                                try {
                                    String savedMessage = (new ObjectMapper()).writeValueAsString(socketMessage);
                                    dto.getSession().sendMessage(new TextMessage(savedMessage));
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
