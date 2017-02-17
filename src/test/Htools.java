package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Htools {

	/**
	 * 将汇编指令转换成机器指令
	 * @param str
	 * @return
	 */
	public List<String> ASEtoMAC(String[] str) {

		List<String> reslut = new ArrayList<String>();
		Map<String, Integer> Tags = new HashMap<>();
		
		for (int i = 0; i < str.length; i++) {
			String res = "";
			String code = "";
			String[] arr = {};
			if(str[i].matches("[^@]+:[^@]+")){
				String tag = str[i].substring(0, str[i].indexOf(":"));
				Tags.put(tag, i);
				System.out.println(tag+Tags.get(tag));
				str[i] = str[i].substring(str[i].indexOf(":")+1);
			}
			
			arr = str[i].split("\\s+|,");
			
			switch (upperStr(arr[0])) {

			case "LOAD":

				if (arr.length != 3) {
					System.out.println("arr's length :"+arr.length);
					return null;
				} else {

					if (!checkRegister(arr[1])) { //寄存器格式错误
						System.out.println(arr[1]);
						System.out.println("寄存器错误");
						return null;
					}

					if (arr[2].substring(0, 1).equals("[")) {
						//System.out.println(arr[2].substring(0, 1));
						String num = arr[2].substring(1,arr[2].length() - 1);
						if (isNumeric(num) && !overflow(num)) { //判断传入的主存单元地址是否合法
							code = "1";
							String register = arr[1].substring(arr[1].length() - 1);
							res = code + register + num;
							//System.out.println(res);
							reslut.add(res);
						} else {
							System.out.println("传入数值错误code=1");
							return null;
						}

					} else {
						code = "2";
						if (isNumeric(arr[2]) && !overflow(arr[2])) {
							String register = arr[1].substring(arr[1].length() - 1);
							res = code + register + arr[2];
							//System.out.println(res);
							reslut.add(res);
						} else {
							System.out.println("传入数值错误code=2");
							return null;
						}
					}
				}
				break;
				
			case "STORE":
				
				code = "3";
				if(checkRegister(arr[1]) && !overflow(arr[2])){
					String register = arr[1].substring(arr[1].length() - 1);
					res = code+register+arr[2];
					reslut.add(res);
				}else{
					System.out.println("发生错误");
				}
				break;
				
			case "MOV":
				
				code = "4";
				if(arr.length != 3){
					return null;
				}
				if(checkRegister(arr[1]) && checkRegister(arr[2])){
					String reg1 = arr[1].substring(arr[1].length()-1);
					String reg2 = arr[2].substring(arr[2].length()-1);
					res = code+"0"+reg1+reg2;
					reslut.add(res);
				}
				break;
			case "ADD":
				
				if(arr.length != 4){
					return null;
				}
				code = "5";
				if(checkRegister(arr[1]) && checkRegister(arr[2]) && checkRegister(arr[3])){
					String reg1 = arr[1].substring(arr[1].length()-1);
					String reg2 = arr[2].substring(arr[2].length()-1);
					String reg3 = arr[3].substring(arr[3].length()-1);
					res = code+reg1+reg2+reg3;
					reslut.add(res);
				}
				break;
			case "SHL":
				
				code = "6";
				if(arr.length != 3){
					System.out.println("length error");
					return null;
				}
				
				if(checkRegister(arr[1]) && !overSHL(arr[2])){
					String reg1 = arr[1].substring(arr[1].length()-1);
					String rnum = arr[2].substring(arr[2].length()-1);
					res = code+reg1+"0"+rnum;
					reslut.add(res);
				}else{
					System.out.println("SHL error");
				}
				break;
			case "NOT":
				
				code = "7";
				if(arr.length != 2){
					return null;
				}
				if(checkRegister(arr[1])){
					String reg1 = arr[1].substring(arr[1].length()-1);
					res = code+reg1+"00";
					reslut.add(res);
				}
				
				break;
			case "JMP":
				
				boolean flag = false;
				code = "8";
				if(arr.length != 3){
					return null;
				}
				
				if(checkRegister(arr[1])){
					String reg1 = arr[1].substring(arr[1].length()-1);
					String tag = arr[2];
					Iterator it = Tags.keySet().iterator();
					while(it.hasNext()){
						String key = it.next().toString();
						if(tag.equals(key)){
							int value = Tags.get(key);
							String hexaddr = Integer.toHexString(value*2);
							res = code+reg1+hexaddr;
							reslut.add(res);
							flag = true;
							break;
						}
					}
					
					if(!flag){
						System.out.println("不存在此標誌");
					}
				}
				break;
			case "HALT":
				
				if(arr.length != 1){
					return null;
				}
				code = "9";
				res = code+"000";
				reslut.add(res);
				break;

			default:
				break;
			}
		}

		return reslut;
	}

	/**
	 * 将汇编操作指令转换为大写
	 * 
	 * @param str 汇编操作指令
	 *            
	 * @return
	 */
	public String upperStr(String str) {
		return str.toUpperCase();
	}

	/**
	 * 检查寄存器是否溢出
	 * 
	 * @param hexnum
	 *            
	 * @return boolean 操作数是否溢出,溢出返回true
	 */
	public boolean overflow(String hexnum) {
		boolean res = false;

		int hex = Integer.parseInt(hexnum, 16);
		if (hex < 0x00 || hex > 0xFF) {
			res = true;
		}
		return res;
	}
	
	public boolean overSHL(String hexnum) {
		boolean res = false;

		int hex = Integer.parseInt(hexnum, 16);
		if (hex < 0x0 || hex > 0xF) {
			res = true;
		}
		return res;
	}

	/**
	 * 检查寄存器是否合法
	 * 
	 * @param register 
	 *            
	 * @return boolean 是否合法寄存器,合法返回true
	 */
	public boolean checkRegister(String register) {

		boolean res = false;
		if (register.length() == 2) {
			String rstr = register.substring(0, 1);
			
			if (rstr.equals("r") || rstr.equals("R")) {
				int rnum = Integer.parseInt(register.substring(register.length() - 1));
			
				if (rnum >= 0 && rnum <= 5) {
					res = true;
				}
			}
		}

		return res;
	}

	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9&a-f&A-F]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
}
