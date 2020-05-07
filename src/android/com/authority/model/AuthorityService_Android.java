package android.com.authority.model;

import com.authority.model.AuthorityService;
import com.authority.model.Authority_interface;

public class AuthorityService_Android extends AuthorityService {
	private Authority_interface dao;

	public AuthorityService_Android() {
		dao = new AuthorityJNDIDAO_Android();
	}

	public Boolean checkAuthority(String staff_id, String fun_num) {
		return ((AuthorityJNDIDAO_Android) dao).checkAuthority(staff_id, fun_num);
	}
}
