package com.alibaba.otter.canal.to.kafka;

import org.apache.oro.text.regex.Perl5Matcher;

import com.alibaba.otter.canal.filter.PatternUtils;
import com.alibaba.otter.canal.filter.aviater.AviaterRegexFilter;

public class AviatorTest {

	public static void main(String[] args) {
		System.out.println("abcdef".hashCode()%2);
//		AviaterRegexFilter regex = new AviaterRegexFilter("test.tcounts,test.user_[0-9]{4}");
		// System.out.println("result====================" +
		// regex.filter("test.tcounts"));
//		System.out.println("result====================" + regex.filter("test.user_0001"));
//		Perl5Matcher matcher = new Perl5Matcher();
//		boolean isMatch = matcher.matches("test.user_0001", PatternUtils.getPattern("^test.user_[0-9][0-9][0-9][0-9]$|^test.tcounts$"));
//		System.out.println("isMatch=========================" + isMatch);
	}
}
