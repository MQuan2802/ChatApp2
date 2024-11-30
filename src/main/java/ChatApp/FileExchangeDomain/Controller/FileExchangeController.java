package ChatApp.FileExchangeDomain.Controller;

import ChatApp.FileExchangeDomain.Service.FileUploadService;
import ChatApp.FileExchangeDomain.Service.S3Service;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequestMapping("/api")
@RestController
public class FileExchangeController {

    @Autowired
    FileUploadService fileUploadService;

    @RequestMapping(value = "file/upload", method = RequestMethod.POST)
    public ResponseEntity uploadFile(@RequestParam("contentType") S3Service.FileType contentType,
                                     @RequestParam("extension") String extension,
                                     @RequestParam("file") MultipartFile file) {
//        Assert.isTrue(Objects.nonNull(senderId), "Failed to send File (Reason: invalid senderId).");
//        Assert.isTrue(Objects.nonNull(conversationId), "Failed to send File (Reason: invalid conversation Id).");
        Assert.isTrue(Objects.nonNull(contentType), "Failed to send File (Reason: invalid content type).");
        Assert.isTrue(Objects.nonNull(extension), "Failed to send File (Reason: invalide extension).");
        String s3Link = this.fileUploadService.uploadFile(file, contentType, extension);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("link", s3Link);
        return ResponseEntity.ok(response);
    }
}
