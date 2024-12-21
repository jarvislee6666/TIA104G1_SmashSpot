package com.test.Autowired;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoDataSourceConfigController_testDS1 {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@RequestMapping("/testDS1")
	@ResponseBody
	public String testDS() {
		
		Long count = jdbcTemplate.queryForObject("select count(*) from emp3", Long.class);
		DataSource ds = jdbcTemplate.getDataSource();
		
		return String.valueOf(count +" : "+ ds);
	}
}