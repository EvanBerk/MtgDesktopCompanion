package org.magic.api.notifiers.impl;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.interfaces.abstracts.AbstractMTGNotifier;
import org.magic.services.MTGConstants;

public class OSTrayNotifier extends AbstractMTGNotifier {

	private TrayIcon trayNotifier;
	private SystemTray tray;

	public SystemTray getTray() {
		
		if(tray==null)
			init();
		
		return tray;
	}
	
	public TrayIcon getTrayNotifier() {
		
		if(trayNotifier==null)
			init();
		
		
		return trayNotifier;
	}
	
	@Override
	public FORMAT_NOTIFICATION getFormat() {
		return FORMAT_NOTIFICATION.TEXT;
	}

	
	private void init()
	{
		try {
			if(trayNotifier==null)
			{
				trayNotifier = new TrayIcon(MTGConstants.IMAGE_LOGO.getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH));
				tray = SystemTray.getSystemTray();
				if (SystemTray.isSupported()) {
					tray.add(trayNotifier);
				}
			}
		}
		catch (Exception e) {
			logger.error(e);
		}
	}
	
	private MessageType convert(MESSAGE_TYPE type) {
		switch(type)
		{
		 case ERROR : return MessageType.ERROR;
		 case INFO : return MessageType.INFO;
		 case WARNING : return MessageType.WARNING;
		 case NONE : return MessageType.NONE;
		 default: return MessageType.INFO;
		}
	}
	
	@Override
	public void send(MTGNotification notification) throws IOException {
		getTrayNotifier().displayMessage(notification.getTitle(), notification.getMessage(), convert(notification.getType()));
	}
	
	@Override
	public boolean isEnable() {
		return SystemTray.isSupported();
	}

	@Override
	public String getName() {
		return "Tray";
	}

}
