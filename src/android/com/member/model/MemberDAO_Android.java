package android.com.member.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.member.model.MemberDAO;
import com.member.model.MemberDAO_interface;
import com.member.model.MemberVO;

import android.com.tools.MyData;

public class MemberDAO_Android extends MemberDAO implements MemberDAO_interface {

	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(MyData.DRIVER_JNDI);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private static final String INSERT_STMT = "INSERT INTO MEMBER "
			+ "(MEMBER_ID, PASSWORD, NAME, GENDER, BIRTHDAY, CELLPHONE, EMAIL, VALIDATION_STATUS, REGISTERATION_DATE, SELF_INTRODUCTION, NICK_NAME) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	@Override
	public boolean insert(MemberVO memberVO) {
		boolean isSuccess = false;

		try (Connection con = ds.getConnection();) {

			con.setAutoCommit(false);

			try (PreparedStatement psmt = con.prepareStatement(INSERT_STMT);) {
				psmt.setString(1, memberVO.getMember_id());
				psmt.setString(2, memberVO.getPassword());
				psmt.setString(3, memberVO.getName());
				psmt.setInt(4, memberVO.getGender());
				psmt.setDate(5, memberVO.getBirthday());
				psmt.setString(6, memberVO.getCellphone());
				psmt.setString(7, memberVO.getEmail());
				psmt.setInt(8, memberVO.getValidation_status());
				psmt.setDate(9, new Date((new java.util.Date()).getTime()));
				psmt.setString(10, memberVO.getSelf_introduction());
				psmt.setString(11, memberVO.getNick_name());

				isSuccess = psmt.execute();

				con.commit();

				con.setAutoCommit(true);
			} catch (SQLException e) {
				isSuccess = false;
				con.rollback();
			}

		} catch (SQLException e) {
			isSuccess = false;
			e.printStackTrace();
		}

		return isSuccess;
	}

	private static final String GET_ALL_STMT = "SELECT MEMBER_ID, PASSWORD, NAME, GENDER, to_char(BIRTHDAY,'yyyy-mm-dd'), CELLPHONE, EMAIL, VALIDATION_STATUS, to_char(REGISTERATION_DATE,'yyyy-mm-dd'), SELF_INTRODUCTION, NICK_NAME, POINT "
			+ "FROM MEMBER " + "ORDER BY MEMBER_ID";

	@Override
	public List<MemberVO> getAll() {

		List<MemberVO> list = new ArrayList<MemberVO>();

		try (Connection con = ds.getConnection();
				PreparedStatement psmt = con.prepareStatement(GET_ALL_STMT);
				ResultSet rs = psmt.executeQuery();) {

			while (rs.next()) {
				MemberVO memberVO = new MemberVO();
				memberVO.setMember_id(rs.getString("Member_id"));
				memberVO.setPassword(rs.getString("Password"));
				memberVO.setName(rs.getString("Name"));
				memberVO.setGender(rs.getInt("Gender"));
				memberVO.setBirthday(rs.getDate("to_char(BIRTHDAY,'yyyy-mm-dd')"));
				memberVO.setCellphone(rs.getString("Cellphone"));
				memberVO.setEmail(rs.getString("Email"));
				memberVO.setValidation_status(rs.getInt("Validation_status"));
				memberVO.setRegisteration_date(rs.getDate("to_char(REGISTERATION_DATE,'yyyy-mm-dd')"));
				memberVO.setSelf_introduction(rs.getString("Self_introduction"));
				memberVO.setNick_name(rs.getString("Nick_name"));
				memberVO.setPoint(rs.getInt("Point"));

				list.add(memberVO);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	private static final String GET_ONE_STMT = "SELECT MEMBER_ID, PASSWORD, NAME, GENDER, to_char(BIRTHDAY,'yyyy-mm-dd'), CELLPHONE, EMAIL, VALIDATION_STATUS, to_char(REGISTERATION_DATE,'yyyy-mm-dd'), SELF_INTRODUCTION, NICK_NAME, POINT "
			+ "FROM MEMBER " + "WHERE MEMBER_ID = ?";

	@Override
	public MemberVO findByPrimaryKey(String member_id) {
		MemberVO memberVO = null;

		try (Connection con = ds.getConnection(); PreparedStatement psmt = con.prepareStatement(GET_ONE_STMT);) {
			psmt.setString(1, member_id);

			try (ResultSet rs = psmt.executeQuery();) {
				if (rs.next()) {
					memberVO = new MemberVO();
					memberVO.setMember_id(rs.getString("Member_id"));
					memberVO.setPassword(rs.getString("Password"));
					memberVO.setName(rs.getString("Name"));
					memberVO.setGender(rs.getInt("Gender"));
					memberVO.setBirthday(rs.getDate("to_char(BIRTHDAY,'yyyy-mm-dd')"));
					memberVO.setCellphone(rs.getString("Cellphone"));
					memberVO.setEmail(rs.getString("Email"));
					memberVO.setValidation_status(rs.getInt("Validation_status"));
					memberVO.setRegisteration_date(rs.getDate("to_char(REGISTERATION_DATE,'yyyy-mm-dd')"));
					memberVO.setSelf_introduction(rs.getString("Self_introduction"));
					memberVO.setNick_name(rs.getString("Nick_name"));
					memberVO.setPoint(rs.getInt("Point"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return memberVO;
	}

	private static final String DELETE = "DELETE FROM MEMBER " + "where MEMBER_ID = ?";

	@Override
	public void delete(String member_id) {
		try (Connection con = ds.getConnection();) {

			con.setAutoCommit(false);

			try (PreparedStatement psmt = con.prepareStatement(DELETE);) {
				psmt.setString(1, member_id);

				psmt.executeUpdate();

				con.commit();

				con.setAutoCommit(true);
			} catch (SQLException e) {
				con.rollback();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String UPDATE = "UPDATE MEMBER "
			+ "set PASSWORD = ?, NAME = ?, GENDER = ?, BIRTHDAY = ?, CELLPHONE = ?, EMAIL = ?, VALIDATION_STATUS = ?, REGISTERATION_DATE = ?, SELF_INTRODUCTION = ?, NICK_NAME = ? "
			+ "where MEMBER_ID = ?";

	@Override
	public boolean update(MemberVO memberVO) {
		boolean isSuccess = false;

		try (Connection con = ds.getConnection();) {

			con.setAutoCommit(false);

			try (PreparedStatement psmt = con.prepareStatement(UPDATE);) {
				psmt.setString(1, memberVO.getPassword());
				psmt.setString(2, memberVO.getName());
				psmt.setInt(3, memberVO.getGender());
				psmt.setDate(4, memberVO.getBirthday());
				psmt.setString(5, memberVO.getCellphone());
				psmt.setString(6, memberVO.getEmail());
				psmt.setInt(7, memberVO.getValidation_status());
				psmt.setDate(8, memberVO.getRegisteration_date());
				psmt.setString(9, memberVO.getSelf_introduction());
				psmt.setString(10, memberVO.getNick_name());

				// where 的條件設定：主鍵
				psmt.setString(11, memberVO.getMember_id());

				isSuccess = psmt.execute();

				con.commit();

				con.setAutoCommit(true);
			} catch (SQLException e) {
				isSuccess = false;
				con.rollback();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			isSuccess = false;
			e.printStackTrace();
		}

		return isSuccess;

	}

	private static final String LOGIN_CHECK = "SELECT PASSWORD FROM (SELECT PASSWORD FROM member WHERE MEMBER_ID = ?) WHERE PASSWORD = ?";

	public boolean loginCheck(String member_id, String password) {

		try (Connection con = ds.getConnection(); PreparedStatement psmt = con.prepareStatement(LOGIN_CHECK);) {
			psmt.setString(1, member_id);
			psmt.setString(2, password);

			try (ResultSet rs = psmt.executeQuery();) {
				rs.last();
				if (rs.getRow() >= 2) {
					throw new RuntimeException("帳號兩筆以上");
				}
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("loginCheck出問題了" + e.toString());
		}
	}

	private static final String EXISTENCE_CHECK = "select MEMBER_ID from MEMBER where MEMBER_ID = ?";

	@Override
	public boolean isAccountExisted(String member_id) {
		boolean isSuccess = false;

		try (Connection con = ds.getConnection(); PreparedStatement psmt = con.prepareStatement(EXISTENCE_CHECK);) {
			psmt.setString(1, member_id);

			isSuccess = psmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isSuccess;
	}

	private static final String SELECT_POINT = "select POINT from MEMBER where MEMBER_ID = ?";

	private static final String UPDATE_POINT = "update MEMBER set POINT = (( select POINT from MEMBER where MEMBER_ID = ? ) + ? ) where MEMBER_ID = ?";

	// 存值點數順便回傳目前點數
	public Integer purchasePoint_getPoint(String member_id, Integer amount) {
		Integer point = 0;

		try (Connection con = ds.getConnection();) {

			con.setAutoCommit(false);

			try (PreparedStatement psmtSelect = con.prepareStatement(SELECT_POINT);
					PreparedStatement psmtUpdate = con.prepareStatement(UPDATE_POINT);) {

				// 設定存值點數量及會員
				psmtUpdate.setString(1, member_id);
				psmtUpdate.setInt(2, amount);
				psmtUpdate.setString(3, member_id);
				psmtUpdate.execute();

				// 查詢用
				psmtSelect.setString(1, member_id);

				try (ResultSet rs = psmtSelect.executeQuery()) {
					if (rs.next()) {
						point = rs.getInt(1);
					}
				}

				con.commit();

				con.setAutoCommit(true);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				con.rollback();
				e.printStackTrace();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return point;
	}

	public boolean purchasePoint_Android(String member_id, Integer amount) {
		boolean isSuccess = false;

		try (Connection con = ds.getConnection();) {

			con.setAutoCommit(false);

			try (PreparedStatement psmtUpdate = con.prepareStatement(UPDATE_POINT);) {

				// 設定存值點數量及會員
				psmtUpdate.setString(1, member_id);
				psmtUpdate.setInt(2, amount);
				psmtUpdate.setString(3, member_id);

				isSuccess = psmtUpdate.execute();

				con.commit();

				con.setAutoCommit(true);

			} catch (SQLException e) {
				isSuccess = false;
				con.rollback();
			}
		} catch (SQLException e) {
			isSuccess = false;
			e.printStackTrace();
		}

		return isSuccess;
	}

	private static final String GET_ONE_PROFILE = "SELECT PROFILE FROM member WHERE member_id = ?";

	public byte[] getOneProfile(String member_id) {
		byte[] profile = null;
		try (Connection con = ds.getConnection(); PreparedStatement psmt = con.prepareStatement(GET_ONE_PROFILE);) {

			psmt.setString(1, member_id);

			try (ResultSet rs = psmt.executeQuery();) {
				profile = rs.getBytes("PROFILE");
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return profile;
	}

	@Override
	public boolean isPasswordCorrect(String member_id, String password) {
		// TODO Auto-generated method stub
		return super.isPasswordCorrect(member_id, password);
	}

	@Override
	public void purchasePoint(String member_id, Integer amount) {
		super.purchasePoint(member_id, amount);

	}

	private static final String UPDATE_POINT_PAY = "update MEMBER set POINT = (( select POINT from MEMBER where MEMBER_ID = ? ) - ? ) where MEMBER_ID = ?";

	// 付出點數順便回傳是否成功
	public boolean payPoint_notCommit(String member_id, Integer amount, Connection con) {
		Integer point = null;

		try (PreparedStatement psmtSelect = con.prepareStatement(SELECT_POINT);
				PreparedStatement psmtUpdate = con.prepareStatement(UPDATE_POINT_PAY);) {
			// 設定存值點數量及會員
			psmtUpdate.setString(1, member_id);
			psmtUpdate.setInt(2, amount);
			psmtUpdate.setString(3, member_id);
			psmtUpdate.execute();

			// 查詢用
			psmtSelect.setString(1, member_id);

			try (ResultSet rs = psmtSelect.executeQuery()) {
				if (rs.next()) {
					point = rs.getInt(1);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		return (point != null && point >= 0);
	}
}
