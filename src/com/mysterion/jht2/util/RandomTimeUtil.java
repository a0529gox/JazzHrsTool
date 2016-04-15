package com.mysterion.jht2.util;

public class RandomTimeUtil {
	
	private static final double SMALLEST_HRS = 7;
	private static final double BIGGEST_HRS = 8;
	
	private static final double INTERNAL = 0.5;

	public RandomTimeUtil() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			boolean isFirst = true;
			System.out.println();
			for (double num : getRandomHrs(8, 5)) {
				if (!isFirst) {
					System.out.print(", ");
				}
				System.out.print(num);
				isFirst = false;
			}
			
		}
		
	}
	
	public static double getTotalHrs() {
		double totalHrs = SMALLEST_HRS + getRandomHrs(BIGGEST_HRS - SMALLEST_HRS);
		return totalHrs;
	}

	public static double getRandomHrs(double smallerThan) {
		if (smallerThan <= 0) {
			return 0;
		}
		
		double baseNum = (smallerThan / INTERNAL) - (smallerThan % INTERNAL);
		double hrs = (int)(Math.random() * (1 + baseNum)) * INTERNAL;
		
		return hrs;
	}
	
	public static double[] getRandomHrs(double smallerThan, int size) {
		double[] hrs = new double[size];
		if (smallerThan <= 0 || size <= 0) {
			return hrs;
		}
		
		double baseNum = (smallerThan / INTERNAL) - (smallerThan % INTERNAL);
		if (baseNum < size) {
			return hrs;
		}
		
		int idx = 0;
		while (idx < size) {
			if (idx == size - 1) {
				hrs[idx] = baseNum * 0.5;
			} else {
				int num = (int)(Math.random() * (baseNum - size + 1)) + 1;
				baseNum -= num;
				hrs[idx] = num * 0.5;
			}
			
			idx++;
		}
		return hrs;
	}
}
