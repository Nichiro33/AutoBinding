package nishimoto.yoshiken;

import java.util.ArrayList;

public class RegisterAllocation {
	private static int[][] regs;
	private static int lines;

	public static int[][] getRegs(){
		return regs;
	}

	public static int getLines(){
		return lines;
	}

	public static void setLines(int k){
		lines = k;
	}

	public static void Basic(int[] edgeId, int[] start, int[] end, int[] chou, int maxtime){
		int t = 0;
		regs = null;
		regs = new int[edgeId.length][maxtime];
		boolean check = false;
		while(!check){
			int h = 0;
			int watermark = 0;

			while(true){
				ArrayList<Integer> xs = new ArrayList<Integer>();
				for(int i = 0; i < edgeId.length; i++){
					if(start[i] > watermark){
						xs.add(i);
					}
				}
				if(xs.isEmpty()){
					break;
				}
				int num = start[xs.get(0)];
				int y = xs.get(0);

				for(int i = 0; i < xs.size(); i++){
					if(num > start[xs.get(i)]){
						num = start[xs.get(i)];
						y = xs.get(i);
					}
				}

				boolean ki = false;

				for(int r = 0; r < chou.length; r++){
					if(chou[r] == y){
						ki = true;
						break;
					}
				}

				if(!ki){
					watermark = end[y];
					regs[t][h] = y;

					System.out.print("regs["+ t +"][" + h + "]=" + regs[t][h] +" ");

					h = h + 1;
				}

				edgeId[y] = -1;
				start[y] = -1;
				end[y] = -1;

				xs.clear();
			}

			for(int p = h; p < maxtime; p++){
				regs[t][p] = -1;
				System.out.print("regs["+ t +"][" + p + "]=" + regs[t][h] +" ");
			}
			System.out.print("\n");

			t = t + 1;

			for(int i = 0; i < edgeId.length; i++){
				if(edgeId[i] != -1){
					check = false;
					break;
				}
				setLines(t);
				check = true;
			}
		}
	}

	public static void Wang(ArrayList<Integer> top, int[] edgeId, int[] ver1,int[] start, int[] end, int[] chou, int maxtime){
		for(int i = 0; i < edgeId.length; i++){
			if(top.contains(ver1[i])){
				end[i] = maxtime;
			}
		}
		Basic(edgeId, start, end, chou, maxtime);
	}
}