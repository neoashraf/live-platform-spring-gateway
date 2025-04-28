package net.max.live.com.filter.webfilter;

import com.github.jknack.handlebars.internal.lang3.tuple.Pair;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Set;

public class AllowedPaths {
    public static final Set<PathAndMethod> allowedPaths = Set.of(
            new PathAndMethod("/business/api/v1/devices", HttpMethod.POST),
            new PathAndMethod("/business/api/v1/logged-in-user", HttpMethod.GET),
            new PathAndMethod("/business/api/v1/devices/status", HttpMethod.GET),
            new PathAndMethod("/business/api/v1/complete-registration", HttpMethod.POST)
    );

}
