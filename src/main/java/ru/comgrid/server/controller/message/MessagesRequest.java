package ru.comgrid.server.controller.message;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessagesRequest extends MessageUnionRequest{
    @Nullable
    public long sinceTimeMillis;
    @Nullable
    public long untilTimeMillis;
}
