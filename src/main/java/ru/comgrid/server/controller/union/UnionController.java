package ru.comgrid.server.controller.union;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.comgrid.server.model.CellUnion;
import ru.comgrid.server.security.annotation.CurrentUser;
import ru.comgrid.server.security.user.info.UserPrincipal;
import ru.comgrid.server.service.cell.CellUnionService;
import ru.comgrid.server.service.user.UserHelp;

import java.util.List;

@RestController
@RequestMapping(value = "/union", produces = "application/json; charset=utf-8")
@SecurityRequirement(name = "bearerAuth")
public class UnionController{

    private final CellUnionService cellUnionService;

    public UnionController(@Autowired CellUnionService cellUnionService){
        this.cellUnionService = cellUnionService;
    }

    @ApiResponse(responseCode = "403", description = "Forbidden access. Error code: chat.read_messages", content = @Content())
    @ApiResponse(responseCode = "400", description = "Bad request. Error code: chat.out_of_borders", content = @Content())
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Get cell unions", description = """
        Suppose you want to get union cells inside some square:
        ![Square](https://sun9-75.userapi.com/impg/o3MOVJFYabFR1upRd_S9x6msrbT7pUGs6pHp3g/DXhWOP-6kx0.jpg?size=1338x694&quality=96&sign=53015cac24b6463a5d97329a005ca4f6&type=album)
                
        This request allows you to get all the checked cell unions
        AND question marked ones(but not crossed out)
        """)
    @GetMapping("/list")
    public ResponseEntity<List<CellUnion>> cellUnions(
        @CurrentUser UserPrincipal user,
        @RequestParam long chatId,
        @RequestParam(required = false, defaultValue = "0") int xcoordLeftTop,
        @RequestParam(required = false, defaultValue = "0") int ycoordLeftTop,
        @RequestParam(required = false, defaultValue = "0") int xcoordRightBottom,
        @RequestParam(required = false, defaultValue = "0") int ycoordRightBottom
    ){
        var userId = UserHelp.extractId(user);

        return cellUnionService.getCellUnionsOfChat(chatId, xcoordLeftTop, ycoordLeftTop, xcoordRightBottom, ycoordRightBottom, userId);
    }
}
