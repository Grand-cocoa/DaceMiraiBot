package com.dace.plugin.reply;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Reply {
	private long lastModified;
	private Map<String, List<String>> replyBases;
	private static Reply reply;

	private Reply() {
	}

	public static Reply getBase(){
		if (reply == null)
			reply = new Reply();
		return reply;
	}

	public String run(String message) {
		long l = ReplyFileUtil.getLastModified();
		if (l > lastModified){
			try {
				replyBases = ReplyFileUtil.readerFileData();
			} catch (IOException e) {
				return "IOException:" + e.toString();
			}
			lastModified = l;
		}
		assert replyBases != null;
		Set<String> strings = replyBases.keySet();
		List<String> list = null;
		for (String s :
				strings) {
			if (message.contains(s)) {
				list = replyBases.get(s);
				break;
			}
		}
		if (list == null){
			return null;
		}
		return list.get(new Random().nextInt(list.size()));
	}

}
