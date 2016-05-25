package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MagicShopper;

public abstract class AbstractMagicShopper implements MagicShopper {

	
	private boolean enable=true;
	protected Properties props;
	protected File confdir = new File(System.getProperty("user.home")+"/magicDeskCompanion/");

	
	public abstract List<ShopItem> search(String search);
	public abstract String getShopName();
	
	public AbstractMagicShopper() {
		props=new Properties();
		load();
	}
	
	

	public Properties getProperties() {
		return props;
	}
	
	public void setProperties(String k, Object value) {
		props.put(k,value);
	}
	
	public void load()
	{
		try {
			File f = new File(confdir, getShopName()+".conf");
			
			if(f.exists())
			{	
				FileInputStream fis = new FileInputStream(f);
				props.load(fis);
				fis.close();
			}
			else
			{
				//save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void save()
	{
		try {
			File f = new File(confdir, getShopName()+".conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public boolean isEnable() {
		return enable;
	}

	public void enable(boolean t) {
		this.enable=t;
		
	}
	
	public int hashCode() {
		return getShopName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.hashCode()==obj.hashCode();
	}
	
	@Override
	public String toString() {
		return getShopName();
	}
}