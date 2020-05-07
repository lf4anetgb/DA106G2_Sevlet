package android.com.order_detail.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.order_detail.model.Order_detailDAO_interface;
import com.order_detail.model.Order_detailService;
import com.order_detail.model.Order_detailVO;

public class Order_detailService_Android extends Order_detailService {
	private Order_detailDAO_interface dao;

	public Order_detailService_Android() {
		dao = new Order_detailJNDIDAO_Android();
	}

	public List<Order_detailVO> getAllByID(String order_id) {
		return ((Order_detailJNDIDAO_Android) dao).getAllByID(order_id);
	}

	public boolean inserts(List<Order_detailVO> list, Connection con) {
		return ((Order_detailJNDIDAO_Android) dao).inserts(list, con);
	}
}
