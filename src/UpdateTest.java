import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig
public class UpdateTest extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
		Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
		InputStream fileContent = filePart.getInputStream();

		// ... (do your job here)

		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuffer videoSb = new StringBuffer();

		videoSb.append("video/").append("test").append("/").append(df1.format(new Date()))
				.append(fileName.substring(fileName.indexOf("."), fileName.length()));

		byte[] video = new byte[(int) filePart.getSize()];
		try (BufferedInputStream input = new BufferedInputStream(fileContent);) {
			input.read(video);
		}
	}
}
