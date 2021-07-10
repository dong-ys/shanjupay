package cn.itcast.sailing.handler;

import cn.itcast.sailing.generator.NumberVerificationCodeGenerator;
import cn.itcast.sailing.generator.UUIDVerificationKeyGenerator;
import cn.itcast.sailing.generator.VerificationCodeGenerator;
import cn.itcast.sailing.generator.VerificationKeyGenerator;
import cn.itcast.sailing.sms.SmsService;
import cn.itcast.sailing.store.VerificationStore;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 短信(数字随机验证码)处理器
 */
@Slf4j
public class SmsNumberVerificationHandler extends AbstractVerificationHandler {

	private String name;

	private int len;

	private VerificationStore verificationStore;

	private VerificationKeyGenerator verificationKeyGenerator;

	private VerificationCodeGenerator verificationCodeGenerator;

	private SmsService smsService;

	public SmsNumberVerificationHandler(String name, int len) {
		this.name = name;
		this.len = len;
		verificationKeyGenerator = new UUIDVerificationKeyGenerator();
		verificationCodeGenerator = new NumberVerificationCodeGenerator(len);
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public VerificationStore getVerificationStore() {
		return verificationStore;
	}

	public void setVerificationStore(VerificationStore verificationStore) {
		this.verificationStore = verificationStore;
	}

	@Override
	public VerificationKeyGenerator getVerificationKeyGenerator() {
		return verificationKeyGenerator;
	}

	@Override
	public VerificationCodeGenerator getVerificationCodeGenerator() {
		return verificationCodeGenerator;
	}

	@Override
	public int getEffectiveTime() {
		return 300;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	@Override
	String confusion(Map<String, Object> payload, String key, String code) {
		String mobile = String.valueOf(payload.get("mobile"));

		// 使用腾讯云发送短信
//		smsService.send(mobile, code, getEffectiveTime());

		// 测试使用，在控制台输出验证码
		smsService.sendOnConsole(mobile, code, getEffectiveTime());
		return null;
	}


}
