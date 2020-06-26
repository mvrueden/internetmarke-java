package de.marskuh;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import lombok.Builder;
import lombok.Data;

import java.util.stream.Collectors;

@Data
@Builder(builderClassName = "Builder")
public class Signature {
    String partnerId;
    String keyPhase;
    String timestamp;
    String partnerSignature;

    public String asMd5() {
        final HashCode hashedBytes = Hashing.md5().hashString(getStringToHash(), Charsets.UTF_8);
        final String hashString = hashedBytes.toString();
        return hashString.substring(0, 8);
    }

    public String asSha256() {
        final HashCode hashedBytes = Hashing.sha256().hashString(getStringToHash(), Charsets.UTF_8);
        final String hashString = hashedBytes.toString();
        return hashString;
    }

    private String getStringToHash() {
        final String hashme = Lists.newArrayList(partnerId, timestamp, keyPhase, partnerSignature).stream()
                .map(String::trim)
                .collect(Collectors.joining("::"));
        return hashme.trim();
    }
}
