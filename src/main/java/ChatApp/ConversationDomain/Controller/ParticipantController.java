package ChatApp.ConversationDomain.Controller;

import ChatApp.ConversationDomain.Service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/participant")
public class ParticipantController {

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "/update/last_view", method = RequestMethod.POST)
    public ResponseEntity updateLastView(@RequestBody List<Long> conversationIds,
                                         @RequestParam("userId") Long userId) {
        this.participantService.updateLastView(userId, conversationIds);
        return ResponseEntity.ok("ok");
    }
}
