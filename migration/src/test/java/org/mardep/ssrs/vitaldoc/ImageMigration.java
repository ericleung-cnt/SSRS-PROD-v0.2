package org.mardep.ssrs.vitaldoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import migration.CodeTable;
import migration.SeafarerImages;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class ImageMigration {

	@Autowired
	IVitalDocClient vd;
	private Properties properties;
	
	public ImageMigration() throws FileNotFoundException, IOException {
		String property = System.getProperty("migration.properties");
		properties = new Properties();
		try (InputStream stream = (property != null) ? new FileInputStream(property) : CodeTable.class
				.getResourceAsStream("/migration.properties")) {
			properties.load(stream);
		}
	}

	/**
	 * read seafarer images from oracle
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@Test
	public void read() throws SQLException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String driverClass = properties.getProperty("CodeTable.driverClass");
		String orclUrl = properties.getProperty("CodeTable.orclUrl");
		String orclUser = properties.getProperty("CodeTable.orclUser");
		String orclPwd = properties.getProperty("CodeTable.orclPwd");
		new SeafarerImages(vd).read(driverClass, orclUrl, orclUser, orclPwd);

	}
	/**
	 * read picture from vitaldoc and write local file
	 * @throws IOException
	 */
	@Test
	public void testDownload() throws IOException {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("SERB number", "SERB_" + "E002847");
		parameter.put("Type", "Fingerprint-left");
		byte[] download = vd.download("MMO-Seafarer Image", parameter, true);
		assert download.length > 0;
		try (OutputStream output = new FileOutputStream("d:\\desktop\\fp.bmp")) {
			IOUtils.write(download, output);
		}
	}

	/**
	 * read from a bmp file to write to vitaldoc
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testConvert() throws FileNotFoundException, IOException {
		String[] list = new File("out").list();
		Properties prop = new Properties();
		try (FileInputStream fis=new FileInputStream("./multiSerb.txt")) {
			prop.load(fis);
		}
		int replaced = 0;
		try {
			SeafarerImages seafarerImages = new SeafarerImages(vd);
			for (String path : list) {
//			String path = "out\\041132_left.bmp";
				String[] tokens = path.substring(path.lastIndexOf('\\') + 1).split("[_\\.]"); // [041132, left, bmp]
				boolean valid = tokens.length == 3 && Arrays.asList("left", "right").contains(tokens[1])
						&& "bmp".equals(tokens[2]);
				if (valid) {
					String serb = tokens[0];
					if (prop.containsKey(serb)) {
						serb = prop.getProperty(serb);
					}
					int type = "left".equals(tokens[1]) ? IVitalDocClient.INDEX_LEFT : IVitalDocClient.INDEX_RIGHT;

					try (FileInputStream in = new FileInputStream("out/" + path)) {
						byte[] content = seafarerImages.bmpToPixel(in);
						seafarerImages.replace(serb, type, content);
						replaced++;
					}
				} else {
					System.out.println("ignore file, check path, " + path);
				}
				if (replaced > 2000000000) {
					break;
				}
			}
		} finally {
			System.out.println("replaced = " + replaced );
		}
	}

	@Test
	public void testUploadPic() throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		try (FileInputStream fis=new FileInputStream("./multiSerb.txt")) {
			prop.load(fis);
		}
		int uploaded = 0;
		try (LineNumberReader reader = new LineNumberReader(new FileReader("target/files.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\\,");
				String serb = tokens[1];
				if (prop.containsKey(serb)) {
					serb = prop.getProperty(serb);
				}
				String front = tokens[5];
				if (!"null".equals(front)) {
					String file = front.substring(front.lastIndexOf('/') + 1);
					String key = "SERB_"+serb;
					try (FileInputStream image = new FileInputStream("./imageIn/" + file)) {
						byte[] content = IOUtils.toByteArray(image);
						vd.uploadSeafarerImage(key, IVitalDocClient.INDEX_FRONT, content, file);
						System.out.println("front " + serb + "," + file);
						uploaded++;
					} catch (FileNotFoundException f) {
						System.out.println("file not found front " + serb + "," + file);
					}
				}
				String side = tokens[6];
				if (!"null".equals(side)) {
					String file = side.substring(side.lastIndexOf('/') + 1);
					String key = "SERB_"+serb;
					try (FileInputStream image = new FileInputStream("./imageIn/" + file)) {
						byte[] content = IOUtils.toByteArray(image);
						vd.uploadSeafarerImage(key, IVitalDocClient.INDEX_SIDE, content, file);
						System.out.println("side " + serb + "," + file);
						uploaded++;
					} catch (FileNotFoundException f) {
						System.out.println("file not found side " + serb + "," + file);
					}
				}
			}
		} finally {
			System.out.println("uploaded " + uploaded);
		}
	}

}
