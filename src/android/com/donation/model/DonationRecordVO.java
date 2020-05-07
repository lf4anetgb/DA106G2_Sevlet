package android.com.donation.model;

import java.sql.Timestamp;
import java.util.Date;

public class DonationRecordVO implements java.io.Serializable {
	private String donation_id, // 斗內ID
			live_id, // 直播ID
			member_id, // 會員ID
			donation_message; // 斗內留言
	private Timestamp donation_time; // 斗內時間
	private Double donation_cost; // 斗內金額

	public DonationRecordVO() {
		super();
	}

	public DonationRecordVO(String donation_id, String live_id, String member_id, String donation_message,
			Date donation_time, Double donation_cost) {
		super();
		this.donation_id = donation_id;
		this.live_id = live_id;
		this.member_id = member_id;
		this.donation_message = donation_message;
		this.donation_time = new Timestamp(donation_time.getTime());
		this.donation_cost = donation_cost;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("donation_id=").append(donation_id).append(", live_id=").append(live_id).append(", member_id=")
				.append(member_id).append(", donation_message=").append(donation_message).append(", donation_time=")
				.append(donation_time).append(", donation_cost=").append(donation_cost).append("\r\n");
		return sb.toString();
	}

	public String getDonation_id() {
		return donation_id;
	}

	public void setDonation_id(String donation_id) {
		this.donation_id = donation_id;
	}

	public String getLive_id() {
		return live_id;
	}

	public void setLive_id(String live_id) {
		this.live_id = live_id;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getDonation_message() {
		return donation_message;
	}

	public void setDonation_message(String donation_message) {
		this.donation_message = donation_message;
	}

	public Timestamp getDonation_time() {
		return donation_time;
	}

	public void setDonation_time(Date donation_time) {
		this.donation_time = new Timestamp(donation_time.getTime());
	}

	public Double getDonation_cost() {
		return donation_cost;
	}

	public void setDonation_cost(Double donation_cost) {
		this.donation_cost = donation_cost;
	}

}
