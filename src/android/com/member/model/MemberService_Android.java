package android.com.member.model;

import java.sql.Connection;
import java.util.List;

import com.member.model.MemberDAO_interface;
import com.member.model.MemberVO;

public class MemberService_Android {
	private MemberDAO_interface dao;

	public MemberService_Android() {
		dao = new MemberDAO_Android();
	}

	public boolean isAccountExisted(String member_id) {
		return dao.isAccountExisted(member_id);
	}

	public boolean addNewMember(MemberVO memberVO) {
		return dao.insert(memberVO);
	}

	public MemberVO getMemberById(String member_id) {
		return dao.findByPrimaryKey(member_id);
	}

	public boolean updateMemberData(MemberVO memberVO) {
		return dao.update(memberVO);
	}

	public List<MemberVO> getMemberList() {
		return dao.getAll();
	}

	public boolean purchasePoint_Android(String member_id, Integer amount) {
		return ((MemberDAO_Android) dao).purchasePoint_Android(member_id, amount);
	}

	public boolean isPasswordCorrect(String member_id, String password) {
		return dao.isPasswordCorrect(member_id, password);
	}

	public Integer purchasePoint_getPoint(String member_id, Integer amount) {
		return ((MemberDAO_Android) dao).purchasePoint_getPoint(member_id, amount);
	}

	public byte[] getOneProfile(String member_id) {
		return ((MemberDAO_Android) dao).getOneProfile(member_id);
	}

	public boolean payPoint_notCommit(String member_id, Integer amount, Connection con) {
		return ((MemberDAO_Android) dao).payPoint_notCommit(member_id, amount, con);
	}

}
