package android.com.orders.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cart.model.CartVO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.order_detail.model.Order_detailVO;
import com.orders.model.OrdersVO;

import android.com.orders.model.OrdersService_Android;

public class OrdersServlet extends HttpServlet {
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
		OrdersService_Android dao = new OrdersService_Android();
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		String action = jsonObject.get("action").getAsString();

		switch (action) {
		case "insertWithOrder_details": {

			List<CartVO> cart = makeCart(jsonObject, gson);
			OrdersVO ordersVO = makeOrdersVO(jsonObject, gson, cart);

			if (ordersVO == null) {
				writeText(res, "false");
				return;
			}

			List<Order_detailVO> Order_details = makeOrder_detailVOs(jsonObject, gson, cart);

			try {
				writeText(res, dao.insertWithOrder_details(ordersVO, Order_details));
			} catch (Exception e) {
				writeText(res, "false");
			}

			return;
		}

		case "getAll": {
			List<OrdersVO> orders = dao.getAll();
			String member_id = jsonObject.get("member_id").getAsString();
			for (int i = 0; i < orders.size(); i++) {
				if (!member_id.equals(orders.get(i).getMember_id())) {
					orders.remove(i);
					i--;
				}
			}
			writeText(res, (orders.size() <= 0) ? "false" : gson.toJson(orders));
			return;
		}

		case "trialCalculation": {
			Type listType = new TypeToken<List<CartVO>>() {
			}.getType();
			List<CartVO> cart = gson.fromJson(jsonObject.get("cart").getAsString(), listType);
			Double total = getTotalPrice(cart);

			if (total == null) {
				writeText(res, "false");
				return;
			}

			writeText(res, String.valueOf(total));
			return;
		}

		case "insertWithOrder_details_payPoints": {

			List<CartVO> cart = makeCart(jsonObject, gson);
			OrdersVO ordersVO = makeOrdersVO(jsonObject, gson, cart);

			if (ordersVO == null) {
				writeText(res, "false");
				return;
			}

			List<Order_detailVO> Order_details = makeOrder_detailVOs(jsonObject, gson, cart);

			int payAmount = (int) Math.round(ordersVO.getOrder_price());

			try {
				writeText(res, dao.insertWithOrder_details_payPoints(ordersVO, Order_details, payAmount));
			} catch (Exception e) {
				writeText(res, "false");
			}

			return;
		}
		}

	}

	private Double getTotalPrice(List<CartVO> cart) {

		if (cart.size() <= 0) {
			return null;
		}

		Double total = new Double(0);
		for (int i = 0; i < cart.size(); i++) {
			CartVO cartvo = cart.get(i);

			if (cartvo.getItem_unit_price() * cartvo.getItem_quantity() < 0) {
				return null;
			}

			total += cartvo.getItem_unit_price() * cartvo.getItem_quantity();
		}
		return total;
	}

	private List<CartVO> makeCart(JsonObject jsonObject, Gson gson) {
		Type listType = new TypeToken<List<CartVO>>() {
		}.getType();
		return gson.fromJson(jsonObject.get("cart").getAsString(), listType);
	}

	private OrdersVO makeOrdersVO(JsonObject jsonObject, Gson gson, List<CartVO> cart) {
		// 讀取並設定訂單參數

		String member_id = jsonObject.get("member_id").getAsString();
		String order_address = jsonObject.get("order_address").getAsString();
		Double total = getTotalPrice(cart);
		Timestamp order_time = new Timestamp((new Date()).getTime());
		Integer paywith = jsonObject.get("paywith").getAsInt();

		// 檢查是否有錯誤
		if (total == null || member_id == null || member_id.length() <= 0 || order_address == null
				|| order_address.length() <= 0) {
			return null;
		}

		OrdersVO ordersVO = new OrdersVO();
		ordersVO.setMember_id(member_id);
		ordersVO.setOrder_address(order_address);
		ordersVO.setOrder_price(total);
		ordersVO.setOrder_time(order_time);
		ordersVO.setOrder_status(1);
		ordersVO.setPaywith(paywith);

		return ordersVO;
	}

	private List<Order_detailVO> makeOrder_detailVOs(JsonObject jsonObject, Gson gson, List<CartVO> cart) {
		List<Order_detailVO> Order_details = new ArrayList<Order_detailVO>();
		Order_detailVO order_detailVO = null;
		CartVO cart_ = null;
		for (int i = 0; i < cart.size(); i++) {
			cart_ = cart.get(i);
			order_detailVO = new Order_detailVO();
			order_detailVO.setItem_id(cart_.getItem_id());
			order_detailVO.setItem_quantity(cart_.getItem_quantity());
			order_detailVO.setItem_unit_price(cart_.getItem_unit_price());

			Order_details.add(order_detailVO);
		}
		return Order_details;
	}

	private void writeText(HttpServletResponse res, String outText) throws IOException {
		res.setContentType(CONTENT_TYPE);
		PrintWriter out = res.getWriter();
		out.print(outText);
		out.close();
		System.out.println("outText: " + outText);
	}

}
