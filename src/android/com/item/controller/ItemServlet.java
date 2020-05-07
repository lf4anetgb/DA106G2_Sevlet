package android.com.item.controller;

import android.com.item.model.ItemService_Android;
import android.com.tools.ImageUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.item.model.ItemVO;

public class ItemServlet extends HttpServlet {
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
		ItemService_Android itemDao = new ItemService_Android();
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		String action = jsonObject.get("action").getAsString();

		switch (action) {
		case "updateItem": {
			ItemVO itemVO = gson.fromJson(jsonObject.get("Item").toString(), ItemVO.class),
					itemVO_ = itemDao.getOneItem(itemVO.getItem_id());

			Integer counter = gson.fromJson(jsonObject.get("counter"), Integer.class);

			itemVO_.setItem_stock(itemVO_.getItem_stock() - counter);

			writeText(res, String.valueOf(itemDao.updateItem(itemVO_)));
		}
			return;

		case "getOneItemNoBLOB": {
			String item_id = jsonObject.get("item_id").getAsString();
			ItemVO itemVO = itemDao.getOneItem(item_id);
			writeText(res, (itemVO == null) ? "false" : gson.toJson(itemVO));
		}
			return;
		case "getAll": {
			List<ItemVO> items = itemDao.getAll();
			writeText(res, (items.size() <= 0) ? "false" : gson.toJson(items));
		}
			return;

		case "getOnePicture": {
			String item_id = jsonObject.get("item_id").getAsString();
			int imageSize = jsonObject.get("imageSize").getAsInt();
			byte[] picture = itemDao.getOnePicture(item_id);

			if (picture == null || picture.length <= 0) {
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				PrintWriter out = res.getWriter();
				out.print("");
				out.close();
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

	private void writeText(HttpServletResponse res, String outText) throws IOException {
		res.setContentType(CONTENT_TYPE);
		PrintWriter out = res.getWriter();
		out.print(outText);
		out.close();
		System.out.println("outText: " + outText);
	}

}
