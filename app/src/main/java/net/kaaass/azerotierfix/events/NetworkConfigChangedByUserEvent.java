package net.kaaass.azerotierfix.events;

import net.kaaass.azerotierfix.model.Network;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户更改 ZT 网络配置事件
 */
@Data
@AllArgsConstructor
public class NetworkConfigChangedByUserEvent {
    private final Network network;
}
