package cn.jdfo.tool;

import java.security.MessageDigest;

public class MD5 {
	
	public static String getMD5(String s) {
	    try {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] bytes = md.digest(s.getBytes("utf-8"));
	        return toHex(bytes);
	    }
	    catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	private static String toHex(byte[] bytes) {
	    final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
	    StringBuilder ret = new StringBuilder(bytes.length * 2);
	    for (int i=0; i<bytes.length; i++) {
	        ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
	        ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
	    }
	    return ret.toString();
	}

	public static void main(String[] args) {
		int[] nums=new int[4];
		nums[0]=10;nums[1]=5;nums[2]=2;nums[3]=6;
		System.out.println(numSubarrayProductLessThanK(nums,100));
	}

	public static int numSubarrayProductLessThanK(int[] nums, int k) {
		int len=nums.length,count=0;
		for(int a=0;a<len;a++){
			for(int b=0;b<len;b++){
				int product=nums[a];
				for(int i=a+1;i<=b;i++){
					product=product*nums[i];
				}
				if(product<k)count++;
				else break;
			}
		}
		return count;
	}
}
