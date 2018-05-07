package utils.IOhelpers;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class InputManager {
	
	private static final int TIME_TO_WAIT = 5;
	private static Scanner s = new Scanner(System.in);
	private static Thread thread;

	public static char enterChar() {
		String nextLine = s.nextLine();
		try {
			return nextLine.charAt(0);
		} catch (StringIndexOutOfBoundsException e) {
			return 'c';
		}
	}
	
}
