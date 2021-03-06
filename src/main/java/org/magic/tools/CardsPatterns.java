package org.magic.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CardsPatterns {
	
	COST_LIFE_PATTERN ("\\QPay\\E (.*?) \\Qlife\\E"),
	MANA_PATTERN ("\\{(.*?)\\}"),
	COUNTERS("(?:[Pp]ut) (a|an|two|three|four|five|six|seven|eight|nine|ten) (.*?) counter[s]? on "),
	ADD_MANA("(?:[Aa]dd[s]?) ("+MANA_PATTERN+")+|((one|two|three|four|five) mana)"),
	REMINDER("(?:\\(.+?\\))"),
	TRIGGER_ENTERS_BATTLEFIELD("(.*?) enters the battlefield");
	
	private String pattern = "";
	
	CardsPatterns(String name){
	    this.pattern = name;
	}
	
	@Override
	public String toString()
	{
		return pattern;
	}
	
	public String getPattern() {
		return pattern;
	}

	public static Matcher extract(String s , CardsPatterns pat)
	{
		Pattern p = Pattern.compile(pat.getPattern());
		return p.matcher(s);
	}
	
	
	public static boolean hasPattern(String s , CardsPatterns pat)
	{
		Pattern p = Pattern.compile(pat.getPattern());
		Matcher m = p.matcher(s);
		return m.find();
	}
	
}