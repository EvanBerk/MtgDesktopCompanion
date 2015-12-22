package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.search.Searchable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.db.MagicDAO;
import org.magic.gui.MagicGUI;
import org.magic.gui.models.MagicEditionsTableModel;
import org.magic.gui.renderer.MagicCollectionTableCellRenderer;
import org.magic.tools.MagicExporter;

import ca.odell.glazedlists.GlazedLists;

public class CollectionPanelGUI extends JPanel {
	private JTable tableEditions;
	private MagicCardsProvider provider;
	private MagicDAO dao;
	private MagicCardsTree tree;
	private MagicEditionsTableModel model;
	private JTextField txtSearch;
	
	private TreePath path;
	static final Logger logger = LogManager.getLogger(CollectionPanelGUI.class.getName());

	
	public CollectionPanelGUI(MagicCardsProvider provider,MagicDAO dao) throws Exception
	{
		this.provider=provider;
		this.dao=dao;
		initGUI();
	}
	
	
	public void add(MagicCollection coll, List<MagicCard> cards)
	{
		
	}
	
	
	public void initGUI() throws Exception {
		setLayout(new BorderLayout(0, 0));
		model = new MagicEditionsTableModel(dao,provider);
			model.init(provider.searchSetByCriteria(null, null));
		
		JPanel panneauHaut = new JPanel();
		add(panneauHaut, BorderLayout.NORTH);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.setEnabled(true);
		
		
		panneauHaut.add(btnRemove);
		
		JButton btnAddAllSet = new JButton("Get all set");
		
		
		
		panneauHaut.add(btnAddAllSet);
		
		JButton btnExportCSV = new JButton("Export Collection");
		btnExportCSV.setEnabled(false);
		btnExportCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jf =new JFileChooser();
				jf.showSaveDialog(null);
				File f=jf.getSelectedFile();
				DefaultMutableTreeNode curr=(DefaultMutableTreeNode) path.getLastPathComponent();
				MagicCollection mc = (MagicCollection)curr.getUserObject();
				
				try {
					MagicExporter.export(dao.getCardsFromCollection(mc), f);
					JOptionPane.showMessageDialog(null, "Export Finished","Finished",JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					logger.error(e);
					JOptionPane.showMessageDialog(null, e,"Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panneauHaut.add(btnExportCSV);
		
		
		
		JScrollPane scrollPane = new JScrollPane();
		
		tableEditions = new JTable();
		
		
		tableEditions.setModel(model);
		tableEditions.setDefaultRenderer(Object.class,new MagicCollectionTableCellRenderer());
		DefaultRowSorter sorterEditions = new TableRowSorter<DefaultTableModel>(model);
		tableEditions.setRowSorter(sorterEditions);
		
		scrollPane.setViewportView(tableEditions);
		
		
		
		JSplitPane splitListPanel = new JSplitPane();
		add(splitListPanel, BorderLayout.CENTER);
		splitListPanel.setLeftComponent(scrollPane);
		
		JPanel panneauDroite = new JPanel();
		panneauDroite.setLayout(new BorderLayout(0, 0));
		splitListPanel.setRightComponent(panneauDroite);
		
		JPanel panneauTreeSearch = new JPanel();
		panneauDroite.add(panneauTreeSearch, BorderLayout.CENTER);
		panneauTreeSearch.setLayout(new BorderLayout(0, 0));
		
		txtSearch = new JTextField();
		panneauTreeSearch.add(txtSearch, BorderLayout.NORTH);
		txtSearch.setColumns(10);
		
		
		JScrollPane scrollPaneCollections = new JScrollPane();
		panneauTreeSearch.add(scrollPaneCollections);
		
		tree = new MagicCardsTree(provider,dao);
		
		 txtSearch.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent evt) {
	               
	            }
	        });
		 
		 
	
		 
		 scrollPaneCollections.setViewportView(tree);
		 
		 JLabel lblCard = new JLabel("");
		 panneauTreeSearch.add(lblCard, BorderLayout.SOUTH);

		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				path = tse.getPath();
				
				DefaultMutableTreeNode curr=(DefaultMutableTreeNode) path.getLastPathComponent();
				
				if(curr.getUserObject() instanceof MagicCollection)
					btnExportCSV.setEnabled(true);
				else
					btnExportCSV.setEnabled(false);
				
				if(curr.isLeaf())
				{	
					MagicCard card = (MagicCard)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
					ImageIcon icon;
					try {
						icon = new ImageIcon(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+card.getEditions().get(0).getMultiverse_id()+"&type=card"));
						lblCard.setIcon(icon);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		
		btnAddAllSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
					MagicEdition ed = (MagicEdition) tableEditions.getValueAt(tableEditions.getSelectedRow(), 1);
					
					int res = JOptionPane.showConfirmDialog(null,"Are you sure you adding " + ed +" to Library ?");
					
					if(res==JOptionPane.YES_OPTION)
					try {
						List<MagicCard> list = provider.searchCardByCriteria("set", ed.getId());
						
						for(MagicCard mc : list)
						{
							MagicCollection col = new MagicCollection();
							col.setName("Library");
							dao.saveCard(mc, col);
						}
						model.calculate();
						model.fireTableDataChanged();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				MagicCollection col = (MagicCollection) ((DefaultMutableTreeNode)path.getPathComponent(1)).getUserObject();
				
				DefaultMutableTreeNode curr=(DefaultMutableTreeNode) path.getLastPathComponent();
				if(curr.isLeaf())
				{	
					MagicCard card = (MagicCard)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
					
					try {
						int res = JOptionPane.showConfirmDialog(null,"Are you sure you wan't delete " + card +" from " + col + "?");
						if(res==JOptionPane.YES_OPTION)
							dao.removeCard(card, col);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					MagicEdition me = (MagicEdition)((DefaultMutableTreeNode)path.getPathComponent(2)).getUserObject();
					
					try {
						int res = JOptionPane.showConfirmDialog(null,"Are you sure you wan't delete " + me +" from " + col + "?");
						if(res==JOptionPane.YES_OPTION)
							dao.removeEdition(me, col);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					model.calculate();
				} catch (Exception e) {
					e.printStackTrace();
				}
				model.fireTableDataChanged();
				tree.refresh();
			}
		});
		
	}
	
	
	
	
	public void setProvider(MagicCardsProvider provider)
	{
		this.provider=provider;
	}
	
	public MagicCardsTree getJTree()
	{
		return tree;
	}

}
