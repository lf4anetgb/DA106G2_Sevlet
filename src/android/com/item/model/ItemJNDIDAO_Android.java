package android.com.item.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.item.model.ItemDAO_interface;
import com.item.model.ItemJNDIDAO;
import com.item.model.ItemVO;

import android.com.tools.MyData;

public class ItemJNDIDAO_Android extends ItemJNDIDAO implements ItemDAO_interface {
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(MyData.DRIVER_JNDI);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private static final String UPDATE = "UPDATE ITEM SET item_type_id=?, item_name=?, item_stock=?, item_price=?,item_content=?, item_status=?"
			+ " WHERE item_id =?";

	public boolean update_Android(ItemVO itemVO) {
		boolean status = false;
		try (Connection con = ds.getConnection();) {
			con.setAutoCommit(false);
			try (PreparedStatement pstmt = con.prepareStatement(UPDATE);) {

				pstmt.setString(1, itemVO.getItem_type_id());
				pstmt.setString(2, itemVO.getItem_name());
				pstmt.setInt(3, itemVO.getItem_stock());
				pstmt.setDouble(4, itemVO.getItem_price());
				pstmt.setString(5, itemVO.getItem_content());
				pstmt.setInt(6, itemVO.getItem_status());
				pstmt.setString(7, itemVO.getItem_id());
				status = pstmt.executeUpdate() == 1;

				if (!status)
					throw new RuntimeException("更新錯誤");

				con.commit();
				con.setAutoCommit(true);

			} catch (Exception e) {
				con.rollback();
				status = false;
				e.printStackTrace();
				throw new RuntimeException("update_Android錯誤");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return status;
	}

	private static final String GET_ALL_STATEMENT = "SELECT item_id,item_type_id,item_name,item_stock,item_price,item_content,item_status "
			+ "FROM ITEM ORDER BY item_id";

	@Override
	public List<ItemVO> getAll() {
		ItemVO itemVO = null;

		try (Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(GET_ALL_STATEMENT);
				ResultSet rs = pstmt.executeQuery();) {

			List<ItemVO> list = new ArrayList<ItemVO>();

			while (rs.next()) {
				itemVO = new ItemVO();
				itemVO.setItem_id(rs.getString("item_id"));
				itemVO.setItem_type_id(rs.getString("item_type_id"));
				itemVO.setItem_name(rs.getString("item_name"));
				itemVO.setItem_stock(rs.getInt("item_stock"));
				itemVO.setItem_price(rs.getDouble("item_price"));
				itemVO.setItem_content(rs.getString("item_content"));
				itemVO.setItem_status(rs.getInt("item_status"));

				list.add(itemVO);
			}

			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("update_Android錯誤");
		}
	}

	private static final String GET_ONE_STATEMENT = "SELECT item_id,item_type_id,item_name,item_stock,item_price,item_content,item_status "
			+ "FROM ITEM WHERE item_id = ?";

	@Override
	public ItemVO findByPrimaryKey(String item_id) {

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ONE_STATEMENT);) {
			pstmt.setString(1, item_id);

			try (ResultSet rs = pstmt.executeQuery();) {
				ItemVO itemVO = new ItemVO();
				if (rs.next()) {

					itemVO.setItem_id(rs.getString("item_id"));
					itemVO.setItem_type_id(rs.getString("item_type_id"));
					itemVO.setItem_name(rs.getString("item_name"));
					itemVO.setItem_stock(rs.getInt("item_stock"));
					itemVO.setItem_price(rs.getDouble("item_price"));
					itemVO.setItem_content(rs.getString("item_content"));
					itemVO.setItem_status(rs.getInt("item_status"));
				}

				return itemVO;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("findByPrimaryKey錯誤");
		}
	}

	private static final String GET_ONE_PICTURE = "SELECT ITEM_PICTURE FROM ITEM WHERE ITEM_ID = ?";

	public byte[] getOnePicture(String item_id) {
		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ONE_PICTURE);) {

			pstmt.setString(1, item_id);

			try (ResultSet rs = pstmt.executeQuery();) {
				if (rs.next())
					return rs.getBytes(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("getOnePicture錯誤");
		}
		return null;
	}
}
