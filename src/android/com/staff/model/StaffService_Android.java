package android.com.staff.model;

import com.staff.model.StaffDAO_interface;
import com.staff.model.StaffService;
import com.staff.model.StaffVO;

public class StaffService_Android extends StaffService {
	private StaffDAO_interface dao;

	public StaffService_Android() {
		dao = new StaffJNDIDAO_Android();
	}

	@Override
	public StaffVO getLogin(String sf_account, String sf_password) {
		// TODO Auto-generated method stub
		return dao.findByAccount(sf_account, sf_password);
	}
}
