
package com.inteink;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
@MapperScan({
		"com.inteink.modules.*.dao",   // 系统模块的DAO路径（SysMenuDao所在包）
		"com.inteink.modules.*.mapper" // 业务模块的Mapper路径（升降杆相关）
})
public class InteinkFaster {

	public static void main(String[] args) {
		SpringApplication.run(InteinkFaster.class, args);
	}

}