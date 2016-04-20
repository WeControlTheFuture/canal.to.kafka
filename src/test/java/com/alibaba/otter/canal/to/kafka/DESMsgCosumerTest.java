package com.alibaba.otter.canal.to.kafka;

import org.junit.Test;

import com.ai.paas.ipaas.uac.vo.AuthDescriptor;

public class DESMsgCosumerTest {
	
	@Test
	public void consumeMdsMsg() {
		String srvId = "MDS002";
		String authAddr = "http://10.1.228.198:14821/iPaas-Auth/service/check";
		AuthDescriptor ad = new AuthDescriptor(authAddr, "491676539@qq.com", "111111", srvId);
		String topic = "FA5AA5D44D7042C891E471E50D323015_MDS002_1903967110";
	}
}
