package android.com.order_detail.model;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.order_detail.model.Order_detailDAO_interface;
import com.order_detail.model.Order_detailJNDIDAO;
import com.order_detail.model.Order_detailVO;

import android.com.tools.MyData;

import java.util.ArrayList;
import java.util.List;

public class Order_detailJNDIDAO_Android extends Order_detailJNDIDAO implements Order_detailDAO_interface {
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(MyData.DRIVER_JNDI);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private static final String GET_ALL_STATEMENT = "SELECT order_id, item_id, item_quantity, item_unit_price, item_review, item_rate\n"
			+ "FROM ORDER_DETAIL WHERE order_id = ? ORDER BY item_id";

	public List<Order_detailVO> getAllByID(String order_id) {
		List<Order_detailVO> list = new ArrayList<Order_detailVO>();
		Order_detailVO order_detailVO = null;

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ALL_STATEMENT);) {

			pstmt.setString(1, order_id);

			try (ResultSet rs = pstmt.executeQuery();) {

				while (rs.next()) {

					order_detailVO = new Order_detailVO();
					order_detailVO.setOrder_id(rs.getString("order_id"));
					order_detailVO.setItem_id(rs.getString("item_id"));
					order_detailVO.setItem_quantity(rs.getInt("item_quantity"));
					order_detailVO.setItem_unit_price(rs.getDouble("item_unit_price"));
					order_detailVO.setItem_review(rs.getString("item_review"));
					order_detailVO.setItem_rate(rs.getDouble("item_rate"));
					list.add(order_detailVO);
				}
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}

	private static final String INSERT_STATEMENT = "INSERT INTO ORDER_DETAIL (ORDER_ID, ITEM_ID, ITEM_QUANTITY, ITEM_UNIT_PRICE) VALUES ( ?, ?, ?, ?)";

	public boolean inserts(List<Order_detailVO> list, Connection con) {
		// TODO Auto-generated method stub
		try (PreparedStatement pstmt = con.prepareStatement(INSERT_STATEMENT);) {

			Order_detailVO order_detailVO = null;
			for (int i = 0; i < list.size(); i++) {
				order_detailVO = list.get(i);

				pstmt.setString(1, order_detailVO.getOrder_id());
				pstmt.setString(2, order_detailVO.getItem_id());
				pstmt.setInt(3, order_detailVO.getItem_quantity());
				pstmt.setDouble(4, order_detailVO.getItem_unit_price());

				pstmt.addBatch();
			}

			int[] err = pstmt.executeBatch();

			for (int i = 0; i < err.length; i++) {
				if (err[i] != -2) {
					throw new RuntimeException("insert2 err. ");
				}
			}

			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				throw new RuntimeException("insert2 err. " + e1.getMessage());
			}
			throw new RuntimeException("insert2 err. " + e.getMessage());
		}
	}

}
