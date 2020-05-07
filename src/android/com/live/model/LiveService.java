package android.com.live.model;

import java.util.Date;
import java.util.List;

public class LiveService {
	private LiveDAO_interface dao;

	public LiveService() {
		dao = new LiveJNDIDAO();
	}

	public LiveVO addLive(String live_id, String member_id, String videoAddress, String teaser_content, String title,
			byte[] picture, byte[] video, Date live_time, Integer status, Integer watched_num) {

		LiveVO liveVO = new LiveVO(live_id, member_id, videoAddress, teaser_content, title, picture, video, live_time,
				status, watched_num);

		if (dao.insert(liveVO)) {
			List<LiveVO> lives = dao.getAllNoBLOB();
			for (int i = lives.size(); i >= 1; i--) {
				LiveVO live = lives.get(i);
				if (live.toString().equals(liveVO.toString())) {
					return live;
				}
			}
		}

		return null;
	}

	public LiveVO addLiveNoBLOB(String live_id, String member_id, String videoAddress, String teaser_content,
			String title, Date live_time, Integer status, Integer watched_num) {

		LiveVO liveVO = new LiveVO(live_id, member_id, videoAddress, teaser_content, title, live_time, status,
				watched_num);

		if (dao.insertNoBLOB(liveVO)) {
			return liveVO;
		}

		return null;
	}

	public LiveVO addLiveNoBLOB(LiveVO liveVO) {

		if (dao.insertNoBLOB(liveVO)) {
			return liveVO;
		}

		return null;
	}

	public LiveVO updateLive(String live_id, String member_id, String videoAddress, String teaser_content, String title,
			byte[] picture, byte[] video, Date live_time, Integer status, Integer watched_num) {

		LiveVO liveVO = new LiveVO(live_id, member_id, videoAddress, teaser_content, title, picture, video, live_time,
				status, watched_num);

		if (dao.update(liveVO)) {
			return liveVO;
		}

		return null;
	}

	public LiveVO updateLiveNoBLOB(String live_id, String member_id, String videoAddress, String teaser_content,
			String title, Date live_time, Integer status, Integer watched_num) {

		LiveVO liveVO = new LiveVO(live_id, member_id, videoAddress, teaser_content, title, live_time, status,
				watched_num);

		if (dao.updateNoBLOB(liveVO)) {
			return liveVO;
		}

		return null;
	}

	public LiveVO updateLiveNoBLOB(LiveVO liveVO) {

		if (dao.updateNoBLOB(liveVO)) {
			return liveVO;
		}

		return null;
	}

	public LiveVO getOneLive(String live_id) {
		return dao.findByLiveID(live_id);
	}

	public LiveVO getOneLiveNoBLOB(String live_id) {
		return dao.findNoBLOBByLiveID(live_id);
	}

	public List<LiveVO> getAll() {
		return dao.getAll();
	}

	public List<LiveVO> getAllNoBLOB() {
		List<LiveVO> lives = dao.getAllNoBLOB();
		for (int i = 0; i < lives.size(); i++) {
			if (lives.get(i).getStatus() == 0) {
				lives.remove(i);
				i--;
			}
		}
		return lives;
	}

	public Boolean updateVideo(String liveID, String path, byte[] video) {
		return dao.updateVideoByLiveID(liveID, path, video);
	}

	public Boolean updatePicture(String liveID, byte[] picture) {
		return dao.updatePictureByLiveID(liveID, picture);
	}

	public byte[] getOneVideo(String live_id) {
		return dao.getVideoByLiveID(live_id);
	}

	public byte[] getOnePicture(String live_id) {
		return dao.getPictureByLiveID(live_id);
	}

}
