package com.qianlou.im.service.user.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImportUserResp {

    private List<String> successIds;
    private List<String> failIds;



}
