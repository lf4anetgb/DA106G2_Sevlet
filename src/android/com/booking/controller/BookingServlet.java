package android.com.booking.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.booking.model.BookingService;
import com.booking.model.BookingVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class BookingServlet extends HttpServlet {
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
		BookingService dao = new BookingService();
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		String action = jsonObject.get("action").getAsString();

		switch (action) {
		case "updateBooking": {

			Integer bk_status = jsonObject.get("bk_status").getAsInt();
			String bk_number = jsonObject.get("bk_number").getAsString();

			try {
				BookingVO bookingVO = dao.updateBkstatus(bk_status, bk_number);
				writeText(res, String.valueOf(bookingVO != null));
			} catch (Exception e) {
				writeText(res, "false");
			}

			return;
		}

		case "getAllByBK_Number": {
			String member_id = jsonObject.get("member_id").getAsString();

			List<BookingVO> bookings = dao.getAll();

			for (int i = 0; i < bookings.size(); i++) {
				if (!member_id.equals(bookings.get(i).getMember_id())) {
					bookings.remove(i);
					i--;
				}
			}

			List<BookingVO> bookings_ = new ArrayList<BookingVO>();
			for (int i = bookings.size(); i > 0; i--) {
				bookings_.add(bookings.get(i - 1));
			}
			writeText(res, (bookings_.size() <= 0) ? "false" : gson.toJson(bookings_));

			return;
		}

		}

		writeText(res, "false");

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String bk_number = req.getParameter("bk_number");

		if (bk_number == null || bk_number.length() <= 0) {
			doPost(req, res);
		}

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		BookingService dao = new BookingService();

		try {
			BookingVO bookingVO = dao.getOneBooking(bk_number);
			writeText(res, (bookingVO == null) ? "false" : gson.toJson(bookingVO));
		} catch (Exception e) {
			writeText(res, "false");
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
