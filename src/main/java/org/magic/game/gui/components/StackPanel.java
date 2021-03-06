package org.magic.game.gui.components;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.magic.game.gui.components.renderer.StackItemRenderer;
import org.magic.game.model.AbstractSpell;
import org.magic.game.model.GameManager;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class StackPanel extends JPanel implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String START = "Start";
	private static final String PAUSE = "Pause";
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JList<AbstractSpell> listStack;
	private DefaultListModel<AbstractSpell> model;
	private JLabel lblCounter;
	private Timer timer;
	private static final int SECONDE=10;
	private long startTime=SECONDE;
	private JButton btnPause;
	
	public StackPanel() {
		model = new DefaultListModel<>();
		
		timer = new Timer(1000,e->{
				startTime=startTime-1;
				lblCounter.setText(String.valueOf(startTime));
				
				if(startTime==0)
				{
					timer.stop();
					if(model.size()>0)
					{
						lblCounter.setText("RESOLVING " + model.size() + " spell(s)");
						GameManager.getInstance().getStack().unstack();
						
					}
					else
					{
						startTime=SECONDE;
					}
				}
			
		});

		lblCounter = new JLabel(String.valueOf(startTime));
		JPanel panel = new JPanel();
		listStack = new JList<>(model);
		
		setLayout(new BorderLayout(0, 0));
		listStack.setCellRenderer(new StackItemRenderer());
		
		add(new JScrollPane(listStack ), BorderLayout.CENTER);
		add(panel, BorderLayout.NORTH);
		panel.add(lblCounter);
		
		btnPause = new JButton(PAUSE);
		
		btnPause.addActionListener(ae->{
				
				if(timer.isRunning())
				{
					timer.stop();
					btnPause.setText(START);
				}
				else
				{
					timer.start();
					btnPause.setText(PAUSE);
				}
		});
		panel.add(btnPause);
	}
	
	
	public void enableChrono(boolean b)
	{
		startTime=SECONDE;
		
		if(b)
		{
			timer.start();
			btnPause.setText(PAUSE);
		}
		else
		{
			timer.stop();
			btnPause.setText(START);
		}
		
		
	}
	
	public void addStack(AbstractSpell sp)
	{
		model.add(0,sp);
		
		
		if(!model.isEmpty())
		{
			enableChrono(true);
		}
	}
	
	public void removeStack()
	{
		try {
		model.removeElementAt(0);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			//do nothing
			}
	}

	

	@Override
	public void update(Observable o, Object ob) {
		
		if(ob!=null)
			addStack((AbstractSpell)ob);
		else
			removeStack();
	}
}




