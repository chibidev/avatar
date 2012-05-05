package dev.chibi.avatar;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import java.net.URL;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

import java.io.IOException;
import java.io.FileNotFoundException;

public class Avatar {
	public static final int DEFAULT = 0x00;
	public static final int SMILE = 0x01;			// :)
	public static final int SAD = 0x02;				// :(
	public static final int BLUSH = 0x03;			// :$
	public static final int LAUGH = 0x04;			// :D
	public static final int WOW = 0x05;				// :O
	public static final int FACEPALM = 0x06;		// -.-
	public static final int ROFLOL = 0x07;			// xD
	public static final int MWAHAH = 0x08;			// }:D
	public static final int TWINKLE_LAUGH = 0x09;	// ;D
	public static final int DOUBT = 0x0A;			// >:O
	public static final int EVIL_SMILE = 0x0B;		// }:)
	public static final int ANGRY = 0x0C;			// >:(

	public Avatar() {
		images = new BufferedImage[13];
	}

	public Avatar(String filename) throws FileNotFoundException, IOException, InvalidAvatarException {
		this();
		load(filename);
	}
	
	public void read(java.io.InputStream stream, byte[] buffer) throws IOException {
		for (int i = 0; i < buffer.length; ++i) {
			int c = stream.read();
			buffer[i] = (byte) c;
		}
	}

	public void load(String filename) throws FileNotFoundException, IOException, InvalidAvatarException {
		FileInputStream avatarInput = new FileInputStream(filename);

		byte[] signature = new byte[10];
		read(avatarInput, signature);

		if (! new String(signature).equals("chibidev:3")) {
			avatarInput.close();
			throw new InvalidAvatarException();
		}

		int opcode; // = avatarInput.read();

		while ((opcode = avatarInput.read()) != -1) {
			readPNG(opcode, avatarInput);
		}

		avatarInput.close();
	}
	

	public void addImage(int opcode, String imagePath) throws IOException {
		FileInputStream imageInput = new FileInputStream(imagePath);
		readPNG(opcode, imageInput);

		imageInput.close();
	}

	public BufferedImage getImage(int opcode) {
		synchronized (images) {
			return images[opcode];
		}
	}

	public void save(String filename) throws IOException {
		final FileOutputStream avatarOutput = new FileOutputStream(filename);

		byte[] signature = new String("chibidev:3").getBytes();
		avatarOutput.write(signature);

		for (int i = 0; i < 13; ++i) {
			synchronized (images) {
				final BufferedImage im = images[i];
				if (im == null) {
					continue;
				}

				final int opcode = i;
				avatarOutput.write(opcode);
				ImageIO.write(im, "png", avatarOutput);
			}
		}
		avatarOutput.close();
	}

	protected void readPNG(final int opcode, InputStream pngStream) throws IOException {
		final java.io.PipedOutputStream pos = new java.io.PipedOutputStream();
		final java.io.PipedInputStream pis = new java.io.PipedInputStream(pos);

		Thread readThread = new Thread(new Runnable() {
			public void run() {
				try {
					synchronized (images) {
						images[opcode] = ImageIO.read(pis);
					}
					pis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		readThread.start();

		byte[] delete = new byte[8];
		read(pngStream, delete);
		long len;
		pos.write(delete, 0, 8);
		// read chunk
		do {
			byte[] length = new byte[4];
			read(pngStream, length);
			len = 0;
			int i = 0;
			do {
				len = (len << 8) + (length[i++] & 0xff);
			} while (i < 4);

			byte[] chType = new byte[4];
			read(pngStream, chType);

			byte[] data = null;
			if (len != 0) {
				data = new byte[(int) len];
				read(pngStream, data);
			}

			byte[] crc = new byte[4];
			read(pngStream, crc);
			pos.write(length, 0, 4);
			pos.write(chType, 0, 4);
			if (data != null) {
				pos.write(data, 0, (int)len);
			}
			pos.write(crc, 0, 4);

				
		} while (len > 0);

		try {
			readThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected BufferedImage[] images;
	
}
