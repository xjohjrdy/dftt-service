package com.atao.dftt.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atao.base.util.StringUtils;
import com.atao.dftt.model.TaoToutiaoUser;
import com.atao.dftt.util.CommonUtils;
import com.atao.dftt.util.MobileHttpUrlConnectUtils;
import com.atao.dftt.util.TaottUtils;

public class TaottHttp {
	private static Logger logger = LoggerFactory.getLogger(TaottHttp.class);
	private static Map<String, TaottHttp> userMap = new HashMap<String, TaottHttp>();
	public TaoToutiaoUser user;
	public Long pubTime = 0l;
	public Integer times = 0;
	public JSONArray eventList = new JSONArray();
	public static Random random = new Random();
	public String rwUrl = null;

	public TaottHttp(TaoToutiaoUser user) {
		this.user = user;
	}

	public static synchronized TaottHttp getInstance(TaoToutiaoUser user) {
		TaottHttp http = (TaottHttp) userMap.get(user.getUsername());
		if (http == null) {
			http = new TaottHttp(user);
			userMap.put(user.getUsername(), http);
		}
		return http;
	}

	public TaottHttp refreshUser(TaoToutiaoUser user) {
		TaottHttp http = new TaottHttp(user);
		userMap.put(user.getUsername(), http);
		return http;
	}

	public JSONObject login() {
		try {
			String url = "https://xwz.coohua.com/user/registerLogin";
			String postData = "password=" + user.getPassword() + "&anMobileRom=" + user.getModel() + "&androidId="
					+ user.getAndroidId() + "&mobile=" + user.getUsername() + "&tdSign=" + user.getX()
					+ "&anMobileVersion=" + user.getSdk() + "&anAppVersion=" + TaottUtils.app_version + "&anMobileImei="
					+ user.getImei() + "&anDownloadChannel=shouji-guanwang&os=" + user.getOs() + "&anMobileBrand="
					+ user.getVendor();
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept-Encoding", "gzip");
			heads.put("User-Agent", "okhttp/3.10.0");
			heads.put("Content-Type", "application/x-www-form-urlencoded");
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 0) {
				logger.info("taott-{}:用户登陆成功,result={}", user.getUsername(), object.toString());
				return object;
			}
			logger.error("taott-{}:用户登陆时失败,result={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:用户登陆时发生异常!{}", user.getUsername(), e.getMessage());
		}
		return null;
	}

	public JSONObject queryMyCoin() {
		try {
			String url = "https://xwz.coohua.com/credit/page";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept-Encoding", "gzip");
			heads.put("User-Agent", "okhttp/3.10.0");
			heads.put("Content-Type", "application/x-www-form-urlencoded");
			heads.put("base-key", TaottUtils.baseKey(user));
			String content = MobileHttpUrlConnectUtils.httpPost(url, null, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			return object;
			/*
			 * if (object.getIntValue("code") == 0) { JSONObject result =
			 * object.getJSONObject("result"); logger.info("taott-{}:查询今日获取的金币信息成功.");
			 * return result; }else { return }
			 */
			// logger.error("taott-{}:查询今日获取的金币信息失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:查询今日获取的金币信息异常,msg={}", user.getUsername(), e.getMessage());
		}
		return null;
	}

	public boolean qiandao() {
		try {
			String baseKey = TaottUtils.baseKey(user);
			String url = "http://xwz.coohua.com/task/signNew?base-key=" + baseKey;
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "application/json, text/plain, */*");
			heads.put("X-Requested-With", "com.coohua.xinwenzhuan");
			String content = MobileHttpUrlConnectUtils.httpGet(url, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 0) {
				logger.info("taott-{}:签到成功", user.getUsername());
				return true;
			}
			logger.error("taott-{}:签到失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:签到异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	public boolean daka() {
		try {
			String url = "https://xwz.coohua.com/punchTheClock/addGold";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept-Encoding", "gzip");
			heads.put("User-Agent", "okhttp/3.10.0");
			heads.put("Content-Type", "application/x-www-form-urlencoded");
			heads.put("base-key", TaottUtils.baseKey(user));
			String content = MobileHttpUrlConnectUtils.httpGet(url, heads, null);
			// logger.info("taott-{}:打卡结果,result={}", user.getUsername(), content);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 0) {
				logger.info("taott-{}:打卡成功", user.getUsername());
				return true;
			}
			logger.error("taott:{}-打卡失败,msg={}", content);
		} catch (Exception e) {
			logger.error("taott:{}-打卡异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	// 日常任务列表
	public JSONArray taskList() {
		try {
			String baseKey = TaottUtils.baseKey(user);
			String url = "http://xwz.coohua.com/task/listH5V2?base-key=" + baseKey + "&&appVersion="
					+ TaottUtils.app_version + "&hasCoohua=false";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "application/json, text/plain, */*");
			heads.put("Accept-Encoding", "gzip, deflate");
			heads.put("User-Agent", user.getUserAgent());
			heads.put("X-Requested-With", "com.coohua.xinwenzhuan");
			String content = MobileHttpUrlConnectUtils.httpGet(url, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 0) {
				JSONArray taskDaily = object.getJSONObject("result").getJSONArray("taskDaily");
				logger.info("taott-{}:获取任务列表,tasklist={}", user.getUsername(), taskDaily.toJSONString());
				return taskDaily;
			}
			logger.error("taott-{}:获取任务列表失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:获取任务列表异常,msg={}", user.getUsername(), e.getMessage());
		}
		return null;
	}

	public boolean doTask(String taskId, String taskUrl) {
		try {
			String url = "https://xwz.coohua.com/task/addGoldForLuckyReadAd";
			String postData = "link=" + taskUrl + "&taskId=" + taskId;
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept-Encoding", "gzip");
			heads.put("User-Agent", "okhttp/3.10.0");
			heads.put("Content-Type", "application/x-www-form-urlencoded");
			heads.put("base-key", TaottUtils.baseKey(user));
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			logger.info("taott-{}:阅读广告任务完成, result={}", user.getUsername(), content);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 0) {
				logger.info("taott-{}:阅读广告任务完成", user.getUsername());
				return true;
			}
			logger.error("taott-{}:阅读广告任务未完成,result={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:阅读广告任务异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	public boolean openBox() {
		try {
			String baseKey = TaottUtils.baseKey(user);
			String url = "http://xwz.coohua.com/task/openBox?base-key=" + baseKey;
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "application/json, text/plain, */*");
			heads.put("Accept-Encoding", "gzip, deflate");
			heads.put("User-Agent", user.getUserAgent());
			heads.put("X-Requested-With", "com.coohua.xinwenzhuan");
			String content = MobileHttpUrlConnectUtils.httpGet(url, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 0) {
				logger.info("taott-{}:开宝箱成功, result={}", user.getUsername(), object.toJSONString());
				return true;
			}
			logger.error("taott-{}:开宝箱失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:开宝箱异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	// 29 签到
	public boolean finishTask(String taskId) {
		try {
			String baseKey = TaottUtils.baseKey(user);
			String url = "http://xwz.coohua.com/taskDaily/dailyProgress?base-key=" + baseKey + "&taskId=" + taskId;
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "application/json, text/plain, */*");
			heads.put("X-Requested-With", "com.coohua.xinwenzhuan");
			String content = MobileHttpUrlConnectUtils.httpGet(url, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 0) {
				logger.info("taott-{}:点击完成任务成功", user.getUsername());
				return true;
			}
			logger.error("taott-{}:点击完成任务失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:点击完成任务异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	public void checkTask() {
		try {
			JSONArray taskDaily = taskList();
			for (int i = 0; i < taskDaily.size(); i++) {
				JSONObject task = taskDaily.getJSONObject(i);
				String taskId = task.getString("taskId");
				if (task.getIntValue("state") == 1) {
					taskSendLog("任务大厅页", task.getString("title"), task.getString("h5Jump"));
					finishTask(taskId);
				} else if (task.getIntValue("state") == 0 && "592".equals(task.getString("type"))) {
					String taskUrl = task.getString("h5Jump");
					taskSendLog("任务大厅页", task.getString("title"), task.getString("h5Jump"));
					Thread.sleep(120 * 1000);
					boolean finished = doTask(taskId, taskUrl);
					if (finished) {
						Thread.sleep(2 * 1000);
						taskSendLog("任务大厅页", task.getString("title"), task.getString("h5Jump"));
						finishTask(taskId);
					}
				}
			}
			Thread.sleep(1 * 1000);
			openBox();
			Thread.sleep(1 * 1000);
			openBox();
		} catch (Exception e) {
			logger.error("taott-{}:点击完成任务异常,msg={}", user.getUsername(), e.getMessage());
		}
	}

	public boolean commonV3() {
		try {
			String url = "https://xinwenzhuanconf.coohua.com/conf/commonV3";
			String postData = "appCurVersion=" + TaottUtils.app_version + "&userId=" + user.getUserId() + "&os=Android";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept-Encoding", "gzip");
			heads.put("User-Agent", "okhttp/3.10.0");
			heads.put("Content-Type", "application/x-www-form-urlencoded");
			heads.put("base-key", TaottUtils.baseKey(user));
			heads.put("Host", "xinwenzhuanconf.coohua.com");
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 0) {
				rwUrl = object.getJSONObject("result").getString("hotArticleDomainNameH5");
				return true;
			}
			logger.error("taott-{}:获取基础配置失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:获取基础配置异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	public JSONArray newsList() {
		try {
			String url = "https://reco.coohua.com/api/news/";
			Map<String, String> map = new HashMap<String, String>();
			map.put("appCurVersion", TaottUtils.app_version);
			map.put("exposureTime", "0");
			map.put("androidId", user.getAndroidId());
			map.put("imei", user.getImei());
			map.put("userId", user.getUserId());
			map.put("times", times + "");
			map.put("pubTime", pubTime + "");
			//map.put("typeId", "26");
			map.put("channel", CommonUtils.encode("shouji-guanwang"));
			map.put("os", "Android");
			map.put("direction", "1");
			map.put("model", CommonUtils.encode(user.getModel()));
			String postData = TaottUtils.getStr(map);
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept-Encoding", "gzip");
			heads.put("User-Agent", "okhttp/3.10.0");
			heads.put("base-key", TaottUtils.baseKey(user));
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 0) {
				JSONArray list = object.getJSONArray("result");
				pubTime = list.getJSONObject(list.size() - 1).getLong("pubTime");
				times++;
				return list;
			}
			logger.error("taott-{}:获取新闻列表失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:获取新闻列表异常,msg={}", user.getUsername(), e.getMessage());
		}
		return null;

	}

	public JSONObject readNews(String newsId) {
		try {
			boolean success = newsDetailSendLog(newsId);
			if (!success) {
				return null;
			}
			String url = "https://xwz.coohua.com/reward/refresh";
			int rnd = random.nextInt(100);
			String sign = TaottUtils.sign(user.getUserId(), newsId + "_1", rnd);
			String postData = "rnd=" + rnd + "&sign=" + sign + "&newsId=" + newsId
					+ "_1&type=read&os=android&isRefresh=0";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept-Encoding", "gzip");
			heads.put("User-Agent", "okhttp/3.10.0");
			heads.put("base-key", TaottUtils.baseKey(user));
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			return object;
		} catch (Exception e) {
			logger.error("taott-{}:读取新闻异常,msg={}", user.getUsername(), e.getMessage());
		}
		return null;
	}

	// 新闻详细页面发送日志
	public boolean newsDetailSendLog(String newsId) {
		/*
		 * try { if (StringUtils.isBlank(TaottUtils.rwUrl)) { boolean common =
		 * commonV3(); if (!common) return false; } String url = TaottUtils.rwUrl +
		 * "?id=" + newsId + "&userId=" + user.getUserId() +
		 * "&environment=production&baseKey=" + TaottUtils.baseKey(user) + "&cuid=" +
		 * user.getUserId() + "&typeId=26&origin=feed&source=4&textSize=1&version=" +
		 * TaottUtils.app_version + "&recommend=1"; String cookie =
		 * "sensorsdata2015jssdkcross={\"distinct_id\":\"" + user.getDistinctId() +
		 * "\",\"$device_id\":\"" + user.getDistinctId() +
		 * "\",\"props\":{\"$latest_referrer\":\"\",\"$latest_referrer_host\":\"\"}}";
		 * Map<String, String> heads = new HashMap<String, String>();
		 * heads.put("User-Agent", user.getUserAgent()); heads.put("X-Requested-With",
		 * "com.coohua.xinwenzhuan"); heads.put("Upgrade-Insecure-Requests", "1");
		 * heads.put("Cookie", cookie); String content =
		 * MobileHttpUrlConnectUtils.httpGet(url, heads, null); Document doc =
		 * Jsoup.parse(content); Elements adBoxs =
		 * doc.body().getElementsByClass("adBox"); boolean success = true; for (int i =
		 * 0; i < adBoxs.size(); i++) { Element adBox = adBoxs.get(i); String v =
		 * adBox.toString(); v = v.replace("\r\n", "").replace("\r", ""); String adId =
		 * v.substring(v.indexOf("sogou_ad_id=") + 12, v.indexOf(";",
		 * v.indexOf("sogou_ad_id="))); JSONObject adObject =
		 * TaottUtils.getJsObject(user, adId, i); String x =
		 * TaottUtils.jsEncode(adObject.toJSONString()); success = newsDetailLog(x,
		 * url); if (!success) return false; Thread.sleep(new Random().nextInt(300)); }
		 * } catch (Exception e) { logger.error("taott-{}:获取新闻详细页面广告id异常,msg={}",
		 * user.getUsername(), e.getMessage()); return false; }
		 */
		try {
			if (StringUtils.isBlank(rwUrl)) {
				boolean common = commonV3();
				if (!common)
					return false;
			}
			String url = rwUrl + "?id=" + newsId + "&userId=" + user.getUserId() + "&environment=production&baseKey="
					+ TaottUtils.baseKey(user) + "&cuid=" + user.getUserId()
					+ "&typeId=26&origin=feed&source=4&textSize=1&version=" + TaottUtils.app_version + "&recommend=1";
			List<String> adBoxs = getAdBoxs();
			for (int i = 0; i < adBoxs.size(); i++) {
				JSONObject adObject = TaottUtils.getJsObject(user, adBoxs.get(i), i, url);
				String x = TaottUtils.jsEncode(adObject.toJSONString());
				boolean success = newsDetailLog(x, "");
				if (!success)
					return false;
				Thread.sleep(new Random().nextInt(300));
			}
		} catch (Exception e) {
			logger.error("taott-{}:获取新闻详细页面广告id异常,msg={}", user.getUsername(), e.getMessage());
			return false;
		}
		return true;
	}

	private boolean newsDetailLog(String x, String pageUrl) {
		try {
			String url = "https://dcs.coohua.com/data/v1?project=newsearn&data=" + x;
			String cookie = "sensorsdata2015jssdkcross={\"distinct_id\":\"" + user.getDistinctId()
					+ "\",\"$device_id\":\"" + user.getDistinctId()
					+ "\",\"props\":{\"$latest_referrer\":\"\",\"$latest_referrer_host\":\"\"}}";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
			heads.put("Accept-Encoding", "gzip, deflate");
			heads.put("User-Agent", user.getUserAgent());
			heads.put("X-Requested-With", "com.coohua.xinwenzhuan");
			heads.put("Cookie", cookie);
			heads.put("Referer", pageUrl);
			String content = MobileHttpUrlConnectUtils.httpGet(url, heads, null);
			// logger.info("taott-{}:发送新闻详细日志,msg={}", user.getUsername(), content);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 200)
				return true;
			logger.error("taott-{}:发送新闻详细日志失败,msg={}", user.getUsername(), object.toJSONString());
		} catch (Exception e) {
			logger.error("taott-{}:发送新闻详细日志异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	// 任务中心
	public boolean taskSendLog(String element_page, String element_name, String jump_url) {
		try {
			JSONObject adObject = TaottUtils.taskJsObject(user, element_page, element_name, jump_url);
			String x = TaottUtils.jsEncode(adObject.toJSONString());
			String url = "https://dcs.coohua.com/data/v1?project=newsearn&data=" + x;
			String cookie = "sajssdk_2015_cross_new_user=1; sensorsdata2015jssdkcross={\"distinct_id\":\""
					+ user.getDistinctId() + "\",\"$device_id\":\"" + user.getDistinctId()
					+ "\",\"props\":{\"$latest_referrer\":\"\",\"$latest_referrer_host\":\"\"}}";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
			heads.put("Accept-Encoding", "gzip, deflate");
			heads.put("User-Agent", user.getUserAgent());
			heads.put("X-Requested-With", "com.coohua.xinwenzhuan");
			heads.put("Cookie", cookie);
			String content = MobileHttpUrlConnectUtils.httpGet(url, heads, null);
			// logger.info("taott-{}:发送任务日志,msg={}", user.getUsername(), content);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 200)
				return true;
			logger.error("taott-{}:发送任务日志失败,msg={}", user.getUsername(), object.toJSONString());
		} catch (Exception e) {
			logger.error("taott-{}:发送任务日志异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	public boolean sendData() {
		try {
			if (eventList.size() < 1)
				return true;
			for (int i = 0; i < eventList.size(); i++) {
				JSONObject event = eventList.getJSONObject(i);
				event.put("_flush_time", System.currentTimeMillis());
			}
			String x = TaottUtils.encodeData(eventList.toJSONString());
			int crc = x.hashCode();
			String url = "http://dcs.coohua.com/data/v1?project=newsearn";
			String postData = "data_list=" + x + "&gzip=1&crc=" + crc;
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept-Encoding", "gzip");
			heads.put("User-Agent", "Dalvik/2.1.0 (Linux; U; " + user.getOs() + " " + user.getOsVersion() + "; "
					+ user.getModel() + ")");
			heads.put("Content-Type", "application/x-www-form-urlencoded");
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			// logger.info("taott-{}:发送手机日志,result={}", user.getUsername(), content);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 200)
				return true;
			logger.error("taott-{}:发送手机日志失败,msg={}", user.getUsername(), object.toJSONString());
		} catch (Exception e) {
			logger.error("taott-{}:发送手机日志异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	public JSONArray cointxList(String type) {
		try {
			int categoryId = 73;
			if ("wxpay".equals(type))// wx
				categoryId = 73;
			else if ("alipay".equals(type)) {// ali
				categoryId = 74;
			}
			String url = "https://newsearnmall.coohua.com/mall/api/category/product/list?categoryId=" + categoryId
					+ "&userId=" + user.getUserId() + "&ticket=" + user.getTicket();
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heads.put("Accept-Encoding", "gzip, deflate");
			heads.put("Accept-Language", "zh-CN,en-US;q=0.8");
			heads.put("X-Requested-With", "XMLHttpRequest");
			heads.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			heads.put("User-Agent", user.getUserAgent());
			heads.put("Referer",
					"https://newsearnmall.coohua.com/mall/static/index.html?userId=" + user.getUserId() + "&ticket="
							+ user.getTicket() + "&baseKey=" + TaottUtils.baseKey(user) + "&version="
							+ TaottUtils.app_version);
			String cookie = "sensorsdata2015jssdkcross={\"distinct_id\":\"" + user.getDistinctId()
					+ "\",\"$device_id\":\"" + user.getDistinctId()
					+ "\",\"props\":{\"$latest_referrer\":\"\",\"$latest_referrer_host\":\"\"}}";
			heads.put("Cookie", cookie);
			String content = MobileHttpUrlConnectUtils.httpGet(url, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("status") == 200) {
				return object.getJSONArray("dataList");
			}
			logger.info("taott-{}:查询可提现金币列表失败,content={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:查询可提现金币列表异常,msg={}", user.getUsername(), e.getMessage());
		}
		return null;
	}

	public JSONObject cointxDetail(int productId) {
		try {
			String url = "https://newsearnmall.coohua.com/mall/api/product/simpleDetail";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heads.put("Accept-Encoding", "gzip, deflate");
			heads.put("Accept-Language", "zh-CN,en-US;q=0.8");
			heads.put("X-Requested-With", "XMLHttpRequest");
			heads.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			heads.put("User-Agent", user.getUserAgent());
			heads.put("Referer",
					"https://newsearnmall.coohua.com/mall/static/index.html?userId=" + user.getUserId() + "&ticket="
							+ user.getTicket() + "&baseKey=" + TaottUtils.baseKey(user) + "&version="
							+ TaottUtils.app_version);
			heads.put("Origin", "https://newsearnmall.coohua.com");
			String cookie = "sensorsdata2015jssdkcross={\"distinct_id\":\"" + user.getDistinctId()
					+ "\",\"$device_id\":\"" + user.getDistinctId()
					+ "\",\"props\":{\"$latest_referrer\":\"\",\"$latest_referrer_host\":\"\"}}";
			heads.put("Cookie", cookie);
			String postData = "userId=" + user.getUserId() + "&ticket=" + user.getTicket() + "&id=" + productId;
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("status") == 200) {
				return object.getJSONObject("data");
			}
			logger.info("taott-{}:查询可提现金币详细失败,content={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:查询可提现金币详细异常,msg={}", user.getUsername(), e.getMessage());
		}
		return null;
	}
	
	/**
	 * 
	 * 支付宝绑定
	 * account 手机号
	 * realName 实名
	 * 
    */
	public boolean bindAliAccount(String account, String realName) {
		try {
			String url = "https://newsearnmall.coohua.com/mall/api/user/saveAliAccount";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept-Encoding", "gzip");
			heads.put("User-Agent", "okhttp/3.10.0");
			heads.put("Content-Type", "application/x-www-form-urlencoded");

			String postData = "userId=" + user.getUserId() + "&account=" + account + "&ticket=" + user.getTicket()+ "&realName=" + CommonUtils.encode(realName);
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getBooleanValue("result")) {
				logger.info("taott-{}:支付宝绑定成功.content={}", user.getUsername(), content);
				return true;
			}
			logger.error("taott-{}:支付宝绑定失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:支付宝绑定异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	public boolean cointx(int productId) {
		try {
			String url = "https://newsearnmall.coohua.com/mall/api/order/create";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heads.put("Accept-Encoding", "gzip, deflate");
			heads.put("Accept-Language", "zh-CN,en-US;q=0.8");
			heads.put("X-Requested-With", "XMLHttpRequest");
			heads.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			heads.put("User-Agent", user.getUserAgent());
			heads.put("Referer",
					"https://newsearnmall.coohua.com/mall/static/index.html?userId=" + user.getUserId() + "&ticket="
							+ user.getTicket() + "&baseKey=" + TaottUtils.baseKey(user) + "&version="
							+ TaottUtils.app_version);
			heads.put("Origin", "https://newsearnmall.coohua.com");
			String cookie = "sensorsdata2015jssdkcross={\"distinct_id\":\"" + user.getDistinctId()
					+ "\",\"$device_id\":\"" + user.getDistinctId()
					+ "\",\"props\":{\"$latest_referrer\":\"\",\"$latest_referrer_host\":\"\"}}";
			heads.put("Cookie", cookie);
			String postData = "userId=" + user.getUserId() + "&ticket=" + user.getTicket() + "&productId=" + productId
					+ "&verifyCode=hasWxWithdrawBefore";
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 200) {
				logger.info("taott-{}:微信提现金币成功.content={}", user.getUsername(), content);
				return true;
			}
			logger.error("taott-{}:微信提现金币失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:微信提现金币异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	public boolean cointxAli(int productId) {
		try {
			String url = "https://newsearnmall.coohua.com/mall/api/order/create";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heads.put("Accept-Encoding", "gzip, deflate");
			heads.put("Accept-Language", "zh-CN,en-US;q=0.8");
			heads.put("X-Requested-With", "XMLHttpRequest");
			heads.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			heads.put("User-Agent", user.getUserAgent());
			heads.put("Referer",
					"https://newsearnmall.coohua.com/mall/static/index.html?userId=" + user.getUserId() + "&ticket="
							+ user.getTicket() + "&baseKey=" + TaottUtils.baseKey(user) + "&version="
							+ TaottUtils.app_version);
			heads.put("Origin", "https://newsearnmall.coohua.com");
			String cookie = "sensorsdata2015jssdkcross={\"distinct_id\":\"" + user.getDistinctId()
					+ "\",\"$device_id\":\"" + user.getDistinctId()
					+ "\",\"props\":{\"$latest_referrer\":\"\",\"$latest_referrer_host\":\"\"}}";
			heads.put("Cookie", cookie);
			String postData = "userId=" + user.getUserId() + "&ticket=" + user.getTicket() + "&productId=" + productId
					+ "&realName=" + CommonUtils.encode(user.getTxName()) + "&rechargeAccount=" + user.getTxUser()
					+ "&idCard=";
			String content = MobileHttpUrlConnectUtils.httpPost(url, postData, heads, null);
			JSONObject object = JSONObject.parseObject(content);
			if (object.getIntValue("code") == 200) {
				logger.info("taott-{}:支付宝提现金币成功.content={}", user.getUsername(), content);
				return true;
			}
			logger.error("taott-{}:支付宝提现金币失败,msg={}", user.getUsername(), content);
		} catch (Exception e) {
			logger.error("taott-{}:支付宝提现金币异常,msg={}", user.getUsername(), e.getMessage());
		}
		return false;
	}

	public JSONObject adJs() {
		try {
			String url = "https://www.coohua.com/share/xwz_article/js/advData.js?v=1.2.6";
			Map<String, String> heads = new HashMap<String, String>();
			heads.put("Accept", "application/json, text/javascript, */*; q=0.01");

			String content = MobileHttpUrlConnectUtils.httpGet(url, heads, null);
			content = content.replace("\r\n", "").replace("\r", "").replace("\t", "");
			int end = content.indexOf("var arr");
			int start = content.indexOf("var advJson = ") + 14;
			content = content.substring(start, end);
			JSONObject object = JSONObject.parseObject(content);
			return object;
		} catch (Exception e) {
			logger.error("taott-{}:查询广告id异常,msg={}", user.getUsername(), e.getMessage());
		}
		return null;
	}

	public List<String> getAdBoxs() {
		if (StringUtils.isBlank(rwUrl)) {
			commonV3();
		}
		List<String> adIds = new ArrayList<String>();
		JSONObject adJs = adJs();
		String adPrefix = rwUrl.substring(rwUrl.indexOf("article_") + 8, rwUrl.indexOf(".html"));
		JSONObject ad = adJs.getJSONObject(adPrefix);
		adIds.add(ad.getJSONObject("sixPic").getString("advid"));
		JSONArray threePics = ad.getJSONArray("threePic");
		int len = threePics.size() > 10 ? 10 : threePics.size();
		for (int i = 0; i < len; i++) {
			JSONObject threePic = threePics.getJSONObject(i);
			adIds.add(threePic.getString("advid"));
		}
		return adIds;
	}

	public static void main(String[] args) {
		/*
		 * TaoToutiaoUser user = new TaoToutiaoUser(); user.setUsername("17755117870");
		 * user.setUserId("39781740");
		 * user.setTicket("f69f33bdfe08a3728a83baf7bd995968"); TaottHttp http =
		 * TaottHttp.getInstance(user); http.finishTask("29");
		 */
	}

}
