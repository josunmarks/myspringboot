/**
 * 
 */
package com.myspringboot.mina;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangjoun
 *
 */
@RestController
@RequestMapping("/mina")
public class MinaController {
	@RequestMapping("/sendmsg")
	public String sendMessage(String key) {
		try {
			String jsonstr = "123";
			SessionMap sessionMap = SessionMap.newInstance();
			sessionMap.sendMessage(key, jsonstr);
			Thread.sleep(3000);
			sessionMap.sendMessage(key, "456");
		} catch (Exception e) {
			return "出错了" + e.getMessage();
		}
		return "success";
	}
}
