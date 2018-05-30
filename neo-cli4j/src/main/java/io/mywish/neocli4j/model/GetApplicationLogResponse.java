package io.mywish.neocli4j.model;

import io.mywish.neocli4j.Event;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetApplicationLogResponse extends JsonRpcResponse<List<Event>> {
}
