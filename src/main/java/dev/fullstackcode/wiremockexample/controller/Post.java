package dev.fullstackcode.wiremockexample.controller;

import lombok.Data;

@Data
public class Post {
    private Integer id;
    private String title;
    private String body;
    private Integer userId;
}
