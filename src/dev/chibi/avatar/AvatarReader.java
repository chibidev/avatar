package dev.chibi.avatar;

import javax.swing.JFrame;

public class AvatarReader extends javax.swing.JPanel {

	protected static String[] smileText;
	static {
		String[] smText = {"(default)", ":)", ":(", ":$", ":D", ":O", "-.-", "xD", "}:D", ";D", ">:O", "}:)", ">:("};
		smileText = smText;
	}

	public AvatarReader(JFrame frame, Avatar a) {
		avatar = a;
		this.frame = frame;
	}

	public void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
		int height = 0;
		int posx = 0;
		for (int i = 0; i < 13; ++i) {
			java.awt.image.BufferedImage im = avatar.getImage(i);
			if (im != null) {
				g.drawImage(im, posx, 0, null);

				g.drawString(smileText[i], posx + im.getWidth() / 2 - (smileText[i].length() * 3), im.getHeight() + 15);
				posx += im.getWidth();
				if (im.getHeight() > height) height = im.getHeight();
			}
		}

		setPreferredSize(new java.awt.Dimension(posx, height + 20));
		frame.setSize(new java.awt.Dimension(posx, height + 50));
	}

	public static void main(final String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String file = null;
				try {
					javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

					/*Avatar a = new Avatar();
					a.addImage(Avatar.DEFAULT, "/home/zsolt/projects/prognyelv/avatar/nagatsuka-saki/default.png");
					a.addImage(Avatar.LAUGH, "/home/zsolt/projects/prognyelv/avatar/nagatsuka-saki/:D.png");
					a.addImage(Avatar.WOW, "/home/zsolt/projects/prognyelv/avatar/nagatsuka-saki/:O.png");
					a.addImage(Avatar.FACEPALM, "/home/zsolt/projects/prognyelv/avatar/nagatsuka-saki/-.-.png");

					a.save("nagatsuka-saki.cha");*/

					if (args.length == 0) {
						javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
						javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("avatar files", "cha");

						fc.addChoosableFileFilter(filter);
						//fc.setAcceptAllFileFilterUsed(false);
						fc.setFileFilter(filter);

						int ret = fc.showDialog(null, "Open avatar");
						if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
							file = fc.getSelectedFile().getName();
						}
					} else {
						file = args[0];
					}

					if (file == null) System.exit(1);
					Avatar a2 = new Avatar(file);
					javax.swing.JFrame frame = new javax.swing.JFrame();
					frame.setSize(640, 480);
					frame.setLayout(new java.awt.FlowLayout());
					frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
					javax.swing.JPanel panel = new AvatarReader(frame, a2);
					panel.setPreferredSize(new java.awt.Dimension(640, 480));
					frame.add(panel);
					//frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected Avatar avatar;
	protected JFrame frame;
}
