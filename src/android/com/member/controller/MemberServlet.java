package android.com.member.controller;

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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.member.model.MemberVO;

import android.com.member.model.MemberService_Android;
import android.com.tools.ImageUtil;

public class MemberServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		BufferedReader br = req.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		System.out.println("input: " + jsonIn);
		MemberService_Android memberDAO = new MemberService_Android();
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		String action = jsonObject.get("action").getAsString();

		switch (action) {
		case "isAccountExisted": {
			String member_id = jsonObject.get("member_id").getAsString();
			boolean result = memberDAO.isAccountExisted(member_id);
			writeText(res, String.valueOf(result));
		}
			return;

		case "addNewMember": {
			MemberVO memberVO = gson.fromJson(jsonObject.get("Member").toString(), MemberVO.class);
			boolean result = memberDAO.addNewMember(memberVO);
			writeText(res, String.valueOf(result));
		}
			return;

		case "getMember": {
			String member_id = jsonObject.get("member_id").getAsString();
			MemberVO memberVO = memberDAO.getMemberById(member_id);
			writeText(res, String.valueOf((memberVO == null) ? "false" : gson.toJson(memberVO)));
		}
			return;

		case "updateMemberData": {
			MemberVO memberVO = gson.fromJson(jsonObject.get("Member").toString(), MemberVO.class);
			boolean result = memberDAO.updateMemberData(memberVO);
			writeText(res, String.valueOf(result));
		}
			return;

		case "getMemberList": {
			List<MemberVO> members = memberDAO.getMemberList();
			writeText(res, (members.size() <= 0) ? "false" : gson.toJson(members));
		}
			return;

		case "purchasePoint_Android": {
			String member_id = jsonObject.get("member_id").getAsString();
			Integer amount = jsonObject.get("amount").getAsInt();
			boolean result = memberDAO.purchasePoint_Android(member_id, amount);
			writeText(res, String.valueOf(result));
		}
			return;

		case "isPasswordCorrect": {
			String member_id = jsonObject.get("member_id").getAsString();
			String password = jsonObject.get("password").getAsString();
			boolean result = memberDAO.isPasswordCorrect(member_id, password);
			writeText(res, String.valueOf(result));
		}
			return;

		case "purchasePoint_getPoint": {
			String member_id = jsonObject.get("member_id").getAsString();
			Integer amount = jsonObject.get("amount").getAsInt(),
					point = memberDAO.purchasePoint_getPoint(member_id, amount);
			writeText(res, String.valueOf(point));
		}
			return;

		case "getOneProfile": {
			String member_id = jsonObject.get("member_id").getAsString();
			int imageSize = jsonObject.get("imageSize").getAsInt();
			byte[] picture = memberDAO.getOneProfile(member_id);

			if (picture == null || picture.length <= 0) {
				writeText(res, "false");
				return;
			}

			picture = ImageUtil.shrink(picture, imageSize);
			res.setContentType("image/gif");
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
