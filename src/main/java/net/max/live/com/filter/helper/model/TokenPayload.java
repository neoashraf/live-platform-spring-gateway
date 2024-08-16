package net.max.live.com.filter.helper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.max.live.com.layersuper.BaseTokenPayload;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenPayload extends BaseTokenPayload {
    private String clientId;
    private String clientHost;
    private String clientAddress;
}
