package jdz.farmKing.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SimpleTime{
	public static List<Integer> daysPerMonth = Arrays.asList(31,28,31,30,31,30,31,31,30,31,30,31);
	public final int year;
	public final int month;
	public final int day;
	public final int hour;
	public final int minute;
	public SimpleTime(int year, int month, int day, int hour, int minute){
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}
	
	public int timeAfter(SimpleTime other){
		int carryOver = 365*(this.year - other.year);
		if (this.month > other.month)
			for (int i = this.month-1; i > other.month+1; i--)
				carryOver += daysPerMonth.get(i);
		if (this.month < other.month)
			for (int i = other.month-1; i > this.month+1; i--)
				carryOver -= daysPerMonth.get(i);
		
		if (this.month != other.month)
			carryOver += this.day + (daysPerMonth.get(other.month) - other.day);
		else
			carryOver += this.day - other.day;
		
		carryOver = carryOver*60*24 + (this.hour-other.hour)*60 + (this.minute - other.minute);
		return carryOver;
	}
	
	public String toString(String separator){
		return year+separator+month+separator+day+separator+hour+separator+minute;
	}
	
	@Override
	public String toString(){
		return toString(",");
	}
	
	public static SimpleTime fromString(String s, String separator){
		String[] args = s.split(separator);
		return new SimpleTime(
				Integer.parseInt(args[0]),
				Integer.parseInt(args[1]),
				Integer.parseInt(args[2]),
				Integer.parseInt(args[3]),
				Integer.parseInt(args[4]));
	}
	
	public static SimpleTime getCurrentTime(){
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
		int day = now.get(Calendar.DAY_OF_MONTH);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		
		return new SimpleTime(year, month, day, hour, minute);
	}
}