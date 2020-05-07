package android.com.orders.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.order_detail.model.Order_detailVO;
import com.orders.model.OrdersDAO_interface;
import com.orders.model.OrdersJNDIDAO;
import com.orders.model.OrdersVO;

import android.com.member.model.MemberService_Android;
import android.com.order_detail.model.Order_detailService_Android;
import android.com.tools.MyData;

public class OrdersJNDIDAO_Android extends OrdersJNDIDAO implements OrdersDAO_interface {
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(MyData.DRIVER_JNDI);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private static final String GET_ALL_STATEMENT = "SELECT order_id, member_id, order_price, order_address, order_status, order_time, paywith FROM ORDERS  WHERE member_id = ? ORDER BY order_id";

	public List<OrdersVO> getAllByID(String member_id) {
		List<OrdersVO> list = new ArrayList<OrdersVO>();
		OrdersVO ordersVO = null;

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ALL_STATEMENT);) {

			pstmt.setString(1, member_id);

			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					ordersVO = new OrdersVO();
					ordersVO.setOrder_id(rs.getString("order_id"));
					ordersVO.setMember_id(rs.getString("member_id"));
					ordersVO.setOrder_status(rs.getInt("order_status"));
					ordersVO.setOrder_address(rs.getString("order_address"));
					ordersVO.setOrder_time(rs.getTimestamp("order_time"));
					ordersVO.setOrder_price(rs.getDouble("order_price"));
					ordersVO.setPaywith(rs.getInt("paywith"));
					list.add(ordersVO);
				}

				return list;
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	private static final String INSERT_STATEMENT = "INSERT INTO ORDERS ( order_id, member_id, order_price, order_address, order_status, order_time, paywith) VALUES"
			+ "('OON-'||to_char(SYSDATE,'YYYYMMDD')||'-'||LPAD(to_char(ORDER_ID_SEQ.NEXTVAL),7,'0'),?,?,?,?,?,?)";

	public String insertWithOrder_details(OrdersVO ordersVO, List<Order_detailVO> list) {

		try (Connection con = ds.getConnection();) {

			// 1●設定於 pstm.executeUpdate()之前
			con.setAutoCommit(false);
			String cols[] = { "order_id" };
			try (PreparedStatement pstmt = con.prepareStatement(INSERT_STATEMENT, cols);) {

				// 新增訂單
				pstmt.setString(1, ordersVO.getMember_id());
				pstmt.setDouble(2, ordersVO.getOrder_price());
				pstmt.setString(3, ordersVO.getOrder_address());
				pstmt.setInt(4, ordersVO.getOrder_status());
				pstmt.setTimestamp(5, ordersVO.getOrder_time());
				pstmt.setInt(6, ordersVO.getPaywith());
				pstmt.executeUpdate();

				String next_order_id = null;// 用來存主鍵

				// 抓取主鍵
				try (ResultSet rs = pstmt.getGeneratedKeys();) {
					if (rs.next()) {
						next_order_id = rs.getString(1);
					} else {
						throw new RuntimeException("insertWithOrder_details出錯拉. ");
					}
				}

				// 同時新增明細
				Order_detailService_Android dao = new Order_detailService_Android();
				System.out.println("list.size()-A=" + list.size());
				for (Order_detailVO aOrder_detail : list) {
					aOrder_detail.setOrder_id(next_order_id);
				}

				if (!dao.inserts(list, con)) {
					throw new RuntimeException("insertWithOrder_details出錯拉. ");
				}

				con.commit();
				con.setAutoCommit(true);
				System.out.println("list.size()-B=" + list.size());
				return next_order_id;
			} catch (SQLException se) {
				con.rollback();
				throw new RuntimeException("insertWithOrder_details出錯拉. " + se.getMessage());
			}
		} catch (SQLException se) {
			throw new RuntimeException("insertWithOrder_details出錯拉. " + se.getMessage());
		}

	}

	public String insertWithOrder_details_payPoints(OrdersVO ordersVO, List<Order_detailVO> list, Integer payAmount) {

		try (Connection con = ds.getConnection();) {

			// 1●設定於 pstm.executeUpdate()之前
			con.setAutoCommit(false);
			String cols[] = { "order_id" };
			try (PreparedStatement pstmt = con.prepareStatement(INSERT_STATEMENT, cols);) {

				// 新增訂單
				pstmt.setString(1, ordersVO.getMember_id());
				pstmt.setDouble(2, ordersVO.getOrder_price());
				pstmt.setString(3, ordersVO.getOrder_address());
				pstmt.setInt(4, ordersVO.getOrder_status());
				pstmt.setTimestamp(5, ordersVO.getOrder_time());
				pstmt.setInt(6, ordersVO.getPaywith());
				pstmt.executeUpdate();

				String next_order_id = null;// 用來存主鍵

				// 抓取主鍵
				try (ResultSet rs = pstmt.getGeneratedKeys();) {
					if (rs.next()) {
						next_order_id = rs.getString(1);
					} else {
						throw new RuntimeException("insertWithOrder_details出錯拉. ");
					}
				}

				// 同時新增明細並扣除會員點數
				Order_detailService_Android orderDetailDao = new Order_detailService_Android();
				MemberService_Android memberDao = new MemberService_Android();
				System.out.println("list.size()-A=" + list.size());
				for (Order_detailVO aOrder_detail : list) {
					aOrder_detail.setOrder_id(next_order_id);
				}

				if (!orderDetailDao.inserts(list, con)
						|| !memberDao.payPoint_notCommit(ordersVO.getMember_id(), payAmount, con)) {
					throw new RuntimeException("副功能出錯拉. ");
				}

				con.commit();
				con.setAutoCommit(true);
				System.out.println("list.size()-B=" + list.size());
				return next_order_id;
			} catch (SQLException se) {
				con.rollback();
				throw new RuntimeException("insertWithOrder_details出錯拉. " + se.getMessage());
			}
		} catch (SQLException se) {
			throw new RuntimeException("insertWithOrder_details出錯拉. " + se.getMessage());
		}

	}
}
