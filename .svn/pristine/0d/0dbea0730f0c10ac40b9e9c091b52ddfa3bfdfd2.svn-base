package imp.lang;

import imp.util.Preferences;
import java.util.*;

public class Lang
{
	private static ResourceBundle resource;

	public static ResourceBundle getInstance()
	{
		try{
			if(resource == null){
				resource = ResourceBundle.getBundle(Lang.class.getName(),new Locale(Preferences.getPreference(Preferences.LANGUAGE)));
			}
		}catch(NullPointerException ex){
			ex.printStackTrace();
		}
		return resource;
	}
}
