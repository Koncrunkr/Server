package ru.comgrid.server.controller.decoration;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.comgrid.server.model.Decoration;
import ru.comgrid.server.security.annotation.CurrentUser;
import ru.comgrid.server.security.user.info.UserPrincipal;
import ru.comgrid.server.service.cell.DecorationService;

import java.util.List;

import static ru.comgrid.server.service.user.UserHelp.extractId;

@Controller
@RestController
@RequestMapping("/decoration")
@SecurityRequirement(name = "bearerAuth")
public class DecorationController{
	private final DecorationService decorationService;

	public DecorationController(@Autowired DecorationService decorationService){
		this.decorationService = decorationService;
	}

	@PostMapping("/list")
	public List<Decoration> getDecorations(
		@CurrentUser UserPrincipal user,
		@RequestBody DecorationRequest decorationRequest
	){
		return decorationService.getDecorationList(extractId(user), decorationRequest);
	}
}
