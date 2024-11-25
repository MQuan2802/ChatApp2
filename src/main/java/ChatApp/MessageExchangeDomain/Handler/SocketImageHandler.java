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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SocketImageHandler extends BinaryWebSocketHandler {

    public static final Logger logger = LoggerFactory.getLogger(SocketImageHandler.class);

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatMessageService chatMessageService;

//    @Override
//    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
//        System.out.println("New Binary ConversationDomain Received");
//        byte[] imageData = message.getPayload().array();
//        String filePath = "/Users/quannguyen/Desktop/softwareArchitecture/webSocketImageHandler.png";
//        File imageFile = new File(filePath);
//        FileOutputStream fos = new FileOutputStream(imageFile);
//        fos.write(imageData);
//        fos.close();
//
//        logger.info("Finished Storing image");
//    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        logger.info("[{}] New Binary img received", SocketImageHandler.class.getName());

        ByteBuffer payload = message.getPayload().asReadOnlyBuffer();
        Map<String, String> messageMetaData = this.processImage(payload);
        List<Long> recipientUserIds = this.userService.getUserIdsInConversation(Long.valueOf(messageMetaData.get("conversationId")))
                .stream()
                .map(id -> id.longValue())
                .collect(Collectors.toList());
        SocketMessageDto socketMessageDto = SocketMessageDto.builder()
                .content(messageMetaData.get("content"))
                .conversationId(Long.valueOf(messageMetaData.get("conversationId")))
                .contentType(ChatMessage.ContentType.IMAGE)
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
    public Map<String, String> processImage(ByteBuffer payload) {
//        byte opCode = payload.get();
//        // Check if the message is binary (0x82 is binary message)
//        if ((opCode & 0x0F) == 0x02) {
        // Get the payload length
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
        String imgUploadName = String.format("%s_%s", fileName, UUID.randomUUID().toString());
        File imgFile = new File(imgUploadName);
        FileUtils.writeByteArrayToFile(imgFile, fileContent);
        String s3Link = this.s3Service.uploadFile(imgFile, imgUploadName, S3Service.FileType.IMAGE);

        Map<String, String> result = new HashMap<>();
        result.put("content", s3Link);
        result.put("conversationId", conversationId);
        result.put("senderParticipantId", senderParticipantId);
        result.put("senderId", senderId);
        return result;
    }
}
