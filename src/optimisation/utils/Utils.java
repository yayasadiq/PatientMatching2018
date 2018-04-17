package optimisation.utils;

public class Utils {

	public static String tableToString(int[] table, String separator){
		String s="";
		boolean firstLine = true;
		for (int object : table) {
			if(!firstLine)s+=separator;
			else firstLine = false;
			s += object;
		}
		return s;
	}

}
