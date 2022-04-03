package ru.comgrid.server.api.message;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessagesRequest extends MessageUnionRequest{
    @Nullable
    public long sinceDateTimeMillis;
    @Nullable
    public long untilDateTimeMillis;
}
