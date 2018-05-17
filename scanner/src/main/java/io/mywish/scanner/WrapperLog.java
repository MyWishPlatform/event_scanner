package io.mywish.scanner;

import lombok.Getter;

import java.util.List;

@Getter
public class WrapperLog {
    String address;
    List<String> topics;
    String data;
}
