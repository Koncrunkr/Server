package ru.comgrid.server.api.message;


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
    public int xCoordLeftTop;
    public int yCoordLeftTop;
    public int xCoordRightBottom;
    public int yCoordRightBottom;
}
