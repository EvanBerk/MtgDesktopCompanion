package org.magic.api.decksniffer.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.RetrievableDeck;
import org.magic.api.interfaces.abstracts.AbstractDeckSniffer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.URLTools;

public class LotusNoirDecks extends AbstractDeckSniffer {

	
	private static final String FORMAT = "FORMAT";
	private static final String URL = "URL";
	private static final String MAX_PAGE = "MAX_PAGE";

	@Override
	public String[] listFilter() {
		return new String[] { "derniers-decks", "decks-du-moment", "decks-populaires" };
	}

	@Override
	public MagicDeck getDeck(RetrievableDeck info) throws IOException {
		MagicDeck deck = new MagicDeck();

		logger.debug("get deck at " + info.getUrl());

		Document d = URLTools.extractHtml(info.getUrl().toString());

		deck.setDescription(info.getUrl().toString());
		deck.setName(info.getName());
		Elements e = d.select("div.demi_page>table").select(MTGConstants.HTML_TAG_TR);
		boolean sideboard = false;
		for (Element cont : e) {
			Elements cont2 = cont.select("span.card_title_us");

			if (cont.text().startsWith("R\u00E9serve"))
				sideboard = true;

			if (cont2.text().length() > 0) {
				Integer qte = Integer.parseInt(cont2.text().substring(0, cont2.text().indexOf(' ')));
				String cardName = cont2.text().substring(cont2.text().indexOf(' '), cont2.text().length()).trim();

				if (cardName.contains("//")) // for transformatble cards
					cardName = cardName.substring(0, cardName.indexOf("//")).trim();

				MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
						.searchCardByName(cardName, null, true).get(0);
				if (!sideboard)
					deck.getMap().put(mc, qte);
				else
					deck.getMapSideBoard().put(mc, qte);
			}
		}
		return deck;
	}

	@Override
	public List<RetrievableDeck> getDeckList() throws IOException {

		String decksUrl = getString(URL) + "?dpage=" + getString(MAX_PAGE) + "&action=" + getString(FORMAT);

		logger.debug("snif decks : " + decksUrl);

		int nbPage = Integer.parseInt(getString(MAX_PAGE));
		List<RetrievableDeck> list = new ArrayList<>();

		for (int i = 1; i <= nbPage; i++) {
			Document d = URLTools.extractHtml(getString(URL) + "?dpage=" + i + "&action=" + getString(FORMAT));

			Elements e = d.select("div.thumb_page");

			for (Element cont : e) {
				RetrievableDeck deck = new RetrievableDeck();
				Element info = cont.select("a").get(0);

				String name = info.attr("title").replaceAll("Lien vers ", "").trim();
				String url = info.attr("href");
				String auteur = cont.select("small").select("a").text();

				deck.setName(name);
				try {
					deck.setUrl(new URI(url));
				} catch (URISyntaxException e1) {
					deck.setUrl(null);
				}
				deck.setAuthor(auteur);
				deck.setColor("");

				list.add(deck);
			}
		}
		return list;
	}


	@Override
	public String getName() {
		return "LotusNoir";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void initDefault() {
		setProperty(URL, "http://www.lotusnoir.info/magic/decks/");
		setProperty(FORMAT, "decks-populaires");
		setProperty(MAX_PAGE, "2");
	

	}


}
