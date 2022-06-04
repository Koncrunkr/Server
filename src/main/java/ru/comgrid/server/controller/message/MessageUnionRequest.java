package ru.comgrid.server.controller.message;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageUnionRequest{
    public long chatId;
    public int xcoordLeftTop;
    public int ycoordLeftTop;
    public int xcoordRightBottom;
    public int ycoordRightBottom;
}
