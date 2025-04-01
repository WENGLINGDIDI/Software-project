package com.shutiao.leasingsystem.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.validator.constraints.Length;

public class addUserDto {

    @NotBlank(message = "用户名不能为空")
    private String name;
    @Email(message = "邮箱格式错误")
    private String email;
    @NotBlank(message = "密码不能为空")
    @Length(min = 2)
    private String password;
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
