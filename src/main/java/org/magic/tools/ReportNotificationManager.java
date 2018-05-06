package org.magic.tools;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class ReportNotificationManager {
	private Configuration cfg;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private boolean errorLoading=false;
	
	public ReportNotificationManager() {
		  	cfg = new Configuration(MTGConstants.FREEMARKER_VERSION);
	        try {
				cfg.setDirectoryForTemplateLoading(new File(ReportNotificationManager.class.getResource(MTGConstants.MTG_REPORTS_DIR).getFile()));
				cfg.setDefaultEncoding("UTF-8");
				cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
				cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(MTGConstants.FREEMARKER_VERSION).build());
			} catch (IOException e) {
				errorLoading=true;
				logger.error("error init Freemarker ",e);
			}
			
	}
	
	
	public <T> String generate(FORMAT_NOTIFICATION f, List<T> list, Class<T> type)
	{
		if(errorLoading)
			return list.toString();
		
		Map<String,Object> input = new HashMap<>();
		input.put("modele", list);
		try {
			
			String tmpl = type.getSimpleName()+"."+f.name().toLowerCase();
			Template template = cfg.getTemplate(tmpl);
			Writer writer = new StringWriter();
		     		template.process(input, writer);
		    
		    return writer.toString();
		      
		} catch (Exception e) {
			logger.error("error to generate notif : " + f,e);
		} 
		return list.toString();
		
	}
	
	
}