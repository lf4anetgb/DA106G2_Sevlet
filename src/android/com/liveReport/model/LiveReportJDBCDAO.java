package android.com.liveReport.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.com.tools.MyData;

public class LiveReportJDBCDAO implements LiveReportDAO_interface {

	String url = MyData.URL_JDBC;
	String userid = MyData.USERID;
	String passwd = MyData.PASSWORD;
	// 載入Driver
	static {
		try {
			Class.forName(MyData.DRIVER_JDBC);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. " + e.getMessage());
			// Handle any SQL errors
		}
	}

	private static final String INSERT = "INSERT INTO LIVE_REPORT (LIVE_REPORT_ID, LIVE_ID, MEMBER_ID, LIVE_REPORT_CONTENT, REPORT_STATUS, REPORT_DATE) VALUES ('LLR'||LPAD(to_char(LIVE_REPORT_ID_SEQ.NEXTVAL), 7, '0'), ?, ?, ?, ?, ?)";

	@Override
	public void insert(LiveReportVO liveReportVO) {
		try (Connection con = DriverManager.getConnection(url, userid, passwd);) {

			try (PreparedStatement pstmt = con.prepareStatement(INSERT);) {

				con.setAutoCommit(false);// 關閉AutoCommit

				pstmt.setString(1, liveReportVO.getLive_id());
				pstmt.setString(2, liveReportVO.getMember_id());
				pstmt.setString(3, liveReportVO.getLive_report_content());
				pstmt.setInt(4, liveReportVO.getReport_status());
				pstmt.setDate(5, liveReportVO.getDate());

				pstmt.executeUpdate();
				con.commit();

			} catch (Exception e) {
				e.printStackTrace();
				if (con != null) {
					con.rollback();
				}

				throw new RuntimeException("insert上傳DB發生錯誤" + e.getMessage());
			}
		} catch (Exception e) {

			throw new RuntimeException("insert連線發生錯誤" + e.getMessage());
		}
	}

	private static final String UPDATE = "UPDATE LIVE_REPORT SET LIVE_ID=?, MEMBER_ID=?, LIVE_REPORT_CONTENT=?, REPORT_STATUS=?, REPORT_DATE=? WHERE LIVE_REPORT_ID=?";

	@Override
	public void update(LiveReportVO liveReportVO) {
		try (Connection con = DriverManager.getConnection(url, userid, passwd);) {

			try (PreparedStatement pstmt = con.prepareStatement(UPDATE);) {

				con.setAutoCommit(false);// 關閉AutoCommit

				pstmt.setString(1, liveReportVO.getLive_id());
				pstmt.setString(2, liveReportVO.getMember_id());
				pstmt.setString(3, liveReportVO.getLive_report_content());
				pstmt.setInt(4, liveReportVO.getReport_status());
				pstmt.setDate(5, liveReportVO.getDate());

				pstmt.setString(6, liveReportVO.getLive_report_id());

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

	private static final String DELETE = "DELETE FROM LIVE_REPORT WHERE LIVE_REPORT_ID=?";

	@Override
	public Boolean delete(String live_report_id) {
		Boolean err = false;

		try (Connection con = DriverManager.getConnection(url, userid, passwd);) {

			try (PreparedStatement pstmt = con.prepareStatement(DELETE);) {

				con.setAutoCommit(false);// 關閉AutoCommit

				pstmt.setString(1, live_report_id);

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

	private static final String GET_ONE = "SELECT LIVE_REPORT_ID,  LIVE_ID, MEMBER_ID, LIVE_REPORT_CONTENT, REPORT_STATUS, REPORT_DATE FROM LIVE_REPORT WHERE LIVE_REPORT_ID = ?";

	@Override
	public LiveReportVO findByLiveReportID(String live_report_id) {
		LiveReportVO liveReportVO = null;

		try (Connection con = DriverManager.getConnection(url, userid, passwd);
				PreparedStatement pstmt = con.prepareStatement(GET_ONE);) {

			pstmt.setString(1, live_report_id);

			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					String live_reportId = rs.getString("LIVE_REPORT_ID"), live_id = rs.getString("LIVE_ID"),
							member_id = rs.getString("MEMBER_ID"),
							live_report_content = rs.getString("LIVE_REPORT_CONTENT");
					Integer report_status = rs.getInt("REPORT_STATUS");// 直播檢舉處理狀態
					Date date = rs.getDate("REPORT_DATE"); // 檢舉日期

					liveReportVO = new LiveReportVO(live_reportId, live_id, member_id, live_report_content,
							report_status, date);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("findByLiveReportID發生錯誤" + e.getMessage());
		}

		return liveReportVO;
	}

	private static final String GET_ALL = "SELECT * FROM LIVE_REPORT ORDER BY LIVE_REPORT_ID";

	@Override
	public List<LiveReportVO> getAll() {
		LiveReportVO liveReportVO = null;
		List<LiveReportVO> liveReports = new ArrayList<LiveReportVO>();

		try (Connection con = DriverManager.getConnection(url, userid, passwd);
				PreparedStatement pstmt = con.prepareStatement(GET_ALL);
				ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {
				String live_reportId = rs.getString("LIVE_REPORT_ID"), live_id = rs.getString("LIVE_ID"),
						member_id = rs.getString("MEMBER_ID"),
						live_report_content = rs.getString("LIVE_REPORT_CONTENT");
				Integer report_status = rs.getInt("REPORT_STATUS");// 直播檢舉處理狀態
				Date date = rs.getDate("REPORT_DATE"); // 檢舉日期

				liveReportVO = new LiveReportVO(live_reportId, live_id, member_id, live_report_content, report_status,
						date);

				liveReports.add(liveReportVO);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return liveReports;
	}

	public static void main(String[] args) {
//		LiveReportJDBCDAO dao = new LiveReportJDBCDAO();
//		java.util.Date nowDate = new java.util.Date();
//
//		String live_report_id = null, // 直播檢舉ID
//				live_id = "LLN0000005", // 直播ID
//				member_id = "MMN0000002", // 會員ID
//				live_report_content = "炫屁炫"; // 直播檢舉內容
//		Integer report_status = 0;// 直播檢舉處理狀態
//		Date date = new Date(nowDate.getTime()); // 檢舉日期
//
//		LiveReportVO liveReportVO = new LiveReportVO(live_report_id, live_id, member_id, live_report_content,
//				report_status, date);
//
//		dao.insert(liveReportVO);
//
//		liveReportVO.setReport_status(2);
//		liveReportVO.setLive_report_id("LLR0000007");
//
//		dao.update(liveReportVO);
//
//		dao.delete("LLR0000007");
//
//		System.out.println(dao.findByLiveReportID("LLR0000005"));
//		System.out.println(dao.getAll().toString());
	}
}
