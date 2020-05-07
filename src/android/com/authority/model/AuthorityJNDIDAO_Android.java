package android.com.authority.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.authority.model.AuthorityJDBCDAO;
import com.authority.model.Authority_interface;

import android.com.tools.MyData;

public class AuthorityJNDIDAO_Android extends AuthorityJDBCDAO implements Authority_interface {
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(MyData.DRIVER_JNDI);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private static final String CHECK_AUTHORITY = "SELECT * FROM AUTHORITY WHERE STAFF_ID = ? AND FUN_NUM = ?";

	public Boolean checkAuthority(String staff_id, String fun_num) {
		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(CHECK_AUTHORITY);) {

			pstmt.setString(1, staff_id);
			pstmt.setString(2, fun_num);
			return pstmt.executeUpdate() == 1;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
