package com.alibaba.otter.canal.to.kafka;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.mds.IMessageProcessor;
import com.ai.paas.ipaas.mds.IMsgProcessorHandler;
import com.ai.paas.ipaas.mds.vo.MessageAndMetadata;
import com.google.gson.Gson;

public class DESMsgProcessorHandler implements IMsgProcessorHandler {
	protected final static Logger logger = LoggerFactory.getLogger(DESMsgProcessorHandler.class);
	private IDESMsgProcessor desMsgProcessor;

	public DESMsgProcessorHandler(IDESMsgProcessor desMsgProcessor) {
		this.desMsgProcessor = desMsgProcessor;
	}

	@Override
	public IMessageProcessor[] createInstances(int partitionNum) {
		List<IMessageProcessor> processors = new ArrayList<IMessageProcessor>();
		for (int i = 0; i < partitionNum; i++) {
			processors.add(new IMessageProcessor() {
				@Override
				public void process(MessageAndMetadata message) throws Exception {
					if (null != message) {
						Gson gson = new Gson();
						System.out.println("msg====================" + new String(message.getMessage(), "UTF-8"));
						DesMsg msg = gson.fromJson(new String(message.getMessage(), "UTF-8"), DesMsg.class);
						desMsgProcessor.process(msg);
						logger.debug("------Topic:" + message.getTopic() + ",key:" + new String(message.getKey(), "UTF-8") + ",content:" + new String(message.getMessage(), "UTF-8"));
					}
				}
			});
		}
		return processors.toArray(new IMessageProcessor[processors.size()]);
	}

}
