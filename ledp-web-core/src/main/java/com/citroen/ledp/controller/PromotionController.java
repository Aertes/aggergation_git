package com.citroen.ledp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Vehicle;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;

@Controller
@RequestMapping("/promotion")
public class PromotionController {
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@RequestMapping(value={"","/index"})
	public String index(Model model,HttpServletRequest request) throws Exception {
		//车系
		String serieses = request.getParameter("serieses");
		//车系集合
		List<VehicleSeries> seriesList = mybaitsGenericDao.executeQuery(VehicleSeries.class,"select * from t_vehicle_series where status=1010 order by id asc");
		
		Map<String,List<Vehicle>> seriesesMap = new  HashMap<String,List<Vehicle>>();
		//车系集合
		List<Vehicle> vehicleList = null;
		//车系不为空检索车型
		if(!StringUtils.isEmpty(serieses)){
			String[] array = serieses.split(",");
			for(String a:array){
				try{
					vehicleList = mybaitsGenericDao.executeQuery(Vehicle.class,"select * from t_vehicle t WHERE t.status ='1010' and t.series= "+a);
					String ser= null;
					for (int i = 0; i < seriesList.size(); i++) {
						if(seriesList.get(i).getId().toString().equals(a)){
							ser = seriesList.get(i).getName();
						}
					}
					seriesesMap.put(ser, vehicleList);
					ser = null;
				}catch(Exception e){
					
				}
			}
			
		}
		model.addAttribute("seriesesMap",seriesesMap);
		return "promotion/index";
	}
	
	@RequestMapping(value = "/series/{id}")
	public String series(Model model, @PathVariable long id) throws LedpException {
		Vehicle series = mybaitsGenericDao.get(Vehicle.class, id);
		model.addAttribute("series",series);
		return "promotion/series";
	}
	
}
