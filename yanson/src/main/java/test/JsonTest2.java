package test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import annotation.MyTest;
import json.JsonArray;
import json.JsonObject;
import utils.AnnotationUtils;


public class JsonTest2 {

	public static final List<String> JSON_LIST = new ArrayList<String>();
	private static String jsonStr;

	static {

		StringBuilder sb = new StringBuilder();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("JsonTest.txt");
		byte[] buffer = new byte[1024 * 8];
		int len;
		try {
			while ((len = is.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, len, "utf-8"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] jsons = sb.toString().split("\\*\\*\\*\\*\\*\\*\\*");
		for (String json : jsons) {
			JSON_LIST.add(json.trim());
		}
		jsonStr = JSON_LIST.get(0);
	}

	@MyTest
	public void yanson() throws Exception {

		if (jsonStr.startsWith("[")) {
			Object parseObject = JsonObject.parseObject(jsonStr);
			JsonArray jsonArray = (JsonArray) parseObject;
			System.out.println(jsonArray);
		} else {
			JsonObject jsonObject = (JsonObject) JsonObject.parseObject(jsonStr);
			BaseTypeVo javaObject = jsonObject.toJavaObject(jsonStr, BaseTypeVo.class);
//			System.out.println(javaObject);
		}

	}

	@MyTest
	public void fastJson() {

		if (jsonStr.startsWith("[")) {

		} else {
			JSONObject jsonObject = JSONObject.parseObject(jsonStr);
			BaseTypeVo javaObject = jsonObject.toJavaObject(BaseTypeVo.class);
//			System.out.println(javaObject);
		}

	}

	@MyTest
	public void jackson() throws JsonMappingException, JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		if (jsonStr.startsWith("[")) {

		} else {
			BaseTypeVo readValue = objectMapper.readValue(jsonStr, BaseTypeVo.class);
//			System.out.println(readValue);
		}

	}

	@MyTest
	public void gson() {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		if (jsonStr.startsWith("[")) {

		} else {
			BaseTypeVo fromJson = gson.fromJson(jsonStr, BaseTypeVo.class);
//			System.out.println(fromJson);
		}

	}

	public static void main(String[] args) throws Exception {

		AnnotationUtils.getExecutedTime("test.JsonTest2", args);
	}
}

