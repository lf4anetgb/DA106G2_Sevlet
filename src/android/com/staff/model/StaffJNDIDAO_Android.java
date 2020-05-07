package android.com.staff.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.staff.model.StaffDAO_interface;
import com.staff.model.StaffJDBCDAO;
import com.staff.model.StaffVO;

import android.com.tools.MyData;

public class StaffJNDIDAO_Android extends StaffJDBCDAO implements StaffDAO_interface {
	private static DataSource ds = null;
	// 載入Driver
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(MyData.DRIVER_JNDI);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private static final String GET_ONE_LOGIN = "select staff_id, sf_status, sf_name from staff where sf_account=? and sf_password=?";

	@Override
	public StaffVO findByAccount(String sf_account, String sf_password) {
		StaffVO staffVO = null;
		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ONE_LOGIN);) {

			pstmt.setString(1, sf_account);
			pstmt.setString(2, sf_password);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					staffVO = new StaffVO();

					staffVO.setStaff_id(rs.getString("staff_id"));
					staffVO.setSf_status(rs.getInt("sf_status"));
					staffVO.setSf_name(rs.getString("sf_name"));

				}

				if (rs.getRow() > 1) {
					throw new RuntimeException("出現兩筆以上");
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("findByAccount出錯拉" + e.toString());
		}

		return staffVO;
	}
}
