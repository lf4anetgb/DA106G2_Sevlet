package android.com.item.model;

import java.util.List;

import com.item.model.ItemDAO_interface;
import com.item.model.ItemVO;

public class ItemService_Android {
	private ItemDAO_interface dao;

	public ItemService_Android() {
		dao = new ItemJNDIDAO_Android();
	}

	public boolean updateItem(ItemVO itemVO) {
		return ((ItemJNDIDAO_Android) dao).update_Android(itemVO);
	}

	public ItemVO getOneItem(String item_id) {
		return dao.findByPrimaryKey(item_id);
	}

	public List<ItemVO> getAll() {
		return dao.getAll();
	}

	public byte[] getOnePicture(String item_id) {
		return ((ItemJNDIDAO_Android) dao).getOnePicture(item_id);
	}
}
