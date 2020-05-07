package android.com.donation.model;

import java.util.Date;
import java.util.List;

public class DonationRecordService {
	private DonationRecordDAO_interface dao;

	public DonationRecordService() {
		dao = new DonationRecordJDBCDAO();
	}

	public DonationRecordVO addDonation(String donation_id, String live_id, String member_id, String donation_message,
			Date donation_time, Double donation_cost) {

		DonationRecordVO donationRecordVO = new DonationRecordVO(donation_id, live_id, member_id, donation_message,
				donation_time, donation_cost);

		dao.insert(donationRecordVO);

		return donationRecordVO;
	}

	public DonationRecordVO updateDonation(String donation_id, String live_id, String member_id,
			String donation_message, Date donation_time, Double donation_cost) {

		DonationRecordVO donationRecordVO = new DonationRecordVO(donation_id, live_id, member_id, donation_message,
				donation_time, donation_cost);

		dao.update(donationRecordVO);
		;

		return donationRecordVO;
	}

	public Boolean deleteDonation(String donation_id) {
		return dao.delete(donation_id);
	}

	public DonationRecordVO getOneDonation(String donation_id) {
		return dao.findByDonationID(donation_id);
	}

	public List<DonationRecordVO> getAll() {
		return dao.getAll();
	}

}
