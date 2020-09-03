package com.dace.plugin;

import com.dace.base.FunctionBase;
import com.dace.utils.ClassLoaderUtil;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PluginMain extends PluginBase {

	private final String path = System.getProperty("user.dir") + "/plugins/DaceMiraiBot";

	@Override
	public void onEnable() {
		getLogger().info("插件已加载 ---- DaceMiraiBot\n" + info());
		List<FunctionBase> functionList = FunctionUtil.loadFunctionList(path + "/functionList.json");
		getEventListener().subscribeAlways(MessageEvent.class, event -> {
			String message = event.getMessage().contentToString();
			if (functionList != null) {
				for (FunctionBase functionBase :
						functionList) {
					if (Pattern.matches(functionBase.getFunctionKey(), message)) {
						Class<?> functionClass = null;
						Object function = null;
						try {
							functionClass = new ClassLoaderUtil(path).findClass(functionBase.getFunctionName());
							function = functionClass.newInstance();
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
									event.getSubject().sendMessage(
											MiraiCode.parseMiraiCode(
													method.invoke(function, map).toString()
											)
									);
								} catch (IllegalAccessException | InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
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
