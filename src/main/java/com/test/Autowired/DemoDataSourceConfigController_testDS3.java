package com.test.Autowired;

import java.sql.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoDataSourceConfigController_testDS3 {

	@Autowired
	DataSource ds;

	@RequestMapping("/testDS3")
	@ResponseBody
	public String testDS() throws SQLException {
		Connection con = ds.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * from emp3");
		
		StringBuffer out = new StringBuffer();
		out.append("<TABLE border='1' bordercolor='blue'>\n");

		try {
			ResultSetMetaData rsmd = rs.getMetaData();

			int numcols = rsmd.getColumnCount();

			out.append("<TR>");
			for (int i = 1; i <= numcols; i++) {
				out.append("<TH>" + rsmd.getColumnLabel(i));
			}
			out.append("</TR>\n");

			while (rs.next()) {
				out.append("<TR>");
				for (int i = 1; i <= numcols; i++) {
					out.append("<TD>");
					Object obj = rs.getObject(i);
					if (obj != null)
						out.append(obj.toString());
					else
						out.append("&nbsp;");
				}
				out.append("</TR>\n");
			}

			out.append("</TABLE>\n");
		} catch (SQLException e) {
			out.append("</TABLE><H1>ERROR:</H1> " + e.getMessage() + "\n");
		}

		return out.toString();
	}

}