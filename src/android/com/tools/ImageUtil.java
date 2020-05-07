package android.com.tools;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

//縮小傳輸用圖片
public class ImageUtil {

	public static byte[] shrink(byte[] inputImageDate, int outputSize) {

		try (ByteArrayInputStream bais = new ByteArrayInputStream(inputImageDate);) {
			BufferedImage bImage = ImageIO.read(bais);
			float sampleSize = 1; // 初始縮小比例，2就大小除於2
			int imageWidth = bImage.getWidth(), imageHeight = bImage.getHeight(),
					type = (bImage.getType() == BufferedImage.TYPE_CUSTOM) ? BufferedImage.TYPE_INT_RGB
							: bImage.getType();

			if (outputSize <= 1 || (Math.min(imageHeight, imageWidth) <= outputSize) || imageWidth == 0
					|| imageHeight == 0) {
				return inputImageDate;
			}

			// 設定縮小後的大小
			int longer = Math.min(imageHeight, imageWidth);
			sampleSize = longer / outputSize;
			imageWidth = (int) (bImage.getWidth() / sampleSize);
			imageHeight = (int) (bImage.getHeight() / sampleSize);

			// 製作縮小後的圖
			BufferedImage scaledBufferedImage = new BufferedImage(imageWidth, imageHeight, type);
			Graphics graphics = scaledBufferedImage.createGraphics();
			graphics.drawImage(bImage, 0, 0, imageWidth, imageHeight, null);
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
				ImageIO.write(scaledBufferedImage, "jpg", baos);
				return baos.toByteArray();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return inputImageDate;
		}
	}
}
