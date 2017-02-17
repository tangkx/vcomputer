package test;

import java.util.ArrayList;
import java.util.List;

public class Test {

	public Test(){}
	
	public int convertToInt(String str){
		
		return Integer.parseInt(str, 16);
	}
	
	public String convertToHex(int num){
		
		return Integer.toHexString(num);
	}
	
	public static void main(String[] args) {
		String a = "5F";
		String b = "66";
		
		Test t = new Test();
		Htools ht = new Htools();
		
//		System.out.println(ht.overflow("00"));
//		System.out.println(ht.overflow("AA"));
//		System.out.println(ht.overflow("FF"));
//		int an = t.convertToInt(a);
//		int bn = t.convertToInt(b);
//		int res = an + bn;
//		System.out.println(t.convertToHex(res));
		
		String []str = {"flag1:LOAD R0,23","LOAD R1,34","LOAD R3,[ff]","flag2:not r1","halt","SHL R1,a","JMP R0,flag2"};
		List<String> list = ht.ASEtoMAC(str);
		for(int i = 0; i < list.size(); i++){
			System.out.println(list.get(i));
		}
		
//		System.out.println(ht.isNumeric("00"));
//		System.out.println(ht.isNumeric("AB"));
//		System.out.println(ht.isNumeric("DD"));
//		System.out.println(ht.isNumeric("gg"));

	}

}
