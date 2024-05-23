package lv.tsi.bookstore.configuration.security

import com.vaadin.flow.spring.security.VaadinWebSecurity
import lv.tsi.bookstore.feature.login.LoginView
import lv.tsi.bookstore.feature.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val userRepository: UserRepository,
) : VaadinWebSecurity() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(
        configuration: AuthenticationConfiguration
    ): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun userDetailsService() = UserDetailsService { username ->
        userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User '$username' not found.")
    }

    override fun configure(http: HttpSecurity) {
        super.configure(http)
        setLoginView(http, LoginView::class.java)
    }
}

@Configuration
@EnableWebSecurity
class EndpointSecurityConfiguration(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    @Bean
    @Order(1)
    fun configure(http: HttpSecurity): SecurityFilterChain {
        return http.securityMatcher("/api/**")
            .cors(withDefaults())
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/v1/auth/**").permitAll()
                    .anyRequest().authenticated()
            }
            .httpBasic(withDefaults())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}
