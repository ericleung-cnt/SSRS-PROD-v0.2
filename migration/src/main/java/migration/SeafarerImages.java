package migration;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;

import net.sf.image4j.util.ConvertUtil;

public class SeafarerImages {

	IVitalDocClient vd;

	public SeafarerImages(IVitalDocClient vd) {
		this.vd = vd;
	}

	private byte[] read(Blob blob) throws IOException, SQLException {
		byte[] bytes = new byte[0];
		if (blob != null) {
			try (InputStream in = blob.getBinaryStream()) {
				bytes = IOUtils.toByteArray(in);
			}
		}
		return bytes;
	}

	public void read(String driverClass, String orclUrl, String orclUser, String orclPwd) throws SQLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, FileNotFoundException {
		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
		try (Connection oracle = DriverManager.getConnection(orclUrl, orclUser, orclPwd)) {
			try (PreparedStatement ps = oracle.prepareStatement("ALTER SESSION SET CURRENT_SCHEMA = sris")) {
				ps.execute();
			}

			try (PreparedStatement statement = oracle.prepareStatement("select SERB_NO, FINGER_IMAGE_LEFT, "
					+ "REMARK, FINGER_IMAGE_RIGHT, FACE_PHOTO, SIDE_PHOTO, FINGER_FLAG " + "from SEAFARER_IMAGE")) {
				try (ResultSet resultSet = statement.executeQuery()) {
					try (FileWriter w = new FileWriter("target/files.txt")) {
						int i = 1;
						while (resultSet.next()) {
							String serb = resultSet.getString(1);
							byte[] leftGif = read(resultSet.getBlob(2));
							byte[] left = new byte[0];
							if (leftGif != null) {
								ByteArrayOutputStream bmp = writeBmp(leftGif, "out/" + serb + "_left");
								if (bmp != null) {
									left = bmp.toByteArray();
								}
							}

							String remark = resultSet.getString(3);
							byte[] righGif = read(resultSet.getBlob(4));
							byte[] right = new byte[0];
							if (righGif != null) {
								ByteArrayOutputStream bmp = writeBmp(righGif, "out/" + serb + "_right");
								if (bmp != null) {
									right = bmp.toByteArray();
								}
							}
							String face = resultSet.getString(5);
							String side = resultSet.getString(6);
							String fingerFlag = resultSet.getString(7);

							if ("Y".equals(fingerFlag)) {
								// replace(serb, IVitalDocClient.INDEX_LEFT,
								// left);
								// replace(serb, IVitalDocClient.INDEX_RIGHT,
								// right);
							}
							if (face != null) {
								// replace(serb, IVitalDocClient.INDEX_FRONT,
								// new byte[0]); // TODO
							}
							if (side != null) {
								// replace(serb, IVitalDocClient.INDEX_FRONT,
								// new byte[0]); // TODO
							}
							w.write(i + "," + serb + "," + remark + "," + left.length + "," + right.length + "," + face
									+ "," + side + "," + fingerFlag + "\r\n");
							i++;
						}
					}
				}
			}

		}
	}

	private ByteArrayOutputStream writeBmp(byte[] bytes, String filename) throws IOException, FileNotFoundException {
		if (bytes != null && bytes.length > 0) {
			BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bytes));
			if (bi == null) {
				System.out.println("write fail " + filename);
			} else {
				bi = ConvertUtil.convert24(bi);

				BufferedImage large = new BufferedImage(500, 500, BufferedImage.TYPE_BYTE_BINARY);
				Graphics g = large.getGraphics();
				g.drawImage(bi, (500 - bi.getWidth()) / 2, (500 - bi.getHeight()) / 2, null);
				bi = large;

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(bi, "bmp", baos);
				// header length 62 ,content 32000, bytes per line = 64, size =
				// 64* 500 + 62 = 32062,
				// 1 bits per pixel
				try (FileOutputStream fos = new FileOutputStream(filename + ".bmp")) {
					fos.write(baos.toByteArray());
				}
				return baos;

			}
		}
		return null;
	}

	public void replace(String serb, int type, byte[] content) throws IOException {
		String key = "SERB_"+serb;
		System.out.println("send " + key);
		vd.uploadSeafarerImage(key, type, content, null);
	}
	public byte[] bmpToPixel(InputStream in) throws IOException {
		byte[] fp = new byte[250000];
		byte[] header = new byte[62];
		IOUtils.read(in, header);
		byte[] content = org.apache.poi.util.IOUtils.toByteArray(in); // length 32000

		for (int x = 0; x < 500; x++) {
			for (int y = 0; y < 500; y++) {
				int index = x * 64 + y / 8;
				byte b = content[index];
//				String s1 = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
//				StringBuilder buffer = new StringBuilder();
//				buffer.append((b >> 7) % 2 == 0 ? 0 : 1);
//				buffer.append((b >> 6) % 2 == 0 ? 0 : 1);
//				buffer.append((b >> 5) % 2 == 0 ? 0 : 1);
//				buffer.append((b >> 4) % 2 == 0 ? 0 : 1);
//				buffer.append((b >> 3) % 2 == 0 ? 0 : 1);
//				buffer.append((b >> 2) % 2 == 0 ? 0 : 1);
//				buffer.append((b >> 1) % 2 == 0 ? 0 : 1);
//				buffer.append((b >> 0) % 2 == 0 ? 0 : 1);
//				String s1 = buffer.toString();
//
//				char bit = s1.charAt(y % 8);

				char bit = (b >> 7 - (y % 8)) % 2 == 0 ? '0' : '1';
				int pt = (499 - x) * 500 + y;
				fp[ pt] = (byte)(bit == '0' ? 0 : -1);
			}
		}
		return fp;
	}
	
	public static void main(String[] args) throws IOException {
		long start = System.nanoTime();
		byte[] array = "abcde!@#$%^".getBytes();
		System.out.println(toBinary((byte) 255));
		System.out.println(toBinary((byte) 162));
		System.out.println(toBinary((byte) 210));
		for (int i = 0; i < 512; i++) {
			toBinary((byte) i);
		}
		for (byte b : array) {
//			String s1 = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
			String s1 = toBinary(b);
			System.out.println(b + " = " + s1);
			/*
			 1, 2, 4, 8, 16, 32, 64, 128
97 = 01100001
98 = 01100010
99 = 01100011
100 = 01100100
101 = 01100101
33 = 00100001
64 = 01000000
35 = 00100011
36 = 00100100
37 = 00100101
94 = 01011110
			 */
		}
		start = System.nanoTime() - start;
		System.out.println(start);
	}

	private static String toBinary(byte b) {
//		String expect = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
//		System.out.println(expect);
		if (b < 0) {
			b = (byte) (256 + b);
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append((b >> 7) % 2 == 0 ? 0 : 1);
		buffer.append((b >> 6) % 2 == 0 ? 0 : 1);
		buffer.append((b >> 5) % 2 == 0 ? 0 : 1);
		buffer.append((b >> 4) % 2 == 0 ? 0 : 1);
		buffer.append((b >> 3) % 2 == 0 ? 0 : 1);
		buffer.append((b >> 2) % 2 == 0 ? 0 : 1);
		buffer.append((b >> 1) % 2 == 0 ? 0 : 1);
		buffer.append((b >> 0) % 2 == 0 ? 0 : 1);
		String s1 = buffer.toString();
//		if (!expect.equals(s1)) {
//			throw new RuntimeException(""+b);
//		}
		return s1;
//		return expect;
//		System.out.println(s1.equals(expect));
	}

}
