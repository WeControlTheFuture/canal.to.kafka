package com.alibaba.otter.canal.to.kafka;

import java.util.ArrayList;
import java.util.List;

import com.ai.paas.ipaas.mds.IMessageProcessor;
import com.ai.paas.ipaas.mds.IMsgProcessorHandler;
import com.ai.paas.ipaas.mds.vo.MessageAndMetadata;

public class MsgProcessorHandlerImpl implements IMsgProcessorHandler {

	@Override
	public IMessageProcessor[] createInstances(int partitionNum) {
		List<IMessageProcessor> processors = new ArrayList<IMessageProcessor>();
		for (int i = 0; i < partitionNum; i++) {
			processors.add(new IMessageProcessor() {
				@Override
				public void process(MessageAndMetadata message) throws Exception {
					if (null != message) {
						System.out.println("------Topic:" + message.getTopic() + ",key:" + new String(message.getKey(), "UTF-8") + ",content:" + new String(message.getMessage(), "UTF-8"));
					}
				}
			});
		}
		return processors.toArray(new IMessageProcessor[processors.size()]);
	}
}
