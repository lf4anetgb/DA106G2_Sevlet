package android.com.donation.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import android.com.tools.MyData;

public class DonationRecordJNDIDAO implements DonationRecordDAO_interface {

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

	private static final String INSERT = "INSERT INTO DONATION_RECORD (DONATION_ID, LIVE_ID, MEMBER_ID, DONATION_TIME, DONATION_COST, DONATION_MESSAGE) VALUES ('LDN'||LPAD(to_char(DONATION_ID_SEQ.NEXTVAL), 7, '0'), ?, ?, ?, ?, ?);";

	@Override
	public void insert(DonationRecordVO donationRecordVO) {
		try (Connection con = ds.getConnection();) {

			try (PreparedStatement pstmt = con.prepareStatement(INSERT);) {

				con.setAutoCommit(false);// 關閉AutoCommit

				pstmt.setString(1, donationRecordVO.getLive_id());
				pstmt.setString(2, donationRecordVO.getMember_id());
				pstmt.setTimestamp(3, donationRecordVO.getDonation_time());
				pstmt.setDouble(4, donationRecordVO.getDonation_cost());
				pstmt.setString(5, donationRecordVO.getDonation_message());

				pstmt.executeUpdate();
				con.commit();

			} catch (Exception e) {

				if (con != null) {
					con.rollback();
				}

				throw new RuntimeException("insert上傳DB發生錯誤" + e.getMessage());
			}
		} catch (Exception e) {

			throw new RuntimeException("insert連線發生錯誤" + e.getMessage());
		}

	}

	private static final String UPDATE = "UPDATE DONATION_RECORD SET LIVE_ID=?, MEMBER_ID=?, DONATION_TIME=?, DONATION_COST=?, DONATION_MESSAGE=? WHERE DONATION_ID=?";

	@Override
	public void update(DonationRecordVO donationRecordVO) {
		try (Connection con = ds.getConnection();) {

			try (PreparedStatement pstmt = con.prepareStatement(UPDATE);) {

				con.setAutoCommit(false);// 關閉AutoCommit

				pstmt.setString(1, donationRecordVO.getLive_id());
				pstmt.setString(2, donationRecordVO.getMember_id());
				pstmt.setTimestamp(3, donationRecordVO.getDonation_time());
				pstmt.setDouble(4, donationRecordVO.getDonation_cost());
				pstmt.setString(5, donationRecordVO.getDonation_message());

				pstmt.setString(6, donationRecordVO.getDonation_id());

				pstmt.executeUpdate();
				con.commit();

			} catch (Exception e) {

				if (con != null) {
					con.rollback();
				}

				throw new RuntimeException("update上傳DB發生錯誤" + e.getMessage());
			}
		} catch (Exception e) {

			throw new RuntimeException("update連線發生錯誤" + e.getMessage());
		}

	}

	private static final String DELETE = "DELETE FROM DONATION_RECORD WHERE DONATION_ID=?";

	@Override
	public Boolean delete(String donation_id) {
		Boolean err = false;

		try (Connection con = ds.getConnection();) {

			try (PreparedStatement pstmt = con.prepareStatement(DELETE);) {

				con.setAutoCommit(false);// 關閉AutoCommit

				pstmt.setString(1, donation_id);

				pstmt.executeUpdate();
				con.commit();

			} catch (Exception e) {
				err = true;

				if (con != null) {
					con.rollback();
				}

				throw new RuntimeException("delete上傳DB發生錯誤" + e.getMessage());
			}
		} catch (Exception e) {

			throw new RuntimeException("delete連線發生錯誤" + e.getMessage());
		}

		return !err;

	}

	private static final String GET_ONE = "SELECT DONATION_ID,  LIVE_ID, MEMBER_ID, DONATION_TIME, DONATION_COST, DONATION_MESSAGE FROM DONATION_RECORD WHERE DONATION_ID = ?";

	@Override
	public DonationRecordVO findByDonationID(String donation_id) {
		DonationRecordVO donationRecordVO = null;

		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ONE);) {

			pstmt.setString(1, donation_id);

			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					String donationId = rs.getString("DONATION_ID"), live_id = rs.getString("LIVE_ID"),
							member_id = rs.getString("MEMBER_ID"), donation_message = rs.getString("DONATION_MESSAGE");
					Date donation_time = rs.getTimestamp("DONATION_TIME");
					Double donation_cost = rs.getDouble("DONATION_COST");

					donationRecordVO = new DonationRecordVO(donationId, live_id, member_id, donation_message,
							donation_time, donation_cost);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("findByLiveID發生錯誤" + e.getMessage());
		}

		return donationRecordVO;
	}

	private static final String GET_ALL = "SELECT * FROM DONATION_RECORD ORDER BY DONATION_ID";

	@Override
	public List<DonationRecordVO> getAll() {
		DonationRecordVO donationRecordVO = null;
		List<DonationRecordVO> donations = new ArrayList<DonationRecordVO>();

		try (Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(GET_ALL);
				ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {
				String donationId = rs.getString("DONATION_ID"), live_id = rs.getString("LIVE_ID"),
						member_id = rs.getString("MEMBER_ID"), donation_message = rs.getString("DONATION_MESSAGE");
				Date donation_time = rs.getTimestamp("DONATION_TIME");
				Double donation_cost = rs.getDouble("DONATION_COST");

				donationRecordVO = new DonationRecordVO(donationId, live_id, member_id, donation_message, donation_time,
						donation_cost);

				donations.add(donationRecordVO);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("getAll發生錯誤" + e.getMessage());
		}

		return donations;
	}

	public static void main(String[] args) {
//		DonationRecordJNDIDAO dao = new DonationRecordJNDIDAO();
//
//		DateFormat df = new SimpleDateFormat("yyyy/MM/dd"); // 設定日期輸入格式
//		String donation_id = null, // 斗內ID
//				live_id = "LLN0000005", // 直播ID
//				member_id = "MMN0000002", // 會員ID
//				donation_message = "顆顆"; // 斗內留言
//		Date donation_time = null; // 斗內時間
//		Double donation_cost = 200.0; // 斗內金額
//
//		try {
//			donation_time = df.parse("2020/01/02");
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		DonationRecordVO donationRecordVO = new DonationRecordVO(donation_id, live_id, member_id, donation_message,
//				donation_time, donation_cost);
//
//		dao.insert(donationRecordVO);
//		
//		donationRecordVO.setDonation_id("LDN0000008");
//		donationRecordVO.setDonation_cost(300.1);
//		
//		dao.update(donationRecordVO);
//		
//		dao.delete("LDN0000008");
//		
//		System.out.println(dao.findByLiveID("LDN0000004"));
//		System.out.println(dao.getAll().toString());

	}
}
