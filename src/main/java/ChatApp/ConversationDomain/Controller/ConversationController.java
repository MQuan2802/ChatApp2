package ChatApp.ConversationDomain.Controller;

import ChatApp.ConversationDomain.Dto.ConversationDTO;
import ChatApp.ConversationDomain.Entity.Conversation;
import ChatApp.ConversationDomain.Request.ConversationCreateRequest;
import ChatApp.ConversationDomain.Request.ConversationUpdateRequest;
import ChatApp.ConversationDomain.Request.FetchConversationRequest;
import ChatApp.ConversationDomain.Service.ConversationService;
import ChatApp.ConversationDomain.Service.ParticipantService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//import ChatApp.MessageExchangeScheduler;

@RestController
@RequestMapping("api/conversation")
public class ConversationController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    ConversationService service;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "/fetch", method = RequestMethod.POST)
    public Iterable<Conversation> fetchUserMessage(@RequestBody FetchConversationRequest request) {
        return this.service.queryWithSpecification(request);
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public ConversationDTO generateConversation(@RequestBody ConversationCreateRequest request) {
        return this.service.create(request);

    }

    @RequestMapping(value = "/add/participant", method = RequestMethod.POST)
    public ResponseEntity addParticipant(@RequestParam("userId") long userId,
                                         @RequestParam("conversationId") long conversationId) {
        Long participantId = this.service.addConversationParticipant(userId, conversationId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OK");
        response.put("userId", userId);
        response.put("conversationId", conversationId);
        response.put("participantId", participantId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/remove/participant", method = RequestMethod.DELETE)
    public ResponseEntity removeParticipant(@RequestParam("participantId") Long participantId,
                                            @RequestParam("conversationId") Long conversationId) {
        Assert.isTrue(Objects.nonNull(participantId), "Failed to remove participant (Reason: invalid participantId).");
        Assert.isTrue(Objects.nonNull(conversationId), "Failed to remove participant (Reason: invalid conversationId).");
        this.participantService.removeParticipant(participantId, conversationId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OK");
        response.put("participantId", participantId);
        response.put("conversationId", conversationId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/update/name", method = RequestMethod.POST)
    public ResponseEntity updateConversationName(@RequestBody ConversationUpdateRequest request) {
        this.service.update(request);
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "archive", method = RequestMethod.DELETE)
    public ResponseEntity archieveConversation(@RequestParam("conversationId") Long conversationId) {
        Assert.isTrue(Objects.nonNull(conversationId), "Failed to remove participant (Reason: invalid conversationId).");
        this.service.archiveConversation(conversationId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OK");
        response.put("conversationId", conversationId);
        return ResponseEntity.ok(response);
    }


}
