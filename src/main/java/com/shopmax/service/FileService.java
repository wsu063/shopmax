package com.shopmax.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    //이미지 파일을 서버에 업로드
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {

        UUID uuid = UUID.randomUUID(); // 중복되지 않은 파일의 이름을 만든다.

        // 이미지1.jpg -> 이미지 확장자 명(jpg)를 구한다.
        String extensioin = originalFileName.substring(originalFileName.lastIndexOf("."));

        // 파일 이름 생성 -> EERERERY-ERERERER-CDFDFGD.jpg
        String savedFileName = uuid.toString() + extensioin;

        // C:/shop/item/EERERERY-ERERERER-CDFDFGD.jpg
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        // 파일 업로드
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl); // 업로드 할 위치
        fos.write(fileData); // 업로드 내용
        fos.close(); // 업로드 종료

        return savedFileName;
    }

    //이미지 파일을 서버에서 삭제
    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);
        
        if(deleteFile.exists()) { //해당 파일이 존재하면
            deleteFile.delete(); // 파일 삭제
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
