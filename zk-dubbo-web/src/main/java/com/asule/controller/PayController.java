package com.asule.controller;

import com.asule.curator.ZKCurator;
import com.asule.entity.Items;
import com.asule.service.ItemsService;
import com.asule.service.OrdersService;
import com.asule.utils.IMoocJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 订购商品controller
 */
@Controller
public class PayController {
	

	@Autowired
	private CulsterService culsterService;

	@Autowired
	private ZKCurator zkCurator;

	@ResponseBody
	@RequestMapping("/index")
	public String index(String itemId) {
//		Items item =
//				itemsService.getItem(itemId);
//		System.out.println("==========>"+item.getName());
		return "index";
	}



	@GetMapping("/buy")
	@ResponseBody
	public IMoocJSONResult doGetlogin(String itemId) {
		return culsterService.displayBuy(itemId)?IMoocJSONResult.ok():
				IMoocJSONResult.errorMsg("订单创建失败");
	}

	@GetMapping("/buy2")
	@ResponseBody
	public IMoocJSONResult doGet2login(String itemId) {
		return culsterService.displayBuy(itemId)?IMoocJSONResult.ok():
				IMoocJSONResult.errorMsg("订单创建失败");
	}


	@GetMapping("/isAlive")
	@ResponseBody
	public IMoocJSONResult isAlive() {
		boolean zkaLive = zkCurator.isZKALive();
		return zkaLive?IMoocJSONResult.ok("连接成功"):IMoocJSONResult.errorMsg("连接失败");
	}

	@ResponseBody
	@RequestMapping("/country")
	public String test(String country) throws UnsupportedEncodingException {
		System.out.println(country);
		// 浏览器"中国"以utf-8编码为字符串
		//
		//country  iso解码的东西了
		String real=new String(country.getBytes("iso-8859-1"),"utf-8");
		System.out.println(real);
		return "index";
	}

}
