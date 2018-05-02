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

	
	public static int enterIntWithTime() {
		try {
            
            System.out.println("Insert int here:");
            FutureTask<Integer> task = new FutureTask<>(() -> {
                return s.nextInt();
            });

            thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            Integer nextInt = task.get(TIME_TO_WAIT, TimeUnit.SECONDS);
            s.next();
            return nextInt;
            
        } catch (TimeoutException | InterruptedException | ExecutionException interruptedException) {
            return -1;
        }
	}
	
	public static char enterCharWithTime() {
		try {
            
            System.out.println("Insert Char here:");
        

            FutureTask<String> task = new FutureTask<>(() -> {
                return s.nextLine();
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            String nextLine = task.get(TIME_TO_WAIT, TimeUnit.SECONDS);
            
            return nextLine.charAt(0);
            
        } catch (TimeoutException | InterruptedException | ExecutionException | StringIndexOutOfBoundsException interruptedException) {
            return 'c';
        }
	}
	
	public static char enterChar() {
		String nextLine = s.nextLine();
		try {
			return nextLine.charAt(0);
		} catch (StringIndexOutOfBoundsException e) {
			return 'c';
		}
	}
	
}
