package pizzeria.food.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pizzeria.food.authentication.JwtAuthenticationEntryPoint;
import pizzeria.food.authentication.JwtRequestFilter;

/**
 * The type Web security config.
 */
@Configuration
public class RequestAuthenticationConfig extends WebSecurityConfigurerAdapter {
    private final transient JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final transient JwtRequestFilter jwtRequestFilter;

    @Autowired
    public RequestAuthenticationConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtRequestFilter jwtRequestFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configuration for the http requests
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception Exception that has arisen during the configuration
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String role = "ROLE_MANAGER";
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/ingredient/save").hasAuthority(role)
                .antMatchers("/ingredient/update").hasAuthority(role)
                .antMatchers("/ingredient/delete").hasAuthority(role)
                .antMatchers("/recipe/save").hasAuthority(role)
                .antMatchers("/recipe/update").hasAuthority(role)
                .antMatchers("/recipe/delete").hasAuthority(role)
                .antMatchers("/allergens/menu").authenticated()
                .antMatchers("/allergens/warn").authenticated()
                .anyRequest().permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
