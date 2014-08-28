package com.paymentalliance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Checking Array and Linked Lists for Speed	 
 * @author //am
 */
public class ListSpeedTest {
	//private static Runtime rt = Runtime.getRuntime();
	//am use -Xmx1024m for MAX_CAPACITY > 1 mill
	private static int MAX_CAPACITY = 1000000;
	//private static int MAX_CAPACITY = 8;
	
	private static LinkedList <String>linkedList = null;
	private static ArrayList <String>arrayList = null;
	
	private static enum LIST_LOCATIONS {FRONT, ONE_FOURTH, CENTER, THREE_FOURTH, BACK};
	private static StringBuffer nameTag = new StringBuffer("Speed Test: Removing n-records from " + ArrayUtils.toString(LIST_LOCATIONS.values()) + " of Array and Linked Lists");
	
	
	public static void main(String[] args) {
		System.out.println(nameTag.toString() + " - Starting...");
		
		arrayList = (ArrayList<String>)fillList(new ArrayList<String>(MAX_CAPACITY), "ArrayList:OrgValue:", MAX_CAPACITY);		
		linkedList = (LinkedList<String>)fillList(new LinkedList<String>(), "LinkedList:OrgValue:", MAX_CAPACITY);
		
		System.out.println("\nRemoving records using iterator...");
		for (LIST_LOCATIONS location: LIST_LOCATIONS.values()){
			System.out.println("\nRemoving from: " + location + "...");
			removeRecordsUsingIterator(arrayList, location, 10000, false);
			removeRecordsUsingIterator(linkedList, location, 20000, false);
			//rt.gc();
		}

		/*
		System.out.println("\nRemoving records using removeAll()...");
		for (LIST_LOCATIONS location: LIST_LOCATIONS.values()){
			System.out.println("\nRemoving from: " + location + "...");
			removeRecords(arrayList, location, 1000, false);
			removeRecords(linkedList, location, 1000, false);
		}
		*/
		
		/*
		System.out.println("\nInserting records using iterator...");
		for (LIST_LOCATIONS location: LIST_LOCATIONS.values()){
			insertRecordsUsingIterator(arrayList, location, 2000, false);
			insertRecordsUsingIterator(linkedList, location, 2000, false);
		}
		*/
		
		System.out.println("\n" + nameTag.toString() + " - Done!");
	}
	

	
	//returns List filled with Strings
	private static List<String>fillList(List <String>rslt, String pref, Integer howMany){
		long t1 = System.currentTimeMillis();
		for (Integer i = 0; i < howMany; i++){
			rslt.add(pref + i);
		}
	
		reportDiff("Adding " + howMany + " records to " + rslt.getClass().getSimpleName() + " took: " , t1, null);
		return rslt;
	}
	
	//reporting procedure
	private static void reportDiff(String pref, long started, String suff){
		System.out.println(pref + (System.currentTimeMillis() - started) + " ms." + (suff == null ? "" : suff));
	}
	
	//Removes howMany records from prescribed location using removeAll (slow)
	private static void removeRecords(List <String>list, LIST_LOCATIONS loc, int howMany, boolean logList) {
		if (logList){
			System.out.println("Content BEFORE: " + ArrayUtils.toString(list.toArray()));
		}
		
		long t1 = System.currentTimeMillis();
		try{
			List <String>killList = getKillList(list, loc, howMany);
			//reportDiff("Getting " + howMany + " records from: " + list.getClass().getSimpleName() + " " + loc + " took: ", t1, null);
			
			//list.removeAll(killList);
			CollectionUtils.removeAll(list, killList);

			reportDiff("Removing " + howMany + " records from: " + list.getClass().getSimpleName() + " " + loc + " took: ", t1, " Items left: " + list.size());
			
			if (logList){
				System.out.println("Content AFTER: " + ArrayUtils.toString(list.toArray()));
			}
		}catch (Exception e){
			System.out.println("Unable to Remove, reason: " + e);
		}
	}

	//Removes howMany records using iterator (fast), log List context before/after if logList=true
	private static void removeRecordsUsingIterator(List<String> list, LIST_LOCATIONS loc, int howMany, boolean logList) {
		String travelingTime = "";
		int position = 0;
		int startPosition = position;
		
		if (logList){
			System.out.println("Content BEFORE: " + ArrayUtils.toString(list.toArray()));
		}
		
		long t1 = System.currentTimeMillis();
		try{
			switch (loc){
				case FRONT:{ //
					startPosition = position;
					break;
				}
				case CENTER:{ //
					startPosition = (list.size() / 2) - (howMany /2);
					break;
				}
				case BACK:{ // 
					startPosition = list.size() - howMany;
					break;
				}
				case ONE_FOURTH:{ // 
					startPosition = (list.size() / 4) - (howMany /2) ;
					break;
				}
				case THREE_FOURTH:{ //
					startPosition = (list.size() / 4 * 3) - (howMany /2) ;
					break;
				}		
				default:
					throw new Exception("Unhandled " + loc + " Exiting..");
			}
			
			int cnt = 0;
			Iterator <String>itr = list.iterator();			
			while (itr.hasNext() && cnt != howMany){
				itr.next();
				
				if (position >= startPosition){

					//log time spent to reach starting point
					if (cnt == 0){
						long diff = System.currentTimeMillis() - t1;
						travelingTime = diff == 0 ? " No Travelling Time!" : " (Travelling Time: " + diff + " ms.)";
					}
					
					itr.remove();
					cnt++;
				}
				
				position++;				
			}
			
			reportDiff("Removing " + howMany + " records from: " + list.getClass().getSimpleName() 
					+ " " + loc
					+ " using Iterator took: ", t1
					, travelingTime + " Items left: " + list.size());

			if (logList){
				System.out.println("Content AFTER: " + ArrayUtils.toString(list.toArray()));
			}
		}catch (Exception e){
			System.out.println("Unable to Remove, reason: " + e);
		}
	}
	
	//return list with items-to-be-killed
	private static List <String>getKillList(List <String>list, LIST_LOCATIONS loc, int howMany) throws Exception{
		ArrayList <String>rslt = new ArrayList<String>(howMany);
		int startPosition = 0;
		int endPosition = howMany;
		
		switch (loc){
			case FRONT:{ //
				startPosition = 0;
				endPosition = howMany - 1;
				break;
			}
			case CENTER:{ //
				startPosition = (list.size() / 2) - (howMany /2);
				endPosition = startPosition + howMany - 1;
				break;
			}
			case BACK:{ // 
				endPosition = list.size() - 1;
				startPosition = endPosition - howMany + 1;
				break;
			}
			case ONE_FOURTH:{ // 
				startPosition = (list.size() / 4) - (howMany /2) ;
				endPosition = startPosition + howMany - 1;
				break;
			}
			case THREE_FOURTH:{ // 
				startPosition = (list.size() / 4 * 3) - (howMany /2) ;
				endPosition = startPosition + howMany - 1;
				break;
			}		
			default:
				throw new Exception("Unhandled " + loc + " Exiting..");
		}
		
		//add requested range to kill-list
		for(int i = startPosition; i <= endPosition; i++){
			rslt.add(list.get(i));
		}

		return rslt;
	}
	
	//Inserting records
	private static void insertRecordsUsingIterator(List<String> list, LIST_LOCATIONS loc, int howMany, boolean logList) {
		int startPosition = 0;
		
		if (logList){
			System.out.println("Content BEFORE, size: " + list.size() + ": " + ArrayUtils.toString(list.toArray()));
		}
		
		long t1 = System.currentTimeMillis();
		try{
			switch (loc){
				case FRONT:{ //
					startPosition = 0;
					break;
				}
				case CENTER:{ //
					startPosition = list.size() / 2;
					break;
				}
				case BACK:{ // 
					startPosition = list.size();
					break;
				}
				case ONE_FOURTH:{ // 
					startPosition = list.size() / 4;
					break;
				}
				case THREE_FOURTH:{ //
					startPosition = list.size() / 4 * 3 ;
					break;
				}		
				default:
					throw new Exception("Unhandled " + loc + " Exiting..");
			}
			
			int cnt = 0;
			for (int i=startPosition; i < (startPosition + howMany); i++){
				list.add(i, list.getClass().getSimpleName() + ":" + loc + ":AddedValue:" + cnt);
				cnt++;
			}
			
			reportDiff("Inserting " + howMany + " records to: " + list.getClass().getSimpleName() 
					+ " " + loc 
					+ " using Iterator took: ", t1, " Items now: " + list.size());

			if (logList){
				System.out.println("Content AFTER, size: " + list.size() + ": " + ArrayUtils.toString(list.toArray()));
			}
		}catch (Exception e){
			System.out.println("Unable to Add, reason: " + e);
		}
	}
	
}
