package org.magic.gui.components;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.CardsPatterns;
import org.magic.tools.ImageUtils;

public class ManaPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int cols = 10;
	private int rows = 7;
	private int chunkWidth = 100;
	private int chunkHeight = 100;
	private transient BufferedImage[] imgs;
	private boolean cached = false;
	private int rowHeight = MTGConstants.TABLE_ROW_HEIGHT;
	private int rowWidth = MTGConstants.TABLE_ROW_WIDTH;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public int getRowHeight() {
		return rowHeight;
	}

	public int getRowWidth() {
		return rowWidth;
	}

	FlowLayout fl = new FlowLayout();

	int chunks = rows * cols;
	int count = 0;

	String manaCost;
	private HashMap<String,Integer> map;

	public ManaPanel() {
		fl.setAlignment(FlowLayout.LEFT);
		setLayout(fl);
		init();

	}

	public String getManaCost() {
		return manaCost;
	}

	public void setManaCost(String manaCost) {

		this.removeAll();
		this.revalidate();
		this.repaint();
		if (manaCost == null)
			return;

		Pattern p = Pattern.compile(CardsPatterns.MANA_PATTERN.getPattern());
		Matcher m = p.matcher(manaCost);

		fl.setVgap(0);
		fl.setHgap(0);
		while (m.find()) {
			JLabel lab = new JLabel();
			Image img = getManaSymbol(m.group());
			lab.setIcon(new ImageIcon(img.getScaledInstance(rowWidth, rowHeight, Image.SCALE_DEFAULT)));
			lab.setHorizontalAlignment(SwingConstants.LEFT);
			add(lab);
		}
	}

	private void init() {
		BufferedImage image;

		if (!cached) {
			imgs = new BufferedImage[chunks];
			try {
				image = ImageIO.read(MTGConstants.URL_MANA_SYMBOLS);
				for (int x = 0; x < rows; x++) {
					for (int y = 0; y < cols; y++) {
						imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());
						Graphics2D gr = imgs[count++].createGraphics();
						gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x,
								chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
						gr.dispose();
					}
				}
				cached = true;

			} catch (IOException e) {
				logger.error(e);
			}

		}
		
		map = new HashMap<>();
		map.put("X", 21);
		map.put("Y",22);
		map.put("Z",23);
		map.put("W",24);
		map.put("U",25);
		map.put("B",26);
		map.put("R",27);
		map.put("G",28);
		map.put("S",29);
		map.put("W/P",45);
		map.put("U/P",46);
		map.put("B/P",47);
		map.put("R/P",48);
		map.put("G/P",49);
		map.put("W/U",30);
		map.put("W/B",31);
		map.put("U/B",32);
		map.put("U/R",33);
		map.put("B/R",34);
		map.put("B/G",35);
		map.put("R/W",36);
		map.put("R/G",37);
		map.put("G/W",38);
		map.put("G/U",39);
		map.put("2/W",40);
		map.put("2/U",41);
		map.put("2/B",42);
		map.put("2/R",43);
		map.put("2/G",44);
		map.put("T",50);
		map.put("Q",51);
		map.put("C",69);
		map.put("\u221e",52);
		map.put("\u00BD",53);
		map.put("CHAOS",67);
		map.put("E",68);
		map.put("hr",58);
		map.put("hw",57);
		

	}

	public Image getManaSymbol(String el) {
		rowWidth = 18;
		el = el.replaceAll("\\{", "").replaceAll("\\}", "").trim();
		int val = 0;
		try {
			val = Integer.parseInt(el);
		} catch (NumberFormatException ne) {
			val= map.get(el);
		
		}
		List<Image> lst = new ArrayList<>();

		if (val == 100)// mox lotus
		{
			lst.add(imgs[65]);
			lst.add(imgs[66]);
			rowWidth = rowWidth * lst.size();
			return ImageUtils.joinBufferedImage(lst);
		}

		if (val == 1000000)// gleemax
		{

			lst.add(imgs[60]);
			lst.add(imgs[61]);
			lst.add(imgs[62]);
			lst.add(imgs[63]);
			lst.add(imgs[64]);
			rowWidth = rowWidth * lst.size();
			return ImageUtils.joinBufferedImage(lst);
		}

		return imgs[val];
	}

}
