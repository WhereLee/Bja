/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.inteink.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig /*implements WebMvcConfigurer*/ {

    @Bean
    public Docket createRestApi() {
        List<ResponseMessage> responseMessageList = new ArrayList<>();
        responseMessageList.add(new ResponseMessageBuilder().code(0).message("success").responseModel(new ModelRef("Success")).build());
        responseMessageList.add(new ResponseMessageBuilder().code(200).message("其他").responseModel(new ModelRef("Error")).build());
        responseMessageList.add(new ResponseMessageBuilder().code(401).message("invalid token").responseModel(new ModelRef("Error")).build());
        responseMessageList.add(new ResponseMessageBuilder().code(500).message("其他错误").responseModel(new ModelRef("Error")).build());
        responseMessageList.add(new ResponseMessageBuilder().code(999).message("授权信息无效或已过期").responseModel(new ModelRef("Error")).build());

        return new Docket(DocumentationType.SWAGGER_2)
//                .globalResponseMessage(RequestMethod.GET,responseMessageList)
//                .globalResponseMessage(RequestMethod.POST,responseMessageList)
                .apiInfo(apiInfo())
                //分组名称
                .groupName("1.X版本")
                .select()
                //加了ApiOperation注解的类，才生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //包下的类，才生成接口文档
                //.apis(RequestHandlerSelectors.basePackage("com.inteink.modules.*.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(security());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("智想科技")
            .description("inteink-region小区开发文档")
            .termsOfServiceUrl("https://www.inteink.com")
            .version("1.0.0")
            .build();
    }

    private List<ApiKey> security() {
        return newArrayList(
            new ApiKey("token", "token", "header")
        );
    }

}