package com.zzyl.WeChat;

import com.zzyl.nursing.service.IWeChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * TODO
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-03 20:22
 */
@SpringBootTest
public class WeChatTest {

	@Autowired
	private IWeChatService weChatService;

	@Test
	public void testGetOpenId() {
		String openId = weChatService.getOpenId("0d3Vnr000mDGOT1tnD300t9N5B4Vnr0c");
		System.out.println("openId = " + openId);
	}

	@Test
	public void testGetPhoneNumber(){
		String phoneNumber = weChatService.getPhoneNumber("e69c5492c8a32828cfcc797b1eeaecacb167cadf502fc3d089d675fb6493a829");

		System.out.println("phoneNumber = " + phoneNumber);
	}

}
