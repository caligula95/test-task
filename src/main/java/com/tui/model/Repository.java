package com.tui.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(exclude = "branches")
public class Repository {

    private String name;
    private String ownerLogin;
    private List<Branch> branches;
}
