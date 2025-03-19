package com.others;

public class TrimExample {
	public static void main(String[] args) {
		// 示例1：字串前後有一般空格
		String name1 = "   John Smith   ";
		System.out.println("原始字串：[" + name1 + "]");
		System.out.println("使用trim後：[" + name1.trim() + "]");

		// 示例2：字串前後有Tab和換行符
		String name2 = "\t\nAlice Johnson\t\n";
		System.out.println("原始字串：[" + name2 + "]");
		System.out.println("使用trim後：[" + name2.trim() + "]");
	}
}
