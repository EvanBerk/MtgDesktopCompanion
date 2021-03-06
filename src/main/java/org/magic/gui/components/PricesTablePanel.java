package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class PricesTablePanel extends JPanel {
	
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private CardsPriceTableModel model;
	private JXTable tablePrices;
	private JBuzyLabel lblLoading;
	private transient DefaultRowSorter<DefaultTableModel, Integer> sorterPrice;
	private transient List<RowSorter.SortKey> sortKeys;
	
	private MagicCard currentCard;
	private MagicEdition currentEd;
	
	public PricesTablePanel() {
		JPanel panel = new JPanel();
		lblLoading = new JBuzyLabel();
		
		panel.setPreferredSize(new Dimension(0,lblLoading.getIcon().getIconHeight()));
		JScrollPane scrollPane = new JScrollPane();
		model = new CardsPriceTableModel();
		tablePrices = new JXTable(model);
		sorterPrice = new TableRowSorter<>(model);
		
		sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorterPrice.setSortKeys(sortKeys);
		
		
		
		setLayout(new BorderLayout(0, 0));
		tablePrices.setColumnControlVisible(true);
		tablePrices.setRowSorter(sorterPrice);
		
		add(panel, BorderLayout.NORTH);
		
		
		panel.add(lblLoading);
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(tablePrices);
		
		tablePrices.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();
					try {
						String url = tablePrices.getValueAt(tablePrices.getSelectedRow(), CardsPriceTableModel.ROW_URL).toString();
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception e) {
						logger.error(e);
					}
				}

			}
		});
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(currentCard,currentEd);
			}

		});
		
	}

	public void init(MagicCard card,MagicEdition ed)
	{
		currentCard = card;
		currentEd=ed;
		
		if(currentCard==null && currentEd==null)
			return;
		
		
		if(ed==null)
			currentEd=card.getCurrentSet();
		
		if(isVisible()&&card!=null)
		{
				ThreadManager.getInstance().execute(() -> {
					try {
						loading(true, MTGControler.getInstance().getLangService().getCapitalize("LOADING_PRICES") + " : " + currentCard + "("+currentEd+")" );
						model.init(currentCard, currentEd);
						model.fireTableDataChanged();
						loading(false, "");
						
					} catch (Exception e) {
						logger.error(e);
					}
				}, "addTreeSelectionListener init graph cards");
		}
		
	}

	private void loading(boolean b, String s) {
		lblLoading.buzy(b,s);
	}
	
	
	public List<MagicPrice> getPrices()
	{
		return model.getPrices();
	}
	
	
}
