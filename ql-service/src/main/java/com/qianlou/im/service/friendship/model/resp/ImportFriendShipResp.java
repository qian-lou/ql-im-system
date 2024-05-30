package com.qianlou.im.service.friendship.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImportFriendShipResp {

    private List<String> successIds;

    private List<String> failIds;
}
