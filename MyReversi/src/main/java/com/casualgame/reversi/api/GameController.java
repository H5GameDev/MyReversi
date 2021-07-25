package com.casualgame.reversi.api;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.casualgame.reversi.service.SessionService;

@CrossOrigin
@Controller
public class GameController {
	private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

	@Autowired
	SessionService sessionService;

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/gs")
	@ResponseBody // GET/POST
	public String gs(HttpServletRequest request) throws Exception {
		switch (request.getMethod()) {
		case "POST":
			// LOGIN
			return sessionService.login();
		case "GET":
			// GET STATUS
			return sessionService.getStatus(request.getParameter("uid"));

		default:
			return "not supported";

		}

	}

	@RequestMapping(value = "/login")
	@ResponseBody // GET/POST
	public String login(HttpServletRequest request) throws Exception {
		return sessionService.login();
	}

	@RequestMapping(value = "/join")
	@ResponseBody // GET/POST
	public String join(HttpServletRequest request) throws Exception {
		LOG.info(request.getParameter("uid") + " -> " + request.getParameter("sid"));
		return sessionService.joinSession(request.getParameter("uid"), request.getParameter("sid"));
	}

	// POST
	@RequestMapping(value = "/play")
	@ResponseBody // GET/POST
	public String play(HttpServletRequest request) throws Exception {
		return sessionService.play(request.getParameter("sid"), request.getParameter("uid"),
				Integer.parseInt(request.getParameter("posx")), Integer.parseInt(request.getParameter("posy")));
	}

	@RequestMapping(value = "/ready")
	@ResponseBody // GET/POST
	public String ready(HttpServletRequest request) throws Exception {
		LOG.info("READY" + request.getParameter("uid") + " -> " + request.getParameter("sid"));
		return sessionService.ready(request.getParameter("uid"), request.getParameter("sid"));
	}
}
