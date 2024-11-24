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
import java.util.UUID;


public class SocketDocumentHandler extends BinaryWebSocketHandler {

    public static final Logger logger = LoggerFactory.getLogger(SocketDocumentHandler.class);

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private S3Service s3Service;

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        logger.info("[{}] New Binary img received", SocketImageHandler.class.getName());

        ByteBuffer payload = message.getPayload().asReadOnlyBuffer();
        Map<String, String> messageMetaData = this.processDocument(payload);
        List<Long> recipientUserIds = this.userService.getUserIdsInConversation(Long.valueOf(messageMetaData.get("conversationId")));
        SocketMessageDto socketMessageDto = SocketMessageDto.builder()
                .content(messageMetaData.get("content"))
                .conversationId(Long.valueOf(messageMetaData.get("conversationId")))
                .contentType(ChatMessage.ContentType.DOCUMENT)
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
    public Map<String, String> processDocument(ByteBuffer payload) {
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
        String docUploadName = String.format("%s_%s", fileName, UUID.randomUUID().toString());
        File docFile = new File(docUploadName);
        FileUtils.writeByteArrayToFile(docFile, fileContent);
        String s3Link = this.s3Service.uploadFile(docFile, docUploadName, S3Service.FileType.DOCUMENT);

        Map<String, String> result = new HashMap<>();
        result.put("content", s3Link);
        result.put("conversationId", conversationId);
        result.put("senderParticipantId", senderParticipantId);
        result.put("senderId", senderId);
        return result;
    }

//    @Override
//    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
//        logger.info("New Binary ConversationDomain of Document Received");
//
//        ByteBuffer payload = message.getPayload().asReadOnlyBuffer();
//        Map<Long, String> groupIdAndDocLink = this.processStoreDocument(payload);
//        List<Long> recipientUserIds = this.userService.getUserIdsInConversation(new ArrayList<>(groupIdAndDocLink.keySet()).get(0));
//        if (CollectionUtils.isNotEmpty(recipientUserIds)) {
//            recipientUserIds.forEach(id -> {
//                Collection<ClientMessageSessionDto> sessionDtos = SocketTextHandler.sessionMap.get(id);
//                if (CollectionUtils.isNotEmpty(sessionDtos)) {
//                    sessionDtos.forEach(dto -> {
//                        try {
//                            String savedMessage = (new ObjectMapper()).writeValueAsString(groupIdAndDocLink);
//                            dto.getSession().sendMessage(new TextMessage(savedMessage));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                }
//            });
//        }
//    }

//    @SneakyThrows
//    public Map<Long, String> processStoreDocument(ByteBuffer payload) {
//        byte opCode = payload.get();
//        // Check if the message is binary (0x82 is binary message)
//        if ((opCode & 0x0F) == 0x02) {
//            // Get the payload length
//            int payloadLength = payload.get() & 0xFF; // Convert to unsigned byte
//
//            // Check if payload length indicates extended length
//            if (payloadLength == 126) {
//                payloadLength = payload.getShort() & 0xFFFF; // Convert to unsigned short
//            } else if (payloadLength == 127) {
//                payloadLength = (int) payload.getLong();
//            }
//
//            // Read the file name bytes
//            byte[] fileNameBytes = new byte[payloadLength];
//            payload.get(fileNameBytes);
//            String fileName_GroupId = new String(fileNameBytes, StandardCharsets.UTF_8);
//            String fileName = fileName_GroupId.split("_")[0];
//            Long groupId = Long.valueOf(fileName_GroupId.split("_")[1]);
//
//            //Read file content
//            byte[] fileContent = new byte[payload.remaining()];
//            payload.get(fileContent);
//
//            File documentFile = new File(String.format("%s_%s", fileName, UUID.randomUUID().toString()));
//            FileUtils.writeByteArrayToFile(documentFile, fileContent);
//
////            String filePath = String.format("/Users/quannguyen/Desktop/softwareArchitecture/%s", fileName);
////            File imageFile = new File(filePath);
////            FileOutputStream fos = new FileOutputStream(imageFile);
////            fos.write(fileContent);
////            fos.close();
//
//            logger.info("Finished Storing document");
//
//            Map<Long, String> result = new HashMap<>();
//            result.put(groupId, fileName);
//        }
//        return new HashMap<>();
//    }
}
