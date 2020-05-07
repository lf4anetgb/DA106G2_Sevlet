package android.com.liveReport.model;

import java.sql.Date;
import java.util.List;

public class LiveReportService {
	private LiveReportDAO_interface dao;

	public LiveReportService() {
		dao = new LiveReportJDBCDAO();
	}

	public LiveReportVO addLiveReport(String live_report_id, String live_id, String member_id,
			String live_report_content, Integer report_status, Date date) {

		LiveReportVO liveReportVO = new LiveReportVO(live_report_id, live_id, member_id, live_report_content,
				report_status, date);

		dao.insert(liveReportVO);

		return liveReportVO;
	}

	public LiveReportVO updateLiveReport(String live_report_id, String live_id, String member_id,
			String live_report_content, Integer report_status, Date date) {

		LiveReportVO liveReportVO = new LiveReportVO(live_report_id, live_id, member_id, live_report_content,
				report_status, date);

		dao.update(liveReportVO);
		;

		return liveReportVO;
	}

	public Boolean deleteLiveReport(String live_report_id) {
		return dao.delete(live_report_id);
	}

	public LiveReportVO getOneDonation(String live_report_id) {
		return dao.findByLiveReportID(live_report_id);
	}

	public List<LiveReportVO> getAll() {
		return dao.getAll();
	}

}
