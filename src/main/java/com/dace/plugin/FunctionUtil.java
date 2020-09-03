package com.dace.plugin;

import com.alibaba.fastjson.JSON;
import com.dace.base.FunctionBase;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FunctionUtil {
	public static List<FunctionBase> loadFunctionList(String path){
		File func = new File(path);
		if (!func.exists()){
			try {
				if (!func.createNewFile()){
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileInputStream is = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		String line;
		StringBuilder json = new StringBuilder();
		try {
			is = new FileInputStream(func);
			isr = new InputStreamReader(is, StandardCharsets.UTF_8);
			reader = new BufferedReader(isr);
			while ((line = reader.readLine()) != null){
				json.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(reader != null) {
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
		if ("".equals(json.toString()))
			return null;
		return JSON.parseArray(json.toString(), FunctionBase.class);
	}
}
