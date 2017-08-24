package com.citroen.ledp.controller;

import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.VehicleParamService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 2015/6/27.
 */
@Controller
@RequestMapping("/vehicleParam")
public class VehicleParamController {

    @Resource
    private VehicleParamService vehicleParamService;

    @RequestMapping("get/{id}")
    @ResponseBody
    public List<Map> get(@PathVariable int id) throws LedpException {
        return vehicleParamService.get(id);
    }
}
