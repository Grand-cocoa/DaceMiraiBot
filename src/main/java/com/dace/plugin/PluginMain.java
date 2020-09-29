package com.dace.plugin;

import com.dace.base.FunctionBase;
import com.dace.plugin.reply.Reply;
import com.dace.utils.ClassLoaderUtil;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PluginMain extends PluginBase {

	private final String path = System.getProperty("user.dir") + "/plugins/DaceMiraiBot";
	private final String jsonPath = path + "/functionList.json";
	private List<FunctionBase> functionList;
	private long lastTime = 0;

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		getLogger().info("插件已加载 ---- DaceMiraiBot\n" + info());
		if (functionList == null)
			functionList = FunctionUtil.loadFunctionList(jsonPath);
		else {
			long l = 0;
			try {
				l = FunctionUtil.lastTime(jsonPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (l != lastTime && lastTime != 0){
				functionList = FunctionUtil.loadFunctionList(jsonPath);
				lastTime = l;
			}
		}
		getEventListener().subscribeAlways(MessageEvent.class, event -> {
			String message = event.getMessage().toString();
			message = message.split("(?:\\[mirai:source:(.*?)?])")[1];
			System.out.println("[info][DaceMiraiBot]接收消息：" + message);
			if (functionList != null) {
				for (FunctionBase functionBase :
						functionList) {
					if (Pattern.matches(functionBase.getFunctionKey(), message)) {
						Class<?> functionClass = null;
						Object function = null;
						try {
							functionClass = new ClassLoaderUtil(path)
									.findClass(functionBase.getFunctionName());
							function = new ClassLoaderUtil(path)
									.findClass(functionBase.getFunctionName())
									.newInstance();
						} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
							e.printStackTrace();
						}
						Method[] methods = new Method[0];
						if (functionClass != null) {
							methods = functionClass.getDeclaredMethods();
						}
						for (Method method :
								methods) {
							if (method.getName().equals("run")) {
								Map<String, String> map = new HashMap<>();
								map.put("message", message);
								map.put("sendId", event.getSender().getId() + "");
								map.put("sendName", event.getSenderName());
								try {
									Object ret = method.invoke(function, map);
									if (ret instanceof String) {
										String s = ret.toString();
										if (!"".equals(s))
											event.getSubject().sendMessage(
												MiraiCode.parseMiraiCode(s)
										);
										return;
									}else if (ret instanceof Map){
										Map<String, Object> s = (Map<String, Object>) ret;
										message = (String)s.get("message");
										if (s.get("message") != null) {
											if (s.get("image") != null) {
												if (s.get("image") instanceof List) {
													String[] split = message.split("\\[image]");
													List<Object> image = (List<Object>) s.get("image");
													for (int i = 0; i < split.length; i++) {
														if (image.get(i) instanceof URL)
															split[i] = split[i] + event.getSubject().uploadImage((URL) image.get(i)).toMiraiCode();
														else
															split[i] = split[i] + event.getSubject().uploadImage((File) image.get(i)).toMiraiCode();
													}
//													if (image.get(0) instanceof URL) {
//														for (int i = 0; i < split.length; i++) {
//															split[i] = split[i] + event.getSubject().uploadImage((URL) image.get(i)).toMiraiCode();
//														}
//													}else {
//														for (int i = 0; i < split.length; i++) {
//															split[i] = split[i] + event.getSubject().uploadImage((File) image.get(i)).toMiraiCode();
//														}
//													}
													StringBuilder stringBuilder = new StringBuilder();
													Arrays.stream(split).forEach(stringBuilder::append);
													event.getSubject().sendMessage(
															MiraiCode.parseMiraiCode(
																	stringBuilder.toString()
															)
													);
												}else {
													String code;
													if (s.get("image") instanceof URL)
														code = event.getSubject().uploadImage((URL) s.get("image")).toMiraiCode();
													else
														code = event.getSubject().uploadImage((File) s.get("image")).toMiraiCode();
													event.getSubject().sendMessage(
															MiraiCode.parseMiraiCode(
																	message.replace("[image]", code)
															)
													);
												}
											}
											//在此处增加if以匹配更多
										}
									}
								} catch (IllegalAccessException | InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}
						return;
					}
				}
				String run = Reply.getBase().run(message);
				if (run != null && !"".equals(run))
					event.getSubject().sendMessage(
							MiraiCode.parseMiraiCode(run)
					);
			}
		});
	}

	@Override
	public void onLoad() {
		super.onLoad();
	}

	private String info(){
		return "                                                 \n" +
				" ________  ________  ________  _______           \n" +
				"|\\   ___ \\|\\   __  \\|\\   ____\\|\\  ___ \\          \n" +
				"\\ \\  \\_|\\ \\ \\  \\|\\  \\ \\  \\___|\\ \\   __/|         \n" +
				" \\ \\  \\ \\\\ \\ \\   __  \\ \\  \\    \\ \\  \\_|/__       \n" +
				"  \\ \\  \\_\\\\ \\ \\  \\ \\  \\ \\  \\____\\ \\  \\_|\\ \\      \n" +
				"   \\ \\_______\\ \\__\\ \\__\\ \\_______\\ \\_______\\     \n" +
				"    \\|_______|\\|__|\\|__|\\|_______|\\|_______|     \n" +
				"                                                 \n" +
				" _____ ______   ___  ________  ________  ___     \n" +
				"|\\   _ \\  _   \\|\\  \\|\\   __  \\|\\   __  \\|\\  \\    \n" +
				"\\ \\  \\\\\\__\\ \\  \\ \\  \\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\   \n" +
				" \\ \\  \\\\|__| \\  \\ \\  \\ \\   _  _\\ \\   __  \\ \\  \\  \n" +
				"  \\ \\  \\    \\ \\  \\ \\  \\ \\  \\\\  \\\\ \\  \\ \\  \\ \\  \\ \n" +
				"   \\ \\__\\    \\ \\__\\ \\__\\ \\__\\\\ _\\\\ \\__\\ \\__\\ \\__\\\n" +
				"    \\|__|     \\|__|\\|__|\\|__|\\|__|\\|__|\\|__|\\|__|\n" +
				"                                                 \n" +
				" ________  ________  _________                   \n" +
				"|\\   __  \\|\\   __  \\|\\___   ___\\                 \n" +
				"\\ \\  \\|\\ /\\ \\  \\|\\  \\|___ \\  \\_|                 \n" +
				" \\ \\   __  \\ \\  \\\\\\  \\   \\ \\  \\                  \n" +
				"  \\ \\  \\|\\  \\ \\  \\\\\\  \\   \\ \\  \\                 \n" +
				"   \\ \\_______\\ \\_______\\   \\ \\__\\                \n" +
				"    \\|_______|\\|_______|    \\|__|                \n" +
				"                                                 ";
	}
}