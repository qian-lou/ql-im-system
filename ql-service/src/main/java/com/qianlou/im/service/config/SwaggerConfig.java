package com.qianlou.im.service.config;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;


@Slf4j
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Value("#{'${swagger.scan-packages}'.split(',')}")
    private List<String> basePackages;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
//                   当前包路径
                .apis(basePackage())
                .paths(PathSelectors.any()).build();

    }

    public Predicate<RequestHandler> basePackage() {
        return input -> declaringClass(input).transform(handlerPackage()).or(true);
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler requestHandler) {
        return Optional.fromNullable(requestHandler.declaringClass());
    }

    private Function<Class<?>, Boolean> handlerPackage() {
        return input -> {
            for (String strPackage : basePackages) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    //构建api文档的详细信息函数
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("通讯服务后台接口" + appName)
                //创建人
                .contact(new Contact("James", null, "1327639463@qq.com"))
                //版本号
                .version("1.0")
                //描述
                .description("提供给前端页面调用的相关接口")
                .build();
    }
}
