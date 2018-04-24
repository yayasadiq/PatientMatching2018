package optimisation.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TimeMeasurer {
	List<TimePoint> timersStarted;
	List<TimePoint> timesMeasured;
	
	public TimeMeasurer() {
		this.timersStarted = new LinkedList<TimePoint>();
		this.timesMeasured = new LinkedList<TimePoint>();
	}
	
	public void startTimer(String name) {
		this.timersStarted.add(new TimePoint(name, System.currentTimeMillis()));
	}
	
	public void stopTimer() {
		long end = System.currentTimeMillis();
		TimePoint timepoint = timersStarted.remove(timersStarted.size() -1);
		timepoint.setTime(end - timepoint.getTime());
		this.timesMeasured.add(timepoint);
	}
	
	public void displayTimes() {
		System.out.println("\n");
		if(timersStarted.size() != 0) {
			System.err.println("Some timers are still running");
		} else {
			NumberFormat formatter = new DecimalFormat("#0.00");
			// the running time of the program is the last arriving in the list
			Double totalTime = (double) timesMeasured.remove(timesMeasured.size() - 1).getTime() / 1000;
			Collections.sort(timesMeasured, (a, b) -> a.getTime() > b.getTime() ? -1 : a.getTime() == b.getTime() ? 0 : 1);
			for (TimePoint timePoint : timesMeasured) {
				Double time = (double) timePoint.getTime() / 1000;
				String percent = formatter.format((time/totalTime) * 100);
				System.out.println(timePoint.getName() + " : " + time + " s " + "Takes " + percent + "%");
			}
			System.out.println("Total time : " + totalTime);
		}
	}
}
