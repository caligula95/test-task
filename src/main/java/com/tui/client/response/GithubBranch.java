package com.tui.client.response;

import lombok.Data;

@Data
public class GithubBranch {

    private String name;
    private GithubCommit commit;
}
