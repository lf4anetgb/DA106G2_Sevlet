package android.com.live.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import android.com.tools.MyData;

public class LiveJNDIDAO implements LiveDAO_interface {
	private static LiveJNDIDAO dao = new LiveJNDIDAO();
	private static DataSource ds = null;
	// 載入Driver
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(MyData.DRIVER_JNDI);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private static final String INSERT_STMT = "INSERT INTO LIVE (LIVE_ID, MEMBER_ID, LIVE_TIME, VIDEOADDRESS, STATUS, PICTURE, TEASER_CONTENT, TITLE, WATCHED_NUM) "
			+ "VALUES ('LLN'||LPAD(to_char(LIVE_ID_SEQ.NEXTVAL), 7, '0'), ?, ?, ?, ?, ?, ?, ?, ?)";

	@Override
	public Boolean insert(LiveVO liveVO) {
		Boolean status = false;

		try (Connection con = ds.getConnection();) {

			// 判斷是否有影片可上傳
			if (liveVO.getVideo() != null) {
				dao.daoUpdateVideo(liveVO.getVideoAddress(), liveVO.getVideo());
			}

			try (PreparedStatement pstmt = con.prepareStatement(INSERT_STMT);) {

				con.setAutoCommit(false);// 關閉AutoCommit

				pstmt.setString(1, liveVO.getMember_id());
				pstmt.setTimestamp(2, liveVO.getLive_time());
				pstmt.setString(3, liveVO.getVideoAddress());
				pstmt.setInt(4, liveVO.getStatus());

				// 判斷有無圖片
				if (liveVO.getPicture() != null) {
//					Blob blob = BLOB.createTemporary(con, true, BLOB.DURATION_SESSION);
//					blob.setBytes(1, liveVO.getPicture());
//					pstmt.setBlob(5, blob);
					pstmt.setBytes(5, liveVO.getPicture());
				} else {
					pstmt.setBytes(5, null);
				}

				pstmt.setString(6, liveVO.getTeaser_content());
				pstmt.setString(7, liveVO.getTitle());
				pstmt.setInt(8, liveVO.getWatched_num());

				status = pstmt.executeUpdate() > 0;
				con.commit();

			} catch (Exception e) {

				// 抓取檔案，如果有就刪除
				if (liveVO.getVideoAddress() != null) {
					File file = new File(liveVO.getVideoAddress());
					if (file.exists()) {
						file.delete();
					}
				}

				if (con != null) {
					con.rollback();
				}

				status = false;

				throw new RuntimeException("insert上傳DB發生錯誤" + e.getMessage());
			}
		} catch (Exception e) {

			throw new RuntimeException("insert連線發生錯誤" + e.getMessage());
		}

		return status;
	}

	private static final String INSERT_NO_BLOB_STMT = "INSERT INTO LIVE (LIVE_ID, MEMBER_ID, LIVE_TIME, VIDEOADDRESS, STATUS, TEASER_CONTENT, TITLE, WATCHED_NUM) "
			+ "VALUES ('LLN'||LPAD(to_char(LIVE_ID_SEQ.NEXTVAL), 7, '0'), ?, ?, ?, ?, ?, ?, ?, ?)";

	@Override
	public Boolean insertNoBLOB(LiveVO liveVO) {
		Boolean status = false;

		try (Connection con = ds.getConnection();) {

			try (PreparedStatement pstmt = con.prepareStatement(INSERT_NO_BLOB_STMT);) {

				con.setAutoCommit(false);// 關閉AutoCommit

				pstmt.setString(1, liveVO.getMember_id());
				pstmt.setTimestamp(2, liveVO.getLive_time());
				pstmt.setString(3, liveVO.getVideoAddress());
				pstmt.setInt(4, liveVO.getStatus());
				pstmt.setString(6, liveVO.getTeaser_content());
				pstmt.setString(7, liveVO.getTitle());
				pstmt.setInt(8, liveVO.getWatched_num());

				status = pstmt.executeUpdate() > 0;
				con.commit();

			} catch (Exception e) {

				if (con != null) {
					con.rollback();
				}

				status = false;

				throw new RuntimeException("insertNoBLOB上傳DB發生錯誤" + e.getMessage());
			}
		} catch (Exception e) {

			throw new RuntimeException("insertNoBLOB連線發生錯誤" + e.getMessage());
		}

		return status;
	}

	private static final String UPDATE = "UPDATE LIVE SET MEMBER_ID=?, LIVE_TIME=?, VIDEO=?, STATUS=?, PICTURE=?, TEASER_CONTENT=?, TITLE=?, WATCHED_NUM=? WHERE LIVE_ID = ?";

	@Override
	public Boolean update(LiveVO liveVO) {
		File fileBackup = null;
		Boolean status = false;

		try (Connection con = ds.getConnection();) {

			// 判斷是否有影片可上傳
			if (liveVO.getVideo() != null) {
				fileBackup = dao.daoUpdateVideo(liveVO.getVideoAddress(), liveVO.getVideo());
			}

			try (PreparedStatement pstmt = con.prepareStatement(UPDATE);) {

				con.setAutoCommit(false);

				pstmt.setString(1, liveVO.getMember_id());
				pstmt.setTimestamp(2, liveVO.getLive_time());
				pstmt.setString(3, liveVO.getVideoAddress());
				pstmt.setInt(4, liveVO.getStatus());

//				Blob blob = BLOB.createTemporary(con, true, BLOB.DURATION_SESSION);
//				blob.setBytes(1, liveVO.getPicture());
//				pstmt.setBlob(5, blob);
				pstmt.setBytes(5, liveVO.getPicture());

				pstmt.setString(6, liveVO.getTeaser_content());
				pstmt.setString(7, liveVO.getTitle());
				pstmt.setInt(8, liveVO.getWatched_num());
				pstmt.setString(9, liveVO.getLive_id());

				status = pstmt.executeUpdate() > 0;
				con.commit();

			} catch (Exception e) {
				status = false;

				if (liveVO.getVideoAddress() != null) {
					File file = new File(liveVO.getVideoAddress());
					// 判斷檔案是否有備份檔，有就把檔案刪除並把備份檔改回
					if (!fileBackup.getName().equals(file.getName())) {

						if (file.isFile()) {
							file.delete();
						}

						fileBackup.renameTo(new File(liveVO.getVideoAddress()));
					}
				}

				if (con != null) {
					con.rollback();
				}

				throw new RuntimeException("update上傳DB發生錯誤" + e.getMessage());
			} finally {
				if (status && fileBackup != null) {
					fileBackup.delete();
				}
			}
		} catch (Exception e) {

			throw new RuntimeException("update連線發生錯誤" + e.getMessage());
		}

		return status;
	}

	private static final String UPDATE_NO_BLOB = "UPDATE LIVE SET MEMBER_ID=?, LIVE_TIME=?, VIDEOADDRESS=?, STATUS=?, TEASER_CONTENT=?, TITLE=?, WATCHED_NUM=? WHERE LIVE_ID = ?";

	@Override
	public Boolean updateNoBLOB(LiveVO liveVO) {
		Boolean status = false;

		try (Connection con = ds.getConnection();) {

			try (PreparedStatement pstmt = con.prepareStatement(UPDATE_NO_BLOB);) {

				con.setAutoCommit(false);

				pstmt.setString(1, liveVO.getMember_id());
				pstmt.setTimestamp(2, liveVO.getLive_time());
				pstmt.setString(3, liveVO.getVideoAddress());
				pstmt.setInt(4, liveVO.getStatus());
				pstmt.setString(5, liveVO.getTeaser_content());
				pstmt.setString(6, liveVO.getTitle());
				pstmt.setInt(7, liveVO.getWatched_num());
				pstmt.setString(8, liveVO.getLive_id());

				status = pstmt.executeUpdate() > 0;
				con.commit();

			} catch (Exception e) {

				if (con != null) {
					con.rollback();
				}

				status = false;

				throw new RuntimeException("updateNoBLOB上傳DB發生錯誤" + e.getMessage());
			}
		} catch (Exception e) {

			throw new RuntimeException("updateNoBLOB連線發生錯誤" + e.getMessage());
		}

		return status;
	}

	// DAO專用上傳影片動作
	private File daoUpdateVideo(String path, byte[] video) {
		File filePath = new File(path.substring(0, (path.lastIndexOf("\\") + 1)));
		File fileNew = new File(path);
		File fileBackup = new File(fileNew.getPath() + ".bak");

		// 如更新影片就將檔案改名
		if (fileNew.exists()) {
			fileNew.renameTo(fileBackup);
		}

		if (!filePath.exists()) {
			filePath.mkdirs();// 創建路徑
		}

		try (FileOutputStream out = new FileOutputStream(path);
				BufferedOutputStream bOut = new BufferedOutputStream(out);) {

			bOut.write(video);// 影片檔案寫入

		} catch (IOException e) {

			// 如發生錯誤就把改名的檔案改回
			fileBackup.renameTo(fileNew);
			throw new RuntimeException("daoUpdateVideo檔案上傳發生錯誤" + e.getMessage());
		}

		// 如有備份檔
		if (fileBackup.exists()) {
			return fileBackup;
		}

		return null;

	}

	private static final String GET_ONE_STMT = "SELECT LIVE_ID, MEMBER_ID, LIVE_TIME, VIDEOADDRESS, STATUS, PICTURE, TEASER_CONTENT, TITLE, WATCHED_NUM FROM live WHERE LIVE_ID = ?";

	@Override
	public LiveVO findByLiveID(String live_id) {
		LiveVO live = null;
		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ONE_STMT);) {
			pstmt.setString(1, live_id);
			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					String liveId = rs.getString("LIVE_ID"), memberId = rs.getString("MEMBER_ID"),
							videoAddress = rs.getString("VIDEOADDRESS"), teaserContent = rs.getString("TEASER_CONTENT"),
							title = rs.getString("TITLE");
					Date liveTime = rs.getTimestamp("LIVE_TIME");
					Integer status = rs.getInt("STATUS"), watcherNum = rs.getInt("WATCHED_NUM");
					byte[] picture = rs.getBytes("PICTURE"), video = null;

					if (!(videoAddress == null) && (new File(videoAddress).isFile())) {
						video = new byte[(int) (new File(videoAddress).length())];
						try (FileInputStream in = new FileInputStream(videoAddress);
								BufferedInputStream bIn = new BufferedInputStream(in)) {
							bIn.read(video);
						}
					}

					live = new LiveVO(liveId, memberId, videoAddress, teaserContent, title, picture, video, liveTime,
							status, watcherNum);

				}
			}

		} catch (Exception e) {

			throw new RuntimeException("查詢發生錯誤：findByLiveID" + e.getMessage());
		}

		return live;
	}

	private static final String GET_ONE_NO_BLOB_STMT = "SELECT LIVE_ID, MEMBER_ID, LIVE_TIME, VIDEOADDRESS, STATUS, TEASER_CONTENT, TITLE, WATCHED_NUM FROM live WHERE LIVE_ID = ?";

	@Override
	public LiveVO findNoBLOBByLiveID(String live_id) {
		LiveVO live = null;
		try (Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(GET_ONE_NO_BLOB_STMT);) {
			pstmt.setString(1, live_id);
			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					String liveId = rs.getString("LIVE_ID"), memberId = rs.getString("MEMBER_ID"),
							videoAddress = rs.getString("VIDEOADDRESS"), teaserContent = rs.getString("TEASER_CONTENT"),
							title = rs.getString("TITLE");
					Date liveTime = rs.getTimestamp("LIVE_TIME");
					Integer status = rs.getInt("STATUS"), watcherNum = rs.getInt("WATCHED_NUM");

					live = new LiveVO(liveId, memberId, videoAddress, teaserContent, title, liveTime, status,
							watcherNum);

				}
			}

		} catch (Exception e) {

			throw new RuntimeException("查詢發生錯誤：findNoBLOBByLiveID" + e.getMessage());
		}

		return live;
	}

	private static final String GET_ALL_STMT = "SELECT LIVE_ID, MEMBER_ID, LIVE_TIME, VIDEOADDRESS, STATUS, PICTURE, TEASER_CONTENT, TITLE, WATCHED_NUM FROM live ORDER BY live_id";

	@Override
	public List<LiveVO> getAll() {
		List<LiveVO> lives = new ArrayList<>();

		try (Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(GET_ALL_STMT);
				ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {
				String liveId = rs.getString("LIVE_ID"), memberId = rs.getString("MEMBER_ID"),
						videoAddress = rs.getString("VIDEOADDRESS"), teaserContent = rs.getString("TEASER_CONTENT"),
						title = rs.getString("TITLE");
				Date liveTime = rs.getTimestamp("LIVE_TIME");
				Integer status = rs.getInt("STATUS"), watcherNum = rs.getInt("WATCHED_NUM");
				byte[] picture = rs.getBytes("PICTURE"), video = null;

				if (!(videoAddress == null) && (new File(videoAddress).isFile())) {
					video = new byte[(int) (new File(videoAddress).length())];
					try (FileInputStream in = new FileInputStream(videoAddress);
							BufferedInputStream bIn = new BufferedInputStream(in)) {
						bIn.read(video);
					}
				}

				LiveVO live = new LiveVO(liveId, memberId, videoAddress, teaserContent, title, picture, video, liveTime,
						status, watcherNum);
				lives.add(live);
			}

		} catch (Exception e) {

			throw new RuntimeException("查詢發生錯誤：getAll" + e.getMessage());
		}

		return lives;
	}

	private static final String GET_ALL_NO_BLOB_STMT = "SELECT LIVE_ID, MEMBER_ID, LIVE_TIME, VIDEOADDRESS, STATUS, PICTURE, TEASER_CONTENT, TITLE, WATCHED_NUM FROM live ORDER BY live_id";

	@Override
	public List<LiveVO> getAllNoBLOB() {
		List<LiveVO> lives = new ArrayList<>();

		try (Connection con = ds.getConnection();
				PreparedStatement pstmt = con.prepareStatement(GET_ALL_NO_BLOB_STMT);
				ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {
				String liveId = rs.getString("LIVE_ID"), memberId = rs.getString("MEMBER_ID"),
						videoAddress = rs.getString("VIDEOADDRESS"), teaserContent = rs.getString("TEASER_CONTENT"),
						title = rs.getString("TITLE");
				Date liveTime = rs.getTimestamp("LIVE_TIME");
				Integer status = rs.getInt("STATUS"), watcherNum = rs.getInt("WATCHED_NUM");

				LiveVO live = new LiveVO(liveId, memberId, videoAddress, teaserContent, title, liveTime, status,
						watcherNum);
				lives.add(live);
			}

		} catch (Exception e) {

			throw new RuntimeException("查詢發生錯誤：getAllNoBLOB" + e.getMessage());
		}

		return lives;
	}

	private static final String UPDATE_VLIDEO = "UPDATE LIVE SET VIDEOADDRESS=? WHERE LIVE_ID = ?";

	@Override
	public Boolean updateVideoByLiveID(String liveID, String path, byte[] video) {
		File fileBackup = null;
		Boolean status = false;

		try (Connection con = ds.getConnection();) {

			// 判斷是否有影片可上傳
			if (video != null) {
				fileBackup = dao.daoUpdateVideo(path, video);
			}

			try (PreparedStatement pstmt = con.prepareStatement(UPDATE_VLIDEO);) {

				con.setAutoCommit(false);

				pstmt.setString(1, path);
				pstmt.setString(2, liveID);

				status = pstmt.executeUpdate() > 0;
				con.commit();

			} catch (Exception e) {
				status = false;

				if (path != null) {
					File file = new File(path);
					// 判斷檔案是否有備份檔，有就把檔案刪除並把備份檔改回
					if (!fileBackup.getName().equals(file.getName())) {

						if (file.isFile()) {
							file.delete();
						}

						fileBackup.renameTo(new File(path));
					}
				}

				if (con != null) {
					con.rollback();
				}

				throw new RuntimeException("updateVideoByLiveID上傳DB發生錯誤" + e.getMessage());
			} finally {
				if (status && fileBackup != null) {
					fileBackup.delete();
				}
			}
		} catch (Exception e) {

			throw new RuntimeException("updateVideoByLiveID連線發生錯誤" + e.getMessage());
		}

		return status;

	}

	private static final String UPDATE_PICTURE = "UPDATE LIVE SET PICTURE=? WHERE LIVE_ID = ?";

	@Override
	public Boolean updatePictureByLiveID(String liveID, byte[] picture) {
		Boolean status = false;

		try (Connection con = ds.getConnection();) {
			try (PreparedStatement pstmt = con.prepareStatement(UPDATE_PICTURE);) {

				con.setAutoCommit(false);

				pstmt.setBytes(1, picture);
				pstmt.setString(2, liveID);

				status = pstmt.executeUpdate() > 0;
				con.commit();

			} catch (Exception e) {

				status = false;

				if (con != null) {
					con.rollback();
				}

				throw new RuntimeException("updatePictureByLiveID上傳DB發生錯誤" + e.getMessage());
			}
		} catch (Exception e) {

			throw new RuntimeException("updatePictureByLiveID連線發生錯誤" + e.getMessage());
		}
		return status;

	}

	private static final String GET_ONE_VIDEO = "SELECT VIDEOADDRESS FROM live WHERE LIVE_ID = ?";

	@Override
	public byte[] getVideoByLiveID(String live_id) {
		byte[] video = null;
		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ONE_VIDEO);) {
			pstmt.setString(1, live_id);
			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					String videoAddress = rs.getString("VIDEOADDRESS");

					if (!(videoAddress == null) && (new File(videoAddress).isFile())) {
						video = new byte[(int) (new File(videoAddress).length())];
						try (FileInputStream in = new FileInputStream(videoAddress);
								BufferedInputStream bIn = new BufferedInputStream(in)) {
							bIn.read(video);
						}
					}

				}
			}

		} catch (Exception e) {

			throw new RuntimeException("查詢發生錯誤：getVideoByLiveID" + e.getMessage());
		}

		return video;
	}

	private static final String GET_ONE_PICTURE = "SELECT PICTURE FROM live WHERE LIVE_ID = ?";

	@Override
	public byte[] getPictureByLiveID(String live_id) {
		byte[] picture = null;
		try (Connection con = ds.getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ONE_PICTURE);) {
			pstmt.setString(1, live_id);
			try (ResultSet rs = pstmt.executeQuery();) {
				while (rs.next()) {
					picture = rs.getBytes("PICTURE");

				}
			}

		} catch (Exception e) {

			throw new RuntimeException("查詢發生錯誤：getPicture" + e.getMessage());
		}

		return picture;
	}

	public static void main(String[] args) {
//		dao.testInsert();

		// 找全部
//		List<LiveVO> lives = dao.getAll();
//		System.out.println(lives.toString());

		// 找一個
//		System.out.println(dao.findByPrimaryKey("LLN0000006").toString());

//		// 更新一個
//		dao.testUpdate(dao.findByPrimaryKey("LLN0000008"));

	}

	private void testInsert() {
		// 建立日期
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd"); // 設定日期輸入格式
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss"); // 設定日期輸入格式
		File fileImg = new File("img/01.jpg");
		File fileVideo = new File("video_out/01.mp4");

		String live_id = null, // 直播ID
				member_id = "MMN0000002", // 會員ID
				videoAddress = null, // 影片位子
				teaser_content = "呵呵", // 直播預告內容
				title = "第二支"; // 直播標頭
		byte[] picture = new byte[(int) fileImg.length()], // 預覽圖片
				video = new byte[(int) fileVideo.length()]; // 影片本體
		Date live_time = null; // 開始時間
		StringBuffer videoSb = new StringBuffer();
		String videoFileName = fileVideo.getName();

		// 影片放置位子：video/該會員的ID/
		// 影片上傳後強制改名為：上船當下時間yyyyMMddHHmmss
		videoSb.append("video/").append(member_id).append("/").append(df1.format(new Date()))
				.append(videoFileName.substring(videoFileName.indexOf("."), videoFileName.length()));
		videoAddress = videoSb.toString();

		// 圖片上傳測試
		try (BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(fileImg));) {

			bIn.read(picture);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 影片上傳測試
		try (BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(fileVideo));) {

			bIn.read(video);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			live_time = df.parse("2020/01/02");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Integer status = 0, // 直播狀態
				watched_num = 0; // 觀看人數

		LiveVO liveVO = new LiveVO(live_id, member_id, videoAddress, teaser_content, title, picture, video, live_time,
				status, watched_num);
		dao.insert(liveVO);
	}

	private void testUpdate(LiveVO liveVO) {
		// 建立日期
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd"); // 設定日期輸入格式
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss"); // 設定日期輸入格式
		File fileImg = new File("img/02.jpg");
		File fileVideo = new File("video_out/02.mp4");

		String teaser_content = "呵呵a", // 直播預告內容
				title = "第三支"; // 直播標頭
		byte[] picture = new byte[(int) fileImg.length()], // 預覽圖片
				video = new byte[(int) fileVideo.length()]; // 影片本體
		Date live_time = null; // 開始時間

		// 影片放置位子：video/該會員的ID/
		// 影片上傳後強制改名為：上船當下時間yyyyMMddHHmmss

		// 圖片上傳測試
		try (BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(fileImg));) {

			bIn.read(picture);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 影片上傳測試
		try (BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(fileVideo));) {

			bIn.read(video);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			live_time = df.parse("2020/01/03");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Integer status = 1, // 直播狀態
				watched_num = 1; // 觀看人數

		LiveVO liveVO_ = new LiveVO(liveVO.getLive_id(), liveVO.getMember_id(), liveVO.getVideoAddress(),
				teaser_content, title, picture, video, live_time, status, watched_num);
		dao.update(liveVO_);
	}

//	private static final String DELETE = "DELETE FROM LIVE WHERE LIVE_ID = ?";
//
//	@Override
//	public void delete(String live_id) {
//		// TODO Auto-generated method stub
//
//	}

}
