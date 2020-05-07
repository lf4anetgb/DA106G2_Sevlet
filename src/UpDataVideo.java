import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.com.live.model.LiveService;
import android.com.live.model.LiveVO;

@WebServlet(name = "LiveTestUpdata", urlPatterns = { "/LiveTestUpdata" })
public class UpDataVideo extends HttpServlet {

	public static void main(String[] args) {
		LiveService dao = new LiveService();
		List<LiveVO> videos = dao.getAllNoBLOB();
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // 設定日期輸入格式
		for (int i = 0; i < videos.size(); i++) {
			LiveVO live = videos.get(i);
			byte[] video = null, picture = null;
			int j = (int) (Math.random() * 100);
			File fileImg = new File("img/0" + ((int) (j % 2) + 1) + ".jpg"), fileVideo = null;
			String videoAddress = "";
			if (live.getVideoAddress() == null) {
				StringBuffer videoSb = new StringBuffer();
				fileVideo = new File("video_out/0" + ((int) (j % 3) + 1) + ".mp4");
				String videoFileName = fileVideo.getName();
				videoSb.append("/video/").append(live.getMember_id()).append("/").append(df1.format(new Date()))
						.append(videoFileName.substring(videoFileName.indexOf("."), videoFileName.length()));
				videoAddress = videoSb.toString();
			} else {
				videoAddress = live.getVideoAddress();
				fileVideo = new File(videoAddress);
			}
			picture = new byte[(int) fileImg.length()];
			video = new byte[(int) fileVideo.length()]; // 影片本體
			try (BufferedInputStream inVideo = new BufferedInputStream(new FileInputStream(fileVideo));
					BufferedInputStream inImg = new BufferedInputStream(new FileInputStream(fileImg));) {
				inVideo.read(video);
				inImg.read(picture);
			} catch (Exception e) {
				// TODO: handle exception
			}

			dao.updateVideo(live.getLive_id(), videoAddress, video);
			dao.updatePicture(live.getLive_id(), picture);
		}

		System.out.println("OK");

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LiveService dao = new LiveService();
		List<LiveVO> videos = dao.getAll();
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // 設定日期輸入格式
		for (int i = 0; i < videos.size(); i++) {
			LiveVO live = videos.get(i);
			byte[] video = null, picture = null;
			int j = (int) (Math.random() * 100);
			String imagePath = "/img/0" + ((int) (j % 2) + 1) + ".jpg";
			String videoPath = "/video_out/0" + ((int) (j % 3) + 1) + ".mp4";
			File fileImg = new File(imagePath), fileVideo = null;
			String videoAddress = "";
			if (live.getVideoAddress() == null) {
				StringBuffer videoSb = new StringBuffer();
				fileVideo = new File(videoPath);
				String videoFileName = fileVideo.getName();
				videoSb.append(getServletContext().getRealPath("")).append("video\\").append(live.getMember_id())
						.append("\\").append(df1.format(new Date()))
						.append(videoFileName.substring(videoFileName.indexOf("."), videoFileName.length()));
				videoAddress = videoSb.toString();
//				videoAddress = videoAddress.substring((videoAddress.indexOf("\\") + 1), videoAddress.length());
			} else {
				videoAddress = live.getVideoAddress();
				fileVideo = new File(videoAddress);
			}

			ServletContext context = getServletContext();
			try (InputStream inVideo = context.getResourceAsStream(videoPath);
					InputStream inImg = context.getResourceAsStream(imagePath);) {
				int imgSize = inImg.available(), videoSize = inVideo.available();
				picture = new byte[imgSize];
				video = new byte[videoSize]; // 影片本體
				inVideo.read(video, 0, videoSize);
				inImg.read(picture, 0, imgSize);
			} catch (Exception e) {
				System.out.println("err");
			}

			dao.updateVideo(live.getLive_id(), videoAddress, video);
			dao.updatePicture(live.getLive_id(), picture);
		}

		System.out.println("OK");
	}

}
