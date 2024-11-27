package ChatApp.FileExchangeDomain.Service;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.model.DeleteObjectRequest;


import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class S3Service {

    public static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secretKey}")
    private String secretKey;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${s3.endpointUrl}")
    private String endpointUrl;

    AmazonS3 s3Client;

    @PostConstruct
    private void init() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3Client = new AmazonS3Client(credentials);
    }

    public String uploadFile(File file, String fileName, FileType fileType, String extension) {
        String fileUrl = "";
        try {
            fileUrl = String.format("%s/%s/%s/%s", endpointUrl, bucketName, fileType.getLabel(), fileName);
            uploadFileTos3bucket(fileName, fileType.getLabel(), file, extension);
//            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    private void uploadFileTos3bucket(String fileName, String directory, File file, String extension) {
        this.s3Client.putObject(new PutObjectRequest(bucketName, String.format("%s/%s.%s", directory, fileName, extension), file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public enum FileType {
        DOCUMENT("document"),
        IMAGE("image"),
        VIDEO("video"),
        PROFILE("profile");

        private final String label;

        FileType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public void deleteS3File(String fileNameWithPath) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, fileNameWithPath);
        this.s3Client.deleteObject(deleteObjectRequest);
    }
}
