package ru.comgrid.server.api.message;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessagesRequest extends MessageUnionRequest{
    public long sinceDateTimeMillis;
    public long untilDateTimeMillis;
}
