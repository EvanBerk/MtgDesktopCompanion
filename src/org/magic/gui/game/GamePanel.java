package org.magic.gui.game;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.magic.api.analyzer.TokenAnalyzer;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.pictures.impl.GathererPicturesProvider;
import org.magic.game.GameManager;
import org.magic.game.Player;
import org.magic.game.tokens.TokenFactory;
import org.magic.gui.game.actions.MouseAction;
import org.magic.services.exports.MagicSerializer;

public class GamePanel extends JPanel implements Observer {
	
	
	private JSpinner spinLife;
	private JSpinner spinPoison;
	private ThumbnailPanel handPanel;
	private BattleFieldPanel panelBattleField;
	private ManaPoolPanel manaPoolPanel ;
	private JPanel panneauGauche;
	private JPanel panneauDroit;
	private JList listActions;
	private JLabel lblLibraryCountCard;
	private JLabel lblPlayer;
	
	private Player player;
	private LibraryPanel panelLibrary;
	private GraveyardPanel panelGrave;
	private SearchLibraryFrame libraryFrame;
	
	
	public void initGame()
	{
		player.init();
	}
	
	public void setPlayer(Player p1) {
		player=p1;
		lblPlayer.setText(p1.getName());
		player.addObserver(this);
		spinLife.setValue(p1.getLife());
		spinPoison.setValue(p1.getPoisonCounter());
		
		handPanel.setPlayer(p1);
		panelGrave.setPlayer(p1);
		manaPoolPanel.setPlayer(p1);
		panelBattleField.setPlayer(p1);
		panelLibrary.setPlayer(p1);
	}
	
	
	public GamePanel() {
		
		setLayout(new BorderLayout(0, 0));
		
		panneauGauche = new JPanel();
		panneauDroit = new JPanel();
		
		
		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		
		splitPane.setLeftComponent(panneauGauche);
		panneauGauche.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollActions = new JScrollPane();
		panneauGauche.add(scrollActions);
		
		listActions = new JList();
		listActions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listActions.setValueIsAdjusting(true);
		listActions.setModel(new DefaultListModel<String>());
		scrollActions.setViewportView(listActions);
		
		
		splitPane.setRightComponent(panneauDroit);
		panneauDroit.setLayout(new BorderLayout(0, 0));
		
		JPanel panelInfo = new JPanel();
		panneauDroit.add(panelInfo, BorderLayout.WEST);
		panelInfo.setLayout(new BorderLayout(0, 0));
		
		JPanel lifePanel = new JPanel();
		lifePanel.setAlignmentY(Component.TOP_ALIGNMENT);
		panelInfo.add(lifePanel, BorderLayout.NORTH);
		GridBagLayout gbl_lifePanel = new GridBagLayout();
		gbl_lifePanel.columnWidths = new int[]{29, 53, 32, 41, 0};
		gbl_lifePanel.rowHeights = new int[] {0, 25, 0, 0};
		gbl_lifePanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_lifePanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		lifePanel.setLayout(gbl_lifePanel);
						
						lblPlayer = new JLabel("");
						lblPlayer.setIcon(new ImageIcon(GamePanel.class.getResource("/res/planeswalker.png")));
						GridBagConstraints gbc_lblPlayer = new GridBagConstraints();
						gbc_lblPlayer.gridwidth = 3;
						gbc_lblPlayer.insets = new Insets(0, 0, 5, 5);
						gbc_lblPlayer.gridx = 0;
						gbc_lblPlayer.gridy = 0;
						lifePanel.add(lblPlayer, gbc_lblPlayer);
				
						
						JLabel lblLife = new JLabel("");
						lblLife.setHorizontalAlignment(SwingConstants.CENTER);
						lblLife.setIcon(new ImageIcon(GamePanel.class.getResource("/res/heart.png")));
						GridBagConstraints gbc_lblLife = new GridBagConstraints();
						gbc_lblLife.anchor = GridBagConstraints.WEST;
						gbc_lblLife.insets = new Insets(0, 0, 0, 5);
						gbc_lblLife.gridx = 0;
						gbc_lblLife.gridy = 2;
						lifePanel.add(lblLife, gbc_lblLife);
				
				spinLife = new JSpinner();
				spinLife.setFont(new Font("Tahoma", Font.BOLD, 17));
				GridBagConstraints gbc_spinLife = new GridBagConstraints();
				gbc_spinLife.insets = new Insets(0, 0, 0, 5);
				gbc_spinLife.gridx = 1;
				gbc_spinLife.gridy = 2;
				lifePanel.add(spinLife, gbc_spinLife);
				spinLife.addChangeListener(new ChangeListener() {
					
					public void stateChanged(ChangeEvent e) {
						if(player !=null) 
							player.setLife((int)spinLife.getValue());
						
					}
				});
				
				JLabel lblPoison = new JLabel("");
				lblPoison.setHorizontalAlignment(SwingConstants.CENTER);
				lblPoison.setIcon(new ImageIcon(GamePanel.class.getResource("/res/poison.png")));
				GridBagConstraints gbc_lblPoison = new GridBagConstraints();
				gbc_lblPoison.anchor = GridBagConstraints.WEST;
				gbc_lblPoison.insets = new Insets(0, 0, 0, 5);
				gbc_lblPoison.gridx = 2;
				gbc_lblPoison.gridy = 2;
				lifePanel.add(lblPoison, gbc_lblPoison);
				
				spinPoison = new JSpinner();
				spinPoison.setFont(new Font("Tahoma", Font.BOLD, 15));
				GridBagConstraints gbc_spinPoison = new GridBagConstraints();
				gbc_spinPoison.gridx = 3;
				gbc_spinPoison.gridy = 2;
				lifePanel.add(spinPoison, gbc_spinPoison);
				
				spinPoison.addChangeListener(new ChangeListener() {
					
					public void stateChanged(ChangeEvent e) {
						if(player !=null)
							player.setPoisonCounter((int)spinPoison.getValue());
						
					}
				});
		
		JPanel panelTools = new JPanel();
		panelTools.setAlignmentY(Component.TOP_ALIGNMENT);
		panelInfo.add(panelTools, BorderLayout.SOUTH);
		panelTools.setLayout(new GridLayout(4, 2, 0, 0));
		
		JButton btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser(new File(System.getProperty("user.home")+"/magicDeskCompanion/decks"));
				choose.showOpenDialog(null);
				try {
					MagicDeck deck = MagicSerializer.read(choose.getSelectedFile(),MagicDeck.class);
					
					Player p = new Player(deck);
					GameManager.getInstance().addPlayer(p);
					GameManager.getInstance().initGame();
					GameManager.getInstance().nextTurn();
					setPlayer(p);
					handPanel.removeAll();
					panelBattleField.removeAll();
					panelGrave.removeAll();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		panelTools.add(btnNewGame);
		
		JButton btnSearch = new JButton("Search");
		panelTools.add(btnSearch);
		
		JButton btnDrawHand = new JButton("Draw Hand");
		panelTools.add(btnDrawHand);
		
		JButton btnShuffle = new JButton("Shuffle");
		btnShuffle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				player.shuffleLibrary();
			}
		});
		panelTools.add(btnShuffle);
		
		JButton btnScry = new JButton("Scry");
		btnScry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String res = JOptionPane.showInputDialog("How many scry card ?");
				new SearchLibraryFrame(player,player.scry(Integer.parseInt(res))).setVisible(true);
				
			}
		});
		panelTools.add(btnScry);
		
		JButton btnEndTurn = new JButton("End Turn");
		panelTools.add(btnEndTurn);
		
		JButton btnToken = new JButton("Token");
		btnToken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				for(Component c : panelBattleField.getComponents())
				{
					if(((DisplayableCard)c).isSelected())
					{
						try{
							MagicCard tok = TokenAnalyzer.generateTokenFrom(  ((DisplayableCard)c).getMagicCard()  );
							DisplayableCard dc = new DisplayableCard( tok, ((DisplayableCard)c).getWidth(), ((DisplayableCard)c).getHeight());
							dc.addMouseListener(new MouseAction(player));
							dc.setMagicCard(tok);
							panelBattleField.addComponent(dc);
							panelBattleField.revalidate();
							panelBattleField.repaint();
						}
						catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
		});
		panelTools.add(btnToken);
		
		JPanel panel = new JPanel();
		panelInfo.add(panel, BorderLayout.CENTER);
		
		manaPoolPanel = new ManaPoolPanel();
		manaPoolPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(manaPoolPanel, BorderLayout.NORTH);
		btnEndTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				GameManager.getInstance().nextTurn();
			}
		});
		
		
		JPanel panelLibraryAndGrave = new JPanel();
		panneauDroit.add(panelLibraryAndGrave, BorderLayout.EAST);
		panelLibraryAndGrave.setLayout(new BorderLayout(0, 0));
		
		JPanel panelDeck = new JPanel();
		panelLibraryAndGrave.add(panelDeck, BorderLayout.NORTH);
		
		panelLibrary = new LibraryPanel();
		panelLibrary.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		panelDeck.setLayout(new BoxLayout(panelDeck, BoxLayout.Y_AXIS));
		panelDeck.add(panelLibrary);
		
		
		panelLibrary.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

				player.drawCard(1);
				DisplayableCard c = new DisplayableCard(player.getHand().get(player.getHand().size()-1),handPanel.getCardWidth(),handPanel.getCardHeight());
				c.enableDrag(true);
				c.addMouseListener(new MouseAction(player));
				handPanel.addComponent(c);
				lblLibraryCountCard.setText(""+player.getLibrary().size());
			}
		});
		
		lblLibraryCountCard = new JLabel();
		lblLibraryCountCard.setHorizontalAlignment(SwingConstants.CENTER);
		panelDeck.add(lblLibraryCountCard);
		
		panelGrave = new GraveyardPanel();
		panelLibraryAndGrave.add(panelGrave);
		
		handPanel = new ThumbnailPanel();
		handPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		handPanel.enableDragging(true);
		handPanel.setThumbnailSize(179, 240);
		handPanel.setRupture(7);
		
		JScrollPane scrollPane = new JScrollPane();
		panneauDroit.add(scrollPane, BorderLayout.SOUTH);
		scrollPane.setPreferredSize(new Dimension(2, handPanel.getCardHeight()));
		
		scrollPane.setViewportView(handPanel);
		
		panelBattleField = new BattleFieldPanel();
		panneauDroit.add(panelBattleField, BorderLayout.CENTER);
		panelBattleField.setLayout(null);
		
		btnDrawHand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.mixHandAndLibrary();
				player.shuffleLibrary();
				player.drawCard(7);
			    handPanel.initThumbnails(player.getHand());
			}
		});
		
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				SearchLibraryFrame f = new SearchLibraryFrame(player);
				f.setVisible(true);
				
			}
		});
		
	
	}
	public JSpinner getSpinLife() {
		return spinLife;
	}
	public JSpinner getSpinPoison() {
		return spinPoison;
	}
	public ThumbnailPanel getThumbnailPanel() {
		return handPanel;
	}
	
	public JLabel getLblLibraryCountCard() {
		return lblLibraryCountCard;
	}
	public LibraryPanel getLblLibrary() {
		return panelLibrary;
	}
	public GraveyardPanel getPanelGrave() {
		return panelGrave;
	}

	@Override
	public void update(Observable o, Object arg) {
		String act = player.getName() +" " + arg.toString();
		((DefaultListModel)listActions.getModel()).addElement(act);
	}

	
}