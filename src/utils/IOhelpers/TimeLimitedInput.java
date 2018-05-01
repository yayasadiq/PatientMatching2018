package utils.IOhelpers;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeLimitedInput {
	public static int enterInt() {
		try {
            
            System.out.println("Insert int here:");
            Scanner s = new Scanner(System.in);

            FutureTask<Integer> task = new FutureTask<>(() -> {
                return s.nextInt();
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            Integer nextInt = task.get(5, TimeUnit.SECONDS);
            s.next();
            return nextInt;
            
        } catch (TimeoutException | InterruptedException | ExecutionException interruptedException) {
            return -1;
        }
	}
	
	public static char enterChar() {
		try {
            
            System.out.println("Insert int here:");
            Scanner s = new Scanner(System.in);

            FutureTask<String> task = new FutureTask<>(() -> {
                return s.nextLine();
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            String nextLine = task.get(5, TimeUnit.SECONDS);
            
            return nextLine.charAt(0);
            
        } catch (TimeoutException | InterruptedException | ExecutionException interruptedException) {
            return 'c';
        }
	}
}
