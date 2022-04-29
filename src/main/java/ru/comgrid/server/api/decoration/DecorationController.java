package ru.comgrid.server.api.decoration;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.model.Decoration;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.repository.DecorationRepository;
import ru.comgrid.server.security.annotation.CurrentUser;
import ru.comgrid.server.security.user.info.UserPrincipal;

import java.util.List;

import static ru.comgrid.server.api.user.UserHelp.extractId;

@Controller
@RestController
@RequestMapping("/decoration")
@SecurityRequirement(name = "bearerAuth")
public class DecorationController{
	private final DecorationRepository decorationRepository;
	private final AccessService accessService;

	public DecorationController(
		@Autowired DecorationRepository decorationRepository,
		@Autowired AccessService accessService
	){
		this.decorationRepository = decorationRepository;
		this.accessService = accessService;
	}

	@PostMapping("/list")
	public List<Decoration> getDecorations(
		@CurrentUser UserPrincipal user,
		@RequestBody DecorationRequest decorationRequest
	){
		if(!accessService.hasAccessTo(extractId(user), decorationRequest.getChatId(), Right.Read))
			throw new IllegalAccessException("chat.read_messages");

		return decorationRepository.findAllByCellUnionIdInAndAndChatId(
				decorationRequest.getCellUnionIds(),
				decorationRequest.getChatId()
		);
	}
}
