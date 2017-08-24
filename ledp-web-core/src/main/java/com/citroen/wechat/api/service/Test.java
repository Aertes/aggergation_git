package com.citroen.wechat.api.service;

import java.util.List;

import com.citroen.wechat.api.model.Fans;
import com.citroen.wechat.api.model.Group;



public class Test {
	private static String access_token="MSjXrZA6Arkni-KNzQhWlNNNKV2CGtILDPQObMnUUdHutt-hSVR21NlVkSDHfx-X-Kk7j1um2cM14ixSAPBzrsIsKdFE0-vg8Zb6IV4lkhbbxpbToxFQfQYBO_ufBmIS";
	private static String openid = "oXiA7t2EpIT_t5OGpYETgkEjAVFs";
	private static String media_id = "HlgPLExxj1tzSOy8JUBYGG2hc8Y0peprReHW-bU8Qvw";
	private static String imagePath = "F:/15931403828701.jpg";
	public static void main(String[] args) throws ApiException {
/*		List<Fans> fanses = ApiFans.list(access_token,null);
		for(Fans fans:fanses){
			Fans f = ApiFans.getFansByOpenId(access_token,fans.getOpenid());
			if(f.getNickname().equals("谎言")){
				System.out.println(f);
				break;
			}
		}*/
		
/*		Fans fans = ApiFans.getFansByOpenId(access_token, openid);
		System.out.println(fans.getCity());
		System.out.println(fans.getHeadimgurl());
		System.out.println(fans.getRemark());
		fans.setOpenid(openid);
		fans.setRemark("happy duanwu");
		ApiFans.update(access_token, fans);*/
/*		
		List<Group> groups = ApiGroup.list(access_token);
		System.out.println(groups);*/
		
		
		//ApiGroup.moveMembers(access_token, "107", "oXiA7t6syqL-Hl2ZXsilJv3CB5Tw");
		
		
/*		String msgtype = "text";
		String content = "Hello word!";
		Response response = ApiMass.preview(access_token, msgtype,content,openid);
		System.out.println(response);*/
/*
		String type = "image";
		String filePath = "F:/15931403828701.jpg";
		Response response = ApiMaterial.upload(access_token, filePath);
		System.out.println(response);*/
/*		List<Menu> menus = new ArrayList<Menu>();
		Menu menu1 = new Menu();
		menu1.setType(Menu.Type.click.getValue());
		menu1.setName("click");
		menu1.setKey("click key");
		menus.add(menu1);
		
		Menu menu2 = new Menu();
		menu2.setName("pop");
			Menu menu21 = new Menu();
			menu21.setType(Menu.Type.view.getValue());
			menu21.setName("seach");
			menu21.setUrl("http://www.baidu.com/");
			menu2.getChildren().add(menu21);
	
			Menu menu22 = new Menu();
			menu22.setType(Menu.Type.scancode_waitmsg.getValue());
			menu22.setName("scancode_waitmsg");
			menu22.setKey("rselfmenu_0_0");
			menu2.getChildren().add(menu22);
	
			Menu menu23 = new Menu();
			menu23.setType(Menu.Type.scancode_push.getValue());
			menu23.setName("scancode_push");
			menu23.setKey("rselfmenu_0_1");
			menu2.getChildren().add(menu23);
	
			Menu menu24 = new Menu();
			menu24.setType(Menu.Type.pic_sysphoto.getValue());
			menu24.setName("pic_sysphoto");
			menu24.setKey("rselfmenu_1_0");
			menu2.getChildren().add(menu24);
	
			Menu menu25 = new Menu();
			menu25.setType(Menu.Type.pic_photo_or_album.getValue());
			menu25.setName("pic_photo_or_album");
			menu25.setKey("rselfmenu_1_0");
			menu2.getChildren().add(menu25);
		menus.add(menu2);
		
		
		Menu menu3 = new Menu();
		menu3.setName("send");
			Menu menu31 = new Menu();
			menu31.setType(Menu.Type.pic_weixin.getValue());
			menu31.setName("pic_weixin");
			menu31.setKey("rselfmenu_1_2");
			menu3.getChildren().add(menu31);
	
			Menu menu32 = new Menu();
			menu32.setType(Menu.Type.location_select.getValue());
			menu32.setName("location_select");
			menu32.setKey("rselfmenu_0_0");
			menu3.getChildren().add(menu32);
	
			Menu menu33 = new Menu();
			menu33.setType(Menu.Type.media_id.getValue());
			menu33.setName("media_id");
			menu33.setMediaId(media_id);
			menu3.getChildren().add(menu33);
		menus.add(menu3);*/
		
		//Response response = ApiMenu.create(access_token,menus);
		//System.out.println(response);
/*		Response response = ApiMenu.delete(access_token);
		System.out.println(response);*/
/*		List<Menu> menus2 = ApiMenu.get(access_token);
		System.out.println(menus2);*/
/*		Response response = ApiMaterialTemporary.download(access_token, "AHkgKjhlkcBDoVQJVWRogqF8ESlFkS9c9XQaU87Deo-lU6d1NZEFNk4T8-1qU6ZN","F:/");
		System.out.println(response);*/
		//ApiMenu.get(access_token);
		//Response response = ApiMaterialTemporary.upload(access_token,imagePath);
		//Response response = ApiMaterial.download(access_token, "iKvykS2yg4_do4WNrZLQfbbTjt2yOSJDxn38PNcSChU","F:/");
		//Response response = ApiMaterial.delete(access_token, "iKvykS2yg4_do4WNrZLQfbbTjt2yOSJDxn38PNcSChU");
		//List<Material> response = ApiMaterial.getList(access_token,"news",0,20);
		//System.out.println(response);
		//Response response = ApiMaterial.upload(access_token,imagePath);
		
	/*	List<MaterialNews> articles = new ArrayList<MaterialNews>();
			MaterialNews news1 = new MaterialNews();
			news1.setAuthor("liaoqihong");
			news1.setContent("test content");
			news1.setContent_source_url("http://www.baidu.com");
			news1.setDigest("test digest");
			news1.setShow_cover_pic(1);
			news1.setThumb_media_id("iKvykS2yg4_do4WNrZLQfQ9WLuCMW4EMFabQ1T5HltQ");
			news1.setTitle("test title");
			articles.add(news1);
			
			MaterialNews news2 = new MaterialNews();
			news2.setAuthor("liaoqihong2");
			news2.setContent("test content2");
			news2.setContent_source_url("http://www.baidu.com");
			news2.setDigest("test digest2");
			news2.setShow_cover_pic(1);
			news2.setThumb_media_id("iKvykS2yg4_do4WNrZLQfQ9WLuCMW4EMFabQ1T5HltQ");
			news2.setTitle("test title2");
			articles.add(news2);
			
			Response response = ApiMaterial.upload(access_token, articles);
			System.out.println(response);*/
		
/*		MaterialNews news22 = new MaterialNews();
		news22.setIndex(1);
		news22.setAuthor("liaoqihong22");
		news22.setContent("test content22");
		news22.setContent_source_url("http://wkc.x-crm.com.cn");
		news22.setDigest("test digest22");
		news22.setShow_cover_pic(0);
		news22.setThumb_media_id("iKvykS2yg4_do4WNrZLQfQ9WLuCMW4EMFabQ1T5HltQ");
		news22.setTitle("test title22");
		Response response = ApiMaterial.update(access_token,"iKvykS2yg4_do4WNrZLQfelufIn7yYprXKGi4IJczLk",news22);
		System.out.println(response);*/
/*		Response response = ApiMaterial.delete(access_token,"iKvykS2yg4_do4WNrZLQfelufIn7yYprXKGi4IJczLk");
		System.out.println(response);*/
		
/*		Response response = ApiMass.preview(access_token,"text","中文乱码", openid);
		System.out.println(response);*/
		
/*		Response response = ApiMass.send(access_token,"text","-NkWdZHIUoeKF7d4-gXLF8oCzhnU6NxfiPYp-_eOJj1FX4HCoaIeS9RLif_qks7_", openid,openid);
		System.out.println(response);*/
/*		
		Response response = ApiMass.send(access_token,"mpnews","-NkWdZHIUoeKF7d4-gXLF8oCzhnU6NxfiPYp-_eOJj1FX4HCoaIeS9RLif_qks7_", "107",false);
		System.out.println(response);
		*/
/*		Response response = ApiMass.send(access_token,"mpnews","-NkWdZHIUoeKF7d4-gXLF8oCzhnU6NxfiPYp-_eOJj1FX4HCoaIeS9RLif_qks7_", "107",false);
		System.out.println(response);*/
/*		Response response = ApiMass.send(access_token,"text","中文123abcABC", openid,"oXiA7t6syqL-Hl2ZXsilJv3CB5Tw");
		System.out.println(response);*/
	}

}
