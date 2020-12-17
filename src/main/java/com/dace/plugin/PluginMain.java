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

/**
 * @author Kane
 */
public class PluginMain extends PluginBase {

	private final String path = System.getProperty("user.dir") + "/plugins/DaceMiraiBot";
	private final String jsonPath = path + "/functionList.json";
	private List<FunctionBase> functionList;
	private long lastTime = 0;

	@Override
	public void onEnable() {
		getLogger().info("插件已加载 ---- DaceMiraiBot\n" + info());
		fileUpdate();
		getEventListener().subscribeAlways(MessageEvent.class, event -> {
			String message = event.getMessage().toString();
			message = message.split("(?:\\[mirai:source:(.*?)?])")[1];
			System.out.println("[info][DaceMiraiBot]接收消息：" + message);
			if (functionList != null) {
				if ("help".equals(message) || "Help".equals(message)) {
					event.getSubject().sendMessage(
							MiraiCode.parseMiraiCode(
									onHelp(functionList)
							)
					);
					return;
				}
				for (FunctionBase functionBase :
						functionList) {
					if (Pattern.matches(functionBase.getFunctionKey(), message)) {
						Class<?> functionClass = null;
						Object function = null;
						try {
							functionClass = new ClassLoaderUtil(path)
									.findClass(functionBase.getFunctionName());
							function = functionClass.newInstance();
						} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
							e.printStackTrace();
						}
						Method[] methods;
						if (functionClass != null) {
							methods = functionClass.getDeclaredMethods();
							runMerhods(event, message, function, methods);
						}
						return;
					}
				}
				reply(event, message);
			}
		});
	}

	private void reply(MessageEvent event, String message) {
		String run = Reply.getBase().run(message);
		event.getSource().getId();
		if (run != null && !"".equals(run)) {
			event.getSubject().sendMessage(
					MiraiCode.parseMiraiCode(run)
			);
		}
	}

	private void fileUpdate() {
		if (functionList == null) {
			functionList = FunctionUtil.loadFunctionList(jsonPath);
		} else {
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
	}

	@SuppressWarnings("unchecked")
	private void runMerhods(MessageEvent event, String message, Object function, Method[] methods) {
		for (Method method :
				methods) {
			if ("run".equals(method.getName())) {
				Map<String, String> map = new HashMap<>(16);
				map.put("message", message);
				map.put("sendId", event.getSender().getId() + "");
				map.put("sendName", event.getSenderName());
				try {
					Object ret = method.invoke(function, map);
					if (ret instanceof String) {
						String s = (String) ret;
						if (!"".equals(s)) {
							event.getSubject().sendMessage(
									MiraiCode.parseMiraiCode(s)
							);
						}
						return;
					}else if (ret instanceof Map){
						message = mapToDealWith(event, (Map<String, Object>) ret);
					}
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String mapToDealWith(MessageEvent event, Map<String, Object> ret) {
		String message;
		message = (String) ret.get("message");
		if (ret.get("message") != null) {
			if (ret.get("image") != null) {
				if (ret.get("image") instanceof List) {
					String[] split = message.split("\\[image]");
					List<Object> image = (List<Object>) ret.get("image");
					for (int i = 0; i < split.length; i++) {
						if (image.get(i) instanceof URL) {
							split[i] = split[i] + event.getSubject().uploadImage((URL) image.get(i)).toMiraiCode();
						} else {
							split[i] = split[i] + event.getSubject().uploadImage((File) image.get(i)).toMiraiCode();
						}
					}
					StringBuilder stringBuilder = new StringBuilder();
					Arrays.stream(split).forEach(stringBuilder::append);
					message = stringBuilder.toString();
				}else {
					String code;
					if (ret.get("image") instanceof URL) {
						code = event.getSubject().uploadImage((URL) ret.get("image")).toMiraiCode();
					} else {
						code = event.getSubject().uploadImage((File) ret.get("image")).toMiraiCode();
					}
					message = message.replace("[image]", code);
				}
			}
			//在此处增加if以匹配更多
			event.getSubject().sendMessage(MiraiCode.parseMiraiCode(message));
		}
		return message;
	}

	private String onHelp(List<FunctionBase> functionList) {
		StringBuilder stringBuilder = new StringBuilder("💬功能列表：");
		functionList.forEach(functionBase -> {
			Class<?> functionClass = null;
			Object function = null;
			try {
				functionClass = new ClassLoaderUtil(path)
						.findClass(functionBase.getFunctionName());
				function = functionClass.newInstance();
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
			Method[] methods;
			if (functionClass != null) {
				methods = functionClass.getDeclaredMethods();
				for (Method method :
						methods) {
					if ("help".equals(method.getName())){
						try {
							stringBuilder.append("\n").append(method.invoke(function));
						} catch (IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}

		});
		return stringBuilder.toString();
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