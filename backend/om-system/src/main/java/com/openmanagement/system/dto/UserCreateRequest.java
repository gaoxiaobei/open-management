package com.openmanagement.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateRequest {

    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Real name must not be blank")
    private String realName;

    private String mobile;

    private String email;

    private Long deptId;

    private Long positionId;

    private List<Long> roleIds;
}
