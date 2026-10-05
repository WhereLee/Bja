package com.inteink.main;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * jasypt加解密测试
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class JasyptTest {
    @Autowired
    private StringEncryptor stringEncryptor;

    @Test
    public void jasyptTest() {
        //H07KThiZqXwz95ynJZoPRvbCOlYHRpe6ymxC6ZACcPjUGnoAW58dq/9WhxF6fgvf
        log.info("root:{}", stringEncryptor.encrypt("root"));
    }

    @Test
    public void test() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("dF6$+WsQ*k1p");
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        //76wLlVf5rYntD5F7Y9ZPk8OzH/uXD/EVL49JoGd9fCF//Lc/JbBoeyWDVyYLutv5
        log.info("root:{}", encryptor.encrypt("root"));
        log.info("name:{}", encryptor.decrypt("KzNFTP+IlY7+TWZAnd/GW4NlbX0b0e/QgtPR0pancMQbYhWEucZnypl/GxilSxEg"));
        log.info("password:{}", encryptor.decrypt("7vDCAgK3Q4ouxdIVgJ8YCIbhZtIk2BzQrFxH+mWtbYvozQgxlDPqK+wEwroIx2gq"));
    }
}
