package com.alibaba.otter.canal.to.kafka;

import org.junit.Test;

import com.ai.paas.ipaas.mds.IMessageConsumer;
import com.ai.paas.ipaas.mds.IMessageSender;
import com.ai.paas.ipaas.mds.MsgConsumerFactory;
import com.ai.paas.ipaas.mds.MsgSenderFactory;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.google.gson.Gson;

public class MDSTest {

	@Test
	public void sendMdsMsg() {
		String srvId = "MDS002";
		String authAddr = "http://10.1.228.198:14821/iPaas-Auth/service/check";
		String authUser = "491676539@qq.com";
		AuthDescriptor ad = new AuthDescriptor(authAddr, authUser, "111111", srvId);
		IMessageSender msgSender = MsgSenderFactory.getClient(ad, "FA5AA5D44D7042C891E471E50D323015_MDS002_1903967110");
		for (int i = 140; i < 150; i++)
			msgSender.send("是不是第" + i + "条", i % 2);
	}

	@Test
	public void consumeMdsMsg() {
		String srvId = "MDS002";
		String authAddr = "http://10.1.228.198:14821/iPaas-Auth/service/check";
		AuthDescriptor ad = new AuthDescriptor(authAddr, "491676539@qq.com", "111111", srvId);
		String topic = "FA5AA5D44D7042C891E471E50D323015_MDS002_1903967110";
		IMessageConsumer msgConsumer = MsgConsumerFactory.getClient(ad, topic, new DESMsgProcessorHandler(new IDESMsgProcessor() {

			@Override
			public void process(DesMsg desMsg) {
				System.out.println("table======="+desMsg.getHeader().getTableName());
				System.out.println(new Gson().toJson(desMsg));
			}
		}));
		msgConsumer.start();
		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
