package com.test.Autowired;

import java.sql.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoDataSourceConfigController_testDS2 {

	@Autowired
	DataSource ds;

	@RequestMapping("/testDS2")
	@ResponseBody
	public String testDS() throws SQLException {
		Connection con = ds.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * from emp3");

		while (rs.next()) {
			String str1 = rs.getString(1);
			String str2 = rs.getString(2);

			System.out.print(" EMPNO= " + str1);
			System.out.print(" ENAME= " + str2);
			System.out.print("\n");
		}
		
		return String.valueOf(ds);
	}
}