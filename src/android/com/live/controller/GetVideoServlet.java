package android.com.live.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.com.live.model.LiveService;

public class GetVideoServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String inStr = req.getParameter("live_id");
		if (inStr == null || inStr.length() <= 0) {
			doPost(req, res);
		}

		LiveService liveDao = new LiveService();

		byte[] video = liveDao.getOneVideo(inStr);

		if (video == null || video.length <= 0) {
			writeText(res, "false");
			return;
		}

		String range = req.getHeader("Range");
		String browser = req.getHeader("User-Agent");

		System.out.println(range);
		System.out.println(browser);

		String videoName = new File(liveDao.getOneLiveNoBLOB(inStr).getVideoAddress()).getName();

		String videoAddress = liveDao.getOneLiveNoBLOB(inStr).getVideoAddress();
		videoAddress = videoAddress.substring(videoAddress.indexOf("video") - 1);

		long rangeStart = (range == null) ? 0
				: Long.valueOf(range.substring(range.indexOf("=") + 1, range.length() - 1));// 設定起始直
		long rangeEnd = ((rangeStart + 256000) > video.length) ? video.length : (rangeStart + 8190);// 設定終值

		res.setContentType("video/mp4");// 設定型態
		res.setContentLength(video.length);// 一次送的長度
		res.setHeader("Accept-Ranges", "bytes");// 設定資料類型
		res.setHeader("Content-Disposition", "attachment; filename=\"" + videoName + "\"");// 設定送的特性
		StringBuffer contentRange = new StringBuffer("bytes ").append(rangeStart).append("-").append(rangeEnd)
				.append("/").append(video.length);// 設定傳送文字格式:bytes=起始值-終值/總長度
		res.setHeader("Content-Range", contentRange.toString());// 設定送的範圍
		res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);// 回傳206
		byte[] content = new byte[video.length];

		System.out.println(video.length);
//		BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(video));
//		RandomAccessFile input = new RandomAccessFile(new File(liveDao.getOneLiveNoBLOB(inStr).getVideoAddress()), "r");
//		ServletContext ct = getServletContext();
//		InputStream input = ct.getResourceAsStream(videoAddress);

		try (RandomAccessFile input = new RandomAccessFile(new File(liveDao.getOneLiveNoBLOB(inStr).getVideoAddress()),
				"r"); ServletOutputStream sverOut = res.getOutputStream();) {
			int read = 0;
			if (range == null) {
				while ((read = input.read(content)) > 0) {
					sverOut.write(content, 0, read);
//					sverOut.flush();
				}
			} else {
				input.seek(Long.valueOf(range));
				int toRead = video.length;

				if ((read = input.read(content)) > 0) {
					if ((toRead -= read) > 0) {
						sverOut.write(content, 0, read);
//						sverOut.flush();
					} else {
						sverOut.write(content, 0, (toRead + read));
//						sverOut.flush();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			writeText(res, "false");
			return;
		}
	}

	private void writeText(HttpServletResponse res, String outText) throws IOException {
		res.setContentType(CONTENT_TYPE);
		PrintWriter out = res.getWriter();
		out.print(outText);
		out.close();
		System.out.println("outText: " + outText);
	}

}
