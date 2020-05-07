package android.com.donation.model;

import java.util.List;

public interface DonationRecordDAO_interface {
	public void insert(DonationRecordVO donationRecordVO);

	public void update(DonationRecordVO donationRecordVO);

	public Boolean delete(String donation_id);

	public DonationRecordVO findByDonationID(String donation_id);

	public List<DonationRecordVO> getAll();

}
