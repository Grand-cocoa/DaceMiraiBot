package com.dace.plugin.reply;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ReplyFileUtil {
	public static final String path =
			System.getProperty("user.dir") + "/plugins/DaceMiraiBot/Reply.json";

	public static long getLastModified() {
		return new File(path).lastModified();
	}

	public static Map<String, List<String>> readerFileData() throws IOException {
		File file = new File(path);
		if (!file.exists())
			if (file.createNewFile())
				return new HashMap<>();
		else
			return null;
		String data = getData(file);
		return JSON.parseObject(data, HashMap.class);
	}

	public static void writerFile(Map<String, List<String>> data) throws IOException {
		File file = new File(path);
		if (!file.exists()) if (!file.createNewFile()) return;
		FileOutputStream os;
		OutputStreamWriter osw;
		BufferedWriter writer;
		try {
			os = new FileOutputStream(file);
			osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
			writer = new BufferedWriter(osw);
			writer.write(JSON.toJSONString(data));
			writer.flush();
			writer.close();
			osw.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getData(File file) {
		FileInputStream is = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		String line;
		StringBuilder json = new StringBuilder();
		try {
			is = new FileInputStream(file);
			isr = new InputStreamReader(is, StandardCharsets.UTF_8);
			reader = new BufferedReader(isr);
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return json.toString();
	}
}
