package com.worktime;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 启动类：Spring Boot 项目从这里开始运行。
@SpringBootApplication
// 扫描 mapper 包，后续 MyBatis 接口会放在 com.worktime.mapper 下。
@MapperScan("com.worktime.mapper")
public class BackendApplication {

	// main 方法是 Java 程序的入口，运行它就会启动后端服务。
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
