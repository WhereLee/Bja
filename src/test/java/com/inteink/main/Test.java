package com.inteink.main;

import com.inteink.common.utils.DesensitizeUtils;
import com.inteink.common.utils.MatrixToImageUtil;
import com.inteink.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

@Slf4j
public class Test {
    public static void main(String[] args) {
        //int random = (int)((Math.random()*9+1)*10000);

//        String random = StringUtils.getRandomString(32);
//
//        log.info("random:{}",random);

//        int width = 200;
//        int height = 200;
//        String suffix = "png";
//        String qrcode = "https://xqtest.inteink.com/jbxxdoor?doorId=2&doorSecquence=4rrhtr357wekf023543krpw";//"1234567890";
//        String path = "./upload/pic";
//        String picname = "WMJ16802988_"+System.currentTimeMillis()+"_"+UUID.randomUUID().toString().replace("-","") +"."+suffix;
//
//        try {
//            MatrixToImageUtil.createQrcodeImage(qrcode, path, picname, width, height, suffix);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        String name = URLDecoder.decode("%E6%88%BF%E5%B1%8B%E4%BD%8F%E6%88%B7%E6%95%B0%E6%8D%AE.xls");
//        System.out.println(name);
//        Long after7days = StringUtils.getDayEnd(System.currentTimeMillis()/1000 + 7 * 24 * 60 * 60);
//        System.out.println("after7days:"+after7days);

//        String email = "inteink.com";
//        log.info(DesensitizeUtils.email(email));

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setPassword("%pR@8jY=zzOS");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        //config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

//        log.info("root:{}", encryptor.encrypt("root"));
//        log.info("password:{}", encryptor.encrypt("%wzn6Pr&+&sZ"));
//        log.info("druds-name:{}", encryptor.encrypt("dr_adm"));
//        log.info("druds-pwd:{}", encryptor.encrypt("^HuQ-WxGDmBl"));
        //log.info("redis:{}", encryptor.encrypt("v($ne@X^~I1t"));
        //log.info("zhpark:{}", encryptor.encrypt("zhpark"));
        //log.info("mongo:{}", encryptor.encrypt("Q_^mBo+j=Oj9"));
        //log.info("pdApi:{}", encryptor.encrypt("prod_api"));
        //log.info("pdApiSecret:{}", encryptor.encrypt("YSBR+@MVdS04"));
        //log.info("zhpark-appkey:{}", encryptor.encrypt("zhpark0217"));
        //log.info("zhpark-appSecret:{}", encryptor.encrypt("ebpud9peer0uaydz12k3etqtr456mpwt"));
//        String mysqlusername = encryptor.decrypt("80Yj751RkkeD9W6YwlIg0Zb5ODf7byHZjxGLWGSz5w/vfX2dmXYbmLREcLaNxpm+");
//        log.info("mysqlusername:{}", mysqlusername);
//        String mysqlpassword = encryptor.decrypt("7vDCAgK3Q4ouxdIVgJ8YCIbhZtIk2BzQrFxH+mWtbYvozQgxlDPqK+wEwroIx2gq");
//        log.info("mysqlpassword:{}", mysqlpassword);
//        String redis = encryptor.decrypt("HmTilR2VGCKp3CHlkkBVB5BX46A+bd5xGmRQIKFKLdG9dsqDhfOQmqTbT1zo8aIy");
//        log.info("redis:{}", redis);
//        String mongodbusername = encryptor.decrypt("eXnet62zvy1iplZ15dcZlHN4ZZF11FJ9/H8SdXeripzgijwxjGQAFl8dz/WVy2WI");
//        log.info("mongodbusername:{}", mongodbusername);
//        String mongodbpassword = encryptor.decrypt("4PghAkO8j0YyzsAm9AmqrvVBVfxeHviXapNcynO1y6NAdRifacOzcIo7JjF2X7pZ");
//        log.info("mongodbpassword:{}", mongodbpassword);
//        String druidusername = encryptor.decrypt("jYk8m7Ofu2IZqYd/1ZJzekEzuOlod9LmNDiZsO5MhhCRZNf1cb4y2dVD4dEQf74V");
//        log.info("druidusername:{}", druidusername);
//        String druidpassword = encryptor.decrypt("wX++iJ+YuyKIVdz7mUk439cYN56JrqN8Hd75uTF2fo1SDuXev3c8cWEvj8ogLH5E");
//        log.info("druidpassword:{}", druidpassword);
//        String knife4jusername = encryptor.decrypt("amIkc4EBOZeBdWzz1asxefNd4SKnQUXaCmhC5S593336eSIEXvfPFg1FQgTUsmi1");
//        log.info("knife4jusername:{}", knife4jusername);
//        String knife4jpassword = encryptor.decrypt("zTQ4nU43kz9occg1YQkGo7E09JhyoVNBjgV7xpDQ2+S9GAmLwR5xdmUtFTQ40ngd");
//        log.info("knife4jpassword:{}", knife4jpassword);
//        String zhparkappkey = encryptor.decrypt("vhPHSUe8WaPkT7zF7R25fytd0khmbwU83kpl+MKjD0K4XEayniKGIlJKk6aE02bN");
//        log.info("zhparkappkey:{}", zhparkappkey);
//        String appkeyappSecret = encryptor.decrypt("xaR23BlIo3jsBSnXl9zrecyvLvbeSCtnpiBfIkrsd1rp8wVZTcFQR5LR0VQ0R5r7bdLmX5WVseBaQsTEtiYZhWTuJKbbCkdD+jZOp30zUiI=");
//        log.info("appkeyappSecret:{}", appkeyappSecret);

        String appId = encryptor.decrypt("BMSA6ZhewmMJuG6iX7VKKHt8MfPfvb+QwFabJrPvmM+rLM/KpnTIRvszaoHUhDZmCe1kxbIlQ2xrBkbgY+S1VQ==");
        log.info("appId:{}", appId);
        String mchId = encryptor.decrypt("CsgbmR7QEq0h9egOQ0OTket8sSEv3wX32qJBSxlRIrDfaq0zsCjPhJ3hyDtmsjZr");
        log.info("mchId:{}", mchId);
        String key = encryptor.decrypt("GkUxy0h28EyEDiV3Mi8o/NBwo72xRaE7SHMhzOngcleLGLufovFEBQC6WqmCkyRrF9IzcTIJbVuTehp6VLAbMXc0P4zLdonKo312vE0rO/A=");
        log.info("key:{}", key);
        String secret = encryptor.decrypt("WxBu0D8K3XR/oj193o6t0WhHLFWNqkALqgWBbDfuvej6rJZ+HYC1hGgxLCWmfcHeSARixS/JR86PFXQ89C6ZQf8tIJUQdw5j3MLwCLNwYQs=");
        log.info("secret:{}", secret);

    }
}
