package ChatApp;

import ChatApp.FileExchangeDomain.Service.S3Service;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api")
public class FirstController {
    String accessKey = "AKIA5G2VGORCPKHQEWVA";
    String secretKey = "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";

    @Autowired
    MailService MailService;

    @Autowired
    private S3Service s3Service;

    @RequestMapping(value = "ping")
    public String ping() {
        return ("Service is alive");
    }


    @RequestMapping(value = "/testSendingMail", method = RequestMethod.GET)
    public ResponseEntity testSendingMail() {
//        this.MailService.testSendMessage();
        return ResponseEntity.ok("Success");
    }
}
