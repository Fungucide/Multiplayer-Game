package Util;

import java.util.Random;

public class WorldGen {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Random r = new Random();
		for (int i = 0; i < 3000 / 50; i++) {
			for (int j = 0; j < 3000 / 50; j++) {
				int num = r.nextInt() % 5;
				if (num == 4) {
					System.out.print("1 ");
				} else {
					System.out.print("0 ");
				}
			}
			System.out.println();
		}
	}

}
