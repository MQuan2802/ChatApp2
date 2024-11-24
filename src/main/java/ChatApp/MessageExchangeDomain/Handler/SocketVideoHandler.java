package ChatApp.MessageExchangeDomain.Handler;

import ChatApp.ConversationDomain.Entity.ChatMessage;
import ChatApp.ConversationDomain.Service.ChatMessageService;
import ChatApp.FileExchangeDomain.Service.S3Service;
import ChatApp.MessageExchangeDomain.Dto.ClientMessageSessionDto;
import ChatApp.MessageExchangeDomain.Dto.SocketMessageDto;
import ChatApp.UserDomain.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SocketVideoHandler extends BinaryWebSocketHandler {
    public static final Logger logger = LoggerFactory.getLogger(SocketImageHandler.class);

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        logger.info("[{}] New Binary video received", SocketImageHandler.class.getName());

        ByteBuffer payload = message.getPayload().asReadOnlyBuffer();
        Map<String, String> messageMetaData = this.processVideo(payload);
        List<Long> recipientUserIds = this.userService.getUserIdsInConversation(Long.valueOf(messageMetaData.get("conversationId")));
        SocketMessageDto socketMessageDto = SocketMessageDto.builder()
                .content(messageMetaData.get("content"))
                .conversationId(Long.valueOf(messageMetaData.get("conversationId")))
                .contentType(ChatMessage.ContentType.VIDEO)
                .senderParticipantId(Long.valueOf(messageMetaData.get("senderParticipantId")))
                .build();

        this.chatMessageService.createMessage(socketMessageDto);
        if (CollectionUtils.isNotEmpty(recipientUserIds)) {
            recipientUserIds.forEach(id -> {
                Collection<ClientMessageSessionDto> sessionDtos = SocketTextHandler.sessionMap.get(id);
                if (CollectionUtils.isNotEmpty(sessionDtos)) {
                    sessionDtos.forEach(dto -> {
                        try {
                            String savedMessage = (new ObjectMapper()).writeValueAsString(socketMessageDto);
                            dto.getSession().sendMessage(new TextMessage(savedMessage));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
    }

    @SneakyThrows
    public Map<String, String> processVideo(ByteBuffer payload) {
        int payloadLength = payload.get() & 0xFF; // Convert to unsigned byte

        // Check if payload length indicates extended length
        if (payloadLength == 126) {
            payloadLength = payload.getShort() & 0xFFFF; // Convert to unsigned short
        } else if (payloadLength == 127) {
            payloadLength = (int) payload.getLong();
        }

        // Read the file name bytes
        byte[] fileNameBytes = new byte[payloadLength];
        payload.get(fileNameBytes);
        String messageMetadata = new String(fileNameBytes, StandardCharsets.UTF_8);
        String fileName = messageMetadata.split("_")[0];
        String conversationId = messageMetadata.split("_")[1];
        String senderId = messageMetadata.split("_")[2];
        String senderParticipantId = messageMetadata.split("_")[3];

        //Read file content
        byte[] fileContent = new byte[payload.remaining()];
        payload.get(fileContent);

        //upload to s3
        String vidUploadName = String.format("%s_%s", fileName, UUID.randomUUID().toString());
        File vidFile = new File(vidUploadName);
        FileUtils.writeByteArrayToFile(vidFile, fileContent);
        String s3Link = this.s3Service.uploadFile(vidFile, vidUploadName, S3Service.FileType.VIDEO);

        Map<String, String> result = new HashMap<>();
        result.put("content", s3Link);
        result.put("conversationId", conversationId);
        result.put("senderParticipantId", senderParticipantId);
        result.put("senderId", senderId);
        return result;
    }
}
