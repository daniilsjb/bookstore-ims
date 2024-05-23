package lv.tsi.bookstore.configuration.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader("Authorization")
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val accessToken = authorization.substring(7)
        val username = jwtService.extractUsername(accessToken)

        val user = userDetailsService.loadUserByUsername(username)
        if (!jwtService.isTokenValid(accessToken, user)) {
            filterChain.doFilter(request, response)
            return
        }

        val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }
}
