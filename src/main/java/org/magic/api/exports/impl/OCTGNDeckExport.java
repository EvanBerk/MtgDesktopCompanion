package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class OCTGNDeckExport extends AbstractCardExport {

	private static final String SHARED = "SHARED";

	public String getFileExtension() {
		return ".o8d";
	}

	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();

		temp.append("<?xml version='1.0' encoding='").append(MTGConstants.DEFAULT_ENCODING).append("' standalone='yes'?>");
		temp.append("<deck game='" + getString("MAGIC_GAME_ID") + "' sleeveid='" + getString("SLEEVE_ID") + "' >");
		temp.append("<section name='Main' shared='" + getString(SHARED) + "'>");
		for (MagicCard mc : deck.getMap().keySet()) {
			temp.append("<card qty='").append(deck.getMap().get(mc)).append("' id='" + mc.getId() + "'>")
					.append(mc.getName()).append("</card>");
		}
		temp.append("</section>");
		temp.append("<section name='Sideboard' shared='" + getString(SHARED) + "'>");
		for (MagicCard mc : deck.getMapSideBoard().keySet()) {
			temp.append("<card qty='").append(deck.getMapSideBoard().get(mc)).append("' id='" + mc.getId() + "'>")
					.append(mc.getName()).append("</card>");
		}
		temp.append("</section>");

		temp.append("<notes><![CDATA[" + deck.getDescription() + "]]></notes>");

		temp.append("</deck>");

		FileUtils.writeStringToFile(dest, temp.toString(), MTGConstants.DEFAULT_ENCODING);

	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		MagicDeck deck = new MagicDeck();
		deck.setName(f.getName().substring(0, f.getName().indexOf('.')));

		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new FileReader(f)));
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//section[@name='Main']/card");
			NodeList result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);
			for (int i = 0; i < result.getLength(); i++) {
				Node it = result.item(i);
				String name = it.getTextContent();
				String qte = it.getAttributes().getNamedItem("qty").getNodeValue();
				MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
						.searchCardByName( name, null, true).get(0);

				deck.getMap().put(mc, Integer.parseInt(qte));
			}

			expr = xpath.compile("//section[@name='Sideboard']/card");
			result = (NodeList) expr.evaluate(d, XPathConstants.NODESET);
			for (int i = 0; i < result.getLength(); i++) {
				Node it = result.item(i);
				String name = it.getTextContent();
				String qte = it.getAttributes().getNamedItem("qty").getNodeValue();
				MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
						.searchCardByName( name, null, true).get(0);

				deck.getMapSideBoard().put(mc, Integer.parseInt(qte));
			}
			return deck;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}


	@Override
	public String getName() {
		return "OCTGN";
	}


	@Override
	public void initDefault() {
		setProperty("MAGIC_GAME_ID", "a6c8d2e8-7cd8-11dd-8f94-e62b56d89593");
		setProperty("SLEEVE_ID", "0");
		setProperty(SHARED, "False");

	}


}
