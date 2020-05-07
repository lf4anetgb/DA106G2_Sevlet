package android.com.live.controller;

import android.com.live.model.LiveService;
import android.com.live.model.LiveVO;
import android.com.tools.ImageUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LiveServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		BufferedReader br = req.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		System.out.println("input: " + jsonIn);
		LiveService liveDao = new LiveService();
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		String action = jsonObject.get("action").getAsString();

		switch (action) {
		case "addLiveNoBLOB": {
			LiveVO liveVO = gson.fromJson(jsonObject.get("Live").getAsString(), LiveVO.class),
					liveVO_ = liveDao.addLiveNoBLOB(liveVO);
			writeText(res, (liveVO_ == null) ? "false" : gson.toJson(liveVO_));
		}
			return;
		case "updateLiveNoBLOB": {
			LiveVO liveVO = gson.fromJson(jsonObject.get("Live").getAsString(), LiveVO.class),
					liveVO_ = liveDao.updateLiveNoBLOB(liveVO);
			writeText(res, (liveVO_ == null) ? "false" : gson.toJson(liveVO_));
		}
			return;
		case "getOneLiveNoBLOB": {
			String live_id = jsonObject.get("live_id").getAsString();
			LiveVO liveVO = liveDao.getOneLiveNoBLOB(live_id);
			writeText(res, (liveVO == null) ? "false" : gson.toJson(liveVO));
		}
			return;
		case "getAll": {
			List<LiveVO> lives = liveDao.getAllNoBLOB();
			writeText(res, (lives.size() <= 0) ? "false" : gson.toJson(lives));
		}
			return;
		case "updateVideo": {
			String liveID = jsonObject.get("live_id").getAsString();
			LiveVO liveVO = liveDao.getOneLiveNoBLOB(liveID);

			Part videoPart = req.getPart("video");

			if (liveVO == null || videoPart == null || videoPart.getSize() <= 0) {
				writeText(res, "false");
				return;
			}

			byte[] video = new byte[(int) videoPart.getSize()];

			// 呼叫路徑，如沒有就建一個
			String videoAddress = liveVO.getVideoAddress();
			if (videoAddress == null || "".equals(videoAddress)) {
				DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // 用於改名用
				StringBuffer videoSb = new StringBuffer();
				String videoFileName = Paths.get(videoPart.getSubmittedFileName()).getFileName().toString();
				videoSb.append("/video/").append(liveVO.getMember_id()).append("/").append(df1.format(new Date()))
						.append(videoFileName.substring(videoFileName.indexOf("."), videoFileName.length()));
				videoAddress = videoSb.toString();
			}

			try (BufferedInputStream input = new BufferedInputStream(videoPart.getInputStream());) {
				input.read(video);
			} catch (IOException e) {
				writeText(res, "false");
			}

			writeText(res, String.valueOf(liveDao.updateVideo(liveID, videoAddress, video)));

		}
			return;
		case "updatePicture": {
			String liveID = jsonObject.get("live_id").getAsString();
			LiveVO liveVO = liveDao.getOneLiveNoBLOB(liveID);
			Part picturePart = req.getPart("picture");

			if (liveVO == null || picturePart == null || picturePart.getSize() <= 0) {
				writeText(res, "false");
				return;
			}

			byte[] picture = new byte[(int) picturePart.getSize()];

			try (BufferedInputStream input = new BufferedInputStream(picturePart.getInputStream());) {
				input.read(picture);
			} catch (IOException e) {
				writeText(res, "false");
			}

			writeText(res, String.valueOf(liveDao.updatePicture(liveID, picture)));
		}
			return;
		case "getOneVideo": {

			String liveID = jsonObject.get("live_id").getAsString();
			byte[] video = liveDao.getOneVideo(liveID);

			if (video == null || video.length <= 0) {
				writeText(res, "false");
				return;
			}

			res.setContentType("video/mp4");
			res.setContentLength(video.length);

			try (ServletOutputStream sverOut = res.getOutputStream();
					BufferedOutputStream bOut = new BufferedOutputStream(sverOut);) {
				bOut.write(video);
			} catch (Exception e) {
				writeText(res, "false");
				return;
			}
		}
			return;
		case "getOnePicture": {
			String liveID = jsonObject.get("live_id").getAsString();
			int imageSize = jsonObject.get("imageSize").getAsInt();
			byte[] picture = liveDao.getOnePicture(liveID);

			if (picture == null || picture.length <= 0) {
				writeText(res, "false");
				return;
			}

			picture = ImageUtil.shrink(picture, imageSize);
			res.setContentType("image/*");
			res.setContentLength(picture.length);

			try (ServletOutputStream sverOut = res.getOutputStream();
					BufferedOutputStream bOut = new BufferedOutputStream(sverOut);) {
				bOut.write(picture);
			} catch (Exception e) {
				writeText(res, "false");
				return;
			}
		}
			return;
		}

	}

//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//		String inStr = req.getParameter("live_id");
//		if (inStr == null || inStr.length() <= 0) {
//			doPost(req, res);
//		}
//
//		LiveService liveDao = new LiveService();
//
//		byte[] video = liveDao.getOneVideo(inStr);
//
//		if (video == null || video.length <= 0) {
//			writeText(res, "false");
//			return;
//		}
//
//		String range = req.getHeader("Range");
//		String browser = req.getHeader("User-Agent");
//
//		System.out.println(range);
//		System.out.println(browser);
//
//		String videoName = new File(liveDao.getOneLiveNoBLOB(inStr).getVideoAddress()).getName();
//
//		String videoAddress = liveDao.getOneLiveNoBLOB(inStr).getVideoAddress();
//		videoAddress = videoAddress.substring(videoAddress.indexOf("video") - 1);
//
//		if (range != null) {
//			range = (range == null) ? "0" : range.substring(range.indexOf("=") + 1, range.length() - 1);// 得到單次請求的起頭
//			long rangeStart = Long.valueOf(range);// 設定起始直
//			long rangeEnd = ((rangeStart + 256000) > video.length) ? video.length : (rangeStart + 8190);// 設定終值
//
//			res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);// 回傳206
//
//			StringBuffer contentRange = new StringBuffer("bytes ").append(rangeStart).append("-").append(rangeEnd)
//					.append("/").append(video.length);// 設定傳送文字格式:bytes=起始值-終值/總長度
//			res.setHeader("Content-Range", contentRange.toString());// 設定送的範圍
//		}
//
//		res.setContentType("video/mp4");// 設定型態
//		res.setContentLength(video.length);// 一次送的長度
//		res.setHeader("Accept-Ranges", "bytes");// 設定資料類型
//		res.setHeader("Content-Disposition", "attachment; filename=\"" + videoName + "\"");// 設定送的特性
//		byte[] content = new byte[video.length];
//		ServletContext ct = getServletContext();
//
//		System.out.println(video.length);
////		BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(video));
////		RandomAccessFile input = new RandomAccessFile(new File(liveDao.getOneLiveNoBLOB(inStr).getVideoAddress()), "r");
////		InputStream input = ct.getResourceAsStream(videoAddress);
//
//		try (InputStream input = ct.getResourceAsStream(videoAddress);
//				ServletOutputStream sverOut = res.getOutputStream();) {
//			int read = 0;
//			if (range == null) {
//				while ((read = input.read(content)) > 0) {
//					sverOut.write(content, 0, read);
////					sverOut.flush();
//				}
//			} else {
////				input.seek(Long.valueOf(range));
//				int toRead = video.length;
//
//				if ((read = input.read(content)) > 0) {
//					if ((toRead -= read) > 0) {
//						sverOut.write(content, 0, read);
////						sverOut.flush();
//					} else {
//						sverOut.write(content, 0, (toRead + read));
////						sverOut.flush();
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			writeText(res, "false");
//			return;
//		}
//
//	}

	private void writeText(HttpServletResponse res, String outText) throws IOException {
		res.setContentType(CONTENT_TYPE);
		PrintWriter out = res.getWriter();
		out.print(outText);
		out.close();
		System.out.println("outText: " + outText);
	}

}
