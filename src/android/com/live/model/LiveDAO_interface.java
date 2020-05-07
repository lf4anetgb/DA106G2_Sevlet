package android.com.live.model;

import java.util.List;

public interface LiveDAO_interface {
	public Boolean insert(LiveVO liveVO);

	public Boolean insertNoBLOB(LiveVO liveVO);

	public Boolean update(LiveVO liveVO);

	public Boolean updateNoBLOB(LiveVO liveVO);

	public Boolean updateVideoByLiveID(String liveID, String path, byte[] video);

	public Boolean updatePictureByLiveID(String liveID, byte[] picture);

	public byte[] getVideoByLiveID(String live_id);

	public byte[] getPictureByLiveID(String live_id);

//	public void delete(String live_id);

	public LiveVO findByLiveID(String live_id);

	public List<LiveVO> getAll();

	public LiveVO findNoBLOBByLiveID(String live_id);

	public List<LiveVO> getAllNoBLOB();
}
