package org.magic.gui.models;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGControler;

import com.itextpdf.text.List;

public class DeckCardsTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columns = new String[] { "NAME",
			"CARD_TYPES",
			"CARD_MANA",
			"CARD_EDITIONS",
			"QTY" };

	private MagicDeck deck;

	public enum TYPE {
		DECK, SIDE
	}

	private TYPE t;

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		if (columnIndex == 3)
			return List.class;

		return super.getColumnClass(columnIndex);
	}

	public DeckCardsTableModel(TYPE t) {
		this.t = t;
		deck = new MagicDeck();
	}

	public void initSide(MagicDeck deck) {
		this.deck = deck;
	}

	public MagicDeck getDeck() {
		return deck;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int column) {
		return MTGControler.getInstance().getLangService().getCapitalize(columns[column]);
	}

	@Override
	public Object getValueAt(int row, int column) {
		MagicCard mc;
		switch (t) {
		case DECK:
			mc = deck.getValueAt(row);
			break;
		case SIDE:
			mc = deck.getSideValueAt(row);
			break;
		default:
			mc = deck.getValueAt(row);
			break;
		}

		if (column == 0)
			return mc;

		if (column == 1)
			return mc.getFullType();

		if (column == 2)
			return mc.getCost();

		if (column == 3)
			return mc.getEditions();

		if (column == 4) {
			switch (t) {
			case DECK:
				return deck.getMap().get(mc);
			case SIDE:
				return deck.getMapSideBoard().get(mc);
			default:
				return null;
			}

		}

		return null;
	}

	@Override
	public int getRowCount() {
		if (deck == null)
			return 0;

		switch (t) {
		case DECK:
			return deck.getMap().size();
		case SIDE:
			return deck.getMapSideBoard().size();
		default:
			return deck.getMap().size();
		}

	}

	public void init(MagicDeck deck) {
		this.deck = deck;
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return (column == 3 || column == 4);
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {

		MagicCard mc = (this.t == TYPE.DECK) ? deck.getValueAt(row) : deck.getSideValueAt(row);
		
		if (column == 3) {
			MagicEdition ed = (MagicEdition) aValue;

			if(!ed.equals(mc.getCurrentSet()))
			{
				int qty = (this.t == TYPE.DECK) ? deck.getMap().get(mc) : deck.getMapSideBoard().get(mc);
				MagicCard newC = MTGControler.getInstance().switchEditions(mc, ed);
				if (t == TYPE.DECK) {
					deck.getMap().remove(mc);
					deck.getMap().put(newC, qty);
				}
				else
				{
					deck.getMapSideBoard().remove(mc);
					deck.getMapSideBoard().put(newC, qty);
				}
			}
		}

		if (column == 4)
			if (Integer.valueOf(aValue.toString()) == 0) {
				if (t == TYPE.DECK) {
					deck.getMap().remove(deck.getValueAt(row));
				} else {
					deck.getMapSideBoard().remove(deck.getValueAt(row));
				}
			} else {
				if (t == TYPE.DECK) {
					deck.getMap().put(deck.getValueAt(row), Integer.valueOf(aValue.toString()));
				} else {
					deck.getMapSideBoard().put(deck.getSideValueAt(row), Integer.valueOf(aValue.toString()));
				}
			}

		fireTableDataChanged();
	}

}
