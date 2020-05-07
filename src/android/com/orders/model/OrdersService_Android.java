package android.com.orders.model;

import java.util.List;

import com.order_detail.model.Order_detailVO;
import com.orders.model.OrdersDAO_interface;
import com.orders.model.OrdersService;
import com.orders.model.OrdersVO;

public class OrdersService_Android extends OrdersService {
	private OrdersDAO_interface dao;

	public OrdersService_Android() {
		dao = new OrdersJNDIDAO_Android();
	}

	public List<OrdersVO> getAllByID(String member_id) {
		return ((OrdersJNDIDAO_Android) dao).getAllByID(member_id);
	}

	public String insertWithOrder_details(OrdersVO ordersVO, List<Order_detailVO> list) {
		return ((OrdersJNDIDAO_Android) dao).insertWithOrder_details(ordersVO, list);
	}

	public String insertWithOrder_details_payPoints(OrdersVO ordersVO, List<Order_detailVO> list, Integer payAmount) {
		return ((OrdersJNDIDAO_Android) dao).insertWithOrder_details_payPoints(ordersVO, list, payAmount);
	}

}
