package io.mywish.eoscli4j.model.response;

import lombok.Getter;

@Getter
public class Error {
    private int code;
    private String name;
    private String what;

    public String toString() {
        return "Error " + name + "(" + code + "): " + what;
    }
}
