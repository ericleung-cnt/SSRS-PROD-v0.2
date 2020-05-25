package org.mardep.ssrs.controller;

import javax.annotation.PostConstruct;

import org.mardep.ssrs.pojo.trackcode.Language;
import org.mardep.ssrs.pojo.trackcode.Result;
import org.mardep.ssrs.service.IShipRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackCodeController {

	public final static String PARAM_TRACK_CODE="trackCode";
	public final static String PARAM_LANGUAGE="language";

	private final static Logger logger = LoggerFactory.getLogger(TrackCodeController.class);

	@Autowired
	IShipRegService shipRegService;

	@PostConstruct
	public void init(){
		logger.info("########### Test  ##############");
	}

	@RequestMapping(value = "/track", params = { PARAM_TRACK_CODE, PARAM_LANGUAGE },  method = RequestMethod.GET)
	public ResponseEntity<Result> check(@RequestParam(PARAM_TRACK_CODE)String trackCode, @RequestParam(PARAM_LANGUAGE)String language){
		Result result = shipRegService.check(trackCode, Language.value(language));
		ResponseEntity<Result> re = new ResponseEntity<Result>(result, HttpStatus.OK);
		return re;
	}

}
