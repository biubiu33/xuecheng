package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSwagger2Doc
//当你使用 @SpringBootApplication 注解时，它隐含了一个 @ComponentScan 注解。默认情况下，Spring 只会扫描启动类所在的当前包（Package）及其所有子包。
@SpringBootApplication
public class XuechengPlusContentApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(XuechengPlusContentApiApplication.class, args);
	}

}
