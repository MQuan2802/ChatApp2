package ChatApp.FileExchangeDomain.Service;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.UUID;

@Service
public class FileUploadService {


    @Autowired
    S3Service s3Service;

    @SneakyThrows
    @Transactional
    public String uploadFile(MultipartFile file, S3Service.FileType contentType, String extension) {
        String imgUploadName = UUID.randomUUID().toString();
        File imgFile = new File(imgUploadName);
        FileUtils.writeByteArrayToFile(imgFile, file.getBytes());
        String s3Link = this.s3Service.uploadFile(imgFile, imgUploadName, contentType, extension);
        return s3Link;
    }

}
