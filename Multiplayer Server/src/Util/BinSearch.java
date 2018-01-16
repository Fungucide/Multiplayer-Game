package Util;

public class BinSearch {
	
	public static int bSearch(int val) {
		int ub=101;
		int lb=1;
		while(ub-lb>1) {
			int cur = (ub+lb)>>1;
			if(cur>val)
				ub = cur;
			else
				lb=cur;
		}
		return lb;
	}
	
	public static void main(String[] args) {
		for(int i =1;i<=100;i++) {
			System.out.println(bSearch(i)==i);
		}
	}
}
