package com.shopmax.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class JasyptConfigTest {
    public String key = "haseo_key"; //암복화에 필요한 키

    @Test
    void stringEncryptor() {
        String url = "jdbc:mysql://database-1.clgygis60ayl.ap-northeast-2.rds.amazonaws.com:3306/shop?serverTimezone=UTC";
        String userName = "podoarena";
        String password = "vhehdkfpsk";

        System.out.println("En_url : " + jasyptEncoding(url));
        System.out.println("En_username : " + jasyptEncoding(userName));
        System.out.println("En_password : " + jasyptEncoding(password));
    }

    @Test
    void stringDecryptor() {
        String url = null;
        String userName = null;
        String password = null;

        System.out.println("De_url : " + jasyptDecoding(url));
        System.out.println("De_username : " + jasyptDecoding(userName));
        System.out.println("De_password : " + jasyptDecoding(password));
    }

    public String jasyptEncoding(String value) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }

    public String jasyptDecoding(String value) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.decrypt(value);
    }
}