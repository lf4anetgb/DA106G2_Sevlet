package android.com.liveReport.model;

import java.util.List;

public interface LiveReportDAO_interface {
	public void insert(LiveReportVO liveReportVO);

	public void update(LiveReportVO liveReportVO);

	public Boolean delete(String live_report_id);

	public LiveReportVO findByLiveReportID(String live_report_id);

	public List<LiveReportVO> getAll();
}
