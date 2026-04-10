package com.smartfarmingassistant.sfa.web.filter;


import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import com.smartfarmingassistant.sfa.constants.JwtConstants;
import com.smartfarmingassistant.sfa.helpers.JwtHelper;
import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.service.domain.UserService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {


    private final JwtHelper jwtHelper;
    private final UserService userService;

    private final JwtConstants jwtConstants;

    public JwtFilter(JwtHelper jwtHelper, UserService userService, JwtConstants jwtConstants) {
        this.jwtHelper = jwtHelper;
        this.userService = userService;
        this.jwtConstants = jwtConstants;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {


        String path = request.getRequestURI();

        if (request.getRequestURI().startsWith("/api/user/register") ||
                request.getRequestURI().startsWith("/api/user/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String headerValue = request.getHeader(jwtConstants.getHeader());

        if (headerValue == null || !headerValue.startsWith(jwtConstants.getTokenPrefix())) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        String token =headerValue.substring(jwtConstants.getTokenPrefix().length()).trim();

        try {
            String username = jwtHelper.extractUsername(token);

            if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtHelper.isExpired(token)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userOpt.get(),
                                null,
                                userOpt.get().getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (JwtException e) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);

    }
}