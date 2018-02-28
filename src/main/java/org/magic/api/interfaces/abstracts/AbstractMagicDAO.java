package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractMagicDAO extends Observable implements MTGDao {

	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private boolean enable=true;
	protected Properties props;

	protected File confdir = new File(MTGControler.CONF_DIR, "dao");

	
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void setProperties(String k, Object value) {
		props.put(k,value);
		
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.DAO;
	}
	
	@Override
	public String getProperty(String k) {
		return String.valueOf(props.get(k));
	}
	
	public Object getProperty(String k,String defaultValue) {
		return props.getProperty(k,defaultValue);
	}
	
	public AbstractMagicDAO() {
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	
	
	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public boolean isEnable() {
		return enable;
	}

	public void load()
	{
		File f=null;
		try {
			f = new File(confdir, getName()+".conf");
			
			if(f.exists())
			{	
				FileInputStream fis = new FileInputStream(f);
				props.load(fis);
				fis.close();
			}
		} catch (Exception e) {
			logger.error("couln't save properties " + f,e);
		} 
	}
	
	public void save()
	{
		File f = null;
		try {
			f = new File(confdir, getName()+".conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			logger.error("couln't load properties " + f,e);
		} 
	}

	@Override
	public void enable(boolean enabled) {
		this.enable=enabled;
	}

	
	
}
