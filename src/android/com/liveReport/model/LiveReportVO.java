package android.com.liveReport.model;

import java.sql.Date;

public class LiveReportVO {
	private String live_report_id, // 直播檢舉ID
			live_id, // 直播ID
			member_id, // 會員ID
			live_report_content; // 直播檢舉內容
	private Integer report_status;// 直播檢舉處理狀態
	private Date date; // 檢舉日期

	public LiveReportVO(String live_report_id, String live_id, String member_id, String live_report_content,
			Integer report_status, Date date) {
		super();
		this.live_report_id = live_report_id;
		this.live_id = live_id;
		this.member_id = member_id;
		this.live_report_content = live_report_content;
		this.report_status = report_status;
		this.date = date;
	}

	public LiveReportVO() {
		super();
	}

	public String getLive_report_id() {
		return live_report_id;
	}

	public void setLive_report_id(String live_report_id) {
		this.live_report_id = live_report_id;
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

	public String getLive_report_content() {
		return live_report_content;
	}

	public void setLive_report_content(String live_report_content) {
		this.live_report_content = live_report_content;
	}

	public Integer getReport_status() {
		return report_status;
	}

	public void setReport_status(Integer report_status) {
		this.report_status = report_status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
