package ChatApp.ConversationDomain.Controller;

import ChatApp.ConversationDomain.Entity.ChatMessage;
import ChatApp.ConversationDomain.Request.FetchMessageRequest;
import ChatApp.ConversationDomain.Service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Objects;

@RestController
@RequestMapping("api/message")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @RequestMapping(value = "fetch", method = RequestMethod.POST)
    public Iterable<ChatMessage> fetchWithSpecification(@RequestBody FetchMessageRequest messageRequest) {
//        if(Objects.isNull(messageRequest.getEndTs()))
//            messageRequest.setEndTs(Calendar.getInstance());
//        if(Objects.isNull(messageRequest.getStartTs())) {
//            Calendar startTs = Calendar.getInstance();
//            startTs.add(Calendar.DAY_OF_MONTH, -5);
//            messageRequest.setStartTs(startTs);
//        }
        return this.chatMessageService.queryWithSpecification(messageRequest);
    }
}
