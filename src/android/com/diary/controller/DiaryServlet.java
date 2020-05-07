package android.com.diary.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ddn.model.DiaryService;
import com.ddn.model.DiaryVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DiaryServlet extends HttpServlet {
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
		DiaryService diaryDao = new DiaryService();
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		String action = jsonObject.get("action").getAsString();

		switch (action) {
		case "add": {
			DiaryVO diary = gson.fromJson(jsonObject.get("diary").toString(), DiaryVO.class);
			try {
				DiaryVO diaryOut = diaryDao.add(diary.getDiary_id(), diary.getMember_id(), diary.getDiary_title(),
						diary.getDiary_write(), diary.getDiary_time(), diary.getDiary_addr(), diary.getDiary_status());
				writeText(res, gson.toJson(diaryOut));
			} catch (Exception e) {
				writeText(res, "false");
			}
		}
			return;

		case "update": {
			DiaryVO diary = gson.fromJson(jsonObject.get("diary").toString(), DiaryVO.class);
			try {
				DiaryVO diaryOut = diaryDao.update(diary.getDiary_title(), diary.getDiary_write(),
						diary.getDiary_addr(), diary.getDiary_id(), diary.getDiary_id());
				writeText(res, gson.toJson(diaryOut));
			} catch (Exception e) {
				writeText(res, "false");
			}
		}
			return;

		case "delete": {
			DiaryVO diary = gson.fromJson(jsonObject.get("diary").toString(), DiaryVO.class);
			try {
				diaryDao.delete(diary.getDiary_id());
				writeText(res, "true");
			} catch (Exception e) {
				writeText(res, "false");
			}
		}
			return;

		case "getOneDdn": {
			DiaryVO diary = gson.fromJson(jsonObject.get("diary").toString(), DiaryVO.class);

			DiaryVO diaryOut = diaryDao.getOneDdn(diary.getDiary_id());
			writeText(res, ((diaryOut == null) ? "false" : gson.toJson(diaryOut)));
		}
			return;

		case "getAll": {
			List<DiaryVO> diarys = diaryDao.getAll();
			if ((diarys == null) || (diarys.size() <= 0)) {
				writeText(res, "false");
				return;
			}

			for (int i = 0; i < diarys.size(); i++) {
				if (diarys.get(i).getDiary_status() != 0) {
					diarys.remove(i);
					i--;
				}
			}

			writeText(res, gson.toJson(diarys));
		}
			return;
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	private void writeText(HttpServletResponse res, String outText) throws IOException {
		res.setContentType(CONTENT_TYPE);
		PrintWriter out = res.getWriter();
		out.print(outText);
		out.close();
		System.out.println("outText: " + outText);
	}

}
