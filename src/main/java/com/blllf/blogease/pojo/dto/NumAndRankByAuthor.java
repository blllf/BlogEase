package com.blllf.blogease.pojo.dto;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumAndRankByAuthor {
    private Integer createUser;
    private Integer articleCount;
    private Integer peopleRank;
    private Integer numberLikes;
}
